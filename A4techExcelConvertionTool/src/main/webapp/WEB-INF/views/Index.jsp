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

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
		  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		<![endif]-->
</head>
<body style="overflow:hidden;">
<!-- <form method="POST" action="uploadFile" enctype="multipart/form-data"> -->
<form:form   name="myForm" enctype="multipart/form-data" commandName="filebean" action="uploadFile.htm"  >
<div class="wrapper">
  <div class="container">
    <div class="col-md-6 col-xs-offset-2 col-lg-8 col-lg-offset-2">
      <div class="panel-heading"><span class="title text-center">
        <h1>A<sup>4</sup>-ASI <span> Connect</span></h1>
        </span> </div>
      <div class="panel-body">
      
        <!-- <div role="alert" class="alert alert-success alert-icon alert-border-color alert-dismissible">
          <div class="icon"><i class="fa fa-check"></i></div>
          <div class="message"> <strong>Good!</strong> Better check yourself, you're not looking too good. </div>
        </div> -->
        <!-- <div role="alert" class="alert alert-primary alert-icon alert-border-color alert-dismissible">
          <div class="icon"><i class="fa fa-times-circle"></i></div>
          <div class="message"> <strong>Good news!</strong> Better check yourself, you're not looking too good. </div>
        </div> -->
        <%-- <div class="fileUpload"> <span class="custom-span">+</span>
          <p class="custom-para">Upload File</p>
          <form:input path="file" />
           <div class="has-error">
           	  <form:errors path="file" cssStyle="color: red;"></form:errors>
           </div>
        </div> --%>
         <!-- <h3 style="margin: 0px 27%;">Please select a file to upload :</h3><br/><input type="file" name="file"> -->
         <table>
         		<tr>
         			<td>ASI Number :</td>
         			<td><form:input path="asiNumber" id="asiNumber" /></td>
         			<td><form:errors path="asiNumber" cssClass="error"/></td>
         		</tr>
         		<tr>
         			<td>UserName :</td>
         			<td><form:input path="userName" id="userName"/></td>
         			<td><form:errors path="userName" cssClass="error"/></td>
         		</tr>
         		<tr>
         			<td>Password :</td>
         			<td><form:input path="password" id="password"/></td>
         			<td><form:errors path="password" cssClass="error"/></td>
         		</tr> 		
         		<tr>
         		<td>Please select a file to upload :</td>
         		<td colspan="2"><form:input path="file" type="file"/>
        <!-- <input id="uploadFile" placeholder="0 files selected" disabled="disabled" /> -->
        <!-- <input type="submit" value="Upload" /> -->
        </td>
         		</tr>
         		<tr>
         		<td></td>
         		<%-- <td>
         		<button type="submit" class="btn btn-primary btn-lg pull-right" style="margin: 1px 37px; float: left !important;" value="Upload" id="load"  data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing Excel">Upload Excel</button>
        <span><form:errors path="file" cssClass="error"/></span></td> --%>
              <td>
 					<form:button value="submit" class="btn btn-primary btn-lg pull-right" style="margin: 1px 37px; float: left !important;">Submit</form:button>             
              </td>
         		</tr>
         		
         </table>
       
        <%-- <h3 style="margin: 0px 27%;">Please select a file to upload :</h3><br/><form:input path="file" type="file"/>
        <!-- <input id="uploadFile" placeholder="0 files selected" disabled="disabled" /> -->
        <!-- <input type="submit" value="Upload" /> -->
        <button type="submit" class="btn btn-primary btn-lg pull-right"  style="margin:-48px 126px;" value="Upload" id="load"  data-loading-text="<i class='fa fa-circle-o-notch fa-spin'></i> Processing Excel">Upload Excel</button>
        <span><form:errors path="file" cssClass="error"/>
        </span> --%>
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
<script type="text/javascript">
$(document).ready(function () {
	$("button[type=submit], .animateBtn").click(function () {
	$(this).addClass("m-progress");
	setTimeout(function () {
	$("button[type=submit], .animateBtn").removeClass("m-progress");
	}, 5000);
	});
	});
document.getElementById("uploadBtn").onchange = function () {
document.getElementById("uploadFile").value = this.value;
};

function validateForm() {
	 if( document.myForm.file.value == "" )
     {
        alert( "Please provide your name!" );
        document.myForm.file.focus() ;
        return false;
     }
}

function validateFields(){
	var asiNumber = document.getElementById('asiNumber').value;
	var userName = document.getElementById('userName').value;
	var password = document.getElementById('password').value;
	if(asiNumber == ""){
		alert("Please enter ASI number");
		document.getElementById('asiNumber').focus();
		return false;
	}
	if(userName == ""){
		alert("Please enter userName");
		document.getElementById('userName').focus();
		return false;
	}
	if(password == ""){
		alert("Please enter password");
		document.getElementById('password').focus();
		return false;
	}
}
</script>

 <!-- </form> -->
 </form:form>
</body>
</html>