package com.shellbye.btalk;

import java.util.UUID;

/**
 * Created by shellbye on 16/3/9.
 */
public class Constant {
    // todo why the UUID 0000110a-0000-1000-8000-00805f9b34fb not working?
    public final static UUID MY_UUID_NOT_WORKING = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");
    public final static UUID MY_UUID= UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public final static int NONE = -1;
    public final static int LISTENING = 0;
    public final static int TRY_CONNECTING = 1;
    public final static int CONNECTED = 21;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int SERVER_CONNECTED = 4;
    public static final int CLIENT_CONNECTED = 5;
    public static final int LOST_CONNECTION = 6;
}
