/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import org.drew739.WBPA.Main;

public class Sound {
    private AudioClip clip;
    public static final Sound[] babyCrying = new Sound[]{new Sound("res/sound/baby-crying.wav"), new Sound("res/sound/baby-crying-2.wav"), new Sound("res/sound/baby-crying-3.wav")};
    public static final Sound thud = new Sound("res/sound/thud.wav");
    public static final Sound rustle = new Sound("res/sound/grass-rustle.wav");
    public static final Sound canHit = new Sound("res/sound/can-hit.wav");

    private Sound(String name) {
        try {
            String jarLoc = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            this.clip = Applet.newAudioClip(new URL("file://" + jarLoc.substring(0, jarLoc.length() - 4) + name));
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            new Thread(){

                @Override
                public void run() {
                    Sound.this.clip.play();
                }
            }.start();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.clip.stop();
    }
}

