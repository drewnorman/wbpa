/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gameObjects;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.gui.Text;
import org.drew739.WBPA.level.Level;
import org.drew739.WBPA.utils.Render;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class TrashCan {
    private Body body;
    private float width = 5.0f;
    private float height = 4.0f;
    private float thickness = 0.2f;
    private int distance;

    public TrashCan(Vec2 position, Level level, int distance) {
        this.init(position, level, distance);
    }

    public TrashCan(Vec2 position, Level level, float width, float height, int distance) {
        this.width = width;
        this.height = height;
        this.init(position, level, distance);
    }

    public void init(Vec2 position, Level level, int distance) {
        this.distance = distance;
        PolygonShape bottom = new PolygonShape();
        bottom.setAsBox(this.width / 2.0f, this.thickness / 2.0f, new Vec2(0.0f, 0.0f), 0.0f);
        PolygonShape left = new PolygonShape();
        left.setAsBox(this.thickness / 2.0f, this.height / 2.0f, new Vec2(-this.width / 2.0f + this.thickness / 2.0f, this.height / 2.0f - this.thickness / 2.0f), 0.0f);
        PolygonShape right = new PolygonShape();
        right.setAsBox(this.thickness / 2.0f, this.height / 2.0f, new Vec2(this.width / 2.0f - this.thickness / 2.0f, this.height / 2.0f - this.thickness / 2.0f), 0.0f);
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(position.x, position.y);
        this.body = level.createBody(bd);
        this.body.createFixture(bottom, 1.0f);
        this.body.createFixture(left, 1.0f);
        this.body.createFixture(right, 1.0f);
        this.body.setUserData("can");
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Body getBody() {
        return this.body;
    }

    public void draw() {
        GL11.glEnable(3553);
        Render.can.bind();
        GL11.glPushMatrix();
        Vec2 bodyPos = this.body.getPosition().mul(Main.OBJECT_SCALE);
        GL11.glTranslatef(bodyPos.x, bodyPos.y, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-this.width / 2.0f * (float)Main.OBJECT_SCALE, this.height * (float)Main.OBJECT_SCALE, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-this.width / 2.0f * (float)Main.OBJECT_SCALE, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(this.width / 2.0f * (float)Main.OBJECT_SCALE, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(this.width / 2.0f * (float)Main.OBJECT_SCALE, this.height * (float)Main.OBJECT_SCALE, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
        TextureImpl.unbind();
        GL11.glDisable(3553);
        Text.scale = 6;
        Text.draw((int)this.body.getPosition().x * Main.OBJECT_SCALE - 19, (int)this.body.getPosition().y * Main.OBJECT_SCALE + 18, this.distance, false);
    }
}

