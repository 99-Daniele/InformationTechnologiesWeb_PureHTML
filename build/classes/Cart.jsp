<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html style="background-color:bisque">
<head>
<link rel="stylesheet" type="text/css" href="/TIW_E-CommerceApp_PureHTML/CSS/mystyle.css">
<meta charset="UTF-8">
<title>CART</title>
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
		<c:when test="${cart.size() > 0}">
			<h2>This is your cart</h2>
			<table border = "1" style="border-collapse:collapse;float:left">
				<tr class="title">
					<th colspan='3' class="cell">Item</th>
				</tr>
				<tbody>
					<tr class="subTitle">
						<th class="cell">Name
						<th class="cell">Amount
						<th class="cell">Price
					</tr>
				</tbody>
				<tbody>
					<c:forEach var="supplier" items="${cart}" varStatus="index1">
						<c:forEach items="${cart.get(index1.count-1).getItemIDs()}" varStatus="index2">
							<tr>
								<td class="cell"><c:out value="${cart.get(index1.count-1).getItemNames().get(index2.count-1)}"/>
								<td class="cell"><c:out value="${cart.get(index1.count-1).getItemAmounts().get(index2.count-1)}"/>
								<td class="cell"><c:out value="${cart.get(index1.count-1).getPrices().get(index2.count-1)} € "/>
							</tr>
						</c:forEach>	
					</c:forEach>
				</tbody>
			</table>
			<table border = "1" style="border-collapse:collapse;float:left">
				<tr class="title">
					<th class="cell">Supplier
				</tr>
				<tbody>
					<tr class="subTitle">
						<th class="cell">Name
					</tr>
				</tbody>
				<tbody>
					<c:forEach var="supplier" items="${cart}" varStatus="index1">
						<tr style="height:${35.4*cart.get(index1.count-1).getItemIDs().size()}px">
							<td class="cell"><c:out value="${supplier.supplierName}"/>
						</tr>	
					</c:forEach>
				</tbody>
			</table>
			<table border = "1" style="border-collapse:collapse;float:left">
				<tr class="title">
					<th colspan='4' class="cell">Cost
				</tr>
				<tbody>
					<tr class="subTitle">
						<th class="cell">Net
						<th class="cell">Shipment
						<th class="cell">Total
						<th class="cell">Order
					</tr>
				</tbody>
				<tbody>
					<c:forEach var="supplier" items="${cart}" varStatus="index1">
						<tr style="height:${35.4*cart.get(index1.count-1).getItemIDs().size()}px">
							<td class="cell"><c:out value="${supplier.getTotalCost()} €"/>
							<td class="cell"><c:out value="${supplier.shipmentCost} €"/>
							<td class="cell"><c:out value="${supplier.getTotalCost() + supplier.shipmentCost} €"/>
							<td>
								<form method="get" action="/TIW_E-CommerceApp_PureHTML/CreateNewOrder">
									<button type="submit">ORDER</button>
									<input type="hidden" name="supplierID" value="${cart.get(index1.count-1).supplierID}" />
								</form>
							</td>
						</tr>	
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<h2>Your cart is empty</h2>
		</c:otherwise>
	</c:choose>
</body>
</html>