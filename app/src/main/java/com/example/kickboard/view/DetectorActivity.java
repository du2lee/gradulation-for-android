package com.example.kickboard.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
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
import com.example.kickboard.R;
import com.example.kickboard.presenter.Contract;
import com.example.kickboard.presenter.Presenter;

public class DetectorActivity extends AppCompatActivity implements Contract.View {

    Contract.Presenter presenter;

    private final int REQUEST_IMAGE_CAPTURE = 12;
    private final int CAMERA_PERMISSION_CODE = 10;
    protected Interpreter tfLite;
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

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_detector);
        presenter = new Presenter(this);
        defineInterpreter();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_UP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionWithCamera();
                } else {
                    sendIntentToCamera(REQUEST_IMAGE_CAPTURE);
                }
                break;
        }
        return true;
    }
    private void checkPermissionWithCamera(){
        if (checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            sendIntentToCamera(REQUEST_IMAGE_CAPTURE);
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            } else {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendIntentToCamera(REQUEST_IMAGE_CAPTURE);
                } else {

                }
                return;
        }
    }

    private void sendIntentToCamera(int requestCode){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent. putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true);
        intent. putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        intent.putExtra("camerafacing", "front");
        intent.putExtra("previous_mode", "front");
        startActivityForResult(intent, requestCode);
    }

    private void defineInterpreter(){
        try {
            tfLite = new Interpreter(loadModelFile(DetectorActivity.this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            processIntent(data);
            setImageBuffer();
            showResult();
        }
    }

    private void processIntent(Intent data){
        bitmap = (Bitmap) data.getExtras().get("data");
        if (bitmap != null){
            presenter.sendBitmapImage(bitmap);
        }
    }

    private void setImageBuffer(){
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
    }

    private MappedByteBuffer loadModelFile(android.app.Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength);
    }

    private TensorImage loadImage(final Bitmap bitmap) {
        inputImageBuffer.load(bitmap);
        int cropsize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropsize, cropsize))
                        .add(new ResizeOp(x, y, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreProcessorNormalizeOP())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private TensorOperator getPreProcessorNormalizeOP() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    private TensorOperator getPostProcessorNormalizeOP() {
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private void showResult() {
        loadLabelsFile();
        doDetection();
    }

    private void loadLabelsFile(){
        try {
            labels = FileUtil.loadLabels(DetectorActivity.this, "labels.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void doDetection() {
        Map<String, Float> labelsProbability = new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                .getMapWithFloatValue();
        float maxValue = (Collections.max(labelsProbability.values()));
        for (Map.Entry<String, Float> entry : labelsProbability.entrySet()) {
            String[] label = labelsProbability.keySet().toArray(new String[0]);
            Float[] label_probability = labelsProbability.values().toArray(new Float[0]);

            for (int i = 0; i < label_probability.length; i++) {
                if (label_probability[i] == maxValue) {
                    if(i==0){
                        makeAlertDialog();
                    }
                    if (i==1) {
                        Toast.makeText(getApplicationContext(), "확인 완료", Toast.LENGTH_SHORT).show();
                        sendIntentTakingActivity();
                    }
                }
            }
        }
    }

    private void makeAlertDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(DetectorActivity.this);
        dlg.setMessage("헬멧 착용 후 다시 촬영해주세요.");
        dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlg.show();
    }

    private void sendIntentTakingActivity(){
        Intent intent = new Intent(getApplicationContext(), TakingActivity.class);
        startActivity(intent);
    }

    @Override
    public void setCircleImage(Bitmap bitmap) {}
}
