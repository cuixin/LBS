<?php
	require_once('web_services/toServer.php');

    	session_start(); //start of the session

	// set timeout period in seconds
	$inactive = 600;

	// check to see if $_SESSION['timeout'] is set
	if(isset($_SESSION['timeout']) ) {
	      $session_life = time() - $_SESSION['timeout'];
	      if($session_life > $inactive) { //if the session was inactive then it's destroyed and the user 
					      //is sent to login page finally, because there is not going to be 
					      //any session to keep the information for the user.
		    session_destroy(); 
		    header("Location: index.php"); 	    
	      }
	}
	//sets $_SESSION['timeout'] equals to current time;
	$_SESSION['timeout'] = time();

	require_once('action/login.inc.php');
	require_once('action/newUserAction.inc.php'); //needed when a new user is going to be created

	if( !isset( $_SESSION['userID']) ){
	      require_once('login.html'); 
	}else{
		  $loginUser = $_SESSION['userID']->userInformation();
		  print_r($loginUser);
	      if($loginUser['administrator']){ //needed when a new admin is going to be created
		    require_once('action/newAdminAction.inc.php');
	      }
	      require_once('home.inc.php'); 
	}
?>