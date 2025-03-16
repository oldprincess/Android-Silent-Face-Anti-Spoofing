package com.mv.engine;

public abstract class Component {
    static {
        System.loadLibrary("engine");
    }

    public abstract long createInstance();

    public abstract void destroy();
}
