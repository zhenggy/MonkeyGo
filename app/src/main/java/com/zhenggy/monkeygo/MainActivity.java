package com.zhenggy.monkeygo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    EditText mPackageET;
    EditText mTimesET;
    EditText mMinuteET;
    Button mStartBT;
    Button mShowNormalLogBT;
    Button mShowErrorLogBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAction();
    }


    private void initView() {
        mPackageET = (EditText) findViewById(R.id.et_package);
        mTimesET = (EditText) findViewById(R.id.et_times);
        mMinuteET = (EditText) findViewById(R.id.et_minute);
        mStartBT = (Button) findViewById(R.id.bt_start);
        mShowNormalLogBT = (Button) findViewById(R.id.bt_show_normal);
        mShowErrorLogBT = (Button) findViewById(R.id.bt_show_error);
    }

    private void initAction() {
        mStartBT.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mPackageET.getText())) {
                    showToast("请输入包名！");
                    mPackageET.setText("com.tencent.mobileqq");
                    return;
                }
                if (TextUtils.isEmpty(mTimesET.getText()) && TextUtils
                        .isEmpty(mMinuteET.getText())) {
                    showToast("请输入事件次数或测试时间！");
                    return;
                }
                mStartBT.setVisibility(View.GONE);
                String packageName = mPackageET.getText().toString().trim();
                int times = 0, minutes = 0;
                if (TextUtils.isEmpty(mTimesET.getText())) {
                    minutes = Integer.valueOf(mMinuteET.getText().toString().trim());
                } else {
                    times = Integer.valueOf(mTimesET.getText().toString().trim());
                }

                try {
                    if (times == 0) {
                        executeCommand(packageName, minutes * 5000);
                    } else {
                        executeCommand(packageName, times);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mShowNormalLogBT.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NormalLogActivity.class));
            }
        });
        mShowErrorLogBT.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ErrorLogActivity.class));
            }
        });

        mTimesET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mMinuteET.setVisibility(View.VISIBLE);
                } else {
                    mMinuteET.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mMinuteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mTimesET.setVisibility(View.VISIBLE);
                } else {
                    mTimesET.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void executeCommand(String packageName, int times) throws IOException {
        Process process = Runtime.getRuntime().exec("su");
        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorInputStream = process.getErrorStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader errorInputReader = new BufferedReader(
                new InputStreamReader(errorInputStream));
        dataOutputStream.writeBytes("monkey -v -p " + packageName + " " + times);
        dataOutputStream.flush();

        dataOutputStream.close();
        outputStream.close();

        writeFile("monkey_log", inputReader);
        writeFile("monkey_error_log", errorInputReader);

        inputStream.close();
        errorInputStream.close();
        inputReader.close();
        errorInputReader.close();
    }

    private void writeFile(String fileName, BufferedReader inputReader) throws IOException {
        FileOutputStream fileOutputStream = openFileOutput(fileName,
                MODE_PRIVATE);
        String line;
        while ((line = inputReader.readLine()) != null) {
            fileOutputStream.write(line.getBytes());
        }
        fileOutputStream.close();
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
//另一种方法：
//    public static synchronized void runShell() {
//        ProcessBuilder pb = new ProcessBuilder("/system/bin/sh");
//        // java.lang.ProcessBuilder: Creates operating system processes.
//        pb.directory(new File("/system/bin"));// 设置shell的当前目录。
//        try {
//            Process proc = pb.start();
//            // 获取输入流，可以通过它获取SHELL的输出。
//            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//            BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
//            // 获取输出流，可以通过它向SHELL发送命令。
//            PrintWriter out = new PrintWriter(
//                    new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
//            out.println("pwd");
//            out.println("su root");// 执行这一句时会弹出对话框（以下程序要求授予最高权限...），要求用户确认。
//            out.println("cat /proc/version");
//            out.println("monkey -v -p com.daodao.qiandaodao 500");
//            // out.println("cd /data/data");//这个目录在系统中要求有root权限才可以访问的。
//            // out.println("ls -l");//这个命令如果能列出当前安装的APK的数据文件存放目录，就说明我们有了ROOT权限。
//            out.println("exit");
//            // proc.waitFor();
//            String line;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line); // 打印输出结果
//            }
//            while ((line = err.readLine()) != null) {
//                System.out.println(line); // 打印错误输出结果
//            }
//            in.close();
//            out.close();
//            proc.destroy();
//        } catch (Exception e) {
//            System.out.println("exception:" + e);
//        }
//    }
}
