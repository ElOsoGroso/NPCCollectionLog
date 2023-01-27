package com.collectionlogdisplay;

import com.collectionlogdisplay.wiki.WikiItem;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NPCCollectionLogDropsOverlay extends OverlayPanel {
    private final NPCCollectionLogPlugin plugin;
    private final NPCCollectionLogConfig config;

    private final Client client;
    private final ItemManager itemManager;
    private final InfoBoxManager infoBoxManager;
    private final TooltipManager tooltipManager;
    private final PanelComponent titlePanel = new PanelComponent();
    private final PanelComponent infoBoxPanel = new PanelComponent();
    private final PanelComponent gapPanel1 = new PanelComponent();
    private final PanelComponent gapPanel2 = new PanelComponent();

    private SplitComponent gapAndInfo;
    private SplitComponent titleComponent;
    private SplitComponent panelSplitter;
    private SplitComponent titleAndLines;
    private SplitComponent gapsAndInfo;
    private TitleComponent title;
    private TitleComponent npcLine;
    private LineComponent obtainedItems;


    @Inject
    public NPCCollectionLogDropsOverlay(NPCCollectionLogPlugin plugin, NPCCollectionLogConfig config, Client client, ItemManager itemManager, InfoBoxManager infoBoxManager, TooltipManager tooltipManager) {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        this.itemManager = itemManager;
        this.infoBoxManager = infoBoxManager;
        this.tooltipManager = tooltipManager;
        setClearChildren(false);
        infoBoxPanel.setOrientation(ComponentOrientation.HORIZONTAL);
        infoBoxPanel.setWrap(true);
        infoBoxPanel.setBorder(new Rectangle(2,1,4,0));
        infoBoxPanel.setBackgroundColor(null);
        panelComponent.setBorder(new Rectangle(2,2,2,2));
        titlePanel.setWrap(false);
        titlePanel.setBorder(new Rectangle(2,1,4,0));
        titlePanel.setBackgroundColor(null);

    }
    public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight)
    {
        BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = scaledImage.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return scaledImage;
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isDisplayPanel()|| plugin.getCollectionLogItemList().size() == 0) {
            return null;
        } else {
            title =  TitleComponent.builder()
                    .text("    Collection Log Info")
                    .color(Color.GREEN)
                    .build();
            npcLine = TitleComponent.builder()
                    .text("  ("+plugin.getCurrentNPC()+")")
                    .color(Color.GREEN)
                    .build();
            obtainedItems = (plugin.getGreenTotal() < plugin.getBothTotal()) ?
                    LineComponent.builder()
                        .left(" Obtained Items: ")
                        .leftColor(Color.white)
                        .right(plugin.getGreenTotal() + "/" + plugin.getBothTotal())
                        .rightColor(Color.WHITE).build()
                    : LineComponent.builder()
                        .left(" Obtained Items: ")
                        .leftColor(Color.white)
                        .right(plugin.getGreenTotal() + "/" + plugin.getBothTotal())
                        .rightColor(Color.GREEN).build()
            ;


            if(plugin.getCollectionLogItemList().size() < 4) {
                for (WikiItem wi : plugin.getCollectionLogItemList()) {
                    InfoBoxComponent infoBoxComponent = new InfoBoxComponent();
                    ItemInfoBox box = new ItemInfoBox(itemManager.getImage(wi.getId()),
                            plugin, wi.getName(),
                            plugin.getRenderColorByIDCompare(wi.getId(),
                            plugin.getCollectionLog().getCollLogItems()), wi.getRarityStr());
                    infoBoxComponent.setText(box.getText());
                    infoBoxComponent.setColor(box.getRenderColor());
                    infoBoxComponent.setBackgroundColor(box.getRenderColor());
                    infoBoxComponent.setImage(box.getImage());
                    infoBoxComponent.setTooltip(box.getTooltip());
                    infoBoxComponent.setInfoBox(box);
                    infoBoxPanel.getChildren().add(infoBoxComponent);
                }
                for (int i = 0; i<4-plugin.getCollectionLogItemList().size();i++){
                    InfoBoxComponent infoBoxComponent = new InfoBoxComponent();
                    ItemInfoBox box = new ItemInfoBox(ImageUtil.loadImageResource(getClass(), "/transparent.png"), plugin, null, new Color(0f,0f,0f,0f ), null);
                    infoBoxComponent.setText(box.getText());
                    infoBoxComponent.setColor(box.getRenderColor());
                    infoBoxComponent.setBackgroundColor(box.getRenderColor());
                    infoBoxComponent.setImage(box.getImage());
                    infoBoxComponent.setInfoBox(box);
                    infoBoxPanel.getChildren().add(infoBoxComponent);
                }
            }
            else {
                for (WikiItem wi : plugin.getCollectionLogItemList()) {
                    InfoBoxComponent infoBoxComponent = new InfoBoxComponent();
                    ItemInfoBox box = new ItemInfoBox(itemManager.getImage(wi.getId()), plugin, wi.getName(), plugin.getRenderColorByIDCompare(wi.getId(), plugin.getCollectionLog().getCollLogItems()), wi.getRarityStr());
                    infoBoxComponent.setText(box.getText());
                    infoBoxComponent.setColor(box.getRenderColor());
                    infoBoxComponent.setBackgroundColor(box.getRenderColor());
                    infoBoxComponent.setImage(box.getImage());
                    infoBoxComponent.setTooltip(box.getTooltip());
                    infoBoxComponent.setInfoBox(box);
                    infoBoxPanel.getChildren().add(infoBoxComponent);
                }
            }

            titleComponent = SplitComponent.builder()
                    .first(title)
                    .second(npcLine)
                    .orientation(ComponentOrientation.VERTICAL)
                    .build();
            titleAndLines = SplitComponent.builder()
                    .first(titleComponent)
                    .second(obtainedItems)
                    .orientation(ComponentOrientation.VERTICAL)
                    .build();

            titlePanel.getChildren().add(titleAndLines);


            panelSplitter = SplitComponent.builder()
                            .first(titlePanel)
                            .second(infoBoxPanel)
                            .gap(new Point(0,5))
                            .build();
            panelComponent.getChildren().add(panelSplitter);

            final Dimension dimension = super.render(graphics);

            // Handle tooltips // from the runelite's InfoBoxOverlay.java code
            final Point mouse = new Point(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY());

            for (final LayoutableRenderableEntity child : infoBoxPanel.getChildren())
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

//            gapPanel1.getChildren().clear();
//            gapPanel2.getChildren().clear();
            infoBoxPanel.getChildren().clear();
            titlePanel.getChildren().clear();
            panelComponent.getChildren().clear();
            plugin.totalsSet = true;
            return dimension;
        }
    }
}