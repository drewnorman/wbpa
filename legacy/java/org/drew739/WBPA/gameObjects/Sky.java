/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gameObjects;

import java.util.Random;
import org.drew739.WBPA.Main;
import org.drew739.WBPA.gameObjects.Cloud;
import org.drew739.WBPA.gameObjects.Ground;
import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;

public class Sky {
    private Vec2 position;
    private Vec2 dimensions;
    public Cloud[] clouds;

    public Sky(Vec2 pos, float width) {
        this.position = pos;
        this.dimensions = new Vec2(width, width + 0.33333334f * width);
        this.clouds = new Cloud[75];
        Random random = new Random();
        int i = 0;
        while (i < this.clouds.length) {
            this.clouds[i] = new Cloud(new Vec2(random.nextInt((int)Ground.DIMENSIONS.x * Main.OBJECT_SCALE + Cloud.TEX_SIDE) - Cloud.TEX_SIDE, random.nextInt(Main.HEIGHT * 2) + Main.HEIGHT / 2), random.nextInt(4));
            ++i;
        }
    }

    public void tick() {
        Cloud[] cloudArray = this.clouds;
        int n = this.clouds.length;
        int n2 = 0;
        while (n2 < n) {
            Cloud cloud = cloudArray[n2];
            cloud.tick();
            ++n2;
        }
    }

    public void draw() {
        GL11.glColor3f(0.4745098f, 0.63529414f, 0.9137255f);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.position.x, this.position.y, 0.0f);
        GL11.glScaled(1.0, 1.0, 0.0);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(this.dimensions.x, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(this.dimensions.x, this.dimensions.y, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, this.dimensions.y, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        Cloud[] cloudArray = this.clouds;
        int n = this.clouds.length;
        int n2 = 0;
        while (n2 < n) {
            Cloud cloud = cloudArray[n2];
            cloud.draw();
            ++n2;
        }
    }
}

