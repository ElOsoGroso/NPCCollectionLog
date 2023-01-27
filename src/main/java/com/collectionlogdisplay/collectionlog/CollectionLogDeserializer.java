package com.collectionlogdisplay.collectionlog;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;

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
            }
        }
        return new CollectionLog(
                newItems
        );
    }
}