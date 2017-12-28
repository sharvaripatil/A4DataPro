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
<title>Upload File</title>

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
<style>
h4 {
 font-size:20px;
 font-style: italic;
}
</style>
<body>
<!-- Top content -->
<div class="top-content">
  <div class="inner-bg">
    <div class="container">
      <div class="row">
        <div class="col-sm-8 col-sm-offset-2 text">
          <h1 style="background:rgba(34, 34, 34, 0.9); padding:15px;width:48%;margin:0px auto;color:#FFC107"><strong>Upload File</strong></h1>
        </div>
      </div>
      <div class="row">
       
        <div class="col-md-4 col-md-offset-4 text">
        
          <div class="form-bottom">
              <form:form action="uploadFile" name="ftpFileUploadBean" enctype="multipart/form-data" modelAttribute="ftpFileUploadBean">
             <div class="form-group">
             <c:choose>
             	<c:when test="${ftpMessage == 'success'}">
             	<h4 style="color: green;">File has been save successfully</h4> 
             	</c:when>
             	<c:when test="${ftpMessage == 'failure'}">
             	  <h4 style="color: red;">File is not saved ,Please check once!</h4>
             	</c:when>
             	<c:when test="${invalidFile == ''}">
             		<h4 style="color: red;">Please Upload File</h4>
             	</c:when>
             	<c:when test="${invalidAsiNum == ''}">
             		<h4 style="color: red;">Please Enter Details in Login Page</h4>
             	</c:when>
             	<c:when test="${misMatchCoumns == 'misMatchCoumns'}">
             	   <h4 style="color: red;">Please Enter Correct Supplier File Format As Columns Are Mismatch</h4>
				 </c:when>
             </c:choose>
    <input type="file" name="file" id="file" class="file">
    <div class="input-group col-xs-12">
      <input type="text" class="form-control input-lg" disabled placeholder="Upload File">
      <span class="input-group-btn">
        <button class="browse btn btn-primary input-lg" type="button"><i class="glyphicon glyphicon-search"></i> Browse</button>
      </span>
       <form:hidden path="asiNumber"/>
       <form:hidden path="environmentType"/>
     <%--  <input type="file" name="file" id="file-1" class="inputfile inputfile-1" data-multiple-caption="{count} files selected" multiple ismap="ismap"/>
      <span class="input-group-btn">
      <form:button value="submit" class="browse btn btn-primary input-lg">Submit</form:button> --%>
       <!--  <button class="browse btn btn-primary input-lg" type="button"><i class="glyphicon glyphicon-search"></i> Browse</button> -->
    
      </span>
    </div>
  </div>
              <div class="submitbtn">
              <form:button val="submit" class="btn1 btn-primary btn-lg pull-right" onclick="return validateForm()">Submit</form:button>
               <a href="ftpLogin" class="btn btn-lg btn-default pull-left">Back</a>
              </div>
              <br>
             </form:form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Javascript --> 
<script src="resources/js/jquery-1.11.1.min.js"></script> 
<script src="resources/bootstrap/js/bootstrap.min.js"></script>
<script src="resources/js/jquery.buttonLoader.js"></script>  
<script src="resources/js/jquery.backstretch.min.js"></script> 
<script src="resources/js/scripts.js"></script> 
<script src="resources/js/custom-file-input.js"></script> 
<script src="resources/js/jquery.custom-file-input.js"></script> 

<!--[if lt IE 10]>
            <script src="assets/js/placeholder.js"></script>
        <![endif]-->
<script>
function validateForm(){
	alert(hi);
	var file = document.getElementById("file").value;
	if (file==null || file==""){
		  document.getElementById("file").innerHTML = "<i><b>!</b></i> &nbsp;Please choose file";
		  document.uploadBean.file.focus();
		  return false;  
	}else{
		document.getElementById("file").innerHTML = "";
	}
}
$(document).on('click', '.browse', function(){
  var file = $(this).parent().parent().parent().find('.file');
  file.trigger('click');
});
$(document).on('change', '.file', function(){
  $(this).parent().find('.form-control').val($(this).val().replace(/C:\\fakepath\\/i, ''));
});

</script>
</body>
</html>