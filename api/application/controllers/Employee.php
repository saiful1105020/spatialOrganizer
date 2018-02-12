<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Employee extends CI_Controller {
	 
	 public function __construct()		
     {
          parent::__construct();
		  
		  //Load Necessary Libraries and helpers
          
		  $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		  $this->load->model('employee_model');
		  
		  //Load necessary modules
     }
	 
	public function index()
	{
		echo "Hello User";
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
		$userName = 'Azad';
		$password = md5('12345');
		
		//$userName = $_POST['userName'];
		//echo $email;
		//$password = md5($_POST['password']);
		//echo $password;
		
		$jsonData = array();
		
		$data=$this->employee_model->getLoginInfo($userName,$password);
		
		$jsonData['status'] = $data['loginStatus'];
		$jsonData['profile'] = $data['workerProfile'];
		$jsonData['workerHomeLocation'] = $data['location'];
		
		echo json_encode($jsonData);
	}
}
