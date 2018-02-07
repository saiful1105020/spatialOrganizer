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
		  $this->load->model('admin_model');
		  
		  //Load necessary modules
     }
	 
	public function index()
	{
		$data=array('login_error'=>false);
		$this->load->view('admin-login',$data);
		  
		if(isset($_SESSION["admin_name"]))
		{
			redirect('/admin', 'refresh');
		}
		else
		{
			
		}
	}
	
	public function login()
	{
		//$admin_name = $_POST['admin_name'];
		
		if(isset($_POST['admin_name']) && isset($_POST['password']))
		{
			$data = array('admin_name'=>trim($_POST['admin_name']),'password'=>md5($_POST["password"]));
			
			$query= $this->admin_model->getLoginInfo($data);
			
			if($query->num_rows()==1)
			{
				$loginInfo=$query->row_array();
				$_SESSION["admin_name"]=$loginInfo['username'];
				
				redirect('/admin', 'refresh');
			}
			else
			{
				$data = array(
				   'login_error' => true
				);
				$this->load->view('admin-login',$data);
			}
			
		}
		else
		{
			$data = array(
				   'login_error' => false
				);
			$this->load->view('admin-login',$data);
		}
	}
}
