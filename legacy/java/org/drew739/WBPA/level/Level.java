/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.level;

import java.util.ArrayList;
import org.drew739.WBPA.Main;
import org.drew739.WBPA.gameObjects.Baby;
import org.drew739.WBPA.gameObjects.Ground;
import org.drew739.WBPA.gameObjects.Player;
import org.drew739.WBPA.gameObjects.Sky;
import org.drew739.WBPA.gameObjects.TrashCan;
import org.drew739.WBPA.level.CollisionListener;
import org.drew739.WBPA.menu.EndMenu;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

public class Level
extends World {
    private ArrayList<Baby> babiesToRemove;
    private Baby currentBaby;
    private ArrayList<TrashCan> cans;
    private Player player;
    private Ground ground;
    public static Vec2 BABY_INIT_POS = new Vec2(7.6f, 30.0f);
    private Sky sky;

    public Level(Vec2 vec2, boolean pool) {
        super(vec2, pool);
        super.setContactListener(new CollisionListener());
    }

    public void init() {
        this.ground = new Ground(this);
        this.sky = new Sky(new Vec2(0.0f, 0.0f), Ground.DIMENSIONS.x * (float)Main.OBJECT_SCALE);
        this.player = new Player(this);
        this.initResettable();
    }

    public void initResettable() {
        this.babiesToRemove = new ArrayList();
        this.cans = new ArrayList();
        int distance = 1;
        Vec2 CAN_MIN = new Vec2(50.0f, 3.5f);
        Vec2 prevPosition = new Vec2(CAN_MIN.x, CAN_MIN.y);
        Vec2 prevDim = new Vec2(5.0f, 6.0f);
        this.addBaby(new Baby(BABY_INIT_POS, this));
        int i = 0;
        while (i < 15) {
            prevPosition.x += 40.0f;
            this.addCan(new TrashCan(new Vec2(prevPosition.x, prevPosition.y), this, prevDim.x, prevDim.y, distance + i));
            ++i;
        }
    }

    public void destroyResettable() {
        for (Baby baby : this.babiesToRemove) {
            this.destroyBody(baby.getBody());
        }
        this.babiesToRemove.clear();
        if (this.currentBaby != null) {
            this.destroyBody(this.currentBaby.getBody());
            this.currentBaby = null;
        }
        for (TrashCan can : this.cans) {
            this.destroyBody(can.getBody());
        }
    }

    public void destroy() {
        this.destroyResettable();
        this.destroyBody(this.ground.getBody());
        this.destroyBody(this.player.getThigh());
        this.destroyBody(this.player.getShin());
        this.destroyBody(this.player.getFoot());
    }

    private void addBaby(Baby baby) {
        this.currentBaby = baby;
    }

    private void setToRemove(Baby baby) {
        baby.makeInactive();
        this.babiesToRemove.add(baby);
        this.destroyBody(baby.getBody());
        this.currentBaby = null;
    }

    private void emptyToRemove() {
        int i = 0;
        while (i < this.babiesToRemove.size()) {
            if (this.babiesToRemove.get(i).isToRemove()) {
                this.babiesToRemove.remove(this.babiesToRemove.get(i));
                --i;
            }
            ++i;
        }
    }

    public Baby getCurrentBaby() {
        return this.currentBaby;
    }

    private void addCan(TrashCan can) {
        this.cans.add(can);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void tick() {
        this.sky.tick();
        this.step(0.016666668f, 8, 3);
        if (this.checkInactive(this.currentBaby) && !Main.gui.kicksLeft.noMoreKicks) {
            int distance = 0;
            if (this.checkCans(this.currentBaby)) {
                System.out.println("Score!");
                distance += 2;
                for (TrashCan can : this.cans) {
                    if (!((double)this.currentBaby.getBody().getPosition().x + 0.3 > (double)can.getBody().getPosition().x)) continue;
                    ++distance;
                }
                Main.gui.score.increaseScoreBy(distance * 10);
            } else {
                for (TrashCan can : this.cans) {
                    if (!(this.currentBaby.getBody().getPosition().x > can.getBody().getPosition().x)) continue;
                    ++distance;
                }
                Main.gui.score.increaseScoreBy(distance * 10);
            }
            Main.gui.kicksLeft.decrement();
            if (Main.gui.kicksLeft.noMoreKicks) {
                boolean newrecord = false;
                if (Main.gui.score.score > Main.hs.score) {
                    Main.hs.score = Main.gui.score.score;
                    newrecord = true;
                }
                Main.menu = new EndMenu(newrecord);
            } else {
                this.setToRemove(this.currentBaby);
                this.next();
            }
        }
        this.emptyToRemove();
        for (Baby baby : this.babiesToRemove) {
            baby.tick();
        }
        if (this.currentBaby != null) {
            this.currentBaby.tick();
        }
        this.player.tick();
    }

    public void reset() {
        this.destroyResettable();
        this.initResettable();
    }

    public void next() {
        this.addBaby(new Baby(BABY_INIT_POS, this));
        Main.render.reset();
        Main.gui.meter.reset();
    }

    private boolean checkInactive(Baby baby) {
        return !baby.getBody().isAwake();
    }

    private boolean checkCans(Baby baby) {
        Vec2 babyPos = baby.getBody().getPosition();
        for (TrashCan can : this.cans) {
            Vec2 canPos = can.getBody().getPosition();
            if (!(babyPos.x > canPos.x - can.getWidth() / 2.0f) || !(babyPos.x < canPos.x + can.getWidth() / 2.0f) || !(babyPos.y > canPos.y - can.getHeight() / 2.0f) || !(babyPos.y < canPos.y + can.getHeight() / 2.0f)) continue;
            return true;
        }
        return false;
    }

    public void draw() {
        this.sky.draw();
        this.currentBaby.draw();
        for (TrashCan can : this.cans) {
            can.draw();
        }
        for (Baby baby : this.babiesToRemove) {
            baby.draw();
        }
        this.player.draw();
        this.ground.draw();
    }
}

