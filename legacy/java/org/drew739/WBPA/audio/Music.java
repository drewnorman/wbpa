/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import org.drew739.WBPA.Main;

public class Music {
    private AudioClip clip;
    public static final Music menuTheme = new Music("res/music/menuTheme.wav");
    public static final Music outdoorAmbient = new Music("res/music/outdoorAmbient.wav");

    private Music(String name) {
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
                    Music.this.clip.loop();
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

