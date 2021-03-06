package com.shellbye.btalk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // http://stackoverflow.com/a/8188316/1398065
    private final static int REQUEST_ENABLE_BT = 1;

    private static String TAG = "MainActivity";
    private static BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.v(TAG, "In Main onCreate");

        // 检测是否支持蓝牙功能
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e("MainActivity", "Bluetooth not working");
            return;
        }

        // 检测蓝牙是否开启
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            // need android.permission.BLUETOOTH_ADMIN
            bluetoothAdapter.enable();
        }

        initFriendsList();

    }

    private void initFriendsList() {
        // 获取已配对的设备列表
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();


        // 获取朋友列表,通过已配对设备朋友化
        final List<Friend> friendList = Friend.getFriends(devices);
        FriendAdapter friendAdapter = new FriendAdapter(MainActivity.this,
                R.layout.friend_item, friendList);

        // 初始化朋友列表
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(friendAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = friendList.get(position);
                Intent intent = new Intent(MainActivity.this, MsgActivity.class);
                intent.putExtra("device", friend.getDevice());
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "In Main onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "In Main onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.add_friends) {
            // goto add friends activity
            Log.v(TAG, "Click add friends");
            Intent intent = new Intent(MainActivity.this, AddFriendsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            initFriendsList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
