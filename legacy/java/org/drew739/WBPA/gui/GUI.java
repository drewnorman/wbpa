/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gui;

import org.drew739.WBPA.Main;
import org.drew739.WBPA.gui.KicksLeft;
import org.drew739.WBPA.gui.PowerMeter;
import org.drew739.WBPA.gui.Score;
import org.jbox2d.common.Vec2;

public class GUI {
    public PowerMeter meter = new PowerMeter(new Vec2(10.0f, Main.HEIGHT / 2 - 15), new Vec2(50.0f, 5.0f));
    public Score score = new Score(new Vec2(Main.WIDTH / 4 - 25, Main.HEIGHT / 2 - 47));
    public KicksLeft kicksLeft = new KicksLeft(new Vec2(Main.WIDTH / 2 - 40, Main.HEIGHT / 2 - 40));

    public void tick() {
        this.meter.tick();
    }

    public void draw() {
        this.meter.draw();
        this.score.draw();
        this.kicksLeft.draw();
    }

    public void reset() {
        this.meter.reset();
    }
}

