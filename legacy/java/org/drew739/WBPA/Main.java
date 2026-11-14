/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA;

import org.drew739.WBPA.gui.GUI;
import org.drew739.WBPA.gui.HighScore;
import org.drew739.WBPA.level.Level;
import org.drew739.WBPA.menu.IntroMenu;
import org.drew739.WBPA.menu.Menu;
import org.drew739.WBPA.utils.Input;
import org.drew739.WBPA.utils.Render;
import org.jbox2d.common.Vec2;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {
    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;
    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    public static int OBJECT_SCALE = 15;
    public static final String NAME = "Widener's Baby Punting Adventure";
    private long previousTime = 0L;
    private double unprocessedSeconds = 0.0;
    private double secondsPerTick = 0.02;
    private int tickCount = 0;
    private int frames = 0;
    public static int FPS = 0;
    public static Level level;
    public static Render render;
    private Input input;
    public static GUI gui;
    public static Menu menu;
    public static HighScore hs;

    public void startGame() {
        render = new Render();
        level = new Level(new Vec2(0.0f, -9.8f), true);
        this.input = new Input();
        gui = new GUI();
        hs = new HighScore();
        System.out.println("High score: " + Main.hs.score);
        level.init();
        System.out.println("Starting game...");
        menu = new IntroMenu();
        this.gameLoop();
    }

    public void gameLoop() {
        this.previousTime = System.nanoTime();
        while (!Display.isCloseRequested()) {
            long currentTime = System.nanoTime();
            long passedTime = currentTime - this.previousTime;
            this.previousTime = currentTime;
            this.unprocessedSeconds += (double)passedTime / 1.0E9;
            while (this.unprocessedSeconds > this.secondsPerTick) {
                this.tick();
                this.unprocessedSeconds -= this.secondsPerTick;
                ++this.tickCount;
                if (this.tickCount % 50 == 0) {
                    FPS = this.frames;
                    this.frames = 0;
                    this.previousTime += 1000L;
                }
                render.draw();
                Display.update();
                ++this.frames;
            }
        }
        Main.destroy();
    }

    public static void restart() {
        gui.reset();
        render.reset();
        level.reset();
    }

    public void tick() {
        this.input.tick();
        if (menu != null) {
            menu.tick();
        } else {
            level.tick();
            gui.tick();
        }
    }

    public static void playAgain() {
        Main.restart();
        level = new Level(new Vec2(0.0f, -9.8f), true);
        gui = new GUI();
        level.init();
    }

    public static void destroy() {
        hs.save();
        render.destroy();
        Display.destroy();
        System.out.println("Closing game...");
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle(NAME);
            Display.setResizable(false);
            Display.create();
        }
        catch (LWJGLException e) {
            e.printStackTrace();
        }
        new Main().startGame();
    }
}

