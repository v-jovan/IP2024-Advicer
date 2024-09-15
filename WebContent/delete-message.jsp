<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="net.etfbl.ip.beans.UserBean"%>
<%@ page import="net.etfbl.ip.service.MessageManager"%>

<jsp:useBean id="userBean" class="net.etfbl.ip.beans.UserBean"
	scope="session" />
<jsp:useBean id="messageManager"
	class="net.etfbl.ip.service.MessageManager" scope="application" />

<%
if (userBean == null || !userBean.isLoggedIn()) {
	response.sendRedirect("login.jsp");
	return;
}

String messageIdParam = request.getParameter("id");
if (messageIdParam != null && messageIdParam.matches("\\d+")) {
	int messageId = Integer.parseInt(messageIdParam);

	messageManager.deleteMessageById(messageId);

	response.sendRedirect("messages.jsp?status=deleted");
} else {
	out.println("<div class='alert alert-danger'>Neispravan ID poruke.</div>");
}
%>
