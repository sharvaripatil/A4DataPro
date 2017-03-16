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
              <form:form name="ftpFileUploadBean" enctype="multipart/form-data" modelAttribute="ftpFileUploadBean">
             <div class="form-group">
    <input type="file" name="img[]" class="file">
    <div class="input-group col-xs-12">
      <input type="text" class="form-control input-lg" disabled placeholder="Upload File">
      <span class="input-group-btn">
        <button class="browse btn btn-primary input-lg" type="button"><i class="glyphicon glyphicon-search"></i> Browse</button>
      </span>
     <%--  <input type="file" name="file" id="file-1" class="inputfile inputfile-1" data-multiple-caption="{count} files selected" multiple ismap="ismap"/>
      <span class="input-group-btn">
      <form:button value="submit" class="browse btn btn-primary input-lg">Submit</form:button> --%>
       <!--  <button class="browse btn btn-primary input-lg" type="button"><i class="glyphicon glyphicon-search"></i> Browse</button> -->
    
      </span>
    </div>
  </div>
             
             
              <div class="submitbtn">
              <button type="submit" class="btn"><a href="">Submit</a></button>
              </div>
              <br>
              <div class="sucessmessage">
              Successful
              </div>
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