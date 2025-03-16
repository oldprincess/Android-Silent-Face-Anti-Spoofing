package com.mv;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.mv.engine.FaceBox;
import com.mv.engine.Live;
import com.mv.engine.FaceDetector;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class EngineWrapper {

    private final AssetManager assetManager;

    private final FaceDetector faceDetector = new FaceDetector();
    private final Live live = new Live();

    /**
     * 需要调用init来初始化, 调用destroy来销毁对象
     *
     * @param assetManager 通过getAssets获取
     */
    public EngineWrapper(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * 初始化模型
     *
     * @return 成功初始化与否. 如果失败, 则检查asset目录是否正确存放了模型配置文件
     * @throws JSONException asset文件live/config.json解析json错误
     * @throws IOException   asset文件live/config.json读取错误
     */
    public boolean init() throws JSONException, IOException {
        int ret = faceDetector.loadModule(assetManager);
        if (ret != 0) {
            return false;
        }
        ret = live.loadModel(assetManager);
        if (ret != 0) {
            faceDetector.destroy();
            return false;
        }
        return true;
    }

    /**
     * 销毁对象, 释放资源
     */
    public void destroy() {
        faceDetector.destroy();
        live.destroy();
    }

    /**
     * 识别图片中的人脸, 并检测活体
     *
     * @param bitmap 人脸
     * @return 检测结果列表
     */
    public List<DetectionResult> detect(Bitmap bitmap) {
        List<FaceBox> boxes = detectFace(bitmap);
        List<DetectionResult> detectionResults = new ArrayList<>();
        for (FaceBox box : boxes) {
            long begin = System.currentTimeMillis();
            box.confidence = detectLive(bitmap, box);
            long end = System.currentTimeMillis();
            detectionResults.add(new DetectionResult(box, end - begin, true));
        }
        return detectionResults;
    }

    /**
     * 识别图片中的人脸, 并检测活体
     *
     * @param yuv         输入, YUV格式(NV21)的图片, 可以通过ImageUtils.bitmapToNv21转化
     * @param width       图片宽度
     * @param height      图片高度
     * @param orientation 图片朝向(取值范围1-8). 代表对图片进行不同处理.
     *                    <p>1: 不处理</p>
     *                    <p>2: 水平翻转</p>
     *                    <p>3: 先水平翻转 然后垂直翻转</p>
     *                    <p>4: 垂直翻转</p>
     *                    <p>5: 转置</p>
     *                    <p>6: 顺时针旋转90度</p>
     *                    <p>7: 水平、垂直翻转 然后转置</p>
     *                    <p>8: 逆时针旋转90度</p>
     * @return 检测结果列表
     */
    public List<DetectionResult> detect(byte[] yuv, int width, int height, int orientation) {
        List<FaceBox> boxes = detectFace(yuv, width, height, orientation);
        List<DetectionResult> detectionResults = new ArrayList<>();
        for (FaceBox box : boxes) {
            long begin = System.currentTimeMillis();
            box.confidence = detectLive(yuv, width, height, orientation, box);
            long end = System.currentTimeMillis();
            detectionResults.add(new DetectionResult(box, end - begin, true));
        }
        return detectionResults;
    }

    /**
     * 检测是否存在人脸, 返回人脸坐标列表
     *
     * @param bitmap 输入, 图片
     * @return 检测结果
     */
    public List<FaceBox> detectFace(Bitmap bitmap) {
        return faceDetector.detect(bitmap);
    }

    /**
     * 检测是否存在人脸, 返回人脸坐标列表
     *
     * @param yuv         输入, YUV格式(NV21)的图片, 可以通过ImageUtils.bitmapToNv21转化
     * @param width       图片宽度
     * @param height      图片高度
     * @param orientation 图片朝向(取值范围1-8). 代表对图片进行不同处理.
     *                    <p>1: 不处理</p>
     *                    <p>2: 水平翻转</p>
     *                    <p>3: 先水平翻转 然后垂直翻转</p>
     *                    <p>4: 垂直翻转</p>
     *                    <p>5: 转置</p>
     *                    <p>6: 顺时针旋转90度</p>
     *                    <p>7: 水平、垂直翻转 然后转置</p>
     *                    <p>8: 逆时针旋转90度</p>
     * @return 检测结果
     */
    public List<FaceBox> detectFace(byte[] yuv, int width, int height, int orientation) {
        return faceDetector.detect(yuv, width, height, orientation);
    }

    /**
     * 检测图片是否是活体
     *
     * @param bitmap  输入, ARGB8888的图片, 可以通过ImageUtils.bitmapToNv21转化
     * @param faceBox 输入, 人脸框
     * @return 置信度0~100, 越大代表越可能是活体
     */
    public float detectLive(Bitmap bitmap, FaceBox faceBox) {
        return live.detect(bitmap, faceBox);
    }

    /**
     * 检测图片是否是活体
     *
     * @param yuv         输入, YUV格式(NV21)的图片, 可以通过ImageUtils.bitmapToNv21转化
     * @param width       图片宽度
     * @param height      图片高度
     * @param orientation 图片朝向(取值范围1-8). 代表对图片进行不同处理.
     *                    <p>1: 不处理</p>
     *                    <p>2: 水平翻转</p>
     *                    <p>3: 先水平翻转 然后垂直翻转</p>
     *                    <p>4: 垂直翻转</p>
     *                    <p>5: 转置</p>
     *                    <p>6: 顺时针旋转90度</p>
     *                    <p>7: 水平、垂直翻转 然后转置</p>
     *                    <p>8: 逆时针旋转90度</p>
     * @param faceBox     人脸框
     * @return 置信度0~100, 越大代表越可能是活体
     */
    public float detectLive(byte[] yuv, int width, int height, int orientation, FaceBox faceBox) {
        return live.detect(yuv, width, height, orientation, faceBox);
    }
}
