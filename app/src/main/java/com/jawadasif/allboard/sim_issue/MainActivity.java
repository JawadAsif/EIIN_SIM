package com.jawadasif.allboard.sim_issue;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    //public static final int SCAN_TYPE = 300;
    public static String directory_name = "Received Exam Scripts";
    public Button scan_btn;
    public TextView result, scanInfo;
    public RelativeLayout main_layout;
    public int error_background_color, default_background_color, error_text_color, success_text_color;
    private int ScanType;
    private String FileName;
    private String root;

    public String scanInfoMessage(String scannedString) {
        String url = "http://www.google.com/search?q=";

        try {
            url = url + scannedString;
            return url;
        } catch (Exception e) {
            Log.e("error", e.getMessage());
            return null;
        }

    }

    public void openUrl(String url) {
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("URL", url);
        startActivity(intent);

    }

    public boolean canWriteOnExternalStorage() {
// get the state of your external storage
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // if storage is mounted return true
            Log.v("info", "Yes, can write to external storage.");
//            Toast.makeText(getApplicationContext(),R.string.done_message,Toast.LENGTH_LONG).show();
            return true;
        }
        Log.v("info", "Yes, can not write to external storage.");
//        Toast.makeText(getApplicationContext(),"Yes, can write to external storage."
//                ,Toast.LENGTH_SHORT).show();
        return false;
    }

    public void checkAndUpdatePermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;

        ArrayList<String> permissionToRequest = new ArrayList<>();
        if (!hasCameraPermission) {
            permissionToRequest.add(Manifest.permission.CAMERA);
        }

        if (!permissionToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionToRequest.toArray(new String[0]), PERMISSION_REQUEST);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        result = (TextView) findViewById(R.id.result);
        scanInfo = (TextView) findViewById(R.id.scanInfo);
        main_layout = (RelativeLayout) findViewById(R.id.main_layout);
        default_background_color = Color.parseColor("#faefe6");
        error_background_color = Color.parseColor("#faefe6");
        error_text_color = Color.parseColor("#e81216");
        success_text_color = Color.parseColor("#05ac50");


        checkAndUpdatePermission();


        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScanType = 0;
                checkAndUpdatePermission();

                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("SCAN_TYPE", ScanType);
                startActivityForResult(intent, REQUEST_CODE);


            }
        });

        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (ScanType == 1) {
                    final Barcode barcode = data.getParcelableExtra("barcode");

                    result.post(new Runnable() {
                        @Override
                        public void run() {

                        }


                    });
                } else {
                    final Barcode barcode = data.getParcelableExtra("barcode");

                    //final Bitmap barcode = data.getStringArrayExtra("barcode")

                    result.post(new Runnable() {
                        public static final String TAG = "test";

                        @Override
                        public void run() {

                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions
                                        (MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
                            }

// create the file in which we will write the content
                            String scanMessage = scanInfoMessage(barcode.displayValue);
                            if (scanMessage == null) {
                                Toast.makeText(getApplicationContext(), R.string.wrong_scan_message, Toast.LENGTH_LONG).show();
                                scanInfo.setText(R.string.wrong_scan_message);
                                main_layout.setBackgroundColor(error_background_color);
                                scanInfo.setTextColor(error_text_color);
                                scanInfo.setTypeface(null, Typeface.BOLD);

                            } else {
//                                scanInfo.setText(scanMessage);
//                                main_layout.setBackgroundColor(default_background_color);
//                                scanInfo.setTextColor(success_text_color);
//                                scanInfo.setTypeface(null, Typeface.BOLD);
                                openUrl(scanMessage);
                            }

                        }
                    });

                }
            }
        }
    }
}