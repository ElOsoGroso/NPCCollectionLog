package com.collectionlogdisplay;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class CollectionLogDisplayInfoOverlay extends OverlayPanel {
    private final CollectionLogNPCDisplayPlugin plugin;
    private final Client client;
    private final ItemManager itemManager;
    @Inject
    public CollectionLogDisplayInfoOverlay(CollectionLogNPCDisplayPlugin plugin, Client client, ItemManager itemManager) {
        this.plugin = plugin;
        this.client = client;
        this.itemManager = itemManager;
        panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        panelComponent.setWrap(true);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.getDisplayPanel()) {
            return null;
        } else {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(" Log Items Missing")
                    .color(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Possible Items: ").right("69").build());
            return super.render(graphics);
        }
    }
}