package com.foodit.example.es.client;

import com.foodit.example.es.IndexerService;
import com.foodit.example.util.Properties;
import com.google.gson.JsonObject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.NodeBuilder;

import java.util.Map;

public class ClientBasedIndexerService implements IndexerService {

    Settings settings = ImmutableSettings.settingsBuilder()
            .put("cluster.name", "elasticsearch")
            .build();

    // uncomment this to use the transport client rather than adding the client as a node to the cluster
//    Client client = new TransportClient(settings)
//            .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));

    Client client = NodeBuilder.nodeBuilder().client(true).clusterName("elasticsearch").node().client();


    public String indexDocument(String documentId, Map<String, String> properties) {
        JsonObject jsonDocument = new JsonObject();

        for(Map.Entry<String, String> entry : properties.entrySet()) {
            jsonDocument.addProperty(entry.getKey(), entry.getValue());
        }
        jsonDocument.addProperty("id", documentId);

        IndexResponse response = client
                .prepareIndex(Properties.INDEX_NAME, Properties.TYPE_NAME)
                .setSource(jsonDocument.toString())
                .execute()
                .actionGet();

        return response.toString();
    }
}
