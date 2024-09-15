<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="net.etfbl.ip.beans.MessageBean"%>
<%@ page import="net.etfbl.ip.beans.UserBean"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!-- Učitavanje UserBean iz sesije i MessageManager-a iz aplikacije -->
<jsp:useBean id="userBean" class="net.etfbl.ip.beans.UserBean"
	scope="session" />
<jsp:useBean id="messageManager"
	class="net.etfbl.ip.service.MessageManager" scope="application" />

<%
// Provera da li je korisnik prijavljen
if (userBean == null || !userBean.isLoggedIn()) {
	response.sendRedirect("login.jsp");
	return;
}

List<MessageBean> messages = messageManager.getMessagesForUser(userBean.getId());
SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm"); // Format datuma
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Poruke</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">

<style>
html, body {
	height: 100%;
}

body {
	display: flex;
	flex-direction: column;
}

.container {
	flex: 1;
}

footer {
	position: relative;
	width: 100%;
	bottom: 0;
	padding: 1rem;
	text-align: center;
}
</style>
</head>
<body class="vh-100">
	<%@ include file="WEB-INF/header.jsp"%>

	<!-- Prikazivanje notifikacije ako je email uspešno poslat -->
	<%
	String status = request.getParameter("status");
	if (status != null && status.equals("success")) {
	%>
	<div class="alert alert-success alert-dismissible fade show"
		role="alert">
		Email je uspješno poslat!
		<button type="button" class="btn-close" data-bs-dismiss="alert"
			aria-label="Close"></button>
	</div>
	<%
	}
	%>

	<div class="container my-5">
		<h2 class="text-left mb-4">Prijemno sanduče:</h2>
		<table class="table table-hover">
			<thead class="table-primary">
				<tr>
					<th>ID</th>
					<th>Predmet</th>
					<th>Kratak sadržaj</th>
					<th>Ime pošiljaoca</th>
					<th>Vrijeme slanja</th>
					<th>Akcije</th>
				</tr>
			</thead>
			<tbody>
				<%
				if (messages != null && !messages.isEmpty()) {
					for (MessageBean message : messages) {
						// Klasa za nepročitane poruke
						String rowClass = (message.getReadAt() == null) ? "unread-message" : "";
						String formattedDate = sdf.format(message.getSentAt());
				%>
				<tr class="<%=rowClass%>">
					<td><%=message.getId()%></td>
					<td><%=message.getSubject()%></td>
					<td><%=message.getShorterContent()%></td>
					<td><%=message.getSenderName()%></td>
					<td><%=formattedDate%></td>
					<td><a href="view-message.jsp?id=<%=message.getId()%>"
						class="btn btn-sm btn-primary">Otvori</a> <a
						href="deleteMessage.jsp?id=<%=message.getId()%>"
						class="btn btn-sm btn-danger">Obriši</a></td>
				</tr>
				<%
				}
				} else {
				%>
				<!-- Prikazivanje poruke da nema dostupnih poruka -->
				<tr class="text-center">
					<td colspan="6">Nema dostupnih poruka.</td>
				</tr>
				<%
				}
				%>
			</tbody>
		</table>
	</div>

	<%@ include file="WEB-INF/footer.jsp"%>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
