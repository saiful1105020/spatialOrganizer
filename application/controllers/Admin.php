<?php

/**
*	Controller for all admin actions
*/

defined('BASEPATH') OR exit('No direct script access allowed');

class Admin extends CI_Controller {
	  
	 public function __construct()
     {
          parent::__construct();
		  
		  /**
		  *	Load Libraries , Models and Helpers
		  */
		  
          $this->load->library('session');
          $this->load->helper('form');
          $this->load->helper('url');
          $this->load->helper('html');
		  $this->load->library('form_validation');
		 
		/**
			If not logged in, redirect to login page
		*/
		if(!isset($_SESSION["admin_name"]))
		{
			redirect('/..', 'refresh');
		}
		  
		
		$this->load->view('templates/header');
		
     }
	 
	
	
	/**
	*	[ADMIN HOME PAGE] --- Search is the default activity
	*/ 
	public function index()			
	{
		//echo $_SESSION['test_session'];
		//redirect('admin/showBarGraph','refresh');
	}
	
	public function logout()
	{
		unset($_SESSION['admin_name']);
		redirect('/../', 'refresh');
	}
}