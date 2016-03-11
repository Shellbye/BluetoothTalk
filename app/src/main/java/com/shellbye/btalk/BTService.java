package com.shellbye.btalk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by shellbye on 16/3/11.
 */
public class BTService {

    private static String TAG = "BTService";

    AcceptThread acceptThread;
    ConnectThread connectThread;
    TalkThread talkThread;
    Handler mHandler;
    BluetoothAdapter bluetoothAdapter;

    public BTService(Handler mHandler) {
        this.mHandler = mHandler;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startAndListen() {
        acceptThread = new AcceptThread();
        acceptThread.start();
        BTalkApplication.APP_STATUS = Constant.LISTENING;
    }

    public synchronized void tryConnect(BluetoothDevice device) {
        acceptThread.cancel();
        connectThread = new ConnectThread(device);
        connectThread.start();
        BTalkApplication.APP_STATUS = Constant.TRY_CONNECTING;
    }

    public synchronized void connected(BluetoothSocket socket) {
        Log.v(TAG, "Entering connected!");
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        talkThread = new TalkThread(socket);
        talkThread.start();
        BTalkApplication.APP_STATUS = Constant.CONNECTED;
    }

    public void write(byte[] bytes) {
        talkThread.write(bytes);
    }

    public void stop() {
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (talkThread != null) {
            talkThread.cancel();
            talkThread = null;
        }
        BTalkApplication.APP_STATUS = Constant.NONE;
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                Log.v(TAG, Constant.MY_UUID.toString());
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                        bluetoothAdapter.getName(),
                        Constant.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (BTalkApplication.APP_STATUS != Constant.CONNECTED) {
                try {
                    Log.v(TAG, "Waiting for connection...");
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                Log.v(TAG, "Server Connected!");
                try {
                    if (socket != null) {
                        mHandler.obtainMessage(Constant.SERVER_CONNECTED, -1, -1, null)
                                .sendToTarget();
                        // 这里不需要加锁是因为这里关闭的是serverSocket,
                        // 而不是传输要用到的socket
                        connected(socket);
                        // 这里也许不应该close
                        mmServerSocket.close();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread {
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
            bluetoothAdapter.cancelDiscovery();

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

            // 这里显示的将connectThread置为null有必要么?
            // 为啥在这里加锁之后就成功了呢?
            // 好吧,原因在于在connected函数内部,connectThread调用了cancel,关闭了socket...
            synchronized (BTService.this) {
                connectThread = null;
            }
            mHandler.obtainMessage(Constant.CLIENT_CONNECTED, -1, -1, null)
                    .sendToTarget();

            Log.v(TAG, "Client Connected!");
            connected(mmSocket);
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

    /**
     * Created by shellbye on 16/3/10.
     */
    private class TalkThread extends Thread {

        private final String TAG = "TalkThread";

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public TalkThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            Log.i(TAG, "Start talk!");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream, 这里总是socket closed
                    // 在connectThread内部连接成功之后加锁杀掉connectThread之后居然成功了?
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Constant.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    mHandler.obtainMessage(Constant.LOST_CONNECTION, -1, -1, null)
                            .sendToTarget();
                    BTalkApplication.APP_STATUS = Constant.NONE;
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Constant.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
                mHandler.obtainMessage(Constant.LOST_CONNECTION, -1, -1, null)
                        .sendToTarget();
                BTalkApplication.APP_STATUS = Constant.NONE;
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
