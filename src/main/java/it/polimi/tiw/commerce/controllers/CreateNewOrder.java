package it.polimi.tiw.commerce.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import it.polimi.tiw.commerce.beans.*;
import it.polimi.tiw.commerce.dao.*;

@WebServlet("/CreateNewOrder")
public class CreateNewOrder extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public CreateNewOrder() {
		super();
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		try {
			SupplierDAO sDAO = new SupplierDAO(connection);
			OrderDAO oDAO = new OrderDAO(connection);
			String supplierID = request.getParameter("supplierID");
			if (supplierID == null || supplierID.isEmpty()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameters must not be null");
				return;
			}
			int sID = Integer.parseInt(supplierID);
			if (!sDAO.doesSupplierExist(sID)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Supplier doesn't exist");
				return;
			}
			ArrayList<Integer> itemIDs = new ArrayList<Integer>();
			ArrayList<Integer> itemAmounts = new ArrayList<Integer>();
			ArrayList<Integer> itemPrices = new ArrayList<Integer>();
			int cost = 0;
			int shipmentCost = 0;
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date dateParam = null;
			dateParam = cal.getTime();
			ArrayList<Order> cart = (ArrayList<Order>) session.getAttribute("cart");
			if(cart == null)
				cart = new ArrayList<Order>();
			if(cart.size() > 0) {
				int i;
				for(i = 0; i < cart.size(); i++) {
					if(cart.get(i).getSupplierID() == sID) {
						itemIDs = cart.get(i).getItemIDs();
						itemAmounts = cart.get(i).getItemAmounts();
						itemPrices = cart.get(i).getPrices();
						cost = cart.get(i).getTotalCost();
						shipmentCost = cart.get(i).getShipmentCost();
						break;
					}
				}
				cart.remove(i);
			}
			session.setAttribute("cart", cart);
			oDAO.createOrder(itemIDs, dateParam, user.getUserID(), itemAmounts, itemPrices, cost, shipmentCost, sID, user.getAddress());
			response.setStatus(HttpServletResponse.SC_OK);
			String path = getServletContext().getContextPath() + "/GetOrders";
			response.sendRedirect(path);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong parameter format");
			return;
		} catch(ClassCastException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR! Can't connect to Database ! Retry later");
			return;
		}
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}
