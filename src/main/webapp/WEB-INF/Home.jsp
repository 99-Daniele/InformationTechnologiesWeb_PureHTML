<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html style="background-color:bisque">
<head>
<link rel="stylesheet" type="text/css" href="/TIW_E-CommerceApp_PureHTML/CSS/mystyle.css">
<meta charset="UTF-8">
<title>HOME</title>
</head>
<body>
	<div class="logout"><a href="/TIW_E-CommerceApp_PureHTML/Logout">LOGOUT</a></div>
	<h1>Welcome back ${user.name} ${user.surname}</h1>
	<table border = "1" style="border-collapse:collapse">
		<tr>
			<td class="cell"><a href="/TIW_E-CommerceApp_PureHTML/GetHome"> <c:out value="HOME" /></a>
			<td class="cell"><a href="/TIW_E-CommerceApp_PureHTML/GetCart"> <c:out value="CART" /></a>
			<td class="cell"><a href="/TIW_E-CommerceApp_PureHTML/GetOrders"> <c:out value="ORDERS" /></a>
		</tr>
	</table>
	<br>
	<form method="get" action="/TIW_E-CommerceApp_PureHTML/GetSearchedItems">
		Enter keyword: <input name="keyword" class="submit_on_enter" type="text" placeholder="Search..." required>
	</form>
	<h2>List of your last visualized items</h2>
	<c:choose>
		<c:when test="${visualizedItems.size()>0}">
			<table border = "1" style="border-collapse:collapse">
				<tr class="subTitle">
					<th class="cell">ID
					<th class="cell">Name
					<th class="cell">Price
				</tr>
				<tbody>
					<c:forEach var="item" items="${visualizedItems}" varStatus="row">
						<tr>
							<td class="cell"><c:out value="${item.itemID}"/>
							<td class="cell"><c:out value="${item.name}" />
							<td class="cell"><c:out value="${item.price} € "/>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>No items recently visualized
		</c:otherwise>
	</c:choose>
</body>
</html>