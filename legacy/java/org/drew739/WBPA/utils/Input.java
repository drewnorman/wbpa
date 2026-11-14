/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.utils;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.audio.Music;
import org.drew739.WBPA.menu.TitleMenu;
import org.lwjgl.input.Keyboard;

public class Input {
    public static int lastKeyPressed = 0;
    public static boolean[] key = new boolean[256];
    public static boolean space = false;
    public static boolean enter = false;
    public static boolean down = false;
    public static boolean up = false;
    public static int downCounter = 0;
    public static int upCounter = 0;
    public static boolean leftClickDragged = false;
    public static boolean leftClickDown = false;
    public static boolean leftClickUp = false;
    public static boolean rightClickDragged = false;
    public static boolean rightClickDown = false;
    public static boolean rightClickUp = false;
    public static int mouseX;
    public static int mouseY;

    public void tick() {
        while (Keyboard.next()) {
            Input.key[Keyboard.getEventKey()] = Keyboard.getEventKeyState();
        }
        enter = key[28];
        space = key[57];
        up = key[200];
        down = key[208];
        if (Main.menu != null) {
            if (Main.menu.type == 2) {
                TitleMenu menu = (TitleMenu)Main.menu;
                if (menu.textChanging) {
                    return;
                }
                if (up) {
                    ++upCounter;
                }
                if (down) {
                    ++downCounter;
                }
                if (menu.currentMenu == 0) {
                    if (upCounter >= 4) {
                        upCounter = 0;
                        --menu.selected;
                        if (menu.selected < 0) {
                            menu.selected = 0;
                        }
                    }
                    if (downCounter >= 4) {
                        downCounter = 0;
                        ++menu.selected;
                        if (menu.selected > 4) {
                            menu.selected = 4;
                        }
                    }
                    if (enter) {
                        if (menu.selected == 0) {
                            Main.menu = null;
                            enter = false;
                            Music.menuTheme.stop();
                            Music.outdoorAmbient.play();
                        } else if (menu.selected == 1) {
                            menu.nextMenu = 1;
                        } else if (menu.selected == 2) {
                            menu.nextMenu = 2;
                        } else {
                            Main.destroy();
                        }
                    }
                } else if ((menu.currentMenu == 1 || menu.currentMenu == 2) && enter) {
                    menu.nextMenu = 0;
                }
            } else if (Main.menu.type == 3) {
                if (enter) {
                    Main.playAgain();
                    Main.menu = null;
                }
                if (space) {
                    Main.playAgain();
                    Main.menu = new TitleMenu();
                }
            }
        } else {
            if (enter) {
                Main.gui.meter.firing = true;
            }
            if (space) {
                Main.gui.meter.firing = false;
                Main.level.getPlayer().firing = true;
            }
        }
        lastKeyPressed = Keyboard.getEventKey();
    }
}

