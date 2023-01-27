package com.collectionlogdisplay;

import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ItemInfoBox extends InfoBox {
    private String name;
    private Color renderColor;
    private String rarity;
    public ItemInfoBox(BufferedImage image, NPCCollectionLogPlugin plugin, String name, Color renderColor, String rarity) {
        super(image, plugin);
        this.name = name;
        this.renderColor = renderColor;
        this.rarity = rarity;
    }

    public Color getRenderColor(){
        return renderColor;
    }
    @Override
    public String getText() {
        return null;
    }
    @Override
    public String getTooltip() {
        return name + ": " + rarity;
    }
    @Override
    public Color getTextColor() {
        return null;
    }
}
