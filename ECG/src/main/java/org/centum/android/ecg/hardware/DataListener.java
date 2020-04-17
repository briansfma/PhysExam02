package org.centum.android.ecg.hardware;

/**
 * Created by Phani on 4/10/2014.
 */
public interface DataListener {
    public abstract void dataRead(long time, int chan, int val);
}
