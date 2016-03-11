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
import android.telephony.TelephonyManager;
import android.util.Log;
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
    BluetoothDevice device;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "In Msg onCreate");
        BTalkApplication.service = new BTService(mHandler);
        BTalkApplication.service.startAndListen();
        Intent intent = getIntent();
        device = intent.getExtras().getParcelable("device");
        setContentView(R.layout.activity_msg);
        adapter = new MsgAdapter(MsgActivity.this, R.layout.msg_item);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        ListView msgListView;
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

        send.setText("连接");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (send.getText() == "连接") {
                    BTalkApplication.service.tryConnect(device);
                    send.setText("发送");
                }
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    BTalkApplication.service.write(content.getBytes());
                    inputText.setText("");// 清空输入框中的内容
                }
            }
        });
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
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Msg msg2 = new Msg(readMessage, Msg.TYPE_RECEIVED);
                    adapter.add(msg2);
                    break;
                case Constant.SERVER_CONNECTED:
                    send.setText("发送");
                    break;
            }
        }
    };

}
