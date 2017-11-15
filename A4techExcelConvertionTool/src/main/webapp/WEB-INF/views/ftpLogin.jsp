<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page session="false"%>
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

<link rel="stylesheet"
	href="http://fonts.googleapis.com/css?family=Roboto:400,100,300,500">
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet"
	href="resources/font-awesome/css/font-awesome.min.css">
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
						<h1
							style="background: rgba(34, 34, 34, 0.9); padding: 15px; width: 48%; margin: 0px auto; color: #FFC107">
							<strong>FTP</strong> Login
						</h1>
					</div>
				</div>
				<div class="row">

					<div class="col-md-4 col-md-offset-4 text">

						<div class="form-bottom">
							<form:form name="fileUpload" enctype="multipart/form-data"
								modelAttribute="ftpLoginBean" action="checkLoginDetails">
								<div class="form-group">
									<c:choose>
										<c:when test="${invalidDetails == ''}">
											<div id="dataId" style="color: red">
												<h4>Please enter correct details</h4>
											</div>
										</c:when>
									</c:choose>
									<label class="sr-only" for="form-asinumber">ASI Number</label>
									<form:input path="asiNumber" name="asiNumber" id="asiNumber"
										placeholder="ASI Number..." autocomplete="off"
										class="form-asi-number form-control" />
									<!-- <input type="text" name="form-first-name" placeholder="ASI Number..." class="form-first-name form-control" id="form-first-name"> -->
									<p id="asinumftp" class="txt_red"></p>
								</div>
								<div class="form-group">
									<label class="sr-only" for="form-last-name">Username</label>
									<form:input path="userName" name="userName" id="userName"
										placeholder="Username..." autocomplete="off"
										class="form-last-name form-control" />
									<!-- <input type="text" name="form-last-name" placeholder="Username..." class="form-last-name form-control" id="form-last-name"> -->
									<p id="asiuserftp" class="txt_red"></p>
								</div>
								<div class="form-group">
									<label class="sr-only" for="form-email">Password</label>
									<form:password path="password" id="password"
										placeholder="Password..." class="form-password form-control" />
									<!-- <input type="password" name="form-email" placeholder="Password..." class="form-email form-control" id="form-email"> -->
									<p id="asipassftp" class="txt_red"></p>
								</div>
								<div class="form-group">

									<%-- <c:if test="${empty environemtType}"> --%>
									<!-- <label for="sel1">Select Type Of Enviornmet</label> -->
									<form:select class="form-control" path="environemtType" id="environmentTypeId">
										<form:option value="NONE" label="Select Type Of Enviornmet"></form:option>
										<form:option value="Sand">Sandbox</form:option>
										<form:option value="Prod">Production</form:option>
									</form:select>
									<p id="envId" class="txt_red"></p>
									<%-- </c:if> --%>
								</div>
								<div class="submitbtn">
									<!-- <a href="fileUpload" class="btn btn-lg btn-primary">Submit</a> -->
									<form:button value="submit"
										class="btn1 btn-primary btn-lg pull-right"
										onclick="return validateForm()">Submit</form:button>
									<a href="uploadFile.htm"
										class="btn btn-lg btn-default pull-left">Back</a>

								</div>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="exampleModal" role="dialog">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">Confirmation
						Environment</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-lg btn-default pull-left"
						data-dismiss="modal">Cancel</button>
					<button type="button" class="btn1 btn-primary btn-lg">Proceed</button>
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
		function validateForm() {
			//alert(1);
			var asiNumber = $("#asiNumber").val();
			var userName = $("#userName").val();
			var password = $("#password").val();
			//document.getElementById("dataId").innerHTML = "";
			//$('#exampleModal').dialog('open');
			if (asiNumber != null && asiNumber != '') {
				$("#asinumftp").html("");
				if (userName != null && userName != "") {
					$("#asiuserftp").html("");
					if (password != null && password != "") {
						$("#asipassftp").html("");
						var environmentType = $("#environmentTypeId").val();
						var envselecttext=$("#environmentTypeId option:selected").text();
						if(environmentType!="NONE"){
							$("#envId").text("");
							if (confirm("Are you sure you want to proceed with "+envselecttext+" ??")) {
								return true;
							}
						}
						else{
							$("#envId").text("Please Select Environment");
						//alert("Please select env");
						}
						
					} else {
						/* document.getElementById("asipassftp").innerHTML = ""; */
						$("#asipassftp").html(
								"<i><b>!</b></i> &nbsp;Enter your Password");
						$("#asipassftp").focus();
					}
				} else {
					$("#asiuserftp").html(
							"<i><b>!</b></i> &nbsp;Enter your Username");
					$("#asiuserftp").focus();
					//$("#asiuserftp").html = "";
				}
			} else {
				//$('#exampleModal').modal('show');
				$("#asinumftp").html(
						"<i><b>!</b></i> &nbsp;Enter your ASI Number");
				$("#asinumftp").focus();

			}
			return false;
			/* $('test123').click(); */
		}
		function checkEnvironment() {
			alert('hi');
			document.getElementById('exampleModal').style.display = "block";
			/* $('#exampleModal').show(); */
		}
		$(function() {
			alert('new')
			$('#exampleModal').change(function() {
				var divselection = $(this);
			});
			alert('new end')
		});
	</script>

	<!--[if lt IE 10]>
            <script src="assets/js/placeholder.js"></script>
        <![endif]-->

</body>
</html>