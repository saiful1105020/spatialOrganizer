<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class User extends CI_Controller {
	 
	 public function __construct()		
     {
          parent::__construct();
		  
		  //Load Necessary Libraries and helpers
		  $this->load->model('user_model');
          
		  $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
     }
	 
	public function index()
	{
		//Do Nothing
	}
	
	/**
		Return a json object
		-status: 0-> Failed
				1-> Successful
	*/
	public function saveTask()
	{
		$task = array();
		$task['userId'] = $_POST['userId'];
		$task['desc'] = $_POST['description'];
		
		$jsonData = array();
		$jsonData['desc'] = $task['desc'];
		
		$task['date'] = $_POST['date'];
		$task['duration'] = $_POST['duration'];
		$task['delFrom'] = $_POST['delFrom'];
		$task['delTo'] = $_POST['delTo'];
		$task['delLat'] = $_POST['delLat'];
		$task['delLong'] = $_POST['delLong'];
		$task['pickLat'] = $_POST['pickLat'];
		$task['pickLong'] = $_POST['pickLong'];
		$task['status'] = $_POST['status'];
		
		//echo json_encode($task);
		
		$data=$this->user_model->saveTask($task);
		
		$jsonData = array();
		
		if($data)
		{
			$jsonData['status']=1;
			$jsonData['taskId']=$data;
		}
		else
		{
			$jsonData['status']=0;
			$jsonData['taskId']=$data;
		}
		
		echo json_encode($jsonData);
	}
	
	/**
		Return a json object
		-status: 0-> Failed
				1-> Successful
		[if status 0, ignore all]
		-userName
		-userId
		-homeLocation
	*/
	public function verifyLogin()
	{	
		$email = $_POST['email'];
		//echo $email;
		$password = md5($_POST['password']);
		//echo $password;
		
		$jsonData = array();
		
		$data=$this->user_model->getLoginInfo($email,$password);
		
		$jsonData['status'] = $data['loginStatus'];
		$jsonData['userId'] = $data['userId'];
		$jsonData['homeLocation'] = $data['homeLocation'];
		
		echo json_encode($jsonData);
	}
	
	/**
		Return a json object
		-array of tasks. 
	*/
	public function getTaskList()
	{
		$userId = $_POST['userId'];
		//$userId = 5;
		
		
		$response = $this->user_model->getTaskList($userId);
		
		echo json_encode($response);
	}
	
	public function unitTest()
	{
		$task['delLat'] = $_GET['delLat'];
		$task['delLong'] = $_GET['delLong'];
		$task['pickLat'] = $_GET['pickLat'];
		$task['pickLong'] = $_GET['pickLong'];
		
		$data=$this->user_model->saveTask($task);
	}
}
