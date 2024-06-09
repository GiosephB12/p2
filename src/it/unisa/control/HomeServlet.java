package it.unisa.control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.unisa.model.ProdottoBean;
import it.unisa.model.ProdottoDao;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String[] ALLOWED_PAGES = {"Home.jsp", "Ps5.jsp", "XboxSeries.jsp", "Switch.jsp", "Ps4.jsp", "XboxOne.jsp"};

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProdottoDao dao = new ProdottoDao();
        ArrayList<ArrayList<ProdottoBean>> categorie = new ArrayList<ArrayList<ProdottoBean>>();

        try {
            ArrayList<ProdottoBean> PS5 = dao.doRetrieveByPiattaforma("PlayStation 5");
            ArrayList<ProdottoBean> XboxSeries = dao.doRetrieveByPiattaforma("Xbox Series");
            ArrayList<ProdottoBean> Switch = dao.doRetrieveByPiattaforma("Nintendo Switch");
            ArrayList<ProdottoBean> PS4 = dao.doRetrieveByPiattaforma("PlayStation 4");
            ArrayList<ProdottoBean> Xone = dao.doRetrieveByPiattaforma("Xbox One");

            categorie.add(PS5);
            categorie.add(XboxSeries);
            categorie.add(Switch);
            categorie.add(PS4);
            categorie.add(Xone);

            request.getSession().setAttribute("categorie", categorie);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String redirectedPage = request.getParameter("page");
        if (redirectedPage == null || redirectedPage.isEmpty()) {
            // Se il parametro 'page' è vuoto o null, rimanda alla home predefinita
            redirectedPage = "Home.jsp";
            }
        if (redirectedPage != null && isValidPage(redirectedPage)) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/" + redirectedPage);
            dispatcher.forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid page parameter");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
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

