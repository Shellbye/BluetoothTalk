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

    private ListView msgListView;
    private EditText inputText;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<>();
    BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "In Msg onCreate");
        Intent intent = getIntent();
        device = intent.getExtras().getParcelable("device");
        setContentView(R.layout.activity_msg);
//        initMsgs();
        adapter = new MsgAdapter(MsgActivity.this, R.layout.msg_item);
        inputText = (EditText) findViewById(R.id.input_text);
        Button send = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    BTalkApplication.talkThread.write(content.getBytes());
//                    Msg msg = new Msg(content, Msg.TYPE_SENT);
//                    msgList.add(msg);
//                    adapter.add(msg); // 当有新消息时,刷新ListView中的显示
                    msgListView.setSelection(msgList.size()); // 将ListView定位到最后一行
                    inputText.setText("");// 清空输入框中的内容
                }
            }
        });
        if (BTalkApplication.APP_STATUS == Constant.LISTENING) {
            BTalkApplication.acceptThread.cancel();
            BTalkApplication.connectThread = new ConnectThread(device);
            BTalkApplication.connectThread.start();
            BTalkApplication.APP_STATUS = Constant.TRY_CONNECTTING;
        } else if (BTalkApplication.APP_STATUS == Constant.CONNECTED) {
            BTalkApplication.talkThread.setmHandler(mHandler);
        } else {
            String ss;
        }
    }

//    private void initMsgs() {
//        Msg msg1 = new Msg("Hello guy.", Msg.TYPE_RECEIVED);
//        msgList.add(msg1);
//        Msg msg2 = new Msg("Hello. Who is that?", Msg.TYPE_SENT);
//        msgList.add(msg2);
//        Msg msg3 = new Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED);
//        msgList.add(msg3);
//    }

    public class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                Log.v(TAG, Constant.MY_UUID.toString());
                tmp = mmDevice.createRfcommSocketToServiceRecord(Constant.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            BTalkApplication.getBluetoothAdapter().cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.v(TAG, "Try to connect");
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }
            Log.v(TAG, "Connected!!!");
            BTalkApplication.APP_STATUS = Constant.CONNECTED;
            // Do work to manage the connection (in a separate thread)
            BTalkApplication.talkThread = new TalkThread(mmSocket, mHandler);
            BTalkApplication.talkThread.start();
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

            }
        }
    };

}
