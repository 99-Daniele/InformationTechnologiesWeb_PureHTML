package it.polimi.tiw.commerce.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import it.polimi.tiw.commerce.beans.*;
import it.polimi.tiw.commerce.dao.*;

@WebServlet("/AddProductToCart")
public class AddProductToCart extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddProductToCart() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String itemID = null;
		String supplierID = null;
		String amount = null;
		itemID = request.getParameter("itemID");
		supplierID = request.getParameter("supplierID");
		amount = request.getParameter("amount");
		if(itemID == null || supplierID == null || amount == null
				|| itemID.isEmpty() || supplierID.isEmpty() || amount.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameters must not be null");
			return;
		}
		try {
			ItemDAO iDAO = new ItemDAO(connection);
			SupplierDAO sDAO = new SupplierDAO(connection);
			int iID = Integer.parseInt(itemID);
			int sID = Integer.parseInt(supplierID);
			int qt = Integer.parseInt(amount);
			if(qt <= 0) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Select positive amount");
				return;
			}
			if(!iDAO.doesItemExist(iID, sID)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item doesn't exist");
				return;
			}
			if(!sDAO.doesSupplierExist(sID)) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Supplier doesn't exist");
				return;
			}
			HttpSession session = request.getSession();
			ArrayList<Order> cart = (ArrayList<Order>) session.getAttribute("cart");
			if(cart == null)
				cart = new ArrayList<Order>();
			cart = addItemToCart(cart, iID, sID, qt);
			session.setAttribute("cart", cart);
			response.setStatus(HttpServletResponse.SC_OK);
			String path = getServletContext().getContextPath() + "/GetCart";
			response.sendRedirect(path);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong parameters format");
			return;
		} catch(ClassCastException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR! Can't connect to Database ! Retry later");
			return;
		}
	}
	
	private ArrayList<Order> addItemToCart(ArrayList<Order> cart, int itemID, int supplierID, int amount) throws SQLException{
		ItemDAO iDAO = new ItemDAO(connection);
		SupplierDAO sDAO = new SupplierDAO(connection);
		boolean loyalCustomer = false;
		int supplierCount = 0;
		for(Order cartSupplier : cart) {
			if(cartSupplier.getSupplierID() == supplierID) {
				loyalCustomer = true;
				ArrayList<Integer> supplierItemIDs = cartSupplier.getItemIDs();
				ArrayList<Integer> supplierItemAmounts = cartSupplier.getItemAmounts();
				ArrayList<String> supplierItemNames = cartSupplier.getItemNames();
				Item item = iDAO.getItemInfoByItemAndSupplierID(itemID, supplierID);
				String itemName = item.getName();
				ArrayList<Integer> supplierItemPrices = cartSupplier.getPrices();
				int price = item.getPrice();
				supplierItemAmounts = insertItemAmount(supplierItemAmounts, amount, supplierItemIDs, itemID);
				supplierItemNames = insertItemName(supplierItemNames, itemName, supplierItemIDs, itemID);
				supplierItemPrices = insertItemPrice(supplierItemPrices, price, supplierItemIDs, itemID);
				supplierItemIDs = insertItemID(supplierItemIDs, itemID);
				cartSupplier.setItemAmounts(supplierItemAmounts);
				cartSupplier.setItemNames(supplierItemNames);
				cartSupplier.setPrices(supplierItemPrices);
				cartSupplier.setItemIDs(supplierItemIDs);
				int supplierShipmentCost = sDAO.calcShipmentCost(supplierID, cartSupplier.getTotalAmount(), cartSupplier.getTotalCost());
				cartSupplier.setShipmentCost(supplierShipmentCost);
			}
			else
				supplierCount++;
		}
		if(!loyalCustomer) {
			Item item = iDAO.getItemInfoByItemAndSupplierID(itemID, supplierID);
			Order supplier = new Order();
			String itemName = item.getName();
			ArrayList<Integer> supplierItemAmounts = new ArrayList<Integer>();
			ArrayList<String> supplierItemNames = new ArrayList<String>();
			ArrayList<Integer> supplierItemPrices = new ArrayList<Integer>();
			ArrayList<Integer> supplierItemIDs = new ArrayList<Integer>();
			int price = item.getPrice();
			supplierItemAmounts = insertItemAmount(supplierItemAmounts, amount, supplierItemIDs, itemID);
			supplierItemNames = insertItemName(supplierItemNames, itemName, supplierItemIDs, itemID);
			supplierItemPrices = insertItemPrice(supplierItemPrices, price, supplierItemIDs, itemID);
			supplierItemIDs = insertItemID(supplierItemIDs, itemID);
			supplier.setSupplierID(supplierID);
			String supplierName = sDAO.findSupplierBySupplierID(supplierID).getName();
			supplier.setSupplierName(supplierName);
			supplier.setItemAmounts(supplierItemAmounts);
			supplier.setItemNames(supplierItemNames);
			supplier.setPrices(supplierItemPrices);
			supplier.setItemIDs(supplierItemIDs);
			int supplierShipmentCost = sDAO.calcShipmentCost(supplierID, supplier.getTotalAmount(), supplier.getTotalCost());
			supplier.setShipmentCost(supplierShipmentCost);
			cart.add(supplierCount, supplier);
		}
		return cart;
	}
	
	public ArrayList<Integer> insertItemAmount(ArrayList<Integer>itemAmounts, int itemAmount, ArrayList<Integer> itemIDs, int itemID) {
		boolean alreadyInsert = false;
		int i = 0;
		if(itemAmounts.size() == 0) {
			itemAmounts.add(itemAmount);
			return itemAmounts;
		}	
		for(i = 0; i < itemIDs.size(); i++) {
			if(itemIDs.get(i) == itemID) {
				alreadyInsert = true;
				break;
			}
			else if(itemIDs.get(i) > itemID)
				break;
		}
		if(alreadyInsert) {
			int amount = itemAmounts.get(i);
			itemAmounts.set(i, amount + itemAmount);
		}
		else if(i < itemIDs.size())
			itemAmounts.add(i, itemAmount);
		else
			itemAmounts.add(itemAmount);
		return itemAmounts;
	}
	
	public ArrayList<String> insertItemName(ArrayList<String> itemNames, String itemName, ArrayList<Integer> itemIDs, int itemID) {
		boolean alreadyInsert = false;
		int i = 0;
		if(itemNames.size() == 0) {
			itemNames.add(itemName);
			return itemNames;
		}
		for(i = 0; i < itemIDs.size(); i++) {
			if(itemIDs.get(i) == itemID) {
				alreadyInsert = true;
				break;
			}
			else if(itemIDs.get(i) > itemID)
				break;
		}
		if(!alreadyInsert && i < itemIDs.size())
			itemNames.add(i, itemName);
		else
			itemNames.add(itemName);
		return itemNames;
	}
	
	public ArrayList<Integer> insertItemPrice(ArrayList<Integer> itemPrices, int itemPrice, ArrayList<Integer> itemIDs, int itemID) {
		boolean alreadyInsert = false;
		int i = 0;
		if(itemPrices.size() == 0) {
			itemPrices.add(itemPrice);
			return itemPrices;
		}
		for(i = 0; i < itemIDs.size(); i++) {
			if(itemIDs.get(i) == itemID) {
				alreadyInsert = true;
				break;
			}
			else if(itemIDs.get(i) > itemID)
				break;
		}
		if(!alreadyInsert && i < itemIDs.size())
			itemPrices.add(i, itemPrice);
		else
			itemPrices.add(itemPrice);
		return itemPrices;
	}
	
	public ArrayList<Integer> insertItemID(ArrayList<Integer> itemIDs, int itemID) {
		boolean alreadyInsert = false;
		int i = 0;
		if(itemIDs.size() == 0) {
			itemIDs.add(itemID);
			return itemIDs;
		}
		for(i = 0; i < itemIDs.size(); i++) {
			if(itemIDs.get(i) == itemID) {
				alreadyInsert = true;
				break;
			}
			else if(itemIDs.get(i) > itemID)
				break;
		}
		if(!alreadyInsert && i < itemIDs.size())
			itemIDs.add(i, itemID);
		else
			itemIDs.add(itemID);
		return itemIDs;
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
