package dantech.com.objecttracker;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothHandler {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private volatile boolean stopWorker;
    private volatile boolean stopDeviceFinder;
    private Launcher launcher;
    private boolean findingBluetooth;
    private String fullMessage;

    public BluetoothHandler(Launcher l){
        launcher = l;
        findingBluetooth = false;
        fullMessage = "";
    }

    public void findBT(){
        findingBluetooth = true;
        mmInputStream = null;
        mmOutputStream = null;
        mmDevice = null;
        mmSocket = null;
        stopWorker = true;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            //System.out.println("No bluetooth adapter available");
            toastMessage("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            launcher.startActivityForResult(enableBluetooth, 0);
        }

        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        stopDeviceFinder = false;
        if (pairedDevices.size() > 0) {
            CharSequence devices[] = new CharSequence[pairedDevices.size()];
            int i = 0;
            for (BluetoothDevice device : pairedDevices) {
                devices[i] = device.getName();
                i++;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(launcher.getApplicationContext());
            builder.setTitle("Pick a device");
            builder.setItems(devices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int k = 0;
                    for (BluetoothDevice device : pairedDevices) {
                        if(i == k) {
                            tryConnection(device);
                            break;
                        }
                        k++;
                    }

                }
            });

            builder.show();
        }
    }

    void tryConnection(BluetoothDevice tempDiv){
        final BluetoothDevice device = tempDiv;
        Thread openDeviceThread = new Thread(new Runnable() {
            public void run() {
                boolean success = false;
                for (int i = 0; i < 1; i++) {
                    toastMessage("Bluetooth try "+device.getName());
                    if(stopDeviceFinder) {
                        System.out.println("Stopping device finder");
                        break;
                    }
                    System.out.println(device.getName());
                    if (mmOutputStream != null) {
                        success = true;
                        break;
                    }
                    mmDevice = device;
                    try {
                        openBT();
                        //System.out.println("success here");
                        success = true;
                        break;
                    } catch (IOException e) {
                        //System.out.println("Failed");
                    }
                }
                if (!success)
                    toastMessage("Failed To Open The Device");
                else{}
                //System.out.println("Bluetooth Device Found");
                findingBluetooth = false;
            }
        });
        openDeviceThread.start();
    }

    private void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        //System.out.println("Bluetooth Opened");
        toastMessage("Bluetooth Opened");

        beginListenForData();
    }

    private void beginListenForData() {
        stopWorker = false;
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        if (launcher.getDrawView().canSendData())
                            sendData("" + launcher.getDrawView().getCenterX());
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    private void sendData(String m) throws IOException {
        String msg = m;
        mmOutputStream.write(msg.getBytes());
        System.out.println("Data Sent");
    }

    public void closeBT() throws IOException {
        stopWorker = true;
        stopDeviceFinder = true;
        if (mmOutputStream != null)
            mmOutputStream.close();
        if (mmInputStream != null)
            mmInputStream.close();
        if (mmSocket != null)
            mmSocket.close();
        System.out.println("BlueTooth Closed");
    }

    private void toastMessage(final String m){
        launcher.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(launcher.getApplicationContext(), m, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMessage() throws IOException {
        int bytesAvailable = mmInputStream.available();
        if(bytesAvailable > 0) {
            byte[] packetBytes = new byte[bytesAvailable];
            mmInputStream.read(packetBytes);
            for (int i = 0; i < bytesAvailable; i++) {
                byte b = packetBytes[i];
                //ignore new lines and returns
                if (b != 10 && b != 13) {
                    byte[] encodedBytes = new byte[1];
                    encodedBytes[0] = b;
                    String data = new String(encodedBytes, "US-ASCII");
                    if (!data.equals("*"))
                        fullMessage += data;
                    else {
                        //Do something with completed message
                        System.out.println(fullMessage);
                        fullMessage = "";
                    }
                }
            }
        }
    }

    public boolean canStartBT(){
        return !findingBluetooth;
    }
}
