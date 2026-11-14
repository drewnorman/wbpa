/*
 * Decompiled with CFR 0.152.
 */
package org.drew739.WBPA.gui;

import org.drew739.WBPA.utils.Render;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

public class Text {
    public static int scale = 2;

    public static void draw(int x, int y, int num, boolean centered) {
        String number = Integer.toString(num);
        GL11.glEnable(3553);
        int width = Render.digitsFont[0].getImageWidth() - 2;
        int height = Render.digitsFont[0].getImageHeight();
        x -= number.length() * width * scale;
        if (centered) {
            x += number.length() * width * scale / 2;
        }
        int i = 0;
        while (i < number.length()) {
            int digit = Character.getNumericValue(number.charAt(i));
            Render.digitsFont[digit].bind();
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f(x + width * scale, y, 0.0f);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f(x + width * scale, y + height * scale, 0.0f);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f(x + width * scale + width * scale, y + height * scale, 0.0f);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f(x + width * scale + width * scale, y, 0.0f);
            GL11.glEnd();
            x += width * scale;
            ++i;
        }
        TextureImpl.unbind();
        GL11.glDisable(3553);
    }
}

