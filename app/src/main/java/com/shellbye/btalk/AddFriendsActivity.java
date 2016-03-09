package com.shellbye.btalk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AddFriendsActivity extends AppCompatActivity {

    List<Friend> friendList;
    FriendAdapter friendAdapter;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        BTalkApplication.getBluetoothAdapter().startDiscovery();

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
//        BTalkApplication.getBluetoothAdapter().cancelDiscovery();

        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(AddFriendsActivity.this,
                R.layout.friend_item, friendList);


        // 初始化朋友列表
        listView = (ListView) findViewById(R.id.strangerListView);
        listView.setAdapter(friendAdapter);
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v("AddFriendsActivity", "Found " + device.getName() +
                        " at " + device.getAddress());
                Friend friend = new Friend(device.getName());
                if (friendList.indexOf(friend) != -1) {
                    Log.v("AddFriendsActivity", "Already there.");
                    return;
                }
                friendList.add(friend);
                friendAdapter.notifyDataSetChanged();
                listView.setSelection(0);
            }
        }
    };
}
