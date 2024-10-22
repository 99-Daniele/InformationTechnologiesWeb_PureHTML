package it.polimi.tiw.commerce.controllers;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import it.polimi.tiw.commerce.beans.Item;
import it.polimi.tiw.commerce.dao.ItemDAO;

@WebServlet("/GetSearchedItems")
public class GetSearchedItems extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetSearchedItems() {
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
		String keyword = null;
		HttpSession session = request.getSession();
		keyword = request.getParameter("keyword");
		if (keyword == null || keyword.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Insert keyword");
			return;
		}
		ItemDAO iDAO = new ItemDAO(connection);
		try {
			List<Item> items = iDAO.findItemsByKeyword(keyword);
			String path = "/WEB-INF/Result.jsp";
			request.setAttribute("keyword", keyword);
			request.setAttribute("items", items);
			request.setAttribute("n_results", items.size());
			if(session.getAttribute("visualize_count") != null) {
				if((int) session.getAttribute("visualize_count") == 2) {
					session.setAttribute("visualize_count", 0);
				}
				if((int) session.getAttribute("visualize_count") == 1) {
					session.setAttribute("visualize_count", 2);
				}
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
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
