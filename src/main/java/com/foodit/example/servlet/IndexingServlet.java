package com.foodit.example.servlet;

import com.foodit.example.es.IndexerService;
import com.foodit.example.es.SearcherService;
import com.foodit.example.es.http.HttpBasedIndexerService;
import com.foodit.example.es.http.HttpBasedSearcherService;
import com.foodit.example.util.Properties;
import com.google.common.collect.ImmutableMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexingServlet extends HttpServlet {

    private IndexerService indexerService = new HttpBasedIndexerService();
//        private IndexerService indexerService = new ClusterBasedIndexerService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String result = indexerService.indexDocument("123", ImmutableMap.of(Properties.SEARCH_FIELD_NAME, "bar"));

        resp.getWriter().append(result);
    }
}
