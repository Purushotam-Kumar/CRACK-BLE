package com.connect.zero;

import java.util.UUID;

public class HexaUtils {
    public static final byte[] MOBOLIZE = new byte[]{35, 4, 80, 49, 49, 63, 71, 111, 67, 111, 100, 101, 115, 36, 36, 62, 62};
    public static final byte[] IM_MOBOLIZE = new byte[]{35, 4, 80, 49, 48, 63, 71, 111, 67, 111, 100, 101, 115, 36, 36, 62, 62};
    public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public static final String WRITE_CHARACTERISTIC = "00004400-0000-1000-8000-00805f9b34fb";
    public static final String WRITE_SERVICE = "00004300-0000-1000-8000-00805f9b34fb";

    public HexaUtils() {
    }

    public UUID convertFromInteger(int i) {
        long MSB = 4096L;
        long LSB = -9223371485494954757L;
        long value = (long)(i & -1);
        return new UUID(4096L | value << 32, -9223371485494954757L);
    }
}
