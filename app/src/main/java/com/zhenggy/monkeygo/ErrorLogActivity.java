package com.zhenggy.monkeygo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.IOException;

public class ErrorLogActivity extends AppCompatActivity {
    TextView normalTV;
    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_log);

        normalTV = (TextView) findViewById(R.id.error_log_tv);
        addText();
    }

    private void addText() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    normalTV.setText(readFile("monkey_error_log"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mHandler.post(runnable);
    }

    private StringBuffer readFile(String fileName) throws IOException {
        FileInputStream fileInputStream = openFileInput(fileName);
        StringBuffer sb = new StringBuffer();
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = fileInputStream.read(bytes)) > 0) {
            sb.append(new String(bytes, 0, len));
        }
        fileInputStream.close();
        return sb;
    }
}
