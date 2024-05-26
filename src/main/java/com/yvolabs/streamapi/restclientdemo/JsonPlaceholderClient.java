package com.yvolabs.streamapi.restclientdemo;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * @author Yvonne N
 */
@Component
public class JsonPlaceholderClient {
    private final RestClient restClient;

    public JsonPlaceholderClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    public JsonPlaceHolderResponse[] getAllTodos() {
        return restClient.get()
                .uri("/todos")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(JsonPlaceHolderResponse[].class);
    }

    public JsonPlaceHolderResponse getTodoById(int id) {
        return restClient.get()
                .uri("/todos/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(JsonPlaceHolderResponse.class);
    }
}
