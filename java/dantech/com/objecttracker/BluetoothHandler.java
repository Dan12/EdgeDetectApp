package dantech.com.objecttracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
        Thread openDeviceThread = new Thread(new Runnable() {
            public void run() {
                if (pairedDevices.size() > 0) {
                    boolean success = false;
                    int tryNum = 1;
                    for (BluetoothDevice device : pairedDevices) {
                        toastMessage("Bluetooth try "+tryNum+"/"+pairedDevices.size());
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
                        tryNum++;
                    }
                    if (!success)
                        toastMessage("Failed To Open Any Device");
                    else{}
                        //System.out.println("Bluetooth Device Found");
                }
                findingBluetooth = false;
                launcher.getDrawView().getMenu().btDone();
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
