/*
 * Copyright (c) 2021, geheur <https://github.com/geheur>
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Ron Young <https://github.com/raiyni>
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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

import com.google.common.primitives.Shorts;
import com.collectionlogdisplay.NPCCollectionLogPlugin;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.QuantityFormatter;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.Point;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class BankTab
{
	private static final int ITEMS_PER_ROW = 8;
	private static final int ITEM_VERTICAL_SPACING = 36;
	private static final int ITEM_HORIZONTAL_SPACING = 48;
	private static final int ITEM_ROW_START = 51;
	private static final int LINE_VERTICAL_SPACING = 5;
	private static final int LINE_HEIGHT = 2;
	private static final int TEXT_HEIGHT = 15;
	private static final int ITEM_HEIGHT = 32;
	private static final int ITEM_WIDTH = 36;
	private static final int EMPTY_BANK_SLOT_ID = 6512;

	private static final int MAX_RESULT_COUNT = 250;

	private final ArrayList<Widget> addedWidgets = new ArrayList<>();

	private ArrayList<Integer> priorEvents = new ArrayList<>();

	boolean isSwappingDuplicates = false;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatMessageManager chatMessageManager;


	private final NPCCollectionLogPlugin questHelper;



	public BankTab(NPCCollectionLogPlugin questHelperPlugin)
	{
		questHelper = questHelperPlugin;
	}

	public void startUp()
	{

	}

	public void shutDown()
	{


	}

	@Subscribe
	public void onGrandExchangeSearched(GrandExchangeSearched event)
	{

	}

	public void updateGrandExchangeResults()
	{

	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired event)
	{

	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{

	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{

	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{

	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{

	}

}
