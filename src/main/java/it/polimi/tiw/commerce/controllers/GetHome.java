package it.polimi.tiw.commerce.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import it.polimi.tiw.commerce.beans.*;
import it.polimi.tiw.commerce.dao.ItemDAO;

@WebServlet("/GetHome")
public class GetHome extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetHome() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		try {
			ArrayList<Item> visualizedItems = (ArrayList<Item>) session.getAttribute("visualizedItems");
			if(visualizedItems == null) {
				ItemDAO iDAO = new ItemDAO(connection);
				visualizedItems = iDAO.getFiveRandomItems();
				session.setAttribute("visualizedItems", visualizedItems);
			}
			String path = "/WEB-INF/Home.jsp";
			ArrayList<Order> cart = (ArrayList<Order>) session.getAttribute("cart");
			if(cart == null) {
				cart = new ArrayList<Order>();
				session.setAttribute("cart", cart);
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
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
