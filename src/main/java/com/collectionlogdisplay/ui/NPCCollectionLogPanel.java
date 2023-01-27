package com.collectionlogdisplay.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.collectionlogdisplay.NPCCollectionLogPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class NPCCollectionLogPanel extends PluginPanel
{
    private final NPCCollectionLogPlugin plugin;

    private final JPanel contentPanel;

    private ResetButton clearButton;

    private JTextArea createTextArea(String text)
    {
        JTextArea textArea = new JTextArea(5, 22);
        textArea.setText(text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);

        return textArea;
    }
    @Inject
    public NPCCollectionLogPanel(NPCCollectionLogPlugin plugin)
    {
        super(false);
        this.plugin = plugin;

        JLabel title = new JLabel("NPC Log Info");
        title.setBorder(new EmptyBorder(0, 0, BORDER_OFFSET, 0));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET));
        add(title, BorderLayout.NORTH);
        JTextArea openLogMessage = createTextArea("This plugin is dependent on the collectionlog.net API. If you have the 'Collection Log' plugin, it will work.");
        contentPanel.add(openLogMessage);
        createButton();
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createButton()
    {
        clearButton = new ResetButton("Clear Panel");
        clearButton.setPreferredSize(new Dimension(PANEL_WIDTH, 30));
        clearButton.addMouseButton1PressedHandler(() -> plugin.displayPanel = false);
        contentPanel.add(clearButton, BorderLayout.NORTH);

    }




}