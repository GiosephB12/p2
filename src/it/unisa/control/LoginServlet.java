package it.unisa.control;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.unisa.model.*;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String[] ALLOWED_PAGES = {"Checkout.jsp", "Home.jsp"};

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao usDao = new UserDao();
        
        try {
            String username = request.getParameter("un");
            String password = request.getParameter("pw");
            String checkout = request.getParameter("checkout");

            UserBean user = usDao.doRetrieve(username, password);

            if (user != null && user.isValid()) {
                // Invalida la vecchia sessione e crea una nuova
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession newSession = request.getSession(true);	    
                newSession.setAttribute("currentSessionUser", user);

                // Determina la pagina di reindirizzamento
                String redirectPage = isValidPage(checkout) ? "/account?page=Checkout.jsp" : "/Home.jsp";
                response.sendRedirect(request.getContextPath() + redirectPage);
            } else {
                response.sendRedirect(request.getContextPath() + "/Login.jsp?action=error");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred");
        }
    }

    private boolean isValidPage(String page) {
        for (String allowedPage : ALLOWED_PAGES) {
            if (allowedPage.equals(page)) {
                return true;
            }
        }
        return false;
    }
}
