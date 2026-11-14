/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.menu;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.audio.Music;
import org.drew739.WBPA.gameObjects.Baby;
import org.drew739.WBPA.gameObjects.Ground;
import org.drew739.WBPA.gameObjects.Sky;
import org.drew739.WBPA.menu.Menu;
import org.drew739.WBPA.utils.Render;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class TitleMenu
extends Menu {
    private Baby baby;
    private Ground ground;
    private Sky sky;
    private World world;
    private Body screenBounds;
    private double SCALE = 1.6;
    private boolean fadingIn = true;
    public boolean textChanging = false;
    private static final float TEXT_SPEED = 5.0f;
    private static final int TOP_TEXT_Y = 100;
    private static final int TEXT_Y_MIDDLE = -125;
    private float text1Y = -125.0f;
    private float text2Y = 100.0f;
    private float alpha = 1.0f;
    public int selected = 0;
    public static final int NUM_OF_OPTIONS = 4;
    public int currentMenu = -1;
    public int nextMenu = 0;

    public TitleMenu() {
        Music.outdoorAmbient.stop();
        Music.menuTheme.play();
        this.world = new World(new Vec2(0.0f, -9.8f), true);
        this.ground = new Ground(this.world, Main.WIDTH, 5.0f);
        this.sky = new Sky(new Vec2(0.0f, 0.0f), Main.WIDTH * Main.OBJECT_SCALE);
        this.baby = new Baby(new Vec2(22.0f, 20.0f), this.world);
        this.type = 2;
        float WIDTH = (float)((double)Main.WIDTH / this.SCALE);
        float HEIGHT = (float)((double)Main.HEIGHT / this.SCALE);
        BodyDef def = new BodyDef();
        def.type = BodyType.STATIC;
        def.position = new Vec2(0.0f, HEIGHT / (float)Main.OBJECT_SCALE + (float)(23.0 / this.SCALE));
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WIDTH, 3.8f);
        this.screenBounds = this.world.createBody(def);
        FixtureDef fix = new FixtureDef();
        fix.density = 1.0f;
        fix.restitution = 5.0f;
        fix.shape = shape;
        this.screenBounds.createFixture(fix);
        shape = new PolygonShape();
        shape.setAsBox(5.0f, HEIGHT / (float)Main.OBJECT_SCALE, new Vec2(-5.0f, -HEIGHT / (float)Main.OBJECT_SCALE), 0.0f);
        fix = new FixtureDef();
        fix.density = 1.0f;
        fix.restitution = 5.0f;
        fix.shape = shape;
        this.screenBounds.createFixture(fix);
        shape = new PolygonShape();
        shape.setAsBox(5.0f, HEIGHT / (float)Main.OBJECT_SCALE, new Vec2(WIDTH / (float)Main.OBJECT_SCALE + (float)(31.0 / this.SCALE), -HEIGHT / (float)Main.OBJECT_SCALE), 0.0f);
        fix = new FixtureDef();
        fix.density = 1.0f;
        fix.restitution = 5.0f;
        fix.shape = shape;
        this.screenBounds.createFixture(fix);
    }

    @Override
    public void tick() {
        if (this.fadingIn) {
            this.alpha = (float)((double)this.alpha - 0.05);
            if (this.alpha < 0.0f) {
                this.alpha = 0.0f;
                this.fadingIn = false;
            }
        } else {
            this.sky.tick();
            this.world.step(0.016666668f, 8, 3);
            this.baby.tick();
            if (this.nextMenu != -1 && !this.textChanging) {
                this.textChanging = true;
            }
            if (this.textChanging) {
                this.text1Y -= 5.0f;
                this.text2Y -= 5.0f;
                if (this.text2Y < -125.0f) {
                    this.textChanging = false;
                    this.text1Y = -125.0f;
                    this.text2Y = 100.0f;
                    this.currentMenu = this.nextMenu;
                    this.nextMenu = -1;
                }
            }
        }
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        GL11.glScaled(Render.SCALE, Render.SCALE, 1.0);
        GL11.glScaled(this.SCALE, this.SCALE, 1.0);
        this.sky.draw();
        this.baby.draw();
        this.ground.draw();
        GL11.glPopMatrix();
        float width = Render.titleScreen.getImageWidth();
        float height = Render.titleScreen.getImageHeight();
        if (this.currentMenu != -1) {
            GL11.glPushMatrix();
            GL11.glScaled(1.3, 1.8, 1.0);
            GL11.glTranslatef(-2.0f, this.text1Y, 0.0f);
            GL11.glEnable(3553);
            if (this.currentMenu == 0) {
                Render.titleScreen.bind();
            } else if (this.currentMenu == 1) {
                Render.instructionsScreen.bind();
            } else if (this.currentMenu == 2) {
                Render.creditsScreen.bind();
            }
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f(0.0f, height, 0.0f);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f(0.0f, 0.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f(width, 0.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f(width, height, 0.0f);
            GL11.glEnd();
            TextureImpl.unbind();
            GL11.glDisable(3553);
            GL11.glPopMatrix();
        }
        if (this.nextMenu != -1) {
            GL11.glPushMatrix();
            GL11.glScaled(1.3, 1.8, 1.0);
            GL11.glTranslatef(-2.0f, this.text2Y, 0.0f);
            GL11.glEnable(3553);
            if (this.nextMenu == 0) {
                Render.titleScreen.bind();
            } else if (this.nextMenu == 1) {
                Render.instructionsScreen.bind();
            } else if (this.nextMenu == 2) {
                Render.creditsScreen.bind();
            }
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f(0.0f, height, 0.0f);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f(0.0f, 0.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f(width, 0.0f, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f(width, height, 0.0f);
            GL11.glEnd();
            TextureImpl.unbind();
            GL11.glDisable(3553);
            GL11.glPopMatrix();
        }
        if (!this.textChanging && this.currentMenu == 0) {
            GL11.glPushMatrix();
            if (this.selected == 0) {
                GL11.glTranslatef(300.0f, 200.0f, 0.0f);
            } else if (this.selected == 1) {
                GL11.glTranslatef(225.0f, 160.0f, 0.0f);
            } else if (this.selected == 2) {
                GL11.glTranslatef(270.0f, 120.0f, 0.0f);
            } else {
                GL11.glTranslatef(300.0f, 80.0f, 0.0f);
            }
            GL11.glColor3f(0.28627452f, 0.2784314f, 0.0f);
            GL11.glRectf(-1.5f, -1.5f, 11.5f, 3.5f);
            GL11.glColor3f(1.0f, 0.9647059f, 0.0f);
            GL11.glRectf(0.0f, 0.0f, 13.0f, 5.0f);
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        GL11.glColor4f(0.0f, 0.0f, 0.0f, this.alpha);
        GL11.glRectf(0.0f, 0.0f, Main.WIDTH, Main.HEIGHT);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}

