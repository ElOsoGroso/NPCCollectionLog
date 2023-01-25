package com.collectionlogdisplay;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ItemInfoBox extends InfoBox {
    private String name;
    private Color renderColor;
    public ItemInfoBox(BufferedImage image, CollectionLogNPCDisplayPlugin plugin, String name, Color renderColor) {
        super(image, plugin);
        this.name = name;
        this.renderColor = renderColor;
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
        return name;
    }
    @Override
    public Color getTextColor() {
        return null;
    }
}
