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
	
	public function getAssignedTasks($date)
	{
		$sql = 'SELECT * FROM `task` WHERE `assignment_status`=1 and `date`=?';
		$query = $this->db->query($sql,array($date));
		$temp = $query->result_array();
		foreach($temp as $t)
		{
			print_r($t);
		}
		return $temp;
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