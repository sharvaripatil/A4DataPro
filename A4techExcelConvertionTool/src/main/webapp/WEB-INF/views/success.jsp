<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page session="false" %>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>A4-ASI Connect</title>
<!-- Bootstrap -->
<link href="resources/bootstrap.css" rel="stylesheet">
<link rel="stylesheet" href="resources/loginstyle.css">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css" rel="stylesheet">
<link href="resources/preview.css" rel="stylesheet" type="text/css">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css" type="text/css">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
		  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		<![endif]-->
</head>
<body style="overflow:hidden;">
<!-- <form method="POST" action="uploadFile" enctype="multipart/form-data"> -->
<div class="wrapper">
  <div class="container">
    <div class="col-md-6 col-xs-offset-2 col-lg-8 col-lg-offset-2">
      <div class="panel-body">
       <div class="success">
        <h1 style="font-size: 39px !important;"><strong>${fileName}</strong> Product's are uploaded successfully.</h1>
        <br/><br/>
      <a class="btn btn-home btn-lg" style="margin: 0px 41% auto;" href="<c:url value='/uploadFile.htm' />"><i class="fa fa-home" aria-hidden="true"></i> Home</a>
    </div>
        <br>
        <br>
        <br>
       <div class="filedownload" align="left">
        <h4>Click on the link to download Product Error File:
	<c:url value="/sendEmails.html" var="sendEmailsLink" />
	<a href="${sendEmailsLink}"><u>Download a File</u></a></h4>
      <br/>
         <h4 style="font-size: 39px !important;"><strong>${successmsg}</strong> </h4>
        </div>
    
      </div>
    </div>
  </div>
</div>
<ul class="bg-bubbles">
  <li></li>
  <li></li>
  <li></li>
  <li></li>
  <li></li>
  <li></li>
  <li></li>
  <li></li>
  <li></li>
  <li></li>
</ul>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
<script src="resources/jquery-1.11.2.min.js"></script> 

<!-- Include all compiled plugins (below), or include individual files as needed --> 
<script src="resources/bootstrap.js"></script> 


</body>
</html>