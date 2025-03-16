package com.mv.engine;

public class ModelConfig {
    public float scale = 0F;
    public float shift_x = 0F;
    public float shift_y = 0F;
    public int height = 0;
    public int width = 0;
    public String name = "";
    public boolean org_resize = false;

    public ModelConfig(float scale, float shift_x, float shift_y, int height, int width, String name, boolean org_resize) {
        this.scale = scale;
        this.shift_x = shift_x;
        this.shift_y = shift_y;
        this.height = height;
        this.width = width;
        this.name = name;
        this.org_resize = org_resize;
    }
}


//import androidx.annotation.Keep
//
//@Keep
//data class ModelConfig(
//        var scale: Float = 0F,
//        var shift_x: Float = 0F,
//        var shift_y: Float = 0F,
//        var height: Int = 0,
//        var width: Int = 0,
//        var name: String = "",
//        var org_resize: Boolean = false
//)