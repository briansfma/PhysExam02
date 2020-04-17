package org.centum.android.ecg.hardware;

/**
 * Created by Phani on 6/1/2014.
 * Extracts and validates data from the serial stream
 */
/*
    This handles the USB data that gets transferred in. The USB data is decoded and then transferred
 */
public abstract class ECGParser {

    private static final int STATE_IDLE = 0;
    private static final int STATE_DATA = 1;

    private int state = STATE_IDLE;

    private long timeOffset = System.currentTimeMillis();
    private int[] packet = new int[4];
    private int byteNum = 0;

    //checks a new byte that came into the stream. When 0XXXXXXX 1XXXXXXX 1XXXXXXX 1XXXXXXX
    // is found, it uses the validate function to extract and display it.
    public void addByte(byte byt) {
        int b = (int) byt & 255;
        switch (state) {
            case STATE_IDLE:
                if ((b >> 7) == 0) {
                    state = STATE_DATA;
                    packet[0] = b;
                    byteNum = 1;
                }
                break;
            case STATE_DATA:
                if ((b >> 7) == 1) {
                    packet[byteNum] = b;
                    byteNum++;
                    if (byteNum == 4) {
                        state = STATE_IDLE;
                        validate();
                    }
                } else {
                    state = STATE_IDLE;
                }
                break;
        }
    }

    private void validate() {
        int currentChan = packet[0] >> 3;
        for (int i = 1; i < 4; i++) {
            packet[i] = packet[i] & 0x7F;
        }
        int currentVal = ((packet[0] & 7) << 21) + (packet[1] << 14) + (packet[2] << 7) + packet[3];
        if (currentChan <= 7 && currentChan >= 0) {
            onValidData(System.currentTimeMillis() - timeOffset, currentChan, currentVal);
        }
    }
    //this is handled in SerialReader.java
    public abstract void onValidData(long time, int chan, int data);

}
