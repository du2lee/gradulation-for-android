package com.example.kickboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Matrix;
import java.io.IOException;
import static android.content.ContentValues.TAG;
import java.util.Locale;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import static android.os.Environment.DIRECTORY_PICTURES;

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
    Button sharebtn;
    TextView id;

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;


    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_detector);


        imageView = (ImageView) findViewById(R.id.imageView);
        btn = (Button) findViewById(R.id.detectBtn);
        id = (TextView) findViewById(R.id.id);


        //take a picture
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent. putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true);
                intent. putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);

                intent.putExtra("camerafacing", "front");
                intent.putExtra("previous_mode", "front");
                startActivityForResult(intent, 12);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {

            bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                id.setText("아래 버튼을 눌러\n 헬멧 착용 인증을 해주세요!");
                btn.setVisibility(View.VISIBLE);
            }
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
                    if(i==0){
                        AlertDialog.Builder dlg = new AlertDialog.Builder(DetectorActivity.this);
                        dlg.setMessage("헬멧 착용 후 다시 촬영해주세요."); // 메시지
                        dlg.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                //토스트 메시지
//                                Toast.makeText(DetectorActivity.this,"확인을 눌르셨습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                        dlg.show();
                        id.setText("헬멧 착용한 후 화면을 터치해\n 예시와 같이 가까이서 촬영해주세요\n(예시)");
                        Bitmap draw_bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.camera);
                        btn.setVisibility(View.GONE);
                        imageView.setImageBitmap(draw_bitmap);
                    }

                    if (i == 1) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        float scale = (float) (1024/(float)bitmap.getWidth());
                        int image_w = (int) (bitmap.getWidth() * scale);
                        int image_h = (int) (bitmap.getHeight() * scale);
                        Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                        resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                        intent.putExtra("image", byteArray);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }

    }
}
