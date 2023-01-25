package com.collectionlogdisplay;

import com.google.gson.Gson;
import com.google.inject.Provides;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxOverlay;

import static net.runelite.client.util.Text.sanitize;

@Slf4j
@PluginDescriptor(
		name = "Ground Markers Images",
		description = "Enable marking of tiles using the Shift key",
		tags = {"overlay", "tiles"}
)
public class CollectionLogNPCDisplayPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "groundMarker";
	private static final String MARK = "PRETTY PICTURE tile";
	private static final String UNMARK = "UNPRETTY PICTURE tile";
	private static final String LABEL = "Label tile";
	private static final String WALK_HERE = "Walk here";
	private static final String REGION_PREFIX = "region_";


	@Getter(AccessLevel.PACKAGE)
	private CollectionLog collectionLog;

	@Getter(AccessLevel.PACKAGE)
	private boolean panelInitialized = false;

	@Getter(AccessLevel.PACKAGE)
	private ArrayList<WikiItem> itemList;
	@Getter(AccessLevel.PACKAGE)
	public ArrayList<Item> itemIdList= new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	public boolean displayPanel;

	@Inject
	private Client client;

	@Getter
	@Inject
	private ClientThread clientThread;
	@Inject
	private CollectionLogNPCDisplayConfig config;
	@Inject
	private ItemManager itemManager;
	@Inject
	private ConfigManager configManager;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private CollectionLogDisplayInfoOverlay overlay;
	@Inject
	private CollectionLogDisplayPanelOverlay paneloverlay;
	@Inject
	private CollectionLogDisplayContainerOverlay containeroverlay;
	@Inject
	private ChatboxPanelManager chatboxPanelManager;
	@Inject
	private CollectionLogApiClient apiClient;
	@Inject
	private EventBus eventBus;

	@Inject
	private Gson gson;


	@Provides
	CollectionLogNPCDisplayConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CollectionLogNPCDisplayConfig.class);
	}


	@Override
	public void startUp(){
		overlayManager.add(overlay);
		overlayManager.add(paneloverlay);
		overlayManager.add(containeroverlay);
		displayPanel = false;
		itemIdList.clear();

	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(paneloverlay);
		overlayManager.remove(containeroverlay);
		displayPanel = false;
		itemIdList.clear();
	}
	private boolean collectionLogExists(String accountHash)
	{
		try
		{
			return apiClient.getCollectionLogExists(accountHash);
		}
		catch (IOException e) {
			log.info("Unable to get existing collection log from collectionlog.net");
		}
		return false;
	}

	private void collectionLogLookup()
	{
		Player localPlayer = client.getLocalPlayer();
		String username = localPlayer.getName();

		try
		{
			this.collectionLog = apiClient.getCollectionLog(sanitize("Nick Dipplez"));
			for (CollectionLogItem cli : this.collectionLog.getCollLogItems()){
				log.info(cli.getName()+Integer.toString(cli.getId()));
			}
		}
		catch (IOException e)
		{
			return;
		}

	}

	public Color getRenderColorByIDCompare(int ID, ArrayList<CollectionLogItem> collLogItems) {
		for (CollectionLogItem cli : collLogItems) {
			if (cli.getId() == ID) {
				if (cli.isObtained()) {
					return Color.GREEN;
				}
				else{
					return Color.RED;
				}
			}
		}
		return Color.BLACK;

	}
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) throws FileNotFoundException {
		if (gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", new File("").getAbsolutePath() +"\\src\\main\\java\\com\\collectionlogdisplay\\itemIds.txt", null);

			Scanner scanner = new Scanner(new FileReader(new File("").getAbsolutePath() +"\\src\\main\\java\\com\\collectionlogdisplay\\itemIds.txt"));

			String line;
			while(scanner.hasNext()){
				line=scanner.nextLine();
				String split[] = line.split(" = ");
				Item item = new Item(Integer.parseInt(split[1]),split[0]);
				itemIdList.add(item);
			}
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "DONE: " +String.valueOf(itemIdList.size()), null);
		}
		else if (gameStateChanged.getGameState() == GameState.LOGGED_IN){
			if(collectionLogExists(String.valueOf(client.getAccountHash())))
				collectionLogLookup();
		}

	}
	public boolean getDisplayPanel(){
		return displayPanel;
	}
	public BufferedImage getImageTest(){
		return itemManager.getImage(2);
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		panelInitialized = false;
//		displayPanel = false;
		if (!(event.getSource() instanceof NPC)) {
			return;
		}

		final NPC sourceNpc = (NPC) event.getSource();

		// the NPC is fighting us
		if (event.getTarget() == client.getLocalPlayer()) {
			itemList = new ArrayList<WikiItem>();

			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "ITEMLIST---", "Started an interaction with an npc", null);

			// panel's already up, don't do it again
			if (panelInitialized) {
				return;
			}
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "ITEMLIST---", "Getting item list for:" +sourceNpc.getName(), null);
			infoBoxManager.removeIf(c -> c instanceof ItemInfoBox);

			WikiScraper.getDropsByMonster(sourceNpc.getName(), sourceNpc.getId(),itemIdList).whenCompleteAsync((wikiItemsList, ex) -> {
				displayPanel = true;
				this.itemList = wikiItemsList;
				Predicate<WikiItem> condition = x -> x.getName().equals("Nothing") || x.getName().toLowerCase().contains(("clue")) || x.getName().toLowerCase().contains(("coins"));
				this.itemList.removeIf(condition);

				for (WikiItem wi : itemList){
					log.info(wi.getName());
				}

				panelInitialized = true;
			});


			return;
		}

	}
	@Subscribe
	public void onNpcLootReceived(NpcLootReceived event){
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "ITEMLIST---", "Got loot", null);
		displayPanel = false;
	}


	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(CollectionLogNPCDisplayConfig.GROUND_MARKER_CONFIG_GROUP)
				&& (event.getKey().equals(CollectionLogNPCDisplayConfig.SHOW_IMPORT_EXPORT_KEY_NAME)
				|| event.getKey().equals(CollectionLogNPCDisplayConfig.SHOW_CLEAR_KEY_NAME)))
		{
		}
	}



}