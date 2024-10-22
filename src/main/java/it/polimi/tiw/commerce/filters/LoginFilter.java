package it.polimi.tiw.commerce.filters;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;


public class LoginFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String path = req.getServletContext().getContextPath() + "/Login";
		HttpSession session = req.getSession(false);
		if (session == null || session.isNew() || session.getAttribute("user") == null) {
			res.sendRedirect(path);
			return;
		}
		else
			chain.doFilter(request, response);
	}
}
