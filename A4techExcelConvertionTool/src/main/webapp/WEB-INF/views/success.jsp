<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page session="false" %>
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
    <div class="col-md-6 col-md-offset-3 form-bottom" align="center"> 
      <div class="row" style="text-align: center;">
        <c:choose>
       		<c:when test="${successProductsCount != 0 && failureProductsCount != 0}">
       			<h3 style="font-size: 25px !important;"><strong>${successProductsCount}</strong> Product(s) have been uploaded <strong class="greens">successfully</strong> & <strong>${failureProductsCount}</strong> Product(s) are <strong class="faileds">failed</strong> </h3>
       		</c:when>
       		<c:when test="${failureProductsCount != 0}">
       			<h3 style="font-size: 25px !important;"><strong>${failureProductsCount}</strong> Product(s) are <strong class="faileds">failed</strong></h3>
       		</c:when>
       		<c:when test="${successProductsCount != 0}">
       			<h3 style="font-size: 25px !important;"><strong>${successProductsCount}</strong> Product(s) have been uploaded <strong class="greens">successfully</strong></h3>
       		</c:when>
       </c:choose>
      </div>
      <hr>
      <div class="row">
      <c:if test="${failureProductsCount != 0}">
       <div class="filedownload" align="center">

        <p>Click on the link to download Product Error File:
	<c:url value="/downloadFile.html" var="sendEmailsLink" /><br/>
	<a href="${sendEmailsLink}"><strong>Download a File</strong></a></p>
      <br/>
        <%--  <h3 class="successtxt"><strong>${successmsg}</strong> </h3> --%>
        </div>
    </c:if>
       <!--  <p>Click on the link to download the Product error file : <strong>Download File</strong></p> -->
      </div>
      
        <div class="row">
        <div class="text">
          <!-- <h1> Email Sent Successfully...!!! </h1> -->
          <h3 style="text-align: center;"> <strong>${successmsg}</strong> </h3>
        </div>
      </div>
    <!--  <div class="row"> -->
        <!-- <button type="submit" class="btn1"><span class="glyphicon glyphicon-home" ></span> &nbsp;Home</button> -->
        <a class="btn btn-home btn-lg btn1" style="margin: 0px 41% auto;" href="<c:url value='/uploadFile.htm' />"><i class="fa fa-home" aria-hidden="true"></i> Home</a>
     <!--  </div> -->
    </div>
      
  </div>
</div>

<!-- Javascript --> 
<script src="resources/js/jquery-1.11.1.min.js"></script> 
<script src="resources/bootstrap/js/bootstrap.min.js"></script> 
<script src="resources/js/jquery.backstretch.min.js"></script> 
<script src="resources/js/scripts.js"></script> 

<!--[if lt IE 10]>
            <script src="resources/js/placeholder.js"></script>
        <![endif]-->

</body>
</html>