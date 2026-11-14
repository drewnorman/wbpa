/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gui;

import org.drew739.WBPA.gui.Text;
import org.drew739.WBPA.utils.Render;
import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class KicksLeft {
    private final int NUM_OF_KICKS = 10;
    private int kicksLeft = 10;
    public boolean noMoreKicks = false;
    private Vec2 position;

    public KicksLeft(Vec2 pos) {
        this.position = pos;
    }

    public void decrement() {
        if (this.noMoreKicks) {
            return;
        }
        --this.kicksLeft;
        if (this.kicksLeft <= 0) {
            this.noMoreKicks = true;
        }
    }

    public void reset() {
        this.kicksLeft = 10;
    }

    public void draw() {
        GL11.glPushMatrix();
        GL11.glEnable(3553);
        Render.kicksLeft.bind();
        GL11.glTranslatef(this.position.x - 30.0f, this.position.y + 21.0f, 0.0f);
        GL11.glScaled(0.6, 0.6, 1.0);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, Render.kicksLeft.getImageHeight(), 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(Render.kicksLeft.getImageWidth(), 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(Render.kicksLeft.getImageWidth(), Render.kicksLeft.getImageHeight(), 0.0f);
        GL11.glEnd();
        TextureImpl.unbind();
        GL11.glDisable(3553);
        GL11.glPopMatrix();
        Text.scale = 2;
        Text.draw((int)this.position.x + 9, (int)this.position.y + 3, this.kicksLeft, false);
    }
}

