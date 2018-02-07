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
}
?>