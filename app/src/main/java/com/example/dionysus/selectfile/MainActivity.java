package com.example.dionysus.selectfile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "sMess";
    Button button;
    Button read_fl;
    TextView txt;
    String filename = "file_paths.csv";
    String each_file_path = "";
    private static final int FILE_SELECT_CODE = 0;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        read_fl = findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        read_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "button 2 clicked");
                //file = new File(filename);
                file = new File(getApplicationContext().getFilesDir(), filename);

                if (file.exists()) {
                    Log.d(TAG, "dir exists");
                    try {
                        FileInputStream fis = getApplicationContext().openFileInput(filename);
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        Log.d(TAG, "***************output - " + sb);
                        txt.setText(sb);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "exiting button 2");
                } else {
                    Log.d(TAG, "dir not exists");
                }
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        each_file_path = getPath(this, uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "File Path: " + each_file_path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                    appendFile();
                }
                break;
        }
        txt.setText(each_file_path);

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private boolean appendFile() {
        //file = new File(filename);
        file = new File(getApplicationContext().getFilesDir(), filename);

        if (!file.exists()) {
            Log.d(TAG, "file does not exists,  crating file.....");
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!file.exists()) {
            Log.d(TAG, "Locha hai boss");
        }else{
            Log.d(TAG,"ALL good");
        }
        FileOutputStream outputStream;
        try {
            Log.d(TAG,"writing to file......" + each_file_path);
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(each_file_path.getBytes());
            outputStream.write(";".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fis = getApplicationContext().openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            Log.d(TAG, file.getAbsolutePath()+"***************output - " + sb);
            txt.setText(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}