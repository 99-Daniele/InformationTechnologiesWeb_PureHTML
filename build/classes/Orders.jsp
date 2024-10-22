<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html style="background-color:bisque">
<head>
<link rel="stylesheet" type="text/css" href="/TIW_E-CommerceApp_PureHTML/CSS/mystyle.css">
<meta charset="UTF-8">
<title>ORDERS</title>
</head>
<body>
	<div class="logout"><a href="/TIW_E-CommerceApp_PureHTML/Logout">LOGOUT</a></div>
	<table border = "1" style="border-collapse:collapse">
		<tr>
			<td class="cell"><a href="/TIW_E-CommerceApp_PureHTML/GetHome"> <c:out value="HOME" /></a>
			<td class="cell"><a href="/TIW_E-CommerceApp_PureHTML/GetCart"> <c:out value="CART" /></a>
			<td class="cell"><a href="/TIW_E-CommerceApp_PureHTML/GetOrders"> <c:out value="ORDERS" /></a>
		</tr>
	</table>
	<c:choose>
		<c:when test="${orders.size()>0}">
			<h2>List of your orders</h2>
			<table border = "1" style="border-collapse:collapse;float:left">
				<tr class="title">
					<th class="cell">Order
				</tr>
				<tbody>
					<tr class="subTitle">
						<th class="cell">ID
					</tr>
				</tbody>
				<tbody>
					<c:forEach var="order" items="${orders}" varStatus="index1">
						<tr style="height:${35.4*orders.get(index1.count-1).getItemIDs().size()}px">
							<td class="cell"><c:out value="${order.orderID}"/>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<table border = "1" style="border-collapse:collapse;float:left">
				<tr class="title">
					<th colspan='2' class="cell">Item
				</tr>
				<tbody>
					<tr class="subTitle">
						<th class="cell">Name
						<th class="cell">Amount
					</tr>
				</tbody>
				<tbody>
					<c:forEach var="order" items="${orders}" varStatus="index1">
						<c:forEach var="item" items="${orders.get(index1.count-1).getItemIDs()}" varStatus="index2">
							<tr>
								<td class="cell"><c:out value="${orders.get(index1.count-1).getItemNames().get(index2.count-1)}"/>
								<td class="cell"><c:out value="${orders.get(index1.count-1).getItemAmounts().get(index2.count-1)}"/>
							</tr>
						</c:forEach>
					</c:forEach>
				</tbody>
			</table>
			<table border = "1" style="border-collapse:collapse">
				<tr class="title">
					<th class="cell">Supplier
					<th colspan='3' class="cell">Cost
					<th colspan='2' class="cell">User
				</tr>
				<tbody>
					<tr class="subTitle">
						<th class="cell">Name
						<th class="cell">Net
						<th class="cell">Shipment
						<th class="cell">Total
						<th class="cell">Date
						<th class="cell">Address
					</tr>
				</tbody>
				<tbody>
					<c:forEach var="order" items="${orders}" varStatus="index1">
						<tr style="height:${35.4*orders.get(index1.count-1).getItemIDs().size()}px">
							<td class="cell"><c:out value="${orders.get(index1.count-1).supplierName}"/>
							<td class="cell"><c:out value="${orders.get(index1.count-1).getTotalCost()} € "/>
							<td class="cell"><c:out value="${orders.get(index1.count-1).shipmentCost} € "/>
							<td class="cell"><c:out value="${orders.get(index1.count-1).getTotalCost() + orders.get(index1.count-1).shipmentCost} € "/>
							<td class="cell"><c:out value="${orders.get(index1.count-1).date}"/>
							<td class="cell"><c:out value="${orders.get(index1.count-1).userAddress}"/>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<h2>You haven't scheduled any orders yet</h2>
		</c:otherwise>
	</c:choose>
</body>
</html>