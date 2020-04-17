package org.centum.android.ecg.model;

import android.text.TextUtils;

import com.parse.ParseObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * All data associated with a Patient
 * <p/>
 * Created by Phani on 4/10/2014.
 */
public class Patient {

    public static final String KEY_PATIENT_ID = "patientId";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_DOB = "dob";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_DATA = "data";

    private long measurementTime;
    private String lastName = "", firstName = "";
    private String dob;
    private String notes = "";
    private String patientID = "";
    private String dataCSV = "";
    private String gender = "";
    private String parseObjectId = null;

    private List<PatientListener> listeners = new ArrayList<PatientListener>();

    public Patient() {
        measurementTime = System.currentTimeMillis();
        patientID = ((int) (Math.random() * 10000000000000000000000d)) + "";
    }

    public Patient clone() {
        Patient patient = new Patient();
        patient.setParseObjectId(parseObjectId);
        patient.setGender(gender);
        patient.setDataCSV(dataCSV);
        patient.setPatientID(patientID);
        patient.setNotes(notes);
        patient.setDob(dob);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setMeasurementTime(measurementTime);
        return patient;
    }

    public void populateParseObject(ParseObject p) {
        p.put(KEY_LAST_NAME, lastName);
        p.put(KEY_FIRST_NAME, firstName);
        p.put(KEY_DOB, dob);
        p.put(KEY_NOTES, notes);
        p.put(KEY_PATIENT_ID, patientID);
        p.put(KEY_DATA, dataCSV);
        p.put(KEY_GENDER, gender);
    }

    public ParseObject getParseObject() {
        ParseObject p = new ParseObject("Patient");
        populateParseObject(p);
        return p;
    }

    public String getParseObjectId() {
        return parseObjectId;
    }

    public void setParseObjectId(String parseObjectId) {
        this.parseObjectId = parseObjectId;
        firePatientChanged();
    }

    public long getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(long measurementTime) {
        this.measurementTime = measurementTime;
        firePatientChanged();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        firePatientChanged();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        firePatientChanged();
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
        firePatientChanged();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (notes == null || TextUtils.isEmpty(notes)) {
            this.notes = "None";
        } else {
            this.notes = notes.trim();
        }
        firePatientChanged();
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
        firePatientChanged();
    }

    public String getDateString() {
        Date date = new Date(getMeasurementTime());
        Format format = new SimpleDateFormat("MM_dd_HH_mm");
        return format.format(date).toString();
    }

    public void addPatientListener(PatientListener patientListener) {
        if (!listeners.contains(patientListener)) {
            listeners.add(patientListener);
        }
    }

    public void removePatientListener(PatientListener patientListener) {
        listeners.remove(patientListener);
    }

    private void firePatientChanged() {
        for (PatientListener pl : listeners) {
            pl.patientChanged(this);
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        firePatientChanged();
    }

    public String getDataCSV() {
        return dataCSV;
    }

    public void setDataCSV(String dataCSV) {
        this.dataCSV = dataCSV;
    }
}
