package com.collectionlogdisplay.collectionlog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Request;
import okhttp3.RequestBody;
//Part of the client from the Collection Log plugin on the plugin-hub
@Slf4j
@Singleton
public class CollectionLogApiClient
{
    private static final String COLLECTION_LOG_API_BASE = "api.collectionlog.net";
    private static final String COLLECTION_LOG_USER_PATH = "user";
    private static final String COLLECTION_LOG_LOG_PATH = "collectionlog";
    private static final String COLLECTION_LOG_JSON_KEY = "collection_log";
    private static final String COLLECTION_LOG_USER_AGENT = "Runelite collection-log/2.2";

    private static final String COLLECTION_LOG_TEMPLATE_BASE = "api.github.com";
    private static final String COLLECTION_LOG_TEMPLATE_USER = "gists";
    private static final String COLLECTION_LOG_TEMPLATE_GIST = "24179c0fbfb370ce162f69dde36d72f0";

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private Gson gson;

    public void createUser(String username, String accountType, String accountHash, boolean isFemale) throws IOException
    {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(COLLECTION_LOG_API_BASE)
                .addPathSegment(COLLECTION_LOG_USER_PATH)
                .build();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("account_type", accountType);
        jsonObject.addProperty("account_hash", accountHash);
        jsonObject.addProperty("is_female", isFemale);

        postRequest(url, jsonObject);
    }

    public boolean getCollectionLogExists(String accountHash) throws IOException
    {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(COLLECTION_LOG_API_BASE)
                .addPathSegment(COLLECTION_LOG_LOG_PATH)
                .addPathSegment("exists")
                .addPathSegment(accountHash)
                .build();

        JsonObject response = getRequest(url);
        if (response == null)
        {
            return false;
        }
        return response.get("exists").getAsBoolean();
    }

    public CollectionLog getCollectionLog(String username) throws IOException
    {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(COLLECTION_LOG_API_BASE)
                .addPathSegment(COLLECTION_LOG_LOG_PATH)
                .addPathSegment(COLLECTION_LOG_USER_PATH)
                .addEncodedPathSegment(username)
                .build();

        return gson.newBuilder()
                .registerTypeAdapter(CollectionLog.class, new CollectionLogDeserializer())
                .create()
                .fromJson(getRequest(url), CollectionLog.class);
    }

    private JsonObject getRequest(HttpUrl url) throws IOException
    {
        Request request = new Request.Builder()
                .header("User-Agent", COLLECTION_LOG_USER_AGENT)
                .url(url)
                .get()
                .build();

        return apiRequest(request);
    }

    private JsonObject postRequest(HttpUrl url, JsonObject postData) throws IOException
    {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, postData.toString());
        Request request = new Request.Builder()
                .header("User-Agent", COLLECTION_LOG_USER_AGENT)
                .url(url)
                .post(body)
                .build();

        return apiRequest(request);
    }
    private JsonObject apiRequest(Request request) throws IOException
    {
        Response response = okHttpClient.newCall(request).execute();
        JsonObject responseJson = processResponse(response);
        response.close();
        return responseJson;
    }

    private JsonObject processResponse(Response response) throws IOException
    {
        if (!response.isSuccessful())
        {
            return null;
        }

        ResponseBody resBody = response.body();
        if (resBody == null)
        {
            return null;
        }
        return new JsonParser().parse(resBody.string()).getAsJsonObject();
    }
}