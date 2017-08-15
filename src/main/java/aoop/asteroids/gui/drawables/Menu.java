package aoop.asteroids.gui.drawables;


import aoop.asteroids.gui.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Menu implements IDrawable, KeyListener{

    private final static int TITLE_SIZE = 50;
    private final static int TEXT_SIZE = 35;
    private final static int SPACING = 20;
    private Renderer parentPanel;

    @Override
    public void keyTyped(KeyEvent e) {/* We're not using this one */}

    @Override
    public void keyPressed(KeyEvent e) {/* We're not using this one */}

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP:
                prevItem();
                break;
            case KeyEvent.VK_DOWN:
                nextItem();
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                items.get(selected).select();
                break;
        }
    }

    public interface MenuEventCallback {
        void OnSelectChange(MenuItem menuItem);
    }

/*
    private class MenuItem{
        public MenuItem(String n, MenuEventCallback m) { name = n; menuEventCallback = m;}
        String name;
        MenuEventCallback menuEventCallback;
    }
*/


    private ArrayList<MenuItem> items;
    private int selected = 0;
    private String title;

    public Menu() { this(""); }

    public Menu (String title) {
        this.title = title;
        items = new ArrayList<>();
    }

    private void prevItem() {
        items.get(selected).setFocus(false);
        selected--;
        if(selected < 0) selected = items.size() - 1;
        items.get(selected).setFocus(true);
        if(parentPanel != null) parentPanel.repaint();
    }

    private void nextItem(){
        items.get(selected).setFocus(false);
        selected++;
        if(selected >= items.size()) selected = 0;
        items.get(selected).setFocus(true);
        if(parentPanel != null) parentPanel.repaint();
    }

    public void addItem(MenuItem menuItem) {
        if (items.size() == 0) menuItem.setFocus(true);
        menuItem.onRendererAttached(parentPanel);
        items.add(menuItem);
    }

    public void removeItem(String name) {
        for(MenuItem item : items) {
            if (item.getName().equals(name)){
                items.remove(item);
                return;
            }
        }
    }


    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        Rectangle clipBounds = g.getClipBounds();

        int height = 125; //(clipBounds.height / items.size()) - SPACING;
        if (title != null || !title.equals("")) height -= (TITLE_SIZE + (SPACING * 2)) / items.size();
        int width  = (clipBounds.width - (SPACING * 2));

        Font oldFont = g.getFont();
        if (title != null && !title.equals("")) {
            g.setFont(oldFont.deriveFont((float) TITLE_SIZE));
            FontMetrics fm = g.getFontMetrics();
            int xStr = (clipBounds.width / 2) - (fm.stringWidth(title) / 2);
            int yStr = SPACING  + (TITLE_SIZE);
            g.drawString(title, xStr, yStr);
        }
        g.setFont(oldFont.deriveFont((float) TEXT_SIZE));



        for (int i = 0; i < items.size(); i++) {
            int xBox = SPACING;
            int yBox = (SPACING * 2) + (i * (height + (SPACING)));
            if (title.equals("Highscores"))  {
                height = 400;
                g.setFont(g.getFontMetrics().getFont().deriveFont((float) 20));
                if (items.get(i) instanceof Button) {
                    yBox = (SPACING * 2) + (i * (height + (SPACING)));
                    height = 100;
                    g.setFont(oldFont.deriveFont((float) TEXT_SIZE));
                }
                g.setClip(xBox, TITLE_SIZE + yBox, width, height);
                items.get(i).draw(g);
            } else {
                g.setClip(xBox, TITLE_SIZE + yBox, width, height);
                items.get(i).draw(g);
            }

        }
        g.setFont(oldFont);
    }

    @Override
    public void onRendererAttached(Renderer parent) {
        parentPanel = parent;
        items.forEach((i) -> i.onRendererAttached(parent));
        if(parentPanel != null) parentPanel.repaint();
    }
}
