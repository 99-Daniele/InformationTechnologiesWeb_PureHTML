package it.polimi.tiw.commerce.controllers;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import it.polimi.tiw.commerce.beans.User;
import it.polimi.tiw.commerce.dao.UserDAO;

@WebServlet("/Login")
public class Login extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Login() {
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
    	String path = "/WEB-INF/Login.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true); 
		String email = null;
		String password = null;
		email = request.getParameter("email");
		password = request.getParameter("password");
		if (email == null || password == null || email.isEmpty() || password.isEmpty() ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Insert email and password");
			return;
		}
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		try {
			user = userDao.checkCredentials(email, password);
			if (user == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You have inserted incorrect email or password. Please check and insert again");
				return;
			}
			else {
				if(session.getAttribute("user") != null) {
					User oldUser = (User) session.getAttribute("user");
					int oldUserID = oldUser.getUserID();
					if(user.getUserID() != oldUserID) {
						session.invalidate();
						session = request.getSession(true);
					}
				}
				session.setAttribute("user", user);
				String path = getServletContext().getContextPath() + "/GetHome";
				response.sendRedirect(path);
			}
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
