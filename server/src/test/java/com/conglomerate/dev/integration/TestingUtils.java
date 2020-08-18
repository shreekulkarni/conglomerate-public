package com.conglomerate.dev.integration;

import com.conglomerate.dev.models.Device;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.models.domain.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TestingUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    //private static final String baseUrl = "http://134.122.21.105:8080";
    private static final String baseUrl = "http://localhost:8080";


    public static void createUserAndExpect(String username, String email,
                                           String password, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(baseUrl + "/users");

        AddUserDomain userDomain = AddUserDomain.builder()
                .userName(username)
                .email(email)
                .passwordHash(password)
                .build();

        System.out.println(userDomain);

        String json = objectMapper.writeValueAsString(userDomain);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        System.out.println("**** MAKING ADDUSER REQUEST ****");
        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }

    public static String loginAndExpect(String username, String password, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(baseUrl + "/users/login");

        LoginRequestDomain loginRequest = LoginRequestDomain.builder()
                .username(username)
                .password(password)
                .build();

        String json = objectMapper.writeValueAsString(loginRequest);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        System.out.println("**** MAKING LOGIN REQUEST ****");
        HttpResponse response = client.execute(httpPost);
        String authToken = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));

        return authToken;
    }

    public static String createUserAndLoginSuccess(String username, String email,
                                                     String password) throws Exception {
        createUserAndExpect(username, email, password, 201);
        return loginAndExpect(username, password, 200);
    }

    public static int createGroupingSuccess(String groupName) throws Exception {
        String username = String.valueOf((int) (Math.random() * 10000000));
        String email = String.valueOf((int) (Math.random() * 10000000));
        String password = String.valueOf((int) (Math.random() * 10000000));
        String authToken = createUserAndLoginSuccess(username, email, password);

        return createGroupingAndExpect(authToken, groupName, 201);
    }

    public static int createGroupingAndExpect(String authToken, String groupingName,
                                           int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/groupings");

        String json = objectMapper.writeValueAsString(groupingName);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        System.out.println("**** MAKING CREATEGROUP REQUEST ****");
        HttpResponse response = client.execute(httpPost);
        int groupingId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();

        return groupingId;
    }

    public static void changeGroupingNameAndExpect(String authToken, int groupingId, String newName,
                                                  int expectedStatusCode) throws Exception {
        UpdateGroupingNameDomain updateGroupingNameDomain = UpdateGroupingNameDomain.builder()
                .groupingId(groupingId)
                .newGroupName(newName)
                .build();
        System.out.println(updateGroupingNameDomain);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/groupings/change-name");

        String json = objectMapper.writeValueAsString(updateGroupingNameDomain);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }

    public static void joinGroupingAndExpect(String authToken, int groupingId,
                                             int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/groupings/join");

        String json = objectMapper.writeValueAsString(groupingId);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }

    public static void deleteUserAndExpect(String authToken, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/users/delete-account");

        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpDelete);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));

        client.close();
    }

    public static void leaveGroupingAndExpect(String authToken, int groupingId,
                                             int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/groupings/leave");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(groupingId);

        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }

    public static int sendMessageAndExpect(String authToken, String content,
                                            int groupingId, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/messages");

        SendMessageDomain sendMessageDomain = SendMessageDomain.builder()
                .content(content)
                .groupingId(groupingId)
                .build();

        String json = objectMapper.writeValueAsString(sendMessageDomain);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        int messageId = -1;
        if (expectedStatusCode == 201) {
            messageId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();

        return messageId;
    }

    public static List<GetMessageDomain> getLatestMessagesAndExpect(String authToken, int groupingId,
                                                                    int expectedStatusCode) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(baseUrl + "/groupings/" + groupingId + "/messages");

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        List<GetMessageDomain> messageDomainList = null;

        if (expectedStatusCode == 200) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Got response:\n" +
                    jsonResponse);
            messageDomainList = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<GetMessageDomain>>() {
                    });
        }

        client.close();
        return messageDomainList;
    }


    public static void registerDeviceAndExpect(String authToken, String deviceToken,
                                            int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/users/devices");

        StringEntity entity = new StringEntity(deviceToken);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }


    public static List<Device> getDevicesAndExpect(String authToken, int expectedStatusCode) throws Exception{
        objectMapper.registerModule(new JavaTimeModule());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(baseUrl + "/users/devices");

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        List<Device> deviceList = null;

        if (expectedStatusCode == 200) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Got response:\n" +
                    jsonResponse);
            deviceList = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<Device>>() {
                    });
        }

        client.close();
        return deviceList;
    }

    public static void removeMemberAndExpect(String authToken, int groupingId, String usernameToRemove,
                                            int expectedStatusCode) throws Exception {
        RemoveMemberDomain removeMemberDomain = RemoveMemberDomain.builder()
                .groupingId(groupingId)
                .username(usernameToRemove)
                .build();

        System.out.println(removeMemberDomain);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/groupings/remove-member");

        String json = objectMapper.writeValueAsString(removeMemberDomain);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }

    public static void likeMessageAndExpect(String authToken, int messageId,
                                            int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/messages/" + messageId + "/like");

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }

    public static int uploadDocumentAndExpect(String authToken, int groupingId,
                                     String filePath, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/documents/" + groupingId + "/upload");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // This attaches the file to the POST:
        File f = new File(filePath);
        builder.addBinaryBody(
                "file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );
        builder.addPart("shared", new StringBody("true", ContentType.TEXT_PLAIN));

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        int documentId = -1;
        if (expectedStatusCode == 201) {
            documentId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();

        return documentId;
    }

    public static int uploadPrivateDocumentAndExpect(String authToken, int groupingId,
                                              String filePath, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/documents/" + groupingId + "/upload");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // This attaches the file to the POST:
        File f = new File(filePath);
        builder.addBinaryBody(
                "file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );
        builder.addPart("shared", new StringBody("false", ContentType.TEXT_PLAIN));

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        int documentId = -1;
        if (expectedStatusCode == 201) {
            documentId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();

        return documentId;
    }

    public static int uploadDocumentToFolderAndExpect(String authToken, int groupingId, int folderId,
                                              String filePath, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/documents/" + groupingId + "/" + folderId + "/upload");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // This attaches the file to the POST:
        File f = new File(filePath);
        builder.addBinaryBody(
                "file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );
        builder.addPart("shared", new StringBody("true", ContentType.TEXT_PLAIN));

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        int documentId = -1;
        if (expectedStatusCode == 201) {
            documentId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();

        return documentId;
    }

    public static int linkGoogleAndExpect(String authToken, String idToken, String refreshToken,
                                          int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/users/link-google?idToken=" + idToken + "&refreshToken=" + refreshToken);

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        int userId = -1;
        if (expectedStatusCode == 200) {
            userId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();

        return userId;
    }
  
    public static int createFolderAndExpect(String authToken, int groupingId,
                                            String folderName, int expectedStatusCode) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/folders/" + groupingId + "/create");

        StringEntity entity = new StringEntity(folderName);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));

        int folderId = -1;
        if (expectedStatusCode == 201) {
            folderId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }

        client.close();
        return folderId;
    }

    public static List<ListGroupDocsDomain> listGroupDocsAndExpect(String authToken, int groupingId,
                                                      int expectedStatusCode) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(baseUrl + "/documents/" + groupingId + "/list-docs");

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        List<ListGroupDocsDomain> domain = null;

        if (expectedStatusCode == 200) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Got response:\n" +
                    jsonResponse);
            domain = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<ListGroupDocsDomain>>() {
                    });
        }


        client.close();
        return domain;
    }

    public static void deleteDocumentAndExpect(String authToken, int documentId, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(baseUrl + "/documents/" + documentId);

        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpDelete);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));

        client.close();
    }


    public static void setSharedAndExpect(String authToken, int documentId, boolean shared,
                                          int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/documents/" + documentId + "/set-shared");

        String json = objectMapper.writeValueAsString(shared);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        client.close();
    }

    public static List<ListDocumentDomain> searchForDocumentAndExpect(String authToken, int groupingId,
                                                                      String searchString,
                                                                      int expectedStatusCode) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(baseUrl + "/documents/" + groupingId + "/search-docs/" + searchString);

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        List<ListDocumentDomain> domain = null;

        if (expectedStatusCode == 200) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Got response:\n" +
                    jsonResponse);
            domain = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<ListDocumentDomain>>() {
                    });
        }

        client.close();
        return domain;
    }

    public static int setViewPermissionsAndExpect(String authToken, int documentId, boolean uploaderOnly,
                                                        int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/documents/" + documentId + "/set-permissions");


        String json = objectMapper.writeValueAsString(uploaderOnly);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));

        int permissionCode = -1;
        if (expectedStatusCode == 200) {
            permissionCode = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }

        client.close();
        return permissionCode;
    }

    public static int createEventAndExpect(String authToken, int groupingId, String eventName, LocalDateTime dateTime,
                                           int duration, int expectedStatusCode) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/events/" + groupingId + "/create-event");

        CreateEventDomain createEventDomain = CreateEventDomain.builder()
                .eventName(eventName)
                .dateTime(dateTime)
                .duration(duration)
                .build();

        String json = objectMapper.writeValueAsString(createEventDomain);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));

        int eventId = -1;
        if (expectedStatusCode == 201) {
            eventId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }

        client.close();
        return eventId;
    }

    public static List<ListEventsDomain> listGroupEventsAndExpect(String authToken, int groupingId, int expectedStatusCode)
        throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(baseUrl + "/events/" + groupingId + "/list-events");

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        List<ListEventsDomain> domain = null;

        if (expectedStatusCode == 200) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Got response:\n" +
                    jsonResponse);
            domain = objectMapper.readValue(jsonResponse,
                    new TypeReference<List<ListEventsDomain>>() {
                    });
        }

        client.close();
        return domain;
    }

    public static int RSVPAndExpect(String authToken, int eventId, int expectedStatusCode) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(baseUrl + "/events/" + eventId + "/RSVP");

        String json = objectMapper.writeValueAsString(eventId);
        System.out.println(json);

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));

        int returnId = -1;
        if (expectedStatusCode == 200) {
            returnId = Integer.parseInt(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }

        client.close();
        return returnId;
    }

    public static boolean checkIfLinkedAndExpect(String authToken, int expectedStatusCode) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(baseUrl + "/users/has-linked");

        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("authorization", "Bearer " + authToken);

        HttpResponse response = client.execute(httpGet);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(expectedStatusCode));
        boolean linked = false;

        if (expectedStatusCode == 200) {
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println("Got response:\n" +
                    jsonResponse);
            linked = objectMapper.readValue(jsonResponse,
                    new TypeReference<Boolean>() {
                    });
        }

        client.close();
        return linked;
    }
}
