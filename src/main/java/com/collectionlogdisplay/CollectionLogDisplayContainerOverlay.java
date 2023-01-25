package com.collectionlogdisplay;

import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class CollectionLogDisplayContainerOverlay extends OverlayPanel {
    private final CollectionLogNPCDisplayPlugin plugin;
    private final Client client;
    private final ItemManager itemManager;
    private final CollectionLogDisplayPanelOverlay panel;
    private final CollectionLogDisplayInfoOverlay infoPanel;
    @Inject
    public CollectionLogDisplayContainerOverlay(CollectionLogNPCDisplayPlugin plugin, Client client, ItemManager itemManager, CollectionLogDisplayPanelOverlay panel, CollectionLogDisplayInfoOverlay infoPanel) {
        this.infoPanel = infoPanel;
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.client = client;
        this.itemManager = itemManager;
        this.panel = panel;
        panelComponent.setOrientation(ComponentOrientation.VERTICAL);
        panelComponent.setWrap(true);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.getDisplayPanel() && infoPanel != null && panel != null) {
            return null;
        } else {
            panelComponent.getChildren().add(infoPanel.getPanelComponent());
            panelComponent.getChildren().add(panel.getPanelComponent());
            return super.render(graphics);
        }
    }
}