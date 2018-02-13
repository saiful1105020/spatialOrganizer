<?php
class Employee_model extends CI_Model 
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
	
	public function getUnassignedTasks($date)
	{
		$sql = 'SELECT * FROM `task` WHERE `assignment_status`=0 and `date`=?';
		$query = $this->db->query($sql,array($date));
		$temp = $query->result_array();
		
		return $temp;
	}
	
	public function setAssignmentStatus($employeeId,$taskId)
	{
		$this->db->trans_start();
		$sql = "UPDATE `task` SET `assignment_status`=1, `employee_id`=? WHERE task_id = ?";
		$query = $this->db->query($sql,array($employeeId, $taskId));
		
		$sql ="UPDATE `employee` SET `newTaskFlag`=1 WHERE `employee_id` = ?";
		$query = $this->db->query($sql,array($employeeId));
		$this->db->trans_complete();
	}
	
	public function getTaskList($employeeId)
	{
		$date = date("Y-m-d");
		$response = array();
		/**
			status = 0/1
			error = error message, no
			tasks = list of tasks
		*/
		
		$tasks = array();
		$error = array();
		
		$sql='SELECT * FROM `task` WHERE employee_id = ? AND `date` = ? AND assignment_status = 1';
		$query = $this->db->query($sql,array($employeeId,$date));
		
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
	
	public function getAssignedTasks($date)
	{
		$sql = 'SELECT * FROM `task` WHERE `assignment_status`=1 and `date`=?';
		$query = $this->db->query($sql,array($date));
		$temp = $query->result_array();
		
		return $temp;
	}
	
	public function getLocation($locationId)
	{
		$sql = "SELECT * FROM `location` WHERE location_id = ?";
		$query = $this->db->query($sql,array($locationId));
		$dbreply = $query->row_array();
		return $dbreply;
	}
	
	public function getWorkerList()
	{
		$sql = 'SELECT a.`employee_id`, b.`lat`, b.`lon` 
		FROM `employee` a, `location` b WHERE 
		b.`location_id` = a.`location_id`';
		$query = $this->db->query($sql);
		$temp = $query->result_array();
		
		return $temp;
	}
	
	public function getLoginInfo($userName,$password)
	{
		$sql='SELECT * FROM `employee` WHERE `name` = ? and `password` = ?';
		$query = $this->db->query($sql,array($userName,$password));
		
		$data = array();
		$data['workerProfile']=array();
		
		if($query->num_rows()==0)
		{
			$data['loginStatus']=0;
		}
		else
		{
			$temp = $query->row_array();
			$data['loginStatus']=1;
			$data['workerProfile'] = $temp;
			/**
				Find user's home location
			*/
			$sql = "SELECT `lat`, `lon`, `description` FROM `location` WHERE `location_id` = ?";
			
			$query = $this->db->query($sql,array($temp['location_id']));
			$data['location'] = $query->row_array();
		}
		
		return $data;
	}
	
}
?>