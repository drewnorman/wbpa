/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gui;

import org.drew739.WBPA.gui.Text;
import org.drew739.WBPA.utils.Render;
import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class Score {
    public int score = 0;
    private Vec2 position;

    public Score(Vec2 pos) {
        this.position = pos;
    }

    public void increaseScoreBy(int score) {
        this.score += score;
    }

    public void reset() {
        this.score = 0;
    }

    public void draw() {
        GL11.glPushMatrix();
        GL11.glEnable(3553);
        Render.score.bind();
        GL11.glTranslatef(this.position.x - 7.0f, this.position.y + 25.0f, 0.0f);
        GL11.glScaled(0.8, 0.8, 1.0);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, Render.score.getImageHeight(), 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(Render.score.getImageWidth(), 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(Render.score.getImageWidth(), Render.score.getImageHeight(), 0.0f);
        GL11.glEnd();
        TextureImpl.unbind();
        GL11.glDisable(3553);
        GL11.glPopMatrix();
        Text.scale = 2;
        Text.draw((int)this.position.x + 7, (int)this.position.y + 10, this.score, true);
    }
}

