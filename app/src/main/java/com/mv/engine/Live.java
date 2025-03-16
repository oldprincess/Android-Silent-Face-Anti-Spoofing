package com.mv.engine;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.mv.ImageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Live extends Component {
    private static final String TAG = "Live";

    private long nativeHandler = 0;// 会在native函数中使用

    /**
     * 活体检测. 对象销毁时需要调用destroy(), 避免资源泄露
     */
    public Live() {
        nativeHandler = createInstance();
    }

    /**
     * 创建实例, 返回实例对象指针.需要确保对象已被销毁才能调用, 否则会造成内存泄露.
     *
     * @return 实例对象指针, 仅用于内部函数使用, 对外部调用者没有意义
     */
    @Override
    public long createInstance() {
        // allocate会自动给对象的nativeHandler属性赋值
        return allocate();
    }

    /**
     * 销毁对象, 释放资源
     */
    @Override
    public void destroy() {
        // deallocate会自动给对象的nativeHandler属性赋值0
        deallocate();
    }

    /**
     * 加载模型
     *
     * @param assetManager 通过getAssets获取
     * @return 错误码, 0表示无错误
     */
    public int loadModel(AssetManager assetManager) throws JSONException, IOException {
        List<ModelConfig> configs = parseConfig(assetManager);

        if (configs.isEmpty()) {
            Log.e(TAG, "parse model config failed");
            return -1;
        }

        return nativeLoadModel(assetManager, configs);
    }

    /**
     * 检测图片是否是活体
     *
     * @param bitmap  输入, ARGB8888的图片, 可以通过ImageUtils.bitmapToNv21转化
     * @param faceBox 输入, 人脸框
     * @return 置信度0~100, 越大代表越可能是活体
     */
    public float detect(Bitmap bitmap, FaceBox faceBox) {
        return nativeDetectYuv(ImageUtils.bitmapToNv21(bitmap), bitmap.getWidth(), bitmap.getHeight(), 1, faceBox.left, faceBox.top, faceBox.right, faceBox.bottom);
    }

    /**
     * 检测图片是否是活体
     *
     * @param yuv           输入, YUV格式(NV21)的图片, 可以通过ImageUtils.bitmapToNv21转化
     * @param previewWidth  输入, 图片宽度
     * @param previewHeight 输入, 图片高度
     * @param orientation   朝向(取值范围1-8)
     * @param faceBox       人脸框
     * @return 置信度0~100, 越大代表越可能是活体
     */
    public float detect(byte[] yuv, int previewWidth, int previewHeight, int orientation, FaceBox faceBox) {
        if (previewWidth * previewHeight * 3 / 2 != yuv.length) {
            throw new IllegalArgumentException("Invalid yuv data");
        }
        return nativeDetectYuv(yuv, previewWidth, previewHeight, orientation, faceBox.left, faceBox.top, faceBox.right, faceBox.bottom);
    }

    /**
     * 加载模型配置
     *
     * @param assetManager 通过getAssets获取
     * @return 模型配置
     * @throws JSONException asset文件live/config.json解析json错误
     * @throws IOException   asset文件live/config.json读取错误
     */
    private List<ModelConfig> parseConfig(AssetManager assetManager) throws JSONException, IOException {
        InputStream inputStream = assetManager.open("live/config.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = br.readLine();

        JSONArray jsonArray = new JSONArray(line);

        List<ModelConfig> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject config = jsonArray.getJSONObject(i);
            list.add(new ModelConfig((float) config.optDouble("scale"), (float) config.optDouble("shift_x"), (float) config.optDouble("shift_y"), config.optInt("height"), config.optInt("width"), config.optString("name"), config.optBoolean("org_resize")));
        }
        return list;
    }

    // ==================================================================
    // ======================= JNI Native 函数 ===========================
    // ==================================================================

    private native long allocate();

    private native void deallocate();

    private native int nativeLoadModel(AssetManager assetManager, List<ModelConfig> configs);

    private native float nativeDetectYuv(byte[] yuv, int previewWidth, int previewHeight, int orientation, int left, int top, int right, int bottom);

}
