package com.foodit.example.es;

import java.util.Map;

public interface IndexerService {

    public String indexDocument(String documentId, Map<String, String> document);
}
