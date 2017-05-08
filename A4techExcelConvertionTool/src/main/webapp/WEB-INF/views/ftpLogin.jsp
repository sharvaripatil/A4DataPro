<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false" %>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FTP Login</title>
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->

<!-- Favicon and touch icons -->

<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:400,100,300,500">
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="resources/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="resources/css/style.css">
<link rel="stylesheet" href="resources/css/component.css">
</head>

<body>
<!-- Top content -->
<div class="top-content">
  <div class="inner-bg">
    <div class="container">
      <div class="row">
        <div class="col-sm-8 col-sm-offset-2 text">
          <h1 style="background:rgba(34, 34, 34, 0.9); padding:15px;width:48%;margin:0px auto;color:#FFC107"><strong>FTP</strong> Login</h1>
        </div>
      </div>
      <div class="row">
       
        <div class="col-md-4 col-md-offset-4 text">
        
          <div class="form-bottom">
            <form:form name="fileUpload" enctype="multipart/form-data" modelAttribute="ftpLoginBean" action="checkLoginDetails">
              <div class="form-group">
				<c:choose>
					<c:when test="${invalidDetails == ''}">
						<div id="dataId" style="color: red">
							<h4>Please enter correct details</h4>
						</div>
					</c:when>
				</c:choose>					
				<label class="sr-only" for="form-asinumber">ASI Number</label>
                <form:input path="asiNumber" name="asiNumber" id="asiNumber" placeholder="ASI Number..." autocomplete="off"  class="form-asi-number form-control"/>
                <!-- <input type="text" name="form-first-name" placeholder="ASI Number..." class="form-first-name form-control" id="form-first-name"> -->
                <p id="asinumftp" class="txt_red"></p>
              </div>
              <div class="form-group">
                <label class="sr-only" for="form-last-name">Username</label>
                <form:input path="userName" name="userName" id="userName" placeholder="Username..." autocomplete="off"  class="form-last-name form-control"/>
                <!-- <input type="text" name="form-last-name" placeholder="Username..." class="form-last-name form-control" id="form-last-name"> -->
              	 <p id="asiuserftp" class="txt_red"></p>
              </div>
              <div class="form-group">
                <label class="sr-only" for="form-email">Password</label>
                <form:password path="password" id="password" placeholder="Password..." class="form-password form-control"/>
                <!-- <input type="password" name="form-email" placeholder="Password..." class="form-email form-control" id="form-email"> -->
               <p id="asipassftp" class="txt_red"></p>
              </div>
              <div class="submitbtn">
              <!-- <a href="fileUpload" class="btn btn-lg btn-primary">Submit</a> -->
              <form:button value="submit" class="btn1 btn-primary btn-lg pull-right" onclick="return validateForm()">Submit</form:button>
              <a href="uploadFile.htm" class="btn btn-lg btn-default pull-left">Back</a>
              
              </div>
              </form:form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Javascript --> 
<!-- <script src="assets/js/jquery-1.11.1.min.js"></script> 
<script src="assets/bootstrap/js/bootstrap.min.js"></script> 
<script src="assets/js/jquery.backstretch.min.js"></script> 
<script src="assets/js/retina-1.1.0.min.js"></script> 
<script src="assets/js/scripts.js"></script>  -->
<script src="resources/js/jquery-1.11.1.min.js"></script> 
<script src="resources/bootstrap/js/bootstrap.min.js"></script>
<script src="resources/js/jquery.buttonLoader.js"></script>  
<script src="resources/js/jquery.backstretch.min.js"></script> 
<script src="resources/js/scripts.js"></script> 
<script src="resources/js/custom-file-input.js"></script> 
<script src="resources/js/jquery.custom-file-input.js"></script> 
<script type="text/javascript">
function validateForm(){
	var asiNumber =  document.getElementById("asiNumber").value;
	var userName = document.getElementById("userName").value;
	var password = document.getElementById("password").value;
	document.getElementById("dataId").innerHTML = "";
	if (asiNumber==null || asiNumber==''){
		  document.getElementById("asinumftp").innerHTML = "<i><b>!</b></i> &nbsp;Enter your ASI Number";
		  document.getElementById("asinumftp").focus();
		  return false;  
	}else{
		document.getElementById("asinumftp").innerHTML = "";
	}
	if (userName==null || userName==""){ 
		  document.getElementById("asiuserftp").innerHTML = "<i><b>!</b></i> &nbsp;Enter your Username";
		  document.getElementById("asiuserftp").focus();
		  return false;  
	}else{
		 document.getElementById("asiuserftp").innerHTML = "";
	}
	if (password==null || password==""){
		  document.getElementById("asipassftp").innerHTML = "<i><b>!</b></i> &nbsp;Enter your Password";
		  document.getElementById("asipassftp").focus();
		  return false;  
	}else{
		document.getElementById("asipassftp").innerHTML = "";
	}
}
</script>

<!--[if lt IE 10]>
            <script src="assets/js/placeholder.js"></script>
        <![endif]-->

</body>
</html>