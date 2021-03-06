package com.example.temi.huhacks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements IOCRCallBack, ImgurCallback{

    private String mAPiKey; //OCR api key
    private boolean isOverlayRequired;
    private String mImageUrl;
    private String mLanguage;
    private IOCRCallBack mIOCRCallBack;
    private ImgurCallback mImgurCallback;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private TextView mTvWait;
    private Button mBtnContinue;

    private boolean mPhotoTaken;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIOCRCallBack = this;
        mImgurCallback = this;

        mPhotoTaken = false;
        mCurrentPhotoPath = "";
        mAPiKey  = "f2e91b2f3e88957";

        //Find views
        mImageView = (ImageView)findViewById(R.id.ivPhoto);
        mTvWait = (TextView)findViewById(R.id.tvWait);
        mBtnContinue = (Button)findViewById(R.id.btnContinue);

        //Set continue button disabled by default
        mBtnContinue.setEnabled(false);

        mImageUrl = ""; // Image url to apply OCR API
        mLanguage = "eng"; //Language
        isOverlayRequired = true;

        takePicture();
        enableContinue();
    }

    private void enableContinue(){
        if(mPhotoTaken && mTvWait.toString().equals("Done")){
            mBtnContinue.setEnabled(true);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void takePicture(){
        Button btnTakePhoto = (Button)findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.temi.huhacks",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            Uri URI = Uri.parse( extras.getString(MediaStore.EXTRA_OUTPUT) );
            //Upload image to imgur
            ImgurUploadTask imgurUploadTask = new ImgurUploadTask(URI, MainActivity.this, mImgurCallback);
            imgurUploadTask.execute();
        }
    }

    private void connectOCR(){
        OCRAsyncTask oCRAsyncTask = new OCRAsyncTask(MainActivity.this, mAPiKey, isOverlayRequired, mImageUrl, mLanguage,mIOCRCallBack);
        oCRAsyncTask.execute();
    }


    @Override
    public void getOCRCallBackResult(String response) {
        mTvWait.setText("Done");
        parseOCRResponse(response);
    }

    @Override
    public void getImgurCallBackResult(String response) {
        //Sample url: http://imgur.com/P5IrmFo
        mImageUrl = "http://imgur.com/" + response;
    }

    private void parseOCRResponse(String response){
        //TODO
    }
}
