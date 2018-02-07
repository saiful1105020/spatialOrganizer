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
		
	}
}
