/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gameObjects;

import org.drew739.WBPA.Main;
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

public class Ground {
    public static Vec2 DIMENSIONS = new Vec2(1200.0f, 2.0f);
    private Body ground;
    private Vec2 position;

    public Ground(World world) {
        BodyDef def = new BodyDef();
        def.type = BodyType.STATIC;
        def.position = this.position = new Vec2(0.0f, Ground.DIMENSIONS.y);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Ground.DIMENSIONS.x, Ground.DIMENSIONS.y);
        this.ground = world.createBody(def);
        FixtureDef groundFixture = new FixtureDef();
        groundFixture.density = 1.0f;
        groundFixture.restitution = 0.3f;
        groundFixture.shape = shape;
        this.ground.createFixture(groundFixture);
        this.ground.setUserData("ground");
    }

    public Ground(World world, float width, float restitution) {
        BodyDef def = new BodyDef();
        def.type = BodyType.STATIC;
        def.position = this.position = new Vec2(0.0f, Ground.DIMENSIONS.y);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, Ground.DIMENSIONS.y);
        this.ground = world.createBody(def);
        FixtureDef groundFixture = new FixtureDef();
        groundFixture.density = 1.0f;
        groundFixture.restitution = restitution;
        groundFixture.shape = shape;
        this.ground.createFixture(groundFixture);
    }

    public void draw() {
        GL11.glEnable(3553);
        Render.ground.bind();
        GL11.glPushMatrix();
        Vec2 bodyPos = this.position.mul(Main.OBJECT_SCALE);
        GL11.glTranslatef(bodyPos.x, bodyPos.y + 50.0f, 0.0f);
        float i = 0.0f;
        while (i <= Ground.DIMENSIONS.x * (float)Main.OBJECT_SCALE) {
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f(i, 0.0f, 0.0f);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f(i, -Render.ground.getImageHeight(), 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f(i + (float)Render.ground.getImageWidth(), -Render.ground.getImageHeight(), 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f(i + (float)Render.ground.getImageWidth(), 0.0f, 0.0f);
            GL11.glEnd();
            i += (float)(Render.ground.getImageWidth() - 100);
        }
        GL11.glPopMatrix();
        TextureImpl.unbind();
        GL11.glDisable(3553);
    }

    public Body getBody() {
        return this.ground;
    }
}

