package com.example.myapplication;



import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    UsbManager usbManager;
    UsbSerialPort serialPort;
    ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        executorService = Executors.newSingleThreadExecutor();

        // Detect available USB devices
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        if (!availableDrivers.isEmpty()) {
            // Get the first available driver
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
            if (connection != null) {
                // Open a port on the driver
                serialPort = driver.getPorts().get(0);
                try {
                    serialPort.open(connection);
                    serialPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                    // Start reading data
                    startIoManager();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startIoManager() {
        SerialInputOutputManager ioManager = new SerialInputOutputManager(serialPort, mListener);
        executorService.submit(ioManager);
    }

    // Data read listener
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
        @Override
        public void onNewData(byte[] data) {
            runOnUiThread(() -> {
                String receivedData = new String(data);
                saveToTextFile(receivedData);
            });
        }

        @Override
        public void onRunError(Exception e) {
            e.printStackTrace();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serialPort != null) {
            try {
                serialPort.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
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








