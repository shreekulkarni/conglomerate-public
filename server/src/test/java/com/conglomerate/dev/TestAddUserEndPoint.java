package com.conglomerate.dev;


import com.conglomerate.dev.models.domain.AddUserDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TestAddUserEndPoint {
    @Test
    public void whenPostJsonUsingHttpClient_thenCorrect()
            throws ClientProtocolException, IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/users");

        AddUserDomain userDomain = AddUserDomain.builder()
                .userName("a")
                .email("b")
                .passwordHash("a")
                .build();

        System.out.println(userDomain);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userDomain);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        client.close();
    }
}
