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
<title>A4 - ASI Connect</title>
<!-- CSS -->
<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:400,100,300,500">
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="resources/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="resources/css/style.css">
<link rel="stylesheet" href="resources/css/component.css">
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
  <div class="row pull-right logo"> <img src="resources/img/logo.png" alt="" height="80px"/> </div>
    <div class="container">
      <div class="row">
        <div class="col-sm-8 col-sm-offset-2 text">
          <h1><strong>A<sup>4</sup> - ASI</strong> Connect </h1>
        </div>
      </div>
      <div class="row">
        <div class="col-sm-6 book"> <img src="resources/img/ebook.png" alt="" height="384px"> </div>
        <div class="col-sm-5">
          <div class="form-bottom">
            <form:form name="uploadBean" enctype="multipart/form-data" modelAttribute="filebean">
              <div class="form-group">
              <c:choose>
            <c:when test="${invalidDetails == ''}">
                 <div id="dataId" style="color:red" >
                  <h4>Please enter correct details</h4> 
                 </div>
           </c:when>
            <c:when test="${invalidUploadFile == ''}">
                <div id="dataId" style="color:red" >
               <h4>Please Upload xls/xlsx Files Only</h4> 
               </div>
           </c:when>
         </c:choose> 
            
                <label class="sr-only" for="form-asi-number">ASI Number</label>
                <form:input path="asiNumber" name="asiNumber" id="asiNumber" placeholder="ASI Number..." class="form-asi-number form-control"/>
                <!-- <input type="text" name="asiNumber" placeholder="ASI Number..." class="form-asi-number form-control" id="asiNumber"> -->
             	<p id="asinum" class="txt_red"></p>
              </div>
              <div class="form-group">
                <label class="sr-only" for="form-username">Username</label>
                <form:input path="userName" name="userName" id="userName" placeholder="Username..." class="form-last-name form-control"/>
                <p id="asiuser" class="txt_red"></p>
              </div>
              <div class="form-group">
                <label class="sr-only" for="form-password">Password</label>
                <form:password path="password" id="password" placeholder="Password..." class="form-password form-control"/>
                <p id="asipass" class="txt_red"></p>
                <!-- <input type="password" name="password" placeholder="Password..." class="form-password form-control" id="form-password"> -->
              </div>
              <div class="form-group pull-left">
              <%-- <form:input path="file" type="file" id="file" class="inputfile inputfile-1" data-multiple-caption="{count} files selected" ismap="ismap" /> --%>
                <input type="file" name="file" id="file-1" class="inputfile inputfile-1" data-multiple-caption="{count} files selected" multiple ismap="ismap"/>
                <label for="file-1">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="17" viewBox="0 0 20 17">
                    <path d="M10 0l-5.2 4.9h3.3v5.1h3.8v-5.1h3.3l-5.2-4.9zm9.3 11.5l-3.2-2.1h-2l3.4 2.6h-3.5c-.1 0-.2.1-.2.1l-.8 2.3h-6l-.8-2.2c-.1-.1-.1-.2-.2-.2h-3.6l3.4-2.6h-2l-3.2 2.1c-.4.3-.7 1-.6 1.5l.6 3.1c.1.5.7.9 1.2.9h16.3c.6 0 1.1-.4 1.3-.9l.6-3.1c.1-.5-.2-1.2-.7-1.5z"/>
                  </svg>
                  <span>Choose a file&hellip;</span></label>
                  <p id="asifile" class="txt_red"></p>
              </div>
              <!--  <form:button value="submit" class="btn1 btn-primary btn-lg pull-right btn-success has-spinner" id="submit">Submit</form:button>   -->
             <form:button value="submit" class="btn1 btn-primary btn-lg pull-right" id="load" data-loading-text="<i class='fa fa-spinner fa-spin '></i>Uploading.." onclick="return validateForm()">Submit</form:button>
            </form:form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>


<!-- loader div -->
<div class="container" id="loader">
		<div class="box1">
			<div class="loader1"></div>
			<h3 style="color:yellow">Please Wait data is Uploading...</h3>
		</div>
</div>
<!-- loader div end-->
<!-- Javascript -->
<!-- <script src="resources/js/dbconcheck.js"></script>  -->
<script src="resources/js/jquery-1.11.1.min.js"></script> 
<script src="resources/bootstrap/js/bootstrap.min.js"></script>
<script src="resources/js/jquery.buttonLoader.js"></script>  
<script src="resources/js/jquery.backstretch.min.js"></script> 
<script src="resources/js/scripts.js"></script> 
<script src="resources/js/custom-file-input.js"></script> 
<script src="resources/js/jquery.custom-file-input.js"></script> 
<script type="text/javascript">
function validateForm(){
	var asiNumber = document.uploadBean.asiNumber.value;
	var userName = document.uploadBean.userName.value;
	var password = document.uploadBean.password.value;
	var file = document.uploadBean.file.value; 
	if (asiNumber==null || asiNumber==''){
		  document.getElementById("asinum").innerHTML = "<i><b>!</b></i> &nbsp;Enter your ASI Number";
		  document.uploadBean.asiNumber.focus();
		  return false;  
	}else{
		document.getElementById("asinum").innerHTML = "";
	}
	if (userName==null || userName==""){ 
		  document.getElementById("asiuser").innerHTML = "<i><b>!</b></i> &nbsp;Enter your Username";
		  document.uploadBean.userName.focus();
		  return false;  
	}else{
		document.getElementById("asiuser").innerHTML = "";
	}
	if (password==null || password==""){
		  document.getElementById("asipass").innerHTML = "<i><b>!</b></i> &nbsp;Enter your Password";
		  document.uploadBean.password.focus();
		  return false;  
	}else{
		document.getElementById("asipass").innerHTML = "";
	}
	if (file==null || file==""){
		  document.getElementById("asifile").innerHTML = "<i><b>!</b></i> &nbsp;Please choose file";
		  document.uploadBean.file.focus();
		  return false;  
	}else{
		document.getElementById("asifile").innerHTML = "";
	}
	
}

$('.btn1').on('click', function() {
	 /*  var elem = document.getElementById('dataId');
	elem.innerHTML  = '';  */
	var $this = $(this);
    var asiNumber = $('#asiNumber').val();
	var userName = $('#userName').val();
	var password = $('#password').val();
	var file = document.uploadBean.file.value; 

    if(file != '' && asiNumber != '' && userName != '' && password != ''){
    	
    	 $('#loader').addClass('in').delay(2000000).fadeOut(); 
  }
});


</script>
<!--[if lt IE 10]>
            <script src="resources/js/placeholder.js"></script>
        <![endif]-->
</body>
</html>
