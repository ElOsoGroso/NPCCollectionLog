/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.collectionlogdisplay;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.WorldType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfileType;

@Slf4j
@Singleton
public class Bank
{
    private final ConfigManager configManager;
    private final Client client;
    private final Gson gson;

    private static final String CONFIG_GROUP = "npclog";
    private static final String BANK_KEY = "bankitems";

    private List<Item> bankItems;
    private final BankData bankData;
    private String rsProfileKey;
    private RuneScapeProfileType worldType;

    public List<WorldType> worldTypes = Arrays.asList(WorldType.SEASONAL, WorldType.TOURNAMENT_WORLD,
            WorldType.DEADMAN, WorldType.NOSAVE_MODE);

    @Inject
    public Bank(Client client, ConfigManager configManager, Gson gson)
    {
        this.configManager = configManager;
        this.client = client;
        this.gson = gson;
        this.bankData = new BankData();
        this.bankItems = new ArrayList<>();
    }

    public List<Item> getBankItems()
    {
        return bankItems;
    }

    public void updateLocalBank(Item[] items)
    {
        bankData.set(items);
        bankItems = bankData.getAsList();
    }

    public void emptyState()
    {
        rsProfileKey = null;
        worldType = null;
        bankData.setEmpty();
        bankItems = new ArrayList<>();
    }

    public void loadState()
    {
        // Only re-load from config if loading from a new profile
        if (!RuneScapeProfileType.getCurrent(client).equals(worldType))
        {
            // If we've hopped between profiles
            if (rsProfileKey != null)
            {
                saveBankToConfig();
            }
            loadBankFromConfig();
        }
    }

    private void loadBankFromConfig()
    {
        // Remove deprecated config
        configManager.unsetConfiguration(CONFIG_GROUP, getCurrentKey());

        rsProfileKey = configManager.getRSProfileKey();
        worldType = RuneScapeProfileType.getCurrent(client);

        String json = configManager.getRSProfileConfiguration(CONFIG_GROUP, BANK_KEY);
        try
        {
            bankData.setIdAndQuantity(gson.fromJson(json, int[].class));
        }
        catch (JsonSyntaxException err)
        {
            // Due to changing data format from list to array, need to handle for old users
            bankData.setIdAndQuantity(new int[0]);
            saveBankToConfig();
        }
        bankItems = bankData.getAsList();
    }

    public void saveBankToConfig()
    {
        if (rsProfileKey == null)
        {
            return;
        }

        configManager.setConfiguration(CONFIG_GROUP, rsProfileKey, BANK_KEY, gson.toJson(bankData.getIdAndQuantity()));
    }

    private String getCurrentKey()
    {
        StringBuilder key = new StringBuilder();
        EnumSet<WorldType> worldType = client.getWorldType();
        for (WorldType type : worldType)
        {
            if (worldTypes.contains(type))
                key.append(type.name()).append(":");
        }
        if (client.getLocalPlayer() == null)
        {
            return "NULL PLAYER";
        }
        key.append(client.getLocalPlayer().getName());
        return key.toString();
    }
}