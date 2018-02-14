<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Home extends CI_Controller {
	 
	 public function __construct()		
     {
          parent::__construct();
		  
		  
		  //Load Necessary Libraries and helpers
          
		  $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		  
		  //Load necessary modules
		  $this->load->model('employee_model');
		  $this->load->model('user_model');
     }
	 
	public function index()
	{
		
	}
	
	public function assignFirst()
	{
		$date = date("Y-m-d");
		$jobStartTime = '10:00:00';
		
		/**
			Get all unassigned tasks on that day
		*/
		
		$uTasks = $this->employee_model->getUnassignedTasks($date);
		$count=0;
		foreach ($uTasks as $key => $row) {
			$delivery_deadline[$key]  = $row['delivery_deadline'];
			$count++;
		}
		if($count>0)
		{
			array_multisort($delivery_deadline, SORT_ASC, $uTasks);
		}
		
		/**
			Get all worker ids and location
		*/
		$temp = $this->employee_model->getWorkerList();
		$workers = array();
		foreach($temp as $w)
		{
			$w['available_time'] = $jobStartTime;
			array_push($workers,$w);
		}
		
		$assign = array();
		foreach($uTasks as $task)
		{
			$minDelay = 99999999;
			$bestWorker = -1;
			$index= 0;
			
			$temp = $this->user_model->getLocation($task['pickup_location_id']);
			$temp2 = $this->user_model->getLocation($task['delivery_location_id']);
				
			foreach($workers as $w)
			{
				$delay = 0;
				$availTime = $this->sqlTimeToSeconds($w['available_time']);	
				
				$pickupDelay = $this->getDrivingDistance($w['lat'], $w['lon'],$temp['lat'], $temp['lon'] );
				$taskDelay = $task['duration_mins']*60;
				$deliveryDelay = $this->getDrivingDistance($temp['lat'], $temp['lon'],$temp2['lat'], $temp2['lon'] );
				
				$delay = $availTime+$pickupDelay+$taskDelay+$deliveryDelay;
				
				if($minDelay>$delay)
				{
					$minDelay = $delay;
					$bestWorker = $index;
				}
				
				$index++;
			}
			
			$taskId = $task['task_id'];
			$assign[$taskId] = $workers[$bestWorker]['employee_id'];
			$workers[$bestWorker]['lat'] = $temp2['lat'];
			$workers[$bestWorker]['lon'] = $temp2['lon'];
			
			$prevTime = $this->sqlTimeToSeconds($workers[$bestWorker]['available_time']);
			$totalSeconds = $delay;
			$newTime = $this->secondsToSqlTime($totalSeconds);
			
			$delStartTime = $this->sqlTimeToSeconds($task['delivery_start_time']);
			if($newTime<$delStartTime)
			{
				$newTime = $delStartTime;
			}
			
			$newTime = $this->secondsToSqlTime($newTime);
			
			$workers[$bestWorker]['available_time'] = $newTime;
			
			$this->employee_model->setAssignmentStatus($workers[$bestWorker]['employee_id'],$taskId);
			
		}
		
		echo 'Task Assignment Successful! <br>';
		print_r($assign);
	}
	
	public function assignAgain()
	{
		$date = date("Y-m-d");
		
		//Get unassigned tasks
		$uTasks = $this->employee_model->getUnassignedTasks($date);
		
		$pendingList = $this->employee_model->getPendingCount($date);
		//print_r($pendingList);
		
		$assign = array();
		foreach($uTasks as $task)
		{
			$v = 99999999;
			$eid = 0;
			foreach($pendingList as $key=>$value)
			{
				if($value<$v)
				{
					$v = $value;
					$eid = $key;
				}
			}

			$assign[$task['task_id']] = $eid;
			$pendingList[$eid]++;
		}
		
		foreach($assign as $key=>$value)
		{
			$this->employee_model->setAssignmentStatus($value,$key);
		}
		
		echo 'Incremental Task Assignment Successful';
		print_r($assign);
	}
	
	public function sqlTimeToSeconds($sqlTime)
	{
		$seconds = 0;
		$temp = explode(":",$sqlTime);
		//print_r($temp);
		$seconds+=$temp[2];
		$seconds+=60*$temp[1];
		$seconds+=60*60*$temp[0];
		return $seconds;
	}
	
	public function secondsToSqlTime($seconds)
	{
		$hrs = (floor($seconds/3600));
		$seconds = ($seconds - $hrs*3600);
		$mins = (floor($seconds/60));
		$seconds = ($seconds - $mins*60);
		return $hrs.':'.$mins.':'.$seconds;
	}
	
	public function unitTest()
	{
		echo $s = $this->sqlTimeToSeconds("18:12:45");
		echo "<br>";
		echo $this->secondsToSqlTime($s);
	}
	
	public function getDrivingDistance($lat1, $long1, $lat2, $long2)
	{
		$url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=".$lat1.",".$long1."&destinations=".$lat2.",".$long2."&mode=driving&language=pl-PL";
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_PROXYPORT, 3128);
		curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
		$response = curl_exec($ch);
		curl_close($ch);
		$response_a = json_decode($response, true);
		$dist = $response_a['rows'][0]['elements'][0]['distance']['value'];
		$time = $response_a['rows'][0]['elements'][0]['duration']['value'];

		return $time;
		//return array('distance' => $dist, 'time' => $time);
	}
}
