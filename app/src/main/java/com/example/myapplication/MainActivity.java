package com.example.myapplication;



import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import android_serialport_api.UsbSerialDevice;
import android_serialport_api.UsbSerialInterface;

public class MainActivity extends AppCompatActivity {

    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // 設備初始化代碼...

        // 讀取數據
        if (device != null) {
            UsbDeviceConnection connection = usbManager.openDevice(device);
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);

            if (serialPort != null) {
                if (serialPort.open()) {
                    // 設置串口參數
                    serialPort.setBaudRate(115200);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);

                    // 註冊讀取數據的回調
                    serialPort.read(mCallback);
                }
            }
        }
    }

    // 設置接收數據的回調
    private final UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] data) {
            String receivedData = new String(data);
            saveToTextFile(receivedData);
        }
    };

    // 將數據存儲為.txt文件
    private void saveToTextFile(String data) {
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path, "received_data.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file, true);  // 追加模式
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.append(data);
            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




/////////////////////////////////////

/*
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.nio.ByteBuffer;
import android.hardware.usb.UsbEndpoint;
import java.nio.ByteBuffer;
///////////////////////////////////////

public class MainActivity extends Activity {
    private UsbEndpoint endpointIn;
    private UsbDeviceConnection connection;
    private TextView textView;
    private UsbManager usbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // 註冊 USB 裝置連接和權限請求的廣播接收器
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction("com.example.myapplication.USB_PERMISSION");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(usbReceiver,filter, Context.RECEIVER_NOT_EXPORTED);
        }
    }
    
    // 廣播接收器
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // 請求 USB 權限
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.example.myapplication.USB_PERMISSION"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    usbManager.requestPermission(device, permissionIntent);
                }
            } else if ("com.example.myapplication.USB_PERMISSION".equals(action)) {
                // 處理權限請求結果
                boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (granted && device != null) {
                    // 權限已授予，您可以開始與 USB 裝置進行通信
                    //textView.append(String.valueOf(usbReceiver));
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            char character = (char) event.getUnicodeChar();
            if (character != 0) {
                // 在 TextView 上添加字符
                textView.append(String.valueOf(character));
            }
        }
        return super.dispatchKeyEvent(event);
    }


    ///////////////////////////////
    //測試區
    ///////////////////////////////
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }
}
*/








