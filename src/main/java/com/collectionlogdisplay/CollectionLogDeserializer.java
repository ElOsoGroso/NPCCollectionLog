package com.collectionlogdisplay;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionLogDeserializer implements JsonDeserializer<CollectionLog>
{
    @Override
    public CollectionLog deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObjectLog = json.getAsJsonObject().get("collectionLog").getAsJsonObject();
        JsonObject jsonObjectTabs  = jsonObjectLog.get("tabs").getAsJsonObject();
        ArrayList<CollectionLogItem> newItems = new ArrayList<>();

        for (String tabKey : jsonObjectTabs.keySet())
        {
            JsonObject tab = jsonObjectTabs.get(tabKey).getAsJsonObject();

            for (String pageKey : tab.keySet())
            {
                JsonObject page = tab.get(pageKey).getAsJsonObject();

                for (JsonElement item : page.get("items").getAsJsonArray())
                {
                    CollectionLogItem newItem = context.deserialize(item, CollectionLogItem.class);
                    newItems.add(newItem);
                }
                JsonElement pageKillCounts = page.get("killCount");

            }
        }
        return new CollectionLog(
                jsonObjectLog.get("username").getAsString(),
                jsonObjectLog.get("totalObtained").getAsInt(),
                jsonObjectLog.get("totalItems").getAsInt(),
                jsonObjectLog.get("uniqueObtained").getAsInt(),
                jsonObjectLog.get("uniqueItems").getAsInt(),
                newItems
        );
    }
}