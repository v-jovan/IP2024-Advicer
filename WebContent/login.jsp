<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="net.etfbl.ip.beans.UserBean"%>

<jsp:useBean id="userBean" class="net.etfbl.ip.beans.UserBean"
	scope="session" />
<jsp:useBean id="userManager" class="net.etfbl.ip.service.UserManager"
	scope="application" />

<jsp:setProperty property="username" name="userBean" param="username" />
<jsp:setProperty property="password" name="userBean" param="password" />

<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

// Check if user is already logged in, if so, redirect to messages page
if (userBean.isLoggedIn()) {
	response.sendRedirect("messages.jsp");
	return;
}

String errorMessage = null;

if (request.getParameter("submit") != null) {
	// Attempt to authenticate the user with entered credentials
	UserBean user = userManager.loginUser(userBean.getUsername(), userBean.getPassword());

	if (user != null) {
		// Check if user has the role "INSTRUCTOR" and if the account is activated
		if ("INSTRUCTOR".equals(user.getRole()) && user.isActivated()) {
	// Set user details to the session-scoped userBean
	userBean.setId(user.getId());
	userBean.setActivated(user.isActivated());
	userBean.setEmail(user.getEmail());
	userBean.setUsername(user.getUsername());
	userBean.setRole(user.getRole());
	userBean.setLoggedIn(true);

	// Redirect to messages page after successful login
	response.sendRedirect("messages.jsp");
	return;
		} else {
	// Set error message if the user does not have the "INSTRUCTOR" role
	errorMessage = "Pristup dozvoljen samo savjetnicima.";
		}
	} else {
		// Set error message if login credentials are incorrect
		errorMessage = "Pogrešno korisničko ime ili lozinka.";
	}
}
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Prijava</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
<style>
.login-container {
	min-width: 20rem;
}
</style>
</head>
<body
	class="bg-light d-flex justify-content-center align-items-center vh-100">
	<div class="login-container">
		<div class="card shadow">
			<div class="card-header text-center">
				<h3>Prijava</h3>
			</div>
			<div class="card-body">
				<!-- Display error message if login fails -->
				<%
				if (errorMessage != null) {
				%>
				<div class="alert alert-danger" role="alert">
					<%=errorMessage%>
				</div>
				<%
				}
				%>
				<!-- Login form -->
				<form action="login.jsp" method="post">
					<div class="mb-3">
						<input placeholder="Korisničko ime" class="form-control"
							type="text" name="username" required>
					</div>
					<div class="mb-5">
						<input placeholder="Lozinka" class="form-control" type="password"
							name="password" required>
					</div>
					<div class="d-grid">
						<button type="submit" class="btn btn-primary" name="submit">Prijavi
							se</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Bootstrap JavaScript bundle -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
