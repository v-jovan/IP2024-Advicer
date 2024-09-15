<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="net.etfbl.ip.beans.MessageBean"%>
<%@ page import="net.etfbl.ip.beans.UserBean"%>
<%@ page import="net.etfbl.ip.service.MessageManager"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.TimeZone"%>

<jsp:useBean id="userBean" class="net.etfbl.ip.beans.UserBean"
	scope="session" />
<jsp:useBean id="messageManager"
	class="net.etfbl.ip.service.MessageManager" scope="application" />

<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

if (userBean == null || !userBean.isLoggedIn()) {
	response.sendRedirect("login.jsp");
	return;
}

// Validate and process the message ID parameter
String messageIdParam = request.getParameter("id");
if (messageIdParam == null || !messageIdParam.matches("\\d+")) {
	out.println("<div class='alert alert-danger'>Neispravan ID poruke.</div>");
	return;
}

int messageId = Integer.parseInt(messageIdParam);
MessageBean message = messageManager.getMessageById(messageId);

// Check if the message exists and if the user has permission to view it
if (message == null || message.getRecipientId() != userBean.getId()) {
	out.println("<div class='alert alert-danger'>Poruka nije pronađena ili nemate pravo da je pregledate.</div>");
	return;
}

if (message.getReadAt() == null) {
	messageManager.markMessageAsRead(messageId);
	message.setReadAt(new java.sql.Timestamp(System.currentTimeMillis()));
}

SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy. HH:mm");
sdf.setTimeZone(TimeZone.getTimeZone("Europe/Sarajevo"));
String sentDateFormatted = sdf.format(message.getSentAt());
String readAtFormatted = sdf.format(message.getReadAt());
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Pregled poruke</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
	rel="stylesheet">
</head>
<body class="vh-100">
	<!-- Include header -->
	<%@ include file="WEB-INF/header.jsp"%>

	<div class="container my-3">
		<h2 class="mb-3">Detalji poruke:</h2>
		<div class="card">
			<div class="card-header">
				<h3><%=message.getSubject()%></h3>
			</div>
			<div class="card-body">
				<p>
					<strong>Pošiljalac:</strong>
					<%=message.getSenderName()%></p>
				<p>
					<strong>Datum slanja:</strong>
					<%=sentDateFormatted%></p>
				<p>
					<strong>Pročitano:</strong>
					<%=readAtFormatted%></p>
				<div class="w-100 mb-2">
					<strong>Sadržaj:</strong>
					<p><%=message.getContent()%></p>
				</div>
				<div class="w-100 d-flex justify-content-end">
					<a href="messages.jsp" class="btn btn-primary">Nazad na poruke</a>
				</div>
			</div>
		</div>
		<form action="upload.jsp" class="my-5" method="POST"
			enctype="multipart/form-data">
			<div class="form-group">
				<textarea id="replyMessage" placeholder="Vaš odgovor..."
					name="replyMessage" class="form-control" rows="5" required></textarea>
			</div>
			<div class="form-group my-3">
				<input type="file" id="attachments" name="attachments" multiple
					class="form-control">
			</div>
			<input type="hidden" name="recipientEmail" id="recipientEmail"
				value="<%=message.getSenderEmail()%>"> <input type="hidden"
				name="recievedSubject" id="recievedSubject"
				value="<%=message.getSubject()%>">
			<div class="d-flex justify-content-end">
				<button type="submit" class="btn btn-primary">Pošalji
					odgovor</button>
			</div>
		</form>
	</div>

	<%@ include file="WEB-INF/footer.jsp"%>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
