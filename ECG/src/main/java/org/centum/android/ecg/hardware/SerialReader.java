package org.centum.android.ecg.hardware;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Streams data from the device and adds it to the parser
 * Whenever a valid packet of data is recovered, an event is fired
 * <p/>
 * Created by Phani on 4/10/2014.
 */
public class SerialReader extends ECGParser implements Runnable {

    private static SerialReader instance = null;

    private static final int BAUD_RATE = 115200;

    private Context context;
    private Physicaloid physicaloid;
    private Handler handler;

    private boolean connected = false;

    private List<DataListener> dataListeners = new ArrayList<DataListener>();
    private List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();

    private Thread thread = null;

    private SerialReader(Context context) {
        this.context = context;
        handler = new Handler(context.getMainLooper());
        physicaloid = new Physicaloid(context);
        physicaloid.setBaudrate(BAUD_RATE);
    }

    /**
     * Try to connect to the device
     *
     * @return Whether the connection is successful
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean attemptConnect() throws IOException, InterruptedException {
        if (!physicaloid.isOpened()) {
            if (connected) {
                fireOnDisconnect();
                connected = false;
            }
            if (physicaloid.open()) {
                connected = true;
                physicaloid.setConfig(new UartConfig(BAUD_RATE, UartConfig.DATA_BITS8, UartConfig.STOP_BITS1, UartConfig.PARITY_NONE, false, false));
                startReading();
                fireOnConnect();
            } else {
                stopReading();
                return false;
            }
        }
        return true;
    }

    public boolean isConnected() {
        return physicaloid.isOpened();
    }

    /**
     * Constantly read data and add it to the parser
     */
    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        int readBytes;
        Log.d("SerialReader", "SerialReader Reading...");
        try {
            while (!Thread.interrupted() && physicaloid.isOpened()) {
                connected = true;
                readBytes = physicaloid.mSerial.read(buffer, buffer.length);
                if (readBytes > 0)
                    addBytes(buffer, readBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (physicaloid.isOpened()) {
            physicaloid.close();
        }
        connected = false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                fireOnDisconnect();
            }
        });
        Log.d("SerialReader", "SerialReader Stopped");
    }


    private void addBytes(byte[] buffer, int readBytes) {
        for (int i = 0; i < readBytes; i++) {
            addByte(buffer[i]);
        }
    }

    public boolean isReading() {
        return (thread != null && thread.isAlive());
    }

    public void startReading() {
        if (!isReading()) {
            thread = new Thread(this, "Serial Reader");
            thread.start();
        }
    }

    public void stopReading() throws InterruptedException, IOException {
        if (isReading()) {
            thread.interrupt();
            physicaloid.close();
        }
    }

    @Override
    public void onValidData(final long time, final int chan, final int val) {
        for (DataListener dataListener : dataListeners) {
            dataListener.dataRead(time, chan, val);
        }
    }

    private void fireOnConnect() {
        for (ConnectionListener connectionListener : connectionListeners) {
            connectionListener.onConnect(physicaloid);
        }
        Log.d("SerialReader", "onConnect Fired");
    }

    private void fireOnDisconnect() {
        for (ConnectionListener connectionListener : connectionListeners) {
            connectionListener.onDisconnect(physicaloid);
        }
        Log.d("SerialReader", "onDisconnect Fired");
    }

    public void addDataListener(DataListener dataListener) {
        if (!dataListeners.contains(dataListener)) {
            dataListeners.add(dataListener);
        }
    }

    public void removeDataListener(DataListener dataListener) {
        dataListeners.remove(dataListener);
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        if (!connectionListeners.contains(connectionListener)) {
            connectionListeners.add(connectionListener);
        }
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        connectionListeners.remove(connectionListener);
    }

    public static SerialReader getInstance(Context context) {
        if (instance == null) {
            instance = new SerialReader(context);
        }
        return instance;
    }

    public static SerialReader getInstance() {
        return instance;
    }

}
