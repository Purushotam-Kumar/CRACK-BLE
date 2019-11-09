package com.connect.zero;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class ActionsActivity extends AppCompatActivity {
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btScanner;
    private BluetoothDevice devicesDiscovered;
    private BluetoothGatt bluetoothGatt;
    private Handler mHandler = new Handler();
    private TextView statusText;
    private TextView actionStatusText;

    //private String deviceMacAddress = "C5:76:DF:A6:33:57";
    //private String deviceMacAddress = "CD:66:99:15:C7:D2";
    private String deviceMacAddress = "F6:32:99:AD:86:C1";

    @SuppressLint("ResourceType")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.device_status);
        actionStatusText = findViewById(R.id.current_action);

        btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();
    }

    public void scanDevice(View view) {
        if (checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 1);
        } else if (btAdapter != null && !btAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        } else {
            startScanning();
        }
    }

    public void connect(View view) {
        try {
            showToast("Trying to connecting : " + deviceMacAddress);
            bluetoothGatt = devicesDiscovered.connectGatt(this, false, btGattCallback);
        } catch (Exception e) {
            showToast("Scan again and retry...");
        }
    }

    public void disconnect(View view) {
        try {
            Toast.makeText(getApplicationContext(), "Trying to disconnect : " + deviceMacAddress, Toast.LENGTH_SHORT).show();
            bluetoothGatt.disconnect();
        } catch (Exception e) {
            showToast("Scan again and retry...");
        }
    }

    public boolean unlock(View view) {
        UUID serviceID = UUID.fromString("00004300-0000-1000-8000-00805f9b34fb");
        UUID writeCharacteristics = UUID.fromString("00004400-0000-1000-8000-00805f9b34fb");
        if (bluetoothGatt == null) {
            showToast("Device not connected!");
            return false;
        } else {
            BluetoothGattService Service = bluetoothGatt.getService(serviceID);
            if (Service == null) {
                return false;
            } else {
                BluetoothGattCharacteristic charac = Service.getCharacteristic(writeCharacteristics);
                if (charac == null) {
                    showToast("Characteristic not found!");
                    return false;
                } else {
                    showToast("Unlocked!");
                    charac.setValue("#OP11?GoCode#@!>>");
                    boolean status = bluetoothGatt.writeCharacteristic(charac);
                    return status;
                }
            }
        }
    }

    public boolean lock(View view) {
        UUID serviceID = UUID.fromString("00004300-0000-1000-8000-00805f9b34fb");
        UUID writeCharacteristics = UUID.fromString("00004400-0000-1000-8000-00805f9b34fb");
        if (bluetoothGatt == null) {
            showToast("Device not connected!");
            return false;
        } else {
            BluetoothGattService Service = bluetoothGatt.getService(serviceID);
            if (Service == null) {
                return false;
            } else {
                BluetoothGattCharacteristic characteristic = Service.getCharacteristic(writeCharacteristics);
                if (characteristic == null) {
                    showToast("Characteristic not found!");
                    return false;
                } else {
                    characteristic.setValue("#OP10?GoCode#@!>>");
                    showToast("Locked");
                    boolean status = bluetoothGatt.writeCharacteristic(characteristic);
                    return status;
                }
            }
        }
    }

    public void startScanning() {
        showToast("Waiting for scanner...");
        devicesDiscovered = null;
        AsyncTask.execute(new Runnable() {
            public void run() {
                btScanner.startScan(scanCallback);
            }
        });
       /* mHandler.postDelayed(new Runnable() {
            public void run() {
                stopScanning();
            }
        }, 10000);*/
    }

    public void stopScanning() {
        showToast("Stopped scanning!");
        AsyncTask.execute(new Runnable() {
            public void run() {
                btScanner.stopScan(scanCallback);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    unlock(null);
                } catch (Exception e) {
                    showToast("Some error occurred!");
                }
            }
        }, 500);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            showToast("Scanning...");
            if (deviceMacAddress.equalsIgnoreCase(result.getDevice().getAddress())) {
                showToast("Device found!");
                devicesDiscovered = result.getDevice();
                btScanner.stopScan(scanCallback);
                connect(null);
            }
        }
    };

    private final BluetoothGattCallback btGattCallback = new BluetoothGattCallback() {
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println(newState);
            switch (newState) {
                case 0:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showToast("Device disconnected!");
                            lock(null);
                        }
                    });
                    break;
                case 2:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showToast("Device connected!");
                        }
                    });
                    bluetoothGatt.discoverServices();
                    break;
                default:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showToast("Unknown state discovered!");
                        }
                    });
            }
        }
    };

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults[0] != 0) {
            requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 1);
        } else {
            startScanning();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == -1) {
                startScanning();
            } else {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
            }
        }
    }

    public void showToast(String toastMessage) {
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

}