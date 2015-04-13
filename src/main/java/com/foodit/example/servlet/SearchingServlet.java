package com.foodit.example.servlet;

import com.foodit.example.es.SearcherService;
import com.foodit.example.es.client.ClientBasedSearcherService;
import com.foodit.example.es.http.HttpBasedSearcherService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SearchingServlet extends HttpServlet {

    private SearcherService searcherService = new HttpBasedSearcherService();
//    private SearcherService searcherService = new ClientBasedSearcherService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("query");
        String results = searcherService.searchByQueryString(query, 0, 20);
        resp.getWriter().append(results);
    }
}
