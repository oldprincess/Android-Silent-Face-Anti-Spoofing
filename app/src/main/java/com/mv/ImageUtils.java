package com.mv;

import android.graphics.Bitmap;

public class ImageUtils {
    public static byte[] bitmapToNv21(Bitmap bp) {
        if (bp.getConfig() != Bitmap.Config.ARGB_8888) {
            throw new IllegalArgumentException("Invalid bitmap config value");
        }
        if (bp.getHeight() % 2 != 0 || bp.getWidth() % 2 != 0) {
            throw new IllegalArgumentException("The height and width of the input image need to be even");
        }

        int width = bp.getWidth();
        int height = bp.getHeight();
        int[] argb = new int[width * height];
        bp.getPixels(argb, 0, width, 0, 0, width, height);

        // =============================================================
        // ================ Cast Bitmap to NV21 ========================
        // =============================================================
        int yIndex = 0;
        int uvIndex = width * height;
        byte[] nv21 = new byte[width * height * 3 / 2];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                int R = (argb[j * width + i] & 0xFF0000) >> 16;
                int G = (argb[j * width + i] & 0x00FF00) >> 8;
                int B = argb[j * width + i] & 0x0000FF;

                int Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                int U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                int V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                nv21[yIndex++] = (byte) (Y);

                if (j % 2 == 0 && i % 2 == 0 && uvIndex < nv21.length - 2) {
                    nv21[uvIndex++] = (byte) (V);
                    nv21[uvIndex++] = (byte) (U);
                }
            }
        }
        return nv21;
    }
}
