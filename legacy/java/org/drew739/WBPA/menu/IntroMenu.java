/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.menu;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.menu.Menu;
import org.drew739.WBPA.menu.TitleMenu;
import org.drew739.WBPA.utils.Render;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class IntroMenu
extends Menu {
    private float alpha = 1.0f;
    private boolean fadingIn = true;
    private boolean waiting = true;
    private int counter = 0;

    public IntroMenu() {
        this.type = 1;
    }

    @Override
    public void tick() {
        if (this.waiting) {
            ++this.counter;
            if (this.counter > 75) {
                this.waiting = false;
                this.counter = 0;
            }
            return;
        }
        if (this.fadingIn) {
            this.alpha = (float)((double)this.alpha - 0.0075);
            if (this.alpha < 0.0f) {
                this.alpha = 0.0f;
                ++this.counter;
                if (this.counter == 50) {
                    this.counter = 0;
                    this.fadingIn = false;
                }
            }
        } else {
            this.alpha = (float)((double)this.alpha + 0.01);
            if (this.alpha > 1.0f) {
                this.alpha = 1.0f;
                ++this.counter;
                if (this.counter == 75) {
                    this.counter = 0;
                    Main.menu = new TitleMenu();
                }
            }
        }
    }

    @Override
    public void draw() {
        int width = Render.introScreen.getImageWidth();
        int height = Render.introScreen.getImageHeight();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRectf(0.0f, 0.0f, Main.WIDTH, Main.HEIGHT);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(118.0f, 118.0f, 0.0f);
        GL11.glEnable(3553);
        Render.introScreen.bind();
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
        GL11.glPushMatrix();
        GL11.glColor4f(0.0f, 0.0f, 0.0f, this.alpha);
        GL11.glRectf(0.0f, 0.0f, Main.WIDTH, Main.HEIGHT);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}

