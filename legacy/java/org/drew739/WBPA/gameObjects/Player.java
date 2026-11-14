/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gameObjects;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.level.Level;
import org.drew739.WBPA.utils.Render;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class Player {
    private Body thigh;
    private Body shin;
    private Body foot;
    public static Vec2 THIGH_DIM;
    public static Vec2 SHIN_DIM;
    public static Vec2[] FOOT_VERTICES;
    public static Vec2 POSITION;
    public boolean firing = false;
    public boolean prepping = false;
    public boolean resetting = false;

    public Player(Level level) {
        POSITION = new Vec2(3.0f, 10.0f);
        THIGH_DIM = new Vec2(1.0f, 3.0f);
        SHIN_DIM = new Vec2(0.8f, 2.5f);
        FOOT_VERTICES = new Vec2[4];
        Player.FOOT_VERTICES[0] = new Vec2(0.0f, 0.0f);
        Player.FOOT_VERTICES[1] = new Vec2(0.0f, -0.75f);
        Player.FOOT_VERTICES[2] = new Vec2(2.0f, -0.75f);
        Player.FOOT_VERTICES[3] = new Vec2(0.6f, 0.0f);
        BodyDef bdThigh = new BodyDef();
        bdThigh.position.set(POSITION);
        bdThigh.type = BodyType.DYNAMIC;
        PolygonShape thighShape = new PolygonShape();
        thighShape.setAsBox(Player.THIGH_DIM.x / 2.0f, Player.THIGH_DIM.y / 2.0f);
        this.thigh = level.createBody(bdThigh);
        FixtureDef thighFix = new FixtureDef();
        thighFix.density = 0.1f;
        thighFix.shape = thighShape;
        thighFix.friction = 1.0f;
        thighFix.restitution = 0.08f;
        this.thigh.createFixture(thighFix);
        BodyDef bdShin = new BodyDef();
        bdShin.position.set(new Vec2(Player.POSITION.x + (Player.THIGH_DIM.x - Player.SHIN_DIM.x) / 2.0f, Player.POSITION.y - Player.THIGH_DIM.y + 0.4f));
        bdShin.type = BodyType.DYNAMIC;
        PolygonShape shinShape = new PolygonShape();
        shinShape.setAsBox(Player.SHIN_DIM.x / 2.0f, Player.SHIN_DIM.y / 2.0f);
        this.shin = level.createBody(bdShin);
        FixtureDef shinFix = new FixtureDef();
        shinFix.density = 0.1f;
        shinFix.shape = shinShape;
        shinFix.friction = 1.0f;
        shinFix.restitution = 0.5f;
        this.shin.createFixture(shinFix);
        BodyDef bdFoot = new BodyDef();
        bdFoot.position.set(new Vec2(Player.POSITION.x + 0.05f, Player.POSITION.y - Player.THIGH_DIM.y - Player.SHIN_DIM.y + 1.8f));
        bdFoot.type = BodyType.DYNAMIC;
        PolygonShape footShape = new PolygonShape();
        footShape.set(FOOT_VERTICES, FOOT_VERTICES.length);
        this.foot = level.createBody(bdFoot);
        FixtureDef footFix = new FixtureDef();
        footFix.density = 0.1f;
        footFix.shape = footShape;
        footFix.friction = 1.0f;
        footFix.restitution = 0.5f;
        this.foot.createFixture(footFix);
        BodyDef hangerBD = new BodyDef();
        Body hanger = level.createBody(hangerBD);
        PolygonShape hangerShape = new PolygonShape();
        hangerShape.setAsEdge(new Vec2(0.0f, 0.0f), new Vec2(10.0f, 0.0f));
        hanger.createFixture(hangerShape, 0.0f);
        RevoluteJointDef jdThigh = new RevoluteJointDef();
        jdThigh.collideConnected = false;
        jdThigh.upperAngle = 1.5f;
        jdThigh.lowerAngle = -1.4f;
        jdThigh.enableLimit = true;
        Vec2 anchorThigh = new Vec2(this.thigh.getPosition().x, this.thigh.getPosition().y + Player.THIGH_DIM.y / 2.0f);
        jdThigh.initialize(hanger, this.thigh, anchorThigh);
        level.createJoint(jdThigh);
        RevoluteJointDef jdShin = new RevoluteJointDef();
        jdShin.collideConnected = false;
        jdShin.upperAngle = 0.0f;
        jdShin.lowerAngle = -2.2f;
        jdShin.enableLimit = true;
        Vec2 anchorShin = new Vec2(this.shin.getPosition().x, this.shin.getPosition().y + Player.SHIN_DIM.y / 2.0f);
        jdShin.initialize(this.thigh, this.shin, anchorShin);
        level.createJoint(jdShin);
        RevoluteJointDef jdFoot = new RevoluteJointDef();
        jdFoot.collideConnected = false;
        jdFoot.upperAngle = 0.1f;
        jdFoot.lowerAngle = -0.4f;
        jdFoot.enableLimit = true;
        Vec2 anchorFoot = new Vec2(this.foot.getPosition().x, this.foot.getPosition().y + 0.25f);
        jdFoot.initialize(this.shin, this.foot, anchorFoot);
        level.createJoint(jdFoot);
        this.thigh.setUserData("thigh");
        this.shin.setUserData("shin");
        this.foot.setUserData("foot");
    }

    public void tick() {
        this.prepping = Main.gui.meter.firing;
        if (this.prepping) {
            this.thigh.applyTorque(-40.0f);
            if (this.thigh.getAngle() < -1.5f) {
                this.thigh.setAngularVelocity(0.0f);
            }
        }
        if (this.firing) {
            this.thigh.applyAngularImpulse(5.0f);
            if (this.thigh.getAngle() > 0.8f) {
                this.firing = false;
                this.thigh.setAngularVelocity(0.0f);
                this.resetting = true;
            }
        }
        if (this.resetting) {
            this.thigh.setAngularVelocity(-1.0f);
            if ((double)this.thigh.getAngle() <= 0.2) {
                this.thigh.setAngularVelocity(0.0f);
                this.resetting = false;
            }
        }
    }

    public void draw() {
        GL11.glColor3f(0.9098039f, 0.73333335f, 0.5568628f);
        GL11.glPushMatrix();
        Vec2 shinPosition = this.shin.getPosition().mul(Main.OBJECT_SCALE);
        GL11.glTranslatef(shinPosition.x, shinPosition.y, 0.0f);
        GL11.glRotated(Math.toDegrees(this.shin.getAngle()), 0.0, 0.0, 1.0);
        GL11.glRectf(-Player.SHIN_DIM.x / 2.0f * (float)Main.OBJECT_SCALE, -Player.SHIN_DIM.y / 2.0f * (float)Main.OBJECT_SCALE, Player.SHIN_DIM.x / 2.0f * (float)Main.OBJECT_SCALE, Player.SHIN_DIM.y / 2.0f * (float)Main.OBJECT_SCALE);
        GL11.glPopMatrix();
        GL11.glColor3f(0.30588236f, 0.30588236f, 0.30588236f);
        GL11.glPushMatrix();
        Vec2 thighPosition = this.thigh.getPosition().mul(Main.OBJECT_SCALE);
        GL11.glTranslatef(thighPosition.x, thighPosition.y, 0.0f);
        GL11.glRotated(Math.toDegrees(this.thigh.getAngle()), 0.0, 0.0, 1.0);
        GL11.glRectf(-Player.THIGH_DIM.x / 2.0f * (float)Main.OBJECT_SCALE, -Player.THIGH_DIM.y / 2.0f * (float)Main.OBJECT_SCALE, Player.THIGH_DIM.x / 2.0f * (float)Main.OBJECT_SCALE, Player.THIGH_DIM.y / 2.0f * (float)Main.OBJECT_SCALE);
        GL11.glPopMatrix();
        GL11.glColor3f(0.0f, 0.0f, 0.0f);
        GL11.glPushMatrix();
        Vec2 footPosition = this.foot.getPosition().mul(Main.OBJECT_SCALE);
        GL11.glTranslatef(footPosition.x, footPosition.y, 0.0f);
        GL11.glRotated(Math.toDegrees(this.foot.getAngle()), 0.0, 0.0, 1.0);
        GL11.glBegin(9);
        Vec2[] vec2Array = FOOT_VERTICES;
        int n = FOOT_VERTICES.length;
        int n2 = 0;
        while (n2 < n) {
            Vec2 vertex = vec2Array[n2];
            GL11.glVertex2f(vertex.x * (float)Main.OBJECT_SCALE, vertex.y * (float)Main.OBJECT_SCALE);
            ++n2;
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        int width = Render.widener.getImageWidth() + 150;
        int height = Render.widener.getImageHeight();
        GL11.glEnable(3553);
        Render.widener.bind();
        GL11.glPushMatrix();
        GL11.glTranslatef(11.0f, -25.0f, 0.0f);
        GL11.glScaled(0.4, 0.4, 1.0);
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
        GL11.glPopMatrix();
        TextureImpl.unbind();
        GL11.glDisable(3553);
    }

    public Body getThigh() {
        return this.thigh;
    }

    public Body getShin() {
        return this.shin;
    }

    public Body getFoot() {
        return this.foot;
    }
}

