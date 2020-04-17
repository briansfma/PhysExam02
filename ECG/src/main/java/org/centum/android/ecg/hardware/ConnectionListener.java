package org.centum.android.ecg.hardware;

import com.physicaloid.lib.Physicaloid;

/**
 * Created by Phani on 6/1/2014.
 */
public interface ConnectionListener {

    public abstract void onConnect(Physicaloid physicaloid);

    public abstract void onDisconnect(Physicaloid physicaloid);

}
