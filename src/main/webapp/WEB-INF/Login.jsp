<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>LOGIN</title>
</head>
<body>
	<h1>LOGIN</h1>
	<form method="post" action="/TIW_E-CommerceApp_PureHTML/Login">
    	Email: <input name="email" required> <br><br>
    	Password: <input name="password" type="password" required> <br><br>
		<input class="submit_on_enter" type="submit" value="LOGIN">
	</form>
</body>
</html>