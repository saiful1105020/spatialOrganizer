<?php
class Admin_model extends CI_Model 
{
	
    public function __construct()
	{
        $this->load->database();
	}
	
	public function getLoginInfo($data)
	{
		$sql='SELECT * FROM system_admin where `username` = ? and `password` = ?';
		$query = $this->db->query($sql,array($data['admin_name'],$data['password']));
		return $query;
	}
}

?>