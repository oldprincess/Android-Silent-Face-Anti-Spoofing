package com.livedetect;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mv.DetectionResult;
import com.mv.EngineWrapper;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageView1 = findViewById(R.id.imageView1);
        ImageView imageView2 = findViewById(R.id.imageView2);
        ImageView imageView3 = findViewById(R.id.imageView3);
        ImageView imageView4 = findViewById(R.id.imageView4);
        TextView textView = findViewById(R.id.textView);

        EngineWrapper engineWrapper = new EngineWrapper(getAssets());
        try {
            // ===========================================================
            // ==================== Init Engine ==========================
            // ===========================================================
            boolean initRet = engineWrapper.init();
            if (!initRet) {
                Log.e("MainActivity", "EngineWrapper: init fail");
                throw new RuntimeException("EngineWrapper: init fail");
            }

            // ===========================================================
            // ==================== do Live Detect =======================
            // ===========================================================
            int[] rawIdList = new int[]{R.raw.pic1, R.raw.pic2, R.raw.pic3, R.raw.pic4};
            ImageView[] imageViews = new ImageView[]{imageView1, imageView2, imageView3, imageView4};
            for (int i = 0; i < rawIdList.length; i++) {
                // draw picture
                Bitmap bp = BitmapFactory.decodeResource(this.getBaseContext().getResources(), rawIdList[i]);
                bp = Bitmap.createScaledBitmap(bp, bp.getWidth() + bp.getWidth() % 2, bp.getHeight() + bp.getHeight() % 2, true);
                imageViews[i].setImageBitmap(
                        Bitmap.createScaledBitmap(bp, 300 * bp.getWidth() / bp.getHeight(), 300, true)
                );
                // detect picture
                List<DetectionResult> detectionResults = engineWrapper.detect(bp);
                if (detectionResults.isEmpty()) {
                    textView.append("\n" + String.format("图像%d未检测到人脸", i + 1));
                } else {
                    textView.append("\n" + String.format("图像%d检测到人脸数: %d", i + 1, detectionResults.size()));
                    for (int j = 0; j < detectionResults.size(); j++) {
                        DetectionResult detectionResult = detectionResults.get(j);
                        textView.append("\n" + String.format("\t图像%d 人脸%d top:%3d left:%3d 置信度:%f",
                                i + 1, j + 1,
                                detectionResult.top,
                                detectionResult.left,
                                detectionResult.confidence)
                        );
                    }
                }

            }
        } catch (Exception e) {
            textView.append("\n" + "EngineWrapper: " + e.getMessage());
        }
        engineWrapper.destroy();

        textView.append("\n" + "结束测试");
    }
}