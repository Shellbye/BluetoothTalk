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
    private int imageId;
    private BluetoothDevice device;

    public Friend(String name, int imageId, BluetoothDevice device) {
        this.name = name;
        this.imageId = imageId;
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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static List<Friend> getFriends() {
        List<Friend> friendList = new ArrayList<>();
        Friend a = new Friend("FriendA", R.drawable.joker_small);
        Friend b = new Friend("FriendB", R.drawable.joker_small);
        Friend c = new Friend("FriendC", R.drawable.joker_small);
        Friend d = new Friend("FriendD", R.drawable.joker_small);
        Friend e = new Friend("FriendF", R.drawable.joker_small);
        Friend ee = new Friend("FriendG", R.drawable.joker_small);
        Friend aa = new Friend("FriendH", R.drawable.joker_small);
        Friend bb = new Friend("FriendI", R.drawable.joker_small);
        Friend cc = new Friend("FriendG", R.drawable.joker_small);
        Friend a2 = new Friend("FriendK", R.drawable.joker_small);
        Friend a3 = new Friend("FriendL", R.drawable.joker_small);
        Friend f = new Friend("FriendM", R.drawable.joker_small);
        friendList.add(a);
        friendList.add(b);
        friendList.add(c);
        friendList.add(d);
        friendList.add(e);
        friendList.add(ee);
        friendList.add(aa);
        friendList.add(bb);
        friendList.add(cc);
        friendList.add(a2);
        friendList.add(a3);
        friendList.add(f);
        return friendList;
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
