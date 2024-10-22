<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html style="background-color:bisque; white-space: nowrap; overflow: auto;">
<head>
<link rel="stylesheet" type="text/css" href="/TIW_E-CommerceApp_PureHTML/CSS/mystyle.css">
<meta charset="UTF-8">
<title>RESULTS</title>
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
	<br>
	<form method="get" action="/TIW_E-CommerceApp_PureHTML/GetSearchedItems">
		Enter keyword: <input name="keyword" value= "" class="submit_on_enter" type="text"placeholder="Search..." required>
	</form>
	<c:choose>
		<c:when test="${items.size() > 0}">
			<h2>${n_results} results for keyword ${keyword}</h2>
			<table border = "1" style="border-collapse:collapse;float:left">
				<tr class="title">
					<th colspan='3' class="cell">Searched items
				</tr>	
				<tr class="subTitle">
					<th class="cell">ID
					<th class="cell">Name
					<th class="cell">Price
				</tr>
				<tbody>
					<c:forEach var="item" items="${items}" varStatus="row">
						<tr>
							<td class="cell"><c:out value="${item.itemID}"/>
							<td class="cell" ><a href="/TIW_E-CommerceApp_PureHTML/AddVisualizedItem?itemID=${item.itemID}&keyword=${keyword}"><c:out value="${item.name}"/></a>
							<td class="cell"><c:out value="${item.price} € "/>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<c:choose>
				<c:when test="${visualize_count == 2}">
				<fieldset style="padding:0; border:0; display:inline">
					<table border = "1" style="border-collapse:collapse;float:left">
						<tr class="title">
							<th colspan='6' class="cell">Item
						</tr>
						<tbody>
							<tr class="subTitle">
								<th class="cell">ID
								<th class="cell">Name
								<th class="cell">Description
								<th class="cell">Category
								<th class="cell">Image
								<th class="cell">Price
							</tr>
						</tbody>	
						<tbody>
							<c:forEach var="visualizedItem" items="${visualizedItem}" varStatus="row">
								<tr style="height:${35.4*visualizedSpendingRanges.get(row.count-1).size()}px">
									<c:choose>
										<c:when test="${row.count == 1}">
											<td rowspan='0' class="cell"><c:out value="${visualizedItem.itemID}"/>
											<td rowspan='0' class="cell" style="width:80px"><c:out value="${visualizedItem.name}" />
											<td rowspan='0' class="cell" style="width:100px"><c:out value="${visualizedItem.description}"/>
											<td rowspan='0' class="cell" style="width:80px"><c:out value="${visualizedItem.category}"/>
											<td rowspan='0'>
												<img src="${visualizedItem.image}" style="height:120px"/>
											</td>
										</c:when>
									</c:choose>	
									<td class="cell" ><c:out value="${visualizedItem.price} € "/>
								<tr>
							</c:forEach>
						</tbody>
					</table>
					<table border = "1" style="border-collapse:collapse;float:left">
						<tr class="title">
							<th colspan='3' class="cell">Supplier
						</tr>
						<tbody>
							<tr class="subTitle">
								<th class="cell">Name
								<th class="cell">Score
								<th class="cell">Threshold
							</tr>
						</tbody>	
						<tbody>
							<c:forEach var="supplier" items="${visualizedSuppliers}" varStatus="index">
								<tr style="height:${35.4*visualizedSpendingRanges.get(index.count-1).size()}px">
									<td class="cell"><c:out value="${supplier.name}"/>
									<td class="cell"><c:out value="${supplier.score} / 5"/>
									<td class="cell">
										<c:choose>
											<c:when test="${supplier.threshold == 0}">
												<c:out value="-"/>
											</c:when>
											<c:otherwise>
												<c:out value="${supplier.threshold} € "/>
											</c:otherwise>
										</c:choose>		
									</td>
								<tr>
							</c:forEach>
						</tbody>
					</table>
					<table border = "1" style="border-collapse:collapse;float:left">
						<tr class="title">
							<th colspan='2' class="cell">SpendingRange
						</tr>
						<tbody>
							<tr class="subTitle">
								<th class="cell">Range
								<th class="cell">Cost
							</tr>
						</tbody>	
						<tbody>
							<c:forEach var="spendingRanges" items="${visualizedSpendingRanges}" varStatus="index1">
								<c:forEach var="spendingRange" items="${visualizedSpendingRanges.get(index1.count-1)}" varStatus="index2">
									<tr>
										<td class="cell">
											<c:choose>
												<c:when test ="${spendingRange.min == 1}">
													<c:out value="up to ${visualizedSpendingRanges.get(index1.count-1).get(index2.count).min -1}"/>	
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${index2.count < visualizedSpendingRanges.get(index1.count-1).size()}">
															<c:choose>
																<c:when test ="${spendingRange.min == visualizedSpendingRanges.get(index1.count-1).get(index2.count).min -1}">
																	<c:out value="${spendingRange.min}"/>
																</c:when>
																<c:otherwise>
																	<c:out value="${spendingRange.min} to ${visualizedSpendingRanges.get(index1.count-1).get(index2.count).min -1}"/>
																</c:otherwise>		
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:out value="more than ${spendingRange.min}"/>
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="cell">
											<c:choose>
												<c:when test ="${spendingRange.price == 0}">
													<c:out value="free"/>
												</c:when>
												<c:otherwise>	
													<c:out value="${spendingRange.price} € "/>
												</c:otherwise>	
											</c:choose>	
										</td>
									<tr>
								</c:forEach>	
							</c:forEach>
						</tbody>
					</table>
					<table border = "1" style="border-collapse:collapse;float:left">
						<tr class="title">
							<th colspan='2' class="cell">Cart
						</tr>
						<tbody>
							<tr class="subTitle">
								<th class="cell">Items
								<th class="cell">Cost
							</tr>
						</tbody>	
						<tbody>
							<c:forEach var="supplier" items="${cartAmounts}" varStatus="index1">
								<tr style="height:${35.4*visualizedSpendingRanges.get(index1.count-1).size()}px">
									<td class="cell"><c:out value="${supplier}"/>
									<c:choose>
										<c:when test="${cartPrices.get(index1.count-1) > 0}">
											<td class="cell"><c:out value="${cartPrices.get(index1.count-1)} € "/>
										</c:when>
										<c:otherwise>
											<td class="cell"><c:out value="-"/>
										</c:otherwise>	
									</c:choose>
								</tr>		
							</c:forEach>
						</tbody>
					</table>
					<table border = "1" style="border-collapse:collapse">
						<tr class="title">
							<th class="cell">Order
						</tr>
						<tbody>
							<tr class="subTitle">
								<th class="cell">Quantity
							</tr>
						</tbody>	
						<tbody>
							<c:forEach var="supplier" items="${visualizedSuppliers}" varStatus="index">
								<tr style="height:${35.4*visualizedSpendingRanges.get(index.count-1).size()}px">
									<td>
										<form method="post" action="/TIW_E-CommerceApp_PureHTML/AddProductToCart">
											<input name="amount" type="number" min="1" max="999" required>		
											<button type="submit">PUT IN THE CART</button>
											<input type="hidden" name="itemID" value="${visualizedItem.get(0).itemID}" />
											<input type="hidden" name="supplierID" value="${supplier.supplierID}" />
										</form>	
									</td>
								<tr>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>						
				</c:when>
			</c:choose>		
		</c:when>
		<c:otherwise>
			<h2>No results for keyword ${keyword}</h2>
		</c:otherwise>
	</c:choose>
</body>
</html>	