/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.level;

import org.drew739.WBPA.audio.Sound;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CollisionListener
implements ContactListener {
    @Override
    public void beginContact(Contact arg0) {
        String fixAData = (String)arg0.getFixtureA().getBody().getUserData();
        String fixBData = (String)arg0.getFixtureB().getBody().getUserData();
        if (fixAData.equals("baby") || fixBData.equals("baby")) {
            if (fixAData.equals("can") || fixBData.equals("can")) {
                Sound.canHit.play();
            }
            if (fixAData.equals("thigh") || fixBData.equals("thigh") || fixAData.equals("shin") || fixBData.equals("shin") || fixAData.equals("foot") || fixBData.equals("foot")) {
                Sound.thud.play();
            }
            if (fixAData.equals("ground") || fixBData.equals("ground")) {
                Sound.rustle.play();
            }
        }
    }

    @Override
    public void endContact(Contact arg0) {
    }

    @Override
    public void postSolve(Contact arg0, ContactImpulse arg1) {
    }

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {
    }
}

