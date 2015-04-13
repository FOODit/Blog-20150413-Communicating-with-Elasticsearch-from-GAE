package com.foodit.example.es.client;

import com.foodit.example.es.SearcherService;
import com.foodit.example.util.Properties;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.NodeBuilder;

public class ClientBasedSearcherService implements SearcherService {

    private Settings settings = ImmutableSettings.settingsBuilder()
            .put("cluster.name", "elasticsearch")
            .build();

    // uncomment this to use the transport client rather than adding the client as a node to the cluster
//    private Client client = new TransportClient(settings)
//            .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

        Client client = NodeBuilder.nodeBuilder().client(true).clusterName("elasticsearch").node().client();

    public String searchByQueryString(String query, int from, int resultsSize) {

        BaseQueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.queryStringQuery(query).field(Properties.SEARCH_FIELD_NAME));

        SearchResponse searchResponse = client.prepareSearch(Properties.INDEX_NAME)
                .setTypes(Properties.TYPE_NAME)
                .setQuery(queryBuilder)
                .setFrom(from)
                .setSize(resultsSize)
                .execute()
                .actionGet();

        return searchResponse.toString();
    }
}
