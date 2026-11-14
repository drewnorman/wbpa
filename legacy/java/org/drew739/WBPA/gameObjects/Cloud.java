/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gameObjects;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.gameObjects.Ground;
import org.drew739.WBPA.utils.Render;
import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class Cloud {
    public int texture;
    public Vec2 position;
    public static int TEX_SIDE = 512;

    public Cloud(Vec2 pos, int tex) {
        this.position = pos;
        this.texture = tex;
    }

    public void tick() {
        this.position.x = (float)((double)this.position.x + 0.3);
        if (this.position.x > Ground.DIMENSIONS.x * (float)Main.OBJECT_SCALE) {
            this.position.x = -TEX_SIDE;
        }
    }

    public void draw() {
        GL11.glEnable(3553);
        Render.clouds[this.texture].bind();
        GL11.glPushMatrix();
        GL11.glTranslatef(-Main.render.currentTransX * 2.3f, -Main.render.currentTransY * 2.3f, 0.0f);
        GL11.glTranslatef(this.position.x, this.position.y, 0.0f);
        GL11.glScaled(300.0, 300.0, 0.0);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
        TextureImpl.unbind();
        GL11.glDisable(3553);
    }
}

