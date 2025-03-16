package com.mv.engine;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.lang.IllegalArgumentException;
import java.util.List;

public class FaceDetector extends Component {

    private long nativeHandler;// 会在native函数中使用

    /**
     * 人脸检测. 对象销毁时需要调用destroy(), 避免资源泄露
     */
    public FaceDetector() {
        nativeHandler = createInstance();
    }

    /**
     * 创建实例, 返回实例对象指针.需要确保对象已被销毁才能调用, 否则会造成内存泄露.
     *
     * @return 实例对象指针, 仅用于内部函数使用, 对外部调用者没有意义
     */
    @Override
    public long createInstance() {
        return allocate();
    }

    /**
     * 加载模型
     *
     * @param assetManager 通过getAssets获取
     * @return 错误码, 0表示无错误
     */
    public int loadModule(AssetManager assetManager) {
        return nativeLoadModel(assetManager);
    }

    /**
     * 检测是否存在人脸, 返回人脸坐标列表
     *
     * @param bitmap 输入, 图片
     * @return 检测结果
     */
    public List<FaceBox> detect(Bitmap bitmap) {
        if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
            throw new IllegalArgumentException("Invalid bitmap config value");
        }
        return nativeDetectBitmap(bitmap);
    }

    /**
     * 检测是否存在人脸, 返回人脸坐标列表
     *
     * @param yuv           输入, YUV格式(NV21)的图片, 可以通过ImageUtils.bitmapToNv21转化
     * @param previewWidth  图片宽度
     * @param previewHeight 图片高度
     * @param orientation   图片朝向(取值范围1-8)
     * @return 检测结果
     */
    public List<FaceBox> detect(byte[] yuv, int previewWidth, int previewHeight, int orientation) {
        if (previewWidth * previewHeight * 3 / 2 != yuv.length) {
            throw new IllegalArgumentException("Invalid yuv data");
        }
        return nativeDetectYuv(yuv, previewWidth, previewHeight, orientation);
    }

    /**
     * 销毁对象, 释放资源
     */
    public void destroy() {
        deallocate();
    }

    // ==================================================================
    // ======================= JNI Native 函数 ===========================
    // ==================================================================

    private native long allocate();

    private native void deallocate();

    private native int nativeLoadModel(AssetManager assetManager);

    private native List<FaceBox> nativeDetectBitmap(Bitmap bitmap);

    private native List<FaceBox> nativeDetectYuv(byte[] yuv, int previewWidth, int previewHeight, int orientation);
}
