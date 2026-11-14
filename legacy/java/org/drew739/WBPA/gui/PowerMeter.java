/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gui;

import org.jbox2d.common.Vec2;
import org.lwjgl.opengl.GL11;

public class PowerMeter {
    public int power = 0;
    private Vec2 position;
    private Vec2 dimensions;
    public boolean firing = false;
    private int rate = 2;

    public PowerMeter(Vec2 pos, Vec2 dim) {
        this.position = pos;
        this.dimensions = dim;
    }

    public void tick() {
        if (this.firing) {
            this.power += this.rate;
            if (this.power > 100) {
                this.power = 100;
            }
            if (this.power < 0) {
                this.power = 0;
                this.rate *= -1;
            }
        }
    }

    public void reset() {
        this.power = 0;
        this.firing = false;
    }

    public void draw() {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.position.x, this.position.y, 0.0f);
        GL11.glColor3f(0.5f, 0.5f, 0.5f);
        GL11.glRectf(-1.0f, -1.0f, this.dimensions.x + 1.0f, this.dimensions.y + 1.0f);
        GL11.glColor3f(1.0f, 0.0f, 0.0f);
        GL11.glRectf(0.0f, 0.0f, this.dimensions.x, this.dimensions.y);
        GL11.glColor3f(0.0f, 1.0f, 0.0f);
        GL11.glRectf(0.0f, 0.0f, this.dimensions.x * (float)this.power / 100.0f, this.dimensions.y);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}

