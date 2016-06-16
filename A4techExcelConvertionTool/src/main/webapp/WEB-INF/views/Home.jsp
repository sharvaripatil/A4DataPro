<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
<form:form   name="myForm" enctype="multipart/form-data" modelAttribute="filebean" >
<div class="wrapper">
  <div class="container">
    <div class="col-md-6 col-xs-offset-2 col-lg-8 col-lg-offset-2">
      <div class="panel-heading"><span class="title text-center">
        <h1>A<sup>4</sup>-ASI <span> Connect</span></h1>
        </span> </div>
      <div class="panel-body">
         <table>
                <c:if test="${invalidDetails == ''}">
                 <div style="color:red">
                    <h2>Please enter correct details</h2> 
            </div>
                </c:if>
         		<tr>
         			<td>ASI Number :</td>
         			<td><form:input path="asiNumber" id="asiNumber" /></td>
         			<td style="width:185px;"><form:errors path="asiNumber" cssClass="error" /></td>
         		</tr>
         		<tr>
         			<td>UserName :</td>
         			<td><form:input path="userName" id="userName"/></td>
         			<td style="width:185px;"><form:errors path="userName" cssClass="error" /></td>
         		</tr>
         		<tr>
         			<td>Password :</td>
         			<td><form:password path="password" id="password"/></td>
         			<td style="width:185px;"><form:errors path="password" cssClass="error" /></td>
         		</tr> 		
         		<tr>
         		<td>Please select a file to upload :</td>
         		<td><form:input path="file" type="file"/></td>
         		<td style="width:185px; margin-left:10px"><form:errors path="file" cssClass="error" style="margin-left:50px"/></td>
         		</tr>
         		<tr>
         		<td></td>
              <td>
 					<form:button value="submit" class="btn btn-primary btn-lg pull-right" style="margin: 1px 37px; float: left !important;">Submit</form:button>             
              </td>
         		</tr>
         		
         </table>
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
</script>

 <!-- </form> -->
 </form:form>
</body>
</html>