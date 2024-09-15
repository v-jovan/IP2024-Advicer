<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@ page
	import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="org.apache.commons.fileupload.FileItem"%>
<%@ page import="net.etfbl.ip.service.EmailManager"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.io.File"%>

<jsp:useBean id="emailManager" class="net.etfbl.ip.service.EmailManager"
	scope="application" />

<jsp:useBean id="userBean" class="net.etfbl.ip.beans.UserBean"
	scope="session" />

<%
if (userBean == null || !userBean.isLoggedIn()) {
	response.sendRedirect("login.jsp");
	return;
}
boolean isMultipart = ServletFileUpload.isMultipartContent(request); // Check if it's a multipart form

if (isMultipart) {
	DiskFileItemFactory factory = new DiskFileItemFactory();
	ServletFileUpload upload = new ServletFileUpload(factory);

	String recipientEmail = null;
	String subject = null;
	String replyMessage = null;
	List<File> attachments = new ArrayList<>(); // For attachments

	try {
		List<FileItem> formItems = upload.parseRequest(request); // Parse the form

		String tempDir = System.getProperty("java.io.tmpdir"); // Just a temp folder to store the files

		for (FileItem item : formItems) {
	if (item.isFormField()) { // If the field is a text field (not a file)
		String fieldName = item.getFieldName();
		String fieldValue = item.getString();

		if (fieldName.equals("recipientEmail")) {
			recipientEmail = fieldValue;
		} else if (fieldName.equals("recievedSubject")) {
			subject = fieldValue;
		} else if (fieldName.equals("replyMessage")) {
			replyMessage = fieldValue;
		}
	} else { // ... and if it's a file
		if (!item.getName().isEmpty()) {
			String fileName = item.getName();
			File uploadedFile = new File(tempDir + File.separator + fileName);
			item.write(uploadedFile); // Save th efile
			attachments.add(uploadedFile); // Add it to the list
		}
	}
		}

		if (recipientEmail != null && subject != null && replyMessage != null) {
	String newSubject = "Odgovor na zahtjev: " + subject;
	emailManager.sendEmailAsync(recipientEmail, newSubject, replyMessage, attachments); // Send the e-Mail
	response.sendRedirect("messages.jsp?status=success");
	return;
		} else {
	out.println("<h3>Nisu unijeti svi podaci.</h3>");
		}

	} catch (Exception e) {
		out.println("<h3>Došlo je do greške: " + e.getMessage() + "</h3>");
	}
} else {
	out.println("<h3>Forma nije ispravna (multipart je potreban).</h3>");
}
%>