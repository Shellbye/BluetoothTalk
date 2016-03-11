package com.shellbye.btalk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MsgActivity extends AppCompatActivity {

    private static String TAG = "MsgActivity";

    private EditText inputText;
    private MsgAdapter adapter;
    private BTService service;
    BluetoothDevice device;
    Button send;
    ListView msgListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.msg_toolbar);
        setSupportActionBar(toolbar);
        Log.v(TAG, "In Msg onCreate");
        service = new BTService(mHandler);
        service.startAndListen();


        Intent intent = getIntent();
        device = intent.getExtras().getParcelable("device");
        adapter = new MsgAdapter(MsgActivity.this, R.layout.msg_item);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BTalkApplication.APP_STATUS != Constant.CONNECTED) {
                    Toast.makeText(MsgActivity.this,
                            "尚未连接,请点击右上角连接!", Toast.LENGTH_LONG).show();
                    return;
                }
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    service.write(content.getBytes());
                    inputText.setText("");// 清空输入框中的内容
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "In Msg onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_msg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "In Msg onOptionsItemSelected");
        int id = item.getItemId();

        if (id == R.id.start_connect) {
            service.tryConnect(device);
            Toast.makeText(MsgActivity.this,
                    "正在连接,请稍后...", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "In Msg onStop");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.v(TAG, "In Msg onBackPressed");
        service.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "In Msg onDestroy");
        service.stop();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Msg msg1 = new Msg(writeMessage, Msg.TYPE_SENT);
                    adapter.add(msg1);
                    break;
                case Constant.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Msg msg2 = new Msg(readMessage, Msg.TYPE_RECEIVED);
                    adapter.add(msg2);
                    break;
                case Constant.SERVER_CONNECTED:
                    Toast.makeText(MsgActivity.this,
                            "连接成功,可以开始聊天了!", Toast.LENGTH_LONG).show();
                    break;
                case Constant.CLIENT_CONNECTED:
                    Toast.makeText(MsgActivity.this,
                            "连接成功,可以开始聊天了!", Toast.LENGTH_LONG).show();
                    break;
                case Constant.LOST_CONNECTION:
                    Toast.makeText(MsgActivity.this,
                            "连接已断开,请重新连接!", Toast.LENGTH_LONG).show();
                    service.stop();
                    service.startAndListen();
                    break;
            }
        }
    };

}
