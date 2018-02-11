<?php
class User_model extends CI_Model 
{
	
	public function __construct()	
	{
		$this->load->database();
	}

	
	/**
		Used for login process.
		Input: email-id and password
		Output: loginStatus, userId, homeLocation
		
		Ignore all if status is false.
	*/
	public function getLoginInfo($email,$password)
	{
		$sql='SELECT `user_id`,`home_location_id` FROM `user` WHERE `email` = ? and `password` = ?';
		$query = $this->db->query($sql,array($email,$password));
		
		$data = array();
		
		if($query->num_rows()==0)
		{
			$data['loginStatus']=0;
			$data['userId']=-1;
			$data['homeLocation']="";
		}
		else
		{
			//echo 'Hello';
			
			$temp = $query->row_array();
			$data['loginStatus']=1;
			$data['userId']=$temp['user_id'];
			
			
			/**
				Find user's home location
			*/
			$sql = "SELECT `lat`, `lon`, `description` FROM `location` WHERE `location_id` = ?";
			
			$query = $this->db->query($sql,array($temp['home_location_id']));
			
			if($query->num_rows()==0)
			{
				/* invalid home location */
				$data['homeLocation']=array("lat"=>-1000,"lon"=>-1000);
			}
			else
			{
				$temp = $query->row_array();
				$data['homeLocation']=array("lat"=>$temp['lat'],"lon"=>$temp['lon']);
			}
		}
		
		//print_r($data);
		
		return $data;
	}
	
	public function getLocation($locationId)
	{
		$sql = "SELECT * FROM `location` WHERE location_id = ?";
		$query = $this->db->query($sql,array($locationId));
		$dbreply = $query->row_array();
		return $dbreply;
	}
	
	public function getTaskList($userId)
	{
		$response = array();
		/**
			status = 0/1
			error = error message, no
			tasks = list of tasks
		*/
		
		$tasks = array();
		$error = array();
		
		$sql='SELECT * FROM `task` WHERE `user_id` = ?';
		$query = $this->db->query($sql,array($userId));
		
		if($query)
		{
			$status = 1;
			$dbreply = $query->result_array();
 			foreach ($dbreply as $task)
			{
				$task['delivery_location'] = $this->getLocation($task['delivery_location_id']);
				$task['pickup_location'] = $this->getLocation($task['delivery_location_id']);
				
				array_push($tasks,$task);
			}
		}
		else
		{
			$status = 0;
			$error['msg'] = $this->db->_error_message();
			$error['code'] = $this->db->_error_number();
		}
		
		$response['status'] = $status;
		$response['error'] = $error;
		$response['tasks'] = $tasks;
		return $response;
	}
	
	public function saveTask($task)
	{
		/**
			Insert into location table and return id
		*/
		$this->db->trans_start();
		
		$delLocation = $this->insertLocation($task['delLat'],$task['delLong']);
		$pickLocation = $this->insertLocation($task['pickLat'],$task['pickLong']);
		
		$sql = "INSERT INTO `task`(`user_id`,`description`,
		`date`, `duration_mins`, `delivery_start_time`
		, `delivery_deadline`, `pickup_location_id`,
		`delivery_location_id`, `assignment_status`)
		VALUES (?,?,?,?,?,?,?,?,?)";
		
		$query = $this->db->query($sql,array($task['userId'],$task['desc'],
		$task['date'], $task['duration'],
		$task['delFrom'],$task['delTo'],
		$pickLocation,
		$delLocation,
		$task['status']));
		
		$id = $this->db->insert_id();
		$this->db->trans_complete();
		return $id;
	}
	
	public function insertLocation($lat,$lon)
	{
		$sql = "INSERT INTO `location` (`lat`, `lon`) VALUES (?,?)";	
		$query = $this->db->query($sql,array($lat,$lon));
		return $this->db->insert_id();
	}
}
?>