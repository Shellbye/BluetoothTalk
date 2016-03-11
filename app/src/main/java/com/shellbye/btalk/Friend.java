package com.shellbye.btalk;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by shellbye on 16/3/7.
 */
public class Friend {
    private String name;
    private String address;
    private BluetoothDevice device;

    public Friend(String name, int imageId, BluetoothDevice device) {
        this.name = name;
        this.device = device;
    }

    public Friend(String name, int imageId) {
        this(name, imageId, null);
    }

    public Friend(String name) {
        this(name, R.drawable.joker_small);
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static List<Friend> getFriends(Set<BluetoothDevice> devices) {
        List<Friend> friendList = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            Friend friend = new Friend(device.getName(), R.drawable.joker_small, device);
            friendList.add(friend);
        }
        return friendList;
    }

    public static List<Friend> getFriends(List<BluetoothDevice> devices) {
        List<Friend> friendList = new ArrayList<>();
        for (BluetoothDevice device : devices) {
            Friend friend = new Friend(device.getName(), R.drawable.joker_small);
            friendList.add(friend);
        }
        return friendList;
    }
}
