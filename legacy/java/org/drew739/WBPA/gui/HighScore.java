/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class HighScore
implements Serializable {
    private static final long serialVersionUID = 1L;
    public int score = 0;

    public HighScore() {
        this.load();
    }

    public void save() {
        try {
            FileOutputStream fout = new FileOutputStream("res/saves/highscore.data");
            ObjectOutputStream objout = new ObjectOutputStream(fout);
            objout.writeObject(this);
            objout.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!new File("res/saves/highscore.data").exists()) {
            System.out.println("High score could not be loaded.");
            return;
        }
        Object obj = null;
        try {
            FileInputStream fin = new FileInputStream("res/saves/highscore.data");
            ObjectInputStream objin = new ObjectInputStream(fin);
            obj = objin.readObject();
            objin.close();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (obj instanceof HighScore) {
            HighScore hs = (HighScore)obj;
            this.score = hs.score;
        }
    }
}

