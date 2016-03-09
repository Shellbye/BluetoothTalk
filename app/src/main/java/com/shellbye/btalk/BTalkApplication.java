package com.shellbye.btalk;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

/**
 * Created by shellbye on 16/3/8.
 */
public class BTalkApplication extends Application {
    // http://stackoverflow.com/a/8188316/1398065
    private final static int REQUEST_ENABLE_BT = 1;

    private static Context context;
    private static BluetoothAdapter bluetoothAdapter;
    public static MainActivity.AcceptThread acceptThread;
    public static Thread connectThread;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // 检测是否支持蓝牙功能
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e("MainActivity", "Bluetooth not working");
            return;
        }

        // 检测蓝牙是否开启
        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            // need android.permission.BLUETOOTH_ADMIN
            bluetoothAdapter.enable();
        }
    }

    public static BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }

    public static Context getContext() {
        return context;
    }
}
