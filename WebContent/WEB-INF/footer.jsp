<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div class="footer bg-light py-3">
	<div class="container">
		<div class="">
			<div>
				Prijavljeni korisnik: <strong><%=userBean.getUsername()%></strong>
			</div>
			<div>
				<a href="logout.jsp" class="btn btn-sm mt-3 btn-danger">Odjava</a>
			</div>
		</div>
	</div>
</div>
