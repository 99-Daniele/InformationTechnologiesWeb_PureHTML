package it.polimi.tiw.commerce.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import it.polimi.tiw.commerce.beans.*;
import it.polimi.tiw.commerce.dao.*;

@WebServlet("/AddVisualizedItem")
public class AddVisualizedItem extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddVisualizedItem() {
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

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String itemID = request.getParameter("itemID");
		String keyword = request.getParameter("keyword");
		if (itemID == null || itemID.isEmpty() || keyword == null || keyword.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameters must not be null");
			return;
		}
		ItemDAO iDAO = new ItemDAO(connection);
		SpendingRangeDAO srDAO = new SpendingRangeDAO(connection);
		try {
			int id = Integer.parseInt(itemID);
			if (!iDAO.doesItemExist(id)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item doesn't exist");
				return;
			}
			ArrayList<Item> itemInfos = iDAO.getItemInfoByItemID(id);
			ArrayList<Supplier> suppliers = iDAO.findSuppliersByItemID(id);
			ArrayList<ArrayList<SpendingRange>> spendingRanges = srDAO.findSpendingRangesByItemID(id);
			ArrayList<Item> visualizedItems = (ArrayList<Item>) session.getAttribute("visualizedItems");
			boolean recentlyVisualized = false;
			for(Item item: visualizedItems) {
				if(item.getItemID() == id) {
					visualizedItems.remove(item);
					recentlyVisualized = true;
					break;
				}
			}
			if(!recentlyVisualized)
				visualizedItems.remove(4);
			visualizedItems.add(0, itemInfos.get(0));
			request.setAttribute("visualizedItems", visualizedItems);
			request.setAttribute("visualizedItem", itemInfos);
			request.setAttribute("visualizedSuppliers", suppliers);
			request.setAttribute("visualizedSpendingRanges", spendingRanges);
			session.setAttribute("visualize_count", 2);
			ArrayList<Order> cart = ((ArrayList<Order>) session.getAttribute("cart"));
			if(cart == null) {
				cart = new ArrayList<Order>();
				session.setAttribute("cart", cart);
			}
			List<Item> items = iDAO.findItemsByKeyword(keyword);
			request.setAttribute("keyword", keyword);
			request.setAttribute("items", items);
			request.setAttribute("n_results", items.size());
			request.setAttribute("cartAmounts", prepareAmounts(cart, suppliers));
			request.setAttribute("cartPrices", prepareCosts(cart, suppliers));
			request.setAttribute("keyword", request.getAttribute("keyword"));
			response.setStatus(HttpServletResponse.SC_OK);
			String path = "/WEB-INF/Result.jsp";
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
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
	
	private ArrayList<Integer> prepareAmounts(ArrayList<Order> cart, ArrayList<Supplier> suppliers) {
		int pos = 0;
		ArrayList<Integer> amounts = new ArrayList<Integer>();
		while(pos < suppliers.size()) {
			int amount = 0;
			for(Order supplier: cart) {
				if(supplier.getSupplierID() == suppliers.get(pos).getSupplierID()) {
					amount = supplier.getTotalAmount();
					break;
				}
			}
			amounts.add(amount);
			pos++;
		}
		return amounts;
	}
	
	private ArrayList<Integer> prepareCosts(ArrayList<Order> cart, ArrayList<Supplier> suppliers) {
		int pos = 0;
		ArrayList<Integer> costs = new ArrayList<Integer>();
		while(pos < suppliers.size()) {
			int cost = 0;
			for(Order supplier: cart) {
				if(supplier.getSupplierID() == suppliers.get(pos).getSupplierID()) {
					cost = supplier.getTotalCost();
					break;
				}
			}
			costs.add(cost);
			pos++;
		}
		return costs;
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
