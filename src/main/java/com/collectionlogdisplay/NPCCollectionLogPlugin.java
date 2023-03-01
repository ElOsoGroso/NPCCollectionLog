package com.collectionlogdisplay;

import com.collectionlogdisplay.collectionlog.CollectionLog;
import com.collectionlogdisplay.collectionlog.CollectionLogApiClient;
import com.collectionlogdisplay.collectionlog.CollectionLogItem;
import com.collectionlogdisplay.ui.NPCCollectionLogPanel;
import com.collectionlogdisplay.wiki.WikiItem;
import com.collectionlogdisplay.wiki.WikiScraper;
import com.google.inject.Provides;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankSearch;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;

import static net.runelite.client.util.Text.sanitize;

@Slf4j
@PluginDescriptor(
		name = "NPC Collection Log",
		description = "When entering combat with an NPC, a display of the possible drops and which ones you're missing will appear",
		tags = {"display", "overlay", "collection", "log"}
)
public class NPCCollectionLogPlugin extends Plugin
{
	@Getter(AccessLevel.PACKAGE)
	private CollectionLog collectionLog;
	@Getter(AccessLevel.PACKAGE)
	private boolean panelInitialized = false;
	@Getter(AccessLevel.PACKAGE)
	private ArrayList<WikiItem> collectionLogItemList;
	@Getter(AccessLevel.PACKAGE)
	private ArrayList<WikiItem> nonCollLogItemList;
	@Getter(AccessLevel.PACKAGE)
	public ArrayList<Item> itemIdList= new ArrayList<>();
	@Getter(AccessLevel.PACKAGE)
	public boolean displayPanel;
	@Getter(AccessLevel.PACKAGE)
	public boolean collectionLogLoaded;
	@Getter(AccessLevel.PACKAGE)
	public boolean itemsLoaded;
	@Getter(AccessLevel.PACKAGE)
	public int greenTotal;
	@Getter(AccessLevel.PACKAGE)
	public int bothTotal;
	@Getter(AccessLevel.PACKAGE)
	public int bankGreenTotal;
	@Getter(AccessLevel.PACKAGE)
	public int bankBothTotal;
	@Getter(AccessLevel.PACKAGE)
	public boolean totalsSet;
	@Getter(AccessLevel.PACKAGE)
	public String previousNPCName;

	@Getter
	public net.runelite.api.Item[] inventoryItems;
	@Getter
	public ArrayList<net.runelite.api.Item> bankAndInventory;
	@Getter(AccessLevel.PACKAGE)
	public boolean bankTotalsSet;
	@Getter(AccessLevel.PACKAGE)
	public String currentNPC;

	@Inject
	private EventBus eventBus;

