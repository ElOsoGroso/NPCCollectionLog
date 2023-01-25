package com.collectionlogdisplay;

import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxOverlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.awt.*;

public class CollectionLogDisplayPanelOverlay extends OverlayPanel {
    private final CollectionLogNPCDisplayPlugin plugin;
    private final CollectionLogNPCDisplayConfig config;

    private final Client client;
    private final ItemManager itemManager;
    private final InfoBoxManager infoBoxManager;
    private final TooltipManager tooltipManager;
    @Inject
    public CollectionLogDisplayPanelOverlay(CollectionLogNPCDisplayPlugin plugin, CollectionLogNPCDisplayConfig config, Client client, ItemManager itemManager, InfoBoxManager infoBoxManager, TooltipManager tooltipManager) {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.itemManager = itemManager;
        this.infoBoxManager = infoBoxManager;
        this.tooltipManager = tooltipManager;
//        this.infoBoxOverlay = infoBoxOverlay;
        setClearChildren(false);
        panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        panelComponent.setWrap(true);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.getDisplayPanel() || plugin.getItemList().size() == 0) {
            return null;
        } else {
            panelComponent.setPreferredSize(new Dimension(4 * (32 +1), 4 * (32 + 1)));
            final Dimension preferredSize = new Dimension(32, 32);

            for (WikiItem wi : plugin.getItemList()){
                InfoBoxComponent infoBoxComponent = new InfoBoxComponent();
                ItemInfoBox box = new ItemInfoBox(itemManager.getImage(wi.getId()),plugin, wi.getName(),plugin.getRenderColorByIDCompare(wi.getId(),plugin.getCollectionLog().getCollLogItems()));
                box.setTooltip(wi.getName());
                infoBoxComponent.setText(box.getText());
                infoBoxComponent.setColor(box.getRenderColor());
                infoBoxComponent.setBackgroundColor(box.getRenderColor());
                infoBoxComponent.setImage(box.getImage());
                infoBoxComponent.setTooltip(box.getTooltip());
                infoBoxComponent.setPreferredSize(preferredSize);
                infoBoxComponent.setInfoBox(box);
                panelComponent.getChildren().add(infoBoxComponent);
            }
            final Dimension dimension = super.render(graphics);

            // Handle tooltips
            final Point mouse = new Point(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY());

            for (final LayoutableRenderableEntity child : panelComponent.getChildren())
            {
                final InfoBoxComponent component = (InfoBoxComponent) child;

                // Create intersection rectangle
                final Rectangle intersectionRectangle = new Rectangle(component.getBounds());
                intersectionRectangle.translate(getBounds().x, getBounds().y);

                if (intersectionRectangle.contains(mouse))
                {
                    final String tooltip = component.getTooltip();
                    if (!StringUtils.isEmpty(tooltip))
                    {
                        tooltipManager.add(new Tooltip(tooltip));
                    }

                    break;
                }
            }

            panelComponent.getChildren().clear();
            return dimension;
        }
    }
}