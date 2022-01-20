package com.example.kickboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DetectorActivity extends AppCompatActivity {


    protected Interpreter tfLite;
    private MappedByteBuffer tfLiteModel;
    private TensorImage inputImageBuffer;
    private int x;
    private int y;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    Uri imageUri;

    ImageView imageView;
    Button btn;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_detector);

        imageView = (ImageView) findViewById(R.id.imageView);
        btn = (Button) findViewById(R.id.detectBtn);

        //카메라 권한 설정
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){}
            else {
                ActivityCompat.requestPermissions(DetectorActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        //take a picture
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //카메라접근
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 12);

                //갤러리 예제
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Take a picture"), 12);
            }
        });

        //define the interpreter with tfLite model
        try {
            tfLite = new Interpreter(loadModelFile(DetectorActivity.this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //click button

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageTensorIndex = 0;
                int[] imageShape = tfLite.getInputTensor(imageTensorIndex).shape();
                x = imageShape[1];
                y = imageShape[2];
                DataType imageDataType = tfLite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape = tfLite.getOutputTensor(probabilityTensorIndex).shape();
                DataType probabilityDataType = tfLite.getInputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostProcessorNormalizeOP()).build();

                inputImageBuffer = loadImage(bitmap);
                tfLite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());

                showResult();
            }
        });
    }

    //load the tfLite model
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);
    }

    //load the image and do the image processing
    private TensorImage loadImage(final Bitmap bitmap) {
        //load bitmap into a TensorImage
        inputImageBuffer.load(bitmap);

        //create processor for the tensorflow
        int cropsize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropsize, cropsize))
                        .add(new ResizeOp(x, y, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreProcessorNormalizeOP())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    //normalize the image
    private TensorOperator getPreProcessorNormalizeOP() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    private TensorOperator getPostProcessorNormalizeOP() {
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {


            bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            //갤러리 예제
//            imageUri = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }


    private void showResult() {
        try {
            labels = FileUtil.loadLabels(DetectorActivity.this, "labels.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Float> labelsProbability = new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                .getMapWithFloatValue();
        float maxValue = (Collections.max(labelsProbability.values()));
        for (Map.Entry<String, Float> entry : labelsProbability.entrySet()) {
            String[] label = labelsProbability.keySet().toArray(new String[0]);
            Float[] label_probability = labelsProbability.values().toArray(new Float[0]);

            for (int i = 0; i < label_probability.length; i++) {
                if (label_probability[i] == maxValue) {
                    Toast.makeText(this, label[i] + " : " + Float.toString(label_probability[i]), Toast.LENGTH_LONG).show();
                    if (i == 1) {
                        Intent intent = new Intent(getApplicationContext(), TakingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }

    }
}
