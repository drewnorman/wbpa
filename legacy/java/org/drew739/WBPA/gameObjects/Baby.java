/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gameObjects;

import java.awt.Color;
import java.util.Random;
import org.drew739.WBPA.Main;
import org.drew739.WBPA.audio.Sound;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.opengl.GL11;

public class Baby {
    private Body body;
    private Vec2[] vertices;
    private float fade = 1.0f;
    private Color blanketColor;
    private Color skinColor;
    private static Color[] skinColors;
    private static Color[] blanketColors;
    private boolean inactive = false;
    private boolean toRemove = false;
    public boolean screaming = false;
    private Random random = new Random();

    public Baby(Vec2 position, World world) {
        blanketColors = new Color[5];
        Baby.blanketColors[0] = new Color(127, 0, 0);
        Baby.blanketColors[1] = new Color(200, 200, 0);
        Baby.blanketColors[2] = new Color(127, 0, 127);
        Baby.blanketColors[3] = new Color(0, 0, 127);
        Baby.blanketColors[4] = new Color(0, 127, 0);
        this.blanketColor = blanketColors[this.random.nextInt(blanketColors.length)];
        skinColors = new Color[5];
        Baby.skinColors[0] = new Color(232, 187, 142);
        Baby.skinColors[1] = new Color(75, 59, 43);
        Baby.skinColors[2] = new Color(183, 122, 61);
        Baby.skinColors[3] = new Color(210, 180, 150);
        Baby.skinColors[4] = new Color(122, 95, 68);
        this.skinColor = skinColors[this.random.nextInt(skinColors.length)];
        BodyDef bd = new BodyDef();
        bd.position.set(position);
        bd.type = BodyType.DYNAMIC;
        PolygonShape shape = new PolygonShape();
        float SCALE = 1.1f;
        this.vertices = new Vec2[8];
        this.vertices[0] = new Vec2(-1.0f, 1.0f);
        this.vertices[1] = new Vec2(-1.5f, 0.5f);
        this.vertices[2] = new Vec2(-1.5f, -0.25f);
        this.vertices[3] = new Vec2(-0.5f, -1.0f);
        this.vertices[4] = new Vec2(1.0f, -1.25f);
        this.vertices[5] = new Vec2(1.5f, -0.5f);
        this.vertices[6] = new Vec2(1.5f, -0.25f);
        this.vertices[7] = new Vec2(1.0f, 0.25f);
        int i = 0;
        while (i < this.vertices.length) {
            this.vertices[i] = this.vertices[i].mul(SCALE);
            ++i;
        }
        shape.set(this.vertices, this.vertices.length);
        this.body = world.createBody(bd);
        FixtureDef fixture = new FixtureDef();
        fixture.density = 0.03f;
        fixture.shape = shape;
        fixture.friction = 1.2f;
        fixture.restitution = 0.003f;
        this.body.createFixture(fixture);
        this.body.setUserData("baby");
    }

    public Body getBody() {
        return this.body;
    }

    public void tick() {
        if (this.inactive) {
            float fadeRate = 0.03f;
            this.fade -= fadeRate;
            if (this.fade <= 0.0f) {
                this.toRemove = true;
            }
        }
        if (Math.abs(this.body.getLinearVelocity().x) > 13.0f || Math.abs(this.body.getLinearVelocity().y) > 13.0f) {
            if (!this.screaming) {
                if (Main.menu == null) {
                    Sound.babyCrying[this.random.nextInt(3)].play();
                }
                this.screaming = true;
            }
        } else if (this.random.nextBoolean() && this.random.nextBoolean()) {
            this.screaming = false;
            Sound.babyCrying[0].stop();
            Sound.babyCrying[1].stop();
            Sound.babyCrying[2].stop();
        }
    }

    public void makeInactive() {
        this.inactive = true;
    }

    public boolean isInactive() {
        return this.inactive;
    }

    public boolean isToRemove() {
        return this.toRemove;
    }

    public void draw() {
        GL11.glPushMatrix();
        Vec2 bodyPosition = this.body.getPosition().mul(Main.OBJECT_SCALE);
        GL11.glTranslatef(bodyPosition.x, bodyPosition.y, 0.0f);
        GL11.glRotated(Math.toDegrees(this.body.getAngle()), 0.0, 0.0, 1.0);
        GL11.glColor4f((float)this.blanketColor.getRed() / 255.0f, (float)this.blanketColor.getGreen() / 255.0f, (float)this.blanketColor.getBlue() / 255.0f, this.fade);
        GL11.glBegin(9);
        Vec2[] vec2Array = this.vertices;
        int n = this.vertices.length;
        int n2 = 0;
        while (n2 < n) {
            Vec2 vertex = vec2Array[n2];
            GL11.glVertex2f(vertex.x * (float)Main.OBJECT_SCALE, vertex.y * (float)Main.OBJECT_SCALE);
            ++n2;
        }
        GL11.glEnd();
        GL11.glColor4f((float)this.skinColor.getRed() / 255.0f, (float)this.skinColor.getGreen() / 255.0f, (float)this.skinColor.getBlue() / 255.0f, this.fade);
        GL11.glTranslatef(-12.0f, 4.0f, 0.0f);
        GL11.glBegin(6);
        float radius = 8.0f;
        float angle = 0.0f;
        while (angle <= 360.0f) {
            GL11.glVertex2f((float)Math.sin(angle) * radius, (float)Math.cos(angle) * radius);
            angle += 5.0f;
        }
        GL11.glEnd();
        GL11.glTranslatef(3.0f, 0.0f, 0.0f);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, this.fade);
        if (this.screaming) {
            GL11.glBegin(6);
            radius = 3.0f;
            angle = 0.0f;
            while (angle <= 360.0f) {
                GL11.glVertex2f((float)Math.sin(angle) * radius, (float)Math.cos(angle) * radius);
                angle += 5.0f;
            }
            GL11.glEnd();
        } else {
            GL11.glBegin(3);
            GL11.glVertex2f(-2.0f, -5.0f);
            GL11.glVertex2f(2.0f, 3.0f);
            GL11.glEnd();
        }
        GL11.glColor4f(0.0f, 0.0f, 0.0f, this.fade);
        GL11.glTranslatef(-5.0f, 3.0f, 0.0f);
        GL11.glBegin(6);
        radius = 1.5f;
        angle = 0.0f;
        while (angle <= 360.0f) {
            GL11.glVertex2f((float)Math.sin(angle) * radius, (float)Math.cos(angle) * radius);
            angle += 5.0f;
        }
        GL11.glEnd();
        GL11.glColor4f(0.0f, 0.0f, 0.0f, this.fade);
        GL11.glTranslatef(-2.0f, -3.0f, 0.0f);
        radius = 1.5f;
        GL11.glBegin(6);
        angle = 0.0f;
        while (angle <= 360.0f) {
            GL11.glVertex2f((float)Math.sin(angle) * radius, (float)Math.cos(angle) * radius);
            angle += 5.0f;
        }
        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
}

