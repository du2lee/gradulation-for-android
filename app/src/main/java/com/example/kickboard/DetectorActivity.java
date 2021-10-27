package com.example.kickboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

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

    ImageView imageView;
    Button btn;

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_detector);

        imageView = (ImageView) findViewById(R.id.imageView);
        btn = (Button) findViewById(R.id.detectBtn);


        //take a picture
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Take a picture"), 12);
            }
        });

        //define the interpreter with tfLite model
        try {
            tfLite = new Interpreter(loadModelFile(DetectorActivity.this));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private TensorImage loadImage(final Bitmap bitmap){
        //load bitmap into a TensorImage
        inputImageBuffer.load(bitmap);

        //create processor for the tensorflow
        int cropsize = Math.min(bitmap.getWidth(),bitmap.getHeight());

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropsize, cropsize))
                        .add(new ResizeOp(x,y, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
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


}
