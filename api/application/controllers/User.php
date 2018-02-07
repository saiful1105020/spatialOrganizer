<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class User extends CI_Controller {
	 
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
		echo "Hello User";
	}
}
