package com.mv;

import android.graphics.Rect;

import com.mv.engine.FaceBox;

public class DetectionResult {
    public int left = 0;
    public int top = 0;
    public int right = 0;
    public int bottom = 0;
    public float confidence = 0;
    public long time = 0;
    boolean hasFace = false;

    public DetectionResult() {

    }

    public DetectionResult(FaceBox faceBox, long time, boolean hasFace) {
        this.left = faceBox.left;
        this.top = faceBox.top;
        this.right = faceBox.right;
        this.bottom = faceBox.bottom;
        this.confidence = faceBox.confidence;
        this.time = time;
        this.hasFace = hasFace;
    }

    public DetectionResult updateLocation(Rect rect) {
        this.left = rect.left;
        this.top = rect.top;
        this.right = rect.right;
        this.bottom = rect.bottom;

        return this;
    }
}
