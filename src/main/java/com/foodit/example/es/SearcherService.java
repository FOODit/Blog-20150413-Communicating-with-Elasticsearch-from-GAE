package com.foodit.example.es;

public interface SearcherService {

    public String searchByQueryString(String query, int from, int resultsSize);
}
