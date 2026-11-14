/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.menu;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.gui.Text;
import org.drew739.WBPA.menu.Menu;
import org.drew739.WBPA.utils.Render;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class EndMenu
extends Menu {
    boolean newrecord;
    boolean textEntering = true;
    private float x = -300.0f;

    public EndMenu(boolean newrecord) {
        this.type = 3;
        this.newrecord = newrecord;
    }

    @Override
    public void tick() {
        if (this.textEntering) {
            this.x += 10.0f;
            if (this.x > -2.0f) {
                this.textEntering = false;
                this.x = 2.0f;
            }
        }
    }

    @Override
    public void draw() {
        float width = Render.endScreen.getImageWidth();
        float height = Render.endScreen.getImageHeight();
        GL11.glPushMatrix();
        GL11.glScaled(1.3, 1.8, 1.0);
        GL11.glTranslatef(this.x, -125.0f, 0.0f);
        GL11.glEnable(3553);
        if (!this.newrecord) {
            Render.endScreen.bind();
        } else {
            Render.endScreenNewHigh.bind();
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
        Text.scale = 3;
        Text.draw((int)(this.x + 270.0f), 179, Main.gui.score.score, false);
        Text.draw((int)(this.x + 270.0f), 159, Main.hs.score, false);
    }
}

