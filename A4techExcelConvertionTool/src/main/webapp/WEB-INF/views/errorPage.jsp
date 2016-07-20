<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>A4 - ASI Connect</title>

<!-- CSS -->
<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:400,100,300,500">
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="resources/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="resources/css/style.css">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
</head>

<body>
<!-- Top content -->
<div class="top-content">
  <div class="inner-bg">
    <div class="form-top" align="center"> 
    <div class="row">
      <img src="resources/img/error.png" alt=""/> </div>
      <div class="row">
        <h3>Oops! Something Went Wrong</h3>
        <p>Please Try Again After Sometime..</p>

      </div>
      <hr>
     <div class="row">
        <!-- <button type="submit" class="btn1"><span class="glyphicon glyphicon-home"></span> &nbsp;Home</button> -->
        <a class="btn btn-home btn-lg btn1" style="margin: 0px 41% auto;" href="<c:url value='/uploadFile.htm' />"><i class="fa fa-home" aria-hidden="true"></i> Home</a>
      </div>
    </div>
      
  </div>
</div>

<!-- Javascript --> 
<script src="resources/js/jquery-1.11.1.min.js"></script> 
<script src="resources/bootstrap/js/bootstrap.min.js"></script> 
<script src="resources/js/jquery.backstretch.min.js"></script> 
<script src="resources/js/scripts.js"></script> 
</body>
</html>