	@Inject
	@Getter
	private Bank bank;
	@Inject
	@Getter
	@Setter
	private Client client;
	@Inject
	private NPCCollectionLogConfig config;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private ItemManager itemManager;
	private boolean displayNameKnown;
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NPCCollectionLogDropsOverlay collLogOverlay;
	@Inject
	private NPCCollectionLogBankDropsOverlay bankLogOverlay;
	@Inject
	private NPCCollectionLogPanel panel;
	@Inject
	private CollectionLogApiClient apiClient;
	@Inject
	private ClientToolbar clientToolbar;
	@Getter
	private int lastTickInventoryUpdated = -1;
	@Getter
	private int lastTickBankUpdated = -1;
	private NavigationButton navigationButton;

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	@Provides
	NPCCollectionLogConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NPCCollectionLogConfig.class);
	}

	@Override
	public void startUp(){
		displayNameKnown = false;
		overlayManager.add(collLogOverlay);
		overlayManager.add(bankLogOverlay);
		displayPanel = false;
		collectionLogLoaded = false;
		itemsLoaded = false;
		itemIdList.clear();
		bothTotal = 0;
		greenTotal = 0;

		panel = new NPCCollectionLogPanel(this);

		navigationButton = NavigationButton
				.builder()
				.tooltip("NPC Collection Log")
				.icon(ImageUtil.loadImageResource(getClass(), "/nav-icon.png"))
				.priority(1001)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navigationButton);

	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(collLogOverlay);
		overlayManager.remove(bankLogOverlay);
		displayPanel = false;
		collectionLogLoaded = false;
		clientToolbar.removeNavigation(navigationButton);
		itemsLoaded = false;
		itemIdList.clear();
		bothTotal = 0;
		greenTotal = 0;
	}
	public void getCombinedBankInventory(){
		bankAndInventory = new ArrayList<net.runelite.api.Item>();
		for(net.runelite.api.Item item : bank.getBankItems()){
			bankAndInventory.add(item);
		}
		for(net.runelite.api.Item item : inventoryItems){
			if(!bankAndInventory.contains(item)){
				bankAndInventory.add(item);
			}
		}
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

	private boolean collectionLogLookup(String username)
	{
		try
		{
			this.collectionLog = apiClient.getCollectionLog(sanitize(username));
//			for (CollectionLogItem cli : this.collectionLog.getCollLogItems()){
//				log.info(cli.getName()+Integer.toString(cli.getId()));
//			}
			return true;
		}
		catch (IOException e)
		{
			return false;
		}

	}

	public Color getRenderColorByIDCompare(int ID, ArrayList<CollectionLogItem> collLogItems) {
		for (CollectionLogItem cli : collLogItems) {
			if (cli.getId() == ID) {
				if (cli.isObtained()) {
					return config.collectionLogObtainedColor();
				}
				else{
					return config.collectionLogMissingColor();
				}
			}
		}
		return config.collectionLogMissingColor();
	}

	public Color getBankRenderColorByIDCompare(int ID, List<net.runelite.api.Item> bankItems) {
		for (net.runelite.api.Item item : bankItems) {

				if (ID == item.getId()) {
					return config.bankLogObtainedColor();
				}
			}
		return config.bankLogMissingColor();
	}

	public void setTotalsCollLog(ArrayList<WikiItem> itemList, ArrayList<CollectionLogItem> collLogItems){
		bothTotal = greenTotal = 0;
		for(WikiItem wi : itemList){
			for (CollectionLogItem cli : collLogItems) {
				if (cli.getId() == wi.getId()) {
					bothTotal++;
					if (cli.isObtained()) {
						greenTotal++;
					}
				}
			}
		}
	}

	public void setTotalsNormal(ArrayList<WikiItem> itemList, List<net.runelite.api.Item> bankItems){
		bankGreenTotal = bankBothTotal = 0;
		for(WikiItem wi : itemList){
			for (net.runelite.api.Item cli : bankItems) {
				if (cli.getId() == wi.getId()) {
					bankGreenTotal++;
				}
			}
			bankBothTotal++;
		}
	}

	@Subscribe
	public void onGameStateChanged(final GameStateChanged gameStateChanged) {
		final GameState state = gameStateChanged.getGameState();

		if (!itemsLoaded && gameStateChanged.getGameState() != GameState.LOGGED_IN)
		{
			for(int i = 0; i< client.getItemCount(); i++){
				ItemComposition ic = client.getItemDefinition(i);
				if(ic!=null){
					itemIdList.add(new Item(ic.getId(),ic.getName()));
				}
			}
			displayNameKnown = false;
			itemsLoaded = true;
			log.info("Loaded the itemIDs");
		}
		if (state == GameState.LOGIN_SCREEN)
		{
			bank.saveBankToConfig();
			bank.emptyState();
		}

	}
	@Subscribe(priority = 100)
	private void onClientShutdown(ClientShutdown e)
	{
		bank.saveBankToConfig();
	}
	@Subscribe
	public void onGameTick(GameTick event)
	{
		if(!StringUtils.isEmpty(previousNPCName)){
			boolean found = false;
			for (NPC npc : client.getNpcs()){
				if(npc.getName().equals(previousNPCName)){
					found = true;
				}
			}
			if(!found)
				displayPanel = false;
		}
		if (!displayNameKnown)
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer != null && localPlayer.getName() != null)
			{
				displayNameKnown = true;
				bank.loadState();
			}
		}

	}
	@Subscribe
	public void onPlayerSpawned(PlayerSpawned playerSpawned) throws IllegalAccessException {
		if(!collectionLogLoaded && client.getLocalPlayer() == playerSpawned.getPlayer() && collectionLogExists(String.valueOf(client.getAccountHash()))){
			boolean success = collectionLogLookup(playerSpawned.getPlayer().getName());
			if (success) collectionLogLoaded = true;
			bank.loadState();
		}

	}

	public void splitListByType(ArrayList<WikiItem> wikiList){
		boolean found = false;
		collectionLogItemList.clear();
		nonCollLogItemList.clear();
		for(WikiItem wi : wikiList){
			found = false;
			for(CollectionLogItem cli : getCollectionLog().getCollLogItems().stream().filter(distinctByKey(CollectionLogItem::getId)).collect(Collectors.toList())){
				if(wi.getId() == cli.getId()){
					collectionLogItemList.add(wi);
					found = true;
				}
			}
			if(!found)
				nonCollLogItemList.add(wi);
		}

	}


	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() == client.getItemContainer(InventoryID.BANK))
		{
			lastTickBankUpdated = client.getTickCount();
			bank.updateLocalBank(event.getItemContainer().getItems());
		}

		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY))
		{
			lastTickInventoryUpdated = client.getTickCount();
			if(bank.getBankItems().size()>0) {
				ItemContainer container = event.getItemContainer();
				inventoryItems = container.getItems();
				getCombinedBankInventory();
				setTotalsNormal(this.nonCollLogItemList, bankAndInventory);
			}
		}
	}
	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		panelInitialized = false;
		if (!(event.getSource() instanceof NPC)) {
			return;
		}

		final NPC sourceNpc = (NPC) event.getSource();

		// the NPC is fighting us
		if (event.getTarget() == client.getLocalPlayer() && sourceNpc.getCombatLevel() > 0) {
			if(sourceNpc.getName() != null)
				previousNPCName = sourceNpc.getName();
			//If the list is null, make it empty, if it is the same as the previous npc we don't want to reload
			if(collectionLogItemList == null){
				greenTotal = bothTotal = bankGreenTotal = bankBothTotal = 0;
				collectionLogItemList = new ArrayList<WikiItem>();
			}
			else if (!StringUtils.isEmpty(previousNPCName) && sourceNpc.getName().equals(previousNPCName)){
				//do nothing
			}

			if(nonCollLogItemList == null){
				greenTotal = bothTotal = bankGreenTotal = bankBothTotal = 0;
				nonCollLogItemList = new ArrayList<WikiItem>();
			}
			else if (!StringUtils.isEmpty(previousNPCName) && sourceNpc.getName().equals(previousNPCName)){
				//do nothing
			}

			// panel's already up, don't do it again
			if (panelInitialized) {
				return;
			}
			log.info("Getting item list for: " +sourceNpc.getName());
			currentNPC = sourceNpc.getName();
			infoBoxManager.removeIf(c -> c instanceof ItemInfoBox);

			WikiScraper.getDropsByMonster(sourceNpc.getName(), sourceNpc.getId(),itemIdList).whenCompleteAsync((wikiItemsList, ex) -> {
				displayPanel = true;
				splitListByType(wikiItemsList);
				Predicate<WikiItem> condition = x -> x.getName().equals("Nothing")
						|| x.getName().toLowerCase().contains(("clue"))
						|| x.getName().toLowerCase().contains(("coins"))
						|| x.getName().toLowerCase().contains(("bones")) //TODO: make this into a separate list
						|| x.getName().toLowerCase().contains(("(medium)"))
						|| x.getName().toLowerCase().contains(("(easy)"))
						|| x.getName().toLowerCase().contains(("seed"))
						|| x.getName().toLowerCase().contains(("raw beef"))
						|| x.getName().toLowerCase().contains(("grimy"))
						|| (x.getName().toLowerCase().split(" ").length == 2
							&& x.getName().toLowerCase().split(" ")[1].contains("rune"));
				this.collectionLogItemList.removeIf(condition);
				this.nonCollLogItemList.removeIf(condition);

				ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
				this.inventoryItems = container.getItems();
				if(bank.getBankItems().size()==0){
					bank.loadState();
				}
				getCombinedBankInventory();

				setTotalsCollLog(this.collectionLogItemList,getCollectionLog().getCollLogItems());
				setTotalsNormal(this.nonCollLogItemList,bankAndInventory);

				panelInitialized = true;
			});
		}

	}

}