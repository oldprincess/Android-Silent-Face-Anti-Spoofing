package com.mv.engine;

public class FaceBox {
    public int left;
    public int top;
    public int right;
    public int bottom;
    public float confidence;

    public FaceBox(int left, int top, int right, int bottom, float confidence) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.confidence = confidence;
    }
}
