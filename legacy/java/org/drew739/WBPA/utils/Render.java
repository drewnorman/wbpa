/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.drew739.WBPA.Main;
import org.drew739.WBPA.gameObjects.Ground;
import org.drew739.WBPA.level.Level;
import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Render {
    private static float INITIAL_TRANS_X = 0.0f;
    private static float INITIAL_TRANS_Y = 0.0f;
    public float currentTransX = INITIAL_TRANS_X;
    public float currentTransY = INITIAL_TRANS_Y;
    public static double SCALE = 0.35;
    public boolean resettingTransX = false;
    public boolean resettingTransY = false;
    private float translateXAmt = 0.0f;
    private float translateYAmt = 0.0f;
    public static Texture[] clouds;
    public static Texture ground;
    public static Texture can;
    public static Texture[] digitsFont;
    public static Texture score;
    public static Texture kicksLeft;
    public static Texture introScreen;
    public static Texture titleScreen;
    public static Texture instructionsScreen;
    public static Texture creditsScreen;
    public static Texture endScreen;
    public static Texture endScreenNewHigh;
    public static Texture widener;

    public Render() {
        GL11.glMatrixMode(5889);
        GL11.glOrtho(0.0, Main.WIDTH / 2, 0.0, Main.HEIGHT / 2, 1.0, -1.0);
        GL11.glMatrixMode(5888);
        GL11.glEnable(3042);
        GL11.glEnable(3008);
        GL11.glBlendFunc(770, 771);
        this.loadTextures();
    }

    public void loadTextures() {
        clouds = new Texture[4];
        digitsFont = new Texture[10];
        try {
            Render.clouds[0] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/sprites/cloud1.png")));
            Render.clouds[1] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/sprites/cloud2.png")));
            Render.clouds[2] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/sprites/cloud3.png")));
            Render.clouds[3] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/sprites/cloud4.png")));
            can = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/sprites/can.png")));
            ground = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/sprites/ground.png")));
            Render.digitsFont[0] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/zero.png")));
            Render.digitsFont[1] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/one.png")));
            Render.digitsFont[2] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/two.png")));
            Render.digitsFont[3] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/three.png")));
            Render.digitsFont[4] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/four.png")));
            Render.digitsFont[5] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/five.png")));
            Render.digitsFont[6] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/six.png")));
            Render.digitsFont[7] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/seven.png")));
            Render.digitsFont[8] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/eight.png")));
            Render.digitsFont[9] = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/nine.png")));
            score = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/score.png")));
            kicksLeft = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/fonts/kicks-left.png")));
            introScreen = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/screens/intro-screen.png")));
            titleScreen = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/screens/title-screen.png")));
            instructionsScreen = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/screens/instructions-screen.png")));
            creditsScreen = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/screens/credits-screen.png")));
            endScreen = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/screens/end-screen.png")));
            endScreenNewHigh = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/screens/end-screen-new-high.png")));
            widener = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/sprites/widener.png")));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw() {
        GL11.glClear(16384);
        GL11.glPushMatrix();
        this.doCamera();
        GL11.glTranslatef(this.currentTransX, this.currentTransY, 0.0f);
        GL11.glScaled(SCALE, SCALE, 1.0);
        Main.level.draw();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        Main.gui.draw();
        GL11.glPopMatrix();
        if (Main.menu != null) {
            GL11.glPushMatrix();
            Main.menu.draw();
            GL11.glPopMatrix();
        }
    }

    public void doCamera() {
        Vec2 babyPos = Main.level.getCurrentBaby().getBody().getPosition();
        Vec2 babyVel = Main.level.getCurrentBaby().getBody().getLinearVelocity();
        if (this.resettingTransX) {
            if (this.currentTransX < INITIAL_TRANS_X) {
                this.translateXAmt = (babyPos.x + Level.BABY_INIT_POS.x) * 2.0f;
                this.translateX();
            } else {
                this.resettingTransX = false;
            }
        }
        if (this.resettingTransY) {
            if (this.currentTransY < INITIAL_TRANS_Y) {
                this.translateYAmt = (babyPos.y + Level.BABY_INIT_POS.y + Ground.DIMENSIONS.y * 2.0f) * 2.0f;
                this.translateY();
            } else {
                this.resettingTransY = false;
            }
        }
        if ((double)(babyPos.x * (float)Main.OBJECT_SCALE) * SCALE + (double)(babyVel.x / (float)Main.OBJECT_SCALE) * SCALE >= (double)(Main.WIDTH / 4)) {
            this.translateXAmt = -babyVel.x / 11.0f;
            this.translateX();
        }
        if ((double)(babyPos.y * (float)Main.OBJECT_SCALE) * SCALE + (double)(babyVel.y / (float)Main.OBJECT_SCALE) * SCALE >= (double)Main.HEIGHT / 2.5) {
            this.translateYAmt = -babyVel.y / 11.0f;
            this.translateY();
        } else if (this.currentTransY != 0.0f) {
            this.resettingTransY = true;
        }
    }

    public void translateX() {
        this.currentTransX += this.translateXAmt;
        if ((double)Ground.DIMENSIONS.x * SCALE * (double)Main.OBJECT_SCALE < (double)(Main.WIDTH / 2)) {
            this.currentTransX -= this.translateXAmt;
        } else if (this.currentTransX > 0.0f) {
            this.currentTransX = 0.0f;
        }
    }

    public void translateY() {
        this.currentTransY += this.translateYAmt;
        if (this.currentTransY > 0.0f) {
            this.currentTransY = 0.0f;
        }
    }

    public void reset() {
        this.resettingTransX = true;
        this.resettingTransY = true;
    }

    public void destroy() {
    }
}

