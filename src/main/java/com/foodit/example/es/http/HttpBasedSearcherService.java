package com.foodit.example.es.http;

import com.foodit.example.es.SearcherService;
import com.foodit.example.util.GaeBytesStream;
import com.foodit.example.util.Properties;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Example of how to search in Elastic Search from an application running on Google App Engine.
 */
public class HttpBasedSearcherService implements SearcherService {

    private static final String ES_BASE_URL = "http://localhost:9200/" + Properties.INDEX_NAME + "/" + Properties.TYPE_NAME + "/_search";

    public String searchByQueryString(String query, int from, int resultsSize) {
        BaseQueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.queryStringQuery(query).field(Properties.SEARCH_FIELD_NAME));

        JsonObject jsonObject = new JsonObject();
        String jsonQuery = createJsonQuery(queryBuilder);
        jsonObject.add("query", new JsonParser().parse(jsonQuery).getAsJsonObject());
        jsonObject.addProperty("from", from);
        jsonObject.addProperty("size", resultsSize);

        return executeSearchRequest(jsonObject);
    }

    /**
     * Create a JSON String that can be used to query elastic search via an HTTP request.
     */
    private String createJsonQuery(BaseQueryBuilder queryBuilder) {
        try {
            XContentBuilder builder = new XContentBuilder(JsonXContent.jsonXContent, new GaeBytesStream(4096));
            builder.prettyPrint();
            queryBuilder.toXContent(builder, ToXContent.EMPTY_PARAMS);
            return builder.string();
        } catch (Exception e) {
            throw new RuntimeException("There was a problem turning the Query into JSON", e);
        }
    }

    /**
     * Create a JSON String that can be used for sorting when querying elastic search via HTTP.
     */
    protected String createJsonSort(Iterable<SortBuilder> sortBuilders) {
        try {
            XContentBuilder builder = new XContentBuilder(JsonXContent.jsonXContent, new GaeBytesStream(4096));
            builder.prettyPrint();
            builder.startArray();
            for (SortBuilder sortBuilder : sortBuilders) {
                builder.startObject();
                sortBuilder.toXContent(builder, ToXContent.EMPTY_PARAMS);
                builder.endObject();
            }
            builder.endArray();
            return builder.string();
        } catch (IOException e) {
            throw new RuntimeException("There was a problem turning the Sort into JSON", e);
        }
    }

    private String executeSearchRequest(JsonObject requestContent) {
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

        try {
            HttpContent httpContent = new GsonHttpContent(new GsonFactory(), requestContent);
            HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(ES_BASE_URL), httpContent);
            return request.execute().parseAsString();
        } catch (IOException e) {
            throw new RuntimeException("Problem communicating with Elastic Search", e);
        }
    }

    private static final class GsonHttpContent extends JsonHttpContent {

        private JsonObject jsonObject;

        public GsonHttpContent(JsonFactory jsonFactory, JsonObject jsonObject) {
            super(jsonFactory, jsonObject);
            this.jsonObject = jsonObject;
        }

        public void writeTo(OutputStream out) throws IOException {
            if (jsonObject != null) {
                out.write(jsonObject.toString().getBytes("UTF-8"));
                out.flush();
            }
        }
    }
}
