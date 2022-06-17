import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;


import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

public class ElasticJava {

    public static RestHighLevelClient createOpenSearchClient() {
        //   String connString = "http://localhost:9200";
        String connString = "https://99cef5721ef14f3a9bfaaf4083f43737.us-central1.gcp.cloud.es.io:443";

        // we build a URI from the connection string
        RestHighLevelClient restHighLevelClient;
        URI connUri = URI.create(connString);
        // extract login information if it exists
        String userInfo = connUri.getUserInfo();

        if (userInfo == null) {
            // REST client without security
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), "http")));

        } else {
            // REST client with security
            String[] auth = userInfo.split(":");

            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), connUri.getScheme()))
                            .setHttpClientConfigCallback(
                                    httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(cp)
                                            .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));


        }

        return restHighLevelClient;
    }
    public static void main(String[] args)
    {
        RestHighLevelClient client = createOpenSearchClient();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("miracle_employee");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        Map<String, Object> map=null;

        try{
            SearchResponse searchResponse = null;
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            if(searchResponse.getHits().getTotalHits().value > 0) {
                SearchHit[] searchHit = searchResponse.getHits().getHits();
                for (SearchHit hit : searchHit) {
                    map = hit.getSourceAsMap();
                    //map = hit.getSourceAsMap();
                    System.out.println("map:"+Arrays.toString(map.entrySet().toArray()));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Hello");
    }
}