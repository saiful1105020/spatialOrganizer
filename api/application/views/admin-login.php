<!DOCTYPE html>
<html >
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spatial Organizer</title>
	
	<link rel="stylesheet" href="<?php echo base_url("assets/css/bootstrap.min.css"); ?>" />
	<link rel="stylesheet" href="<?php echo base_url("assets/css/bootstrap-theme.min.css"); ?>" />
    <title>Admin Login</title>
    
    
    
    
    <style>
		@import url(http://fonts.googleapis.com/css?family=Exo:100,200,400);
		@import url(http://fonts.googleapis.com/css?family=Source+Sans+Pro:700,400,300);

		body{
			margin: 0;
			padding: 0;
			background: #fff;

			color: #fff;
			font-family: Arial;
			font-size: 12px;
		}

		.body{
			position: absolute;
			top: -20px;
			left: -20px;
			right: -40px;
			bottom: -40px;
			width: auto;
			height: auto;
			/*background-image: url(<?php echo base_url('images/back2.jpg'); ?>);
			background-image: url(http;//i62.tinypic.com/nmj4p0.jpg);*/
			background-size: cover;
			/*-webkit-filter: blur(5px);*/
			z-index: 0;
		}

		.grad{
			position: absolute;
			top: -20px;
			left: -20px;
			right: -40px;
			bottom: -40px;
			width: auto;
			height: auto;
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,rgba(0,0,0,0)), color-stop(100%,rgba(0,0,0,0.65))); /* Chrome,Safari4+ */
			z-index: 1;
			opacity: 0.7;
		}

		.header1{
			position: absolute;
			top: calc(40% - 55px);
			left: calc(50% - 50px);
			z-index: 2;
			font-size: 20px;
			}
		.header2{
			position: absolute;
			top: calc(80% + 20px);
			left: calc(50% - 130px);
			z-index: 2;
			color: #f18989;
			font-family: 'Exo', veranda;
			font-size: 16px;
		}

		.header{
			position: absolute;
			top: calc(30% - 90px);
			left: calc(50% - 150px);
			z-index: 2;
		}

		.header div{
			float: left;
			color: #fff;
			font-family: 'Exo', verdana;
			font-size: 35px;
			font-weight: 255;
		}

		.header div span{
			color:  !important;
		}

		.login{
			position: absolute;
			top: calc(50% - 75px);
			left: calc(50% - 50px);
			height: 150px;
			width: 350px;
			padding: 10px;
			z-index: 2;
		}

		.login input[type=text]{
			width: 250px;
			height: 30px;
			background: transparent;
			border: 1px solid rgba(255,255,255,0.6);
			border-radius: 2px;
			color: #fff;
			font-family: 'Exo', sans-serif;
			font-size: 16px;
			font-weight: 400;
			padding: 4px;
		}

		.login input[type=password]{
			width: 250px;
			height: 30px;
			background: transparent;
			border: 1px solid rgba(255,255,255,0.6);
			border-radius: 2px;
			color: #fff;
			font-family: 'Exo', sans-serif;
			font-size: 16px;
			font-weight: 400;
			padding: 4px;
			margin-top: 10px;
		}

		.login input[type=button]{
			width: 260px;
			height: 35px;
			background: #fff;
			border: 1px solid #fff;
			cursor: pointer;
			border-radius: 2px;
			color: #a18d6c;
			font-family: 'Exo', sans-serif;
			font-size: 16px;
			font-weight: 400;
			padding: 6px;
			margin-top: 10px;
		}

		.login input[type=button]:hover{
			opacity: 0.8;
		}

		.login input[type=button]:active{
			opacity: 0.6;
		}

		.login input[type=text]:focus{
			outline: none;
			border: 1px solid rgba(255,255,255,0.9);
		}

		.login input[type=password]:focus{
			outline: none;
			border: 1px solid rgba(255,255,255,0.9);
		}

		.login input[type=button]:focus{
			outline: none;
		}

		::-webkit-input-placeholder{
		   color: rgba(255,255,255,0.6);
		}

		::-moz-input-placeholder{
		   color: rgba(255,255,255,0.6);
		}
		
		
<!--amar kaj-->		
		
		@import url(http://fonts.googleapis.com/css?family=Roboto:400);
body {
  background-color:#fff;
  -webkit-font-smoothing: antialiased;
  font: normal 14px Roboto,arial,sans-serif;
}

.container {
    padding: 25px;
    position: fixed;
	
}

.form-login {
	margin-top: 60%;
	background-color: #aaa;
    padding-top: 10px;
    padding-bottom: 20px;
    padding-left: 20px;
    padding-right: 20px;
    border-radius: 15px;
    border-color:#d2d2d2;
    border-width: 5px;
    box-shadow:0 1px 0 #cfcfcf;
}

h4 { 
 border:0 solid #fff; 
 border-bottom-width:1px;
 padding-bottom:10px;
 text-align: center;
}

.form-control {
    border-radius: 10px;
}

.wrapper {
    text-align: center;
}
body{
	background-color: teal;
}
    </style>


    
</head>

<body>

    <div class="body">
	<div class = "container">
    <div class="row" >

        <div class="col-md-offset-5 col-md-3">
		
		<form name="loginForm" method="post" action="<?php echo site_url('home/login');?>" >	<!-- CHECK LATER-->
            <div class="form-login" >
            
			<div class="header">	
			<div>Spatial<span>Organizer</span></div>
			</div>
			
			<!--<div class="header1">Admin</div>-->
			
			<h4>Welcome back Admin ... </h4>
          
			<input type="text" id="userName" name="admin_name" class="form-control input-sm chat-input" placeholder="username" required/>
            </br>
            <input type="password" id="userPassword" name="password" class="form-control input-sm chat-input" placeholder="password" required/>
            </br>
            
			<button type="submit" class="btn btn-default">Sign-In</button>
            </div>
			<!--
			<div>
				Don't have any account?
			</div>
			-->
        </form>
        </div>
    </div>
	</div>
	
	
	<script src="<?php echo base_url("assets/js/prefixfree.min.js"); ?>"></script>
	<script type="text/javascript" src="<?php echo base_url("assets/js/jquery-1.11.2.min.js"); ?>"></script>
	<script type="text/javascript" src="<?php echo base_url("assets/js/bootstrap.js"); ?>"></script>

		
	<?php
		if($login_error==true)
		{
			echo '<div class="header2" style="text-align:center; margin:-10%" ><strong> Login Failed! Username and password didn\'t match </strong></div>';
		}
	?>
</body>

</html>
