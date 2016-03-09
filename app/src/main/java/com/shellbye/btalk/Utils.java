package com.shellbye.btalk;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by shellbye on 16/3/8.
 */
public class Utils {
    public static boolean BluetoothUsable(BluetoothAdapter bluetoothAdapter) {
        return true;
    }

    public static void Toast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static UUID getUuidFromAdapter(BluetoothAdapter adapter) {

        try {
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids");

            ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter);

            return uuids[0].getUuid();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return UUID.randomUUID();
    }
}
