package org.centum.android.ecg.model;

/**
 * Created by Phani on 8/10/2014.
 */
public interface PatientManagerListener {

    public abstract void patientAdded(Patient p);

    public abstract void patientRemoved(Patient p);

}
