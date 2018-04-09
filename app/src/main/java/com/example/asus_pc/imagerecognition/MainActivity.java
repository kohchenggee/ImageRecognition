package com.example.asus_pc.imagerecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final int ImageGallery_RequestCode = 20;
    ImageView imageView;
    Button button;
    Bitmap bitmap;
    Bitmap tempBitmap;
    Canvas canvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.image);
        button = (Button)findViewById(R.id.btnProgress);

        bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.face);
        imageView.setImageBitmap(bitmap);
        final Paint rectPaint = new Paint();
        rectPaint.setColor(Color.GREEN);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
                canvas = new Canvas(tempBitmap);
                canvas.drawBitmap(bitmap,0,0,null);
                FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setMode(FaceDetector.FAST_MODE)
                        .build();
                if(!faceDetector.isOperational()){
                    Toast.makeText(MainActivity.this,"Face Detector could not set up",Toast.LENGTH_SHORT).show();
                    return;
                }
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Face> sparseArray =faceDetector.detect(frame);
                for(int i = 0 ; i <sparseArray.size();i++){
                    Face face = sparseArray.valueAt(i);
                    float x1=0;
                    float y1=0;
                    float x2=0;
                    float y2=0;
                    for (Landmark landmark : face.getLandmarks()) {
                        if(landmark.getType() == Landmark.LEFT_EYE){
                            x1 = landmark.getPosition().x;
                            y1 = landmark.getPosition().y;
                        }else if(landmark.getType() == Landmark.RIGHT_EYE){
                            x2 = landmark.getPosition().x;
                            y2 = landmark.getPosition().y;
                        }else if(landmark.getType() == Landmark.NOSE_BASE){
                            //y2 = landmark.getPosition().y;
                        }
                    }
                    Log.d("x1",x1+"");
                    Log.d("x2",x2+"");
                    Log.d("y2",y2+"");
                    Log.d("y1",y1+"");
                    float ydiff = (y2-y1)/2;
                    float xdiff =(x1-x2)/2;
                    //RectF rectF = new RectF(x1+xdiff,y1-ydiff,x2-xdiff,y2-ydiff);
                    canvas.drawCircle(x1,y1,15,rectPaint);
                    canvas.drawCircle(x2,y2,15,rectPaint);
                    //canvas.drawRect(rectF,rectPaint);
                    //canvas.drawRoundRect(rectF,2,2,rectPaint);
                    imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
                }

            }
        });
    }
    public void onChooseImage(View view){
        Intent photoPickereIntent = new Intent (Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirPath);

        photoPickereIntent.setDataAndType(data,"image/*");
        startActivityForResult(photoPickereIntent, ImageGallery_RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == ImageGallery_RequestCode){


            InputStream inputStream;

            try{
                Uri imageUri = data.getData();
                inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            }catch(FileNotFoundException e){
                e.printStackTrace();
                Toast.makeText(this,"Unable to open image",Toast.LENGTH_SHORT).show();
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}
