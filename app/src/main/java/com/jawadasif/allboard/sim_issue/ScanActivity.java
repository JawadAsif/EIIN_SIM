package com.jawadasif.allboard.sim_issue;


import static android.hardware.Camera.Parameters.FLASH_MODE_AUTO;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {
    private static final String SAVED_INSTANCE_URI = "uri";
    public byte[] bb;
    public String FileName = "";
    public Intent intent = new Intent();
    private SurfaceView cameraView;
    private BarcodeDetector barcode;
    private CameraSource cameraSource;
    private SurfaceHolder holder;
    private int flashMode;
    private int ScanType;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cameraSource.stop();
        cameraSource.release();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if (!barcode.isOperational()) {
            Toast.makeText(getApplicationContext(), "দুঃক্ষীত! এই ডিভাইসে এই এ্যাপলিকেশন চলবে না।", Toast.LENGTH_LONG).show();
            this.finish();
        }


        cameraSource = new CameraSource.Builder(this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setFlashMode(FLASH_MODE_AUTO)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
                //.setAutoFocusEnabled(true)
                //.setRequestedPreviewSize(1920,1024) //Default: 1024x768
                .setRequestedPreviewSize(1024, 768) //Default: 1024x768
                .build();


        //.setRequestedPreviewSize(1920,1024)

        //cameraView.setDrawingCacheEnabled(true);
        //cameraView.buildDrawingCache(true);

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //flash=getIntent().getStringExtra("FLASH_ONOFF");
                        //if(flash=="true"){
                        //}

                        cameraSource.start(cameraView.getHolder());
                        //Toast.makeText(ScanActivity.this,"Camera started!",Toast.LENGTH_LONG);
                    }
                } catch (IOException e) {
                    //Toast.makeText(ScanActivity.this,e.getCause().toString(),Toast.LENGTH_LONG);
                    e.printStackTrace();

                }
            }


            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

        });
        //
        //
        barcode.setProcessor(new Detector.Processor<Barcode>() {
            /*
            @Override
            public void release() {
                //cameraSource.stop();
                //cameraSource=null;
            }

            */


            private final CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback() {
                public void onShutter() {
                    AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
                }
            };
            final transient private CameraSource.PictureCallback onPicTaken = new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data) {

                    if (data != null) {


                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        if (bitmap != null) {
                            cameraSource.stop();
                            //cameraSource.release();
                            cameraSource = null;
                        }


                    }

                }
            };

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                //final byte bb[];

                ScanType = getIntent().getIntExtra("SCAN_TYPE", 0);

                if ((barcodes.size() == 1)) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.PERMISSION_REQUEST);
                        //Toast.makeText(getApplicationContext(), "Camera permission granted!", Toast.LENGTH_LONG).show();
                    }
                    intent.putExtra("barcode", barcodes.valueAt(0));
                    FileName = barcodes.valueAt(0).displayValue;

                    cameraSource.takePicture(shutterCallback, onPicTaken);
                    setResult(RESULT_OK, intent);

                    finish();
                }

            }

            @Override
            public void release() {
                //cameraSource.stop();
                //cameraSource.release();
                //cameraSource=null;

            }


        });
    }

}