package org.centum.android.ecg.model;

import android.util.Log;

import org.centum.android.ecg.hardware.DataListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Stores a recordings data for all channels
 * A datastore can be added as a listener to automatically gather new data
 * <p/>
 * Created by Phani on 4/11/2014.
 */
public class DataStore implements DataListener {

    private long creationTime = System.currentTimeMillis();
    private long dataTimes[][] = new long[8][10000];
    private int dataVals[][] = new int[8][10000];
    private int indexes[] = new int[8];

    private boolean printedOverflow = false;

    @Override
    public void dataRead(long time, int chan, int val) {
        if (indexes[chan] >= dataVals[0].length) {
            if (!printedOverflow)
                Log.d("DataStore", "Data array overflow!");
            printedOverflow = true;
        } else {
            dataVals[chan][indexes[chan]] = val;
            dataTimes[chan][indexes[chan]] = time;
            indexes[chan]++;
        }
    }

    public void writeCSV(File file) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.write(toString());
        writer.flush();
        writer.close();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int chan = 0; chan < 8; chan++) {
            for (int i = 0; i < indexes[chan]; i++) {
                builder.append(dataTimes[chan][i] + "," + chan + "," + dataVals[chan][i] + "\n");
            }
        }
        return builder.toString();
    }

    public long getCreationTime() {
        return creationTime;
    }
}
