package org.centum.android.ecg.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.parse.ParseException;

import org.centum.android.ecg.R;
import org.centum.android.ecg.model.Patient;
import org.centum.android.ecg.network.ParseInterface;

/**
 * Created by Phani on 4/10/2014.
 */
/*
    This handles the actions when the Patient Dialog box is shown.
 */
public class PatientDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText firstNameField, lastNameField, dobField, notesField;
    private Button cancelButton, saveButton;
    private CheckBox maleCheckBox, femaleCheckBox;
    private boolean forceValid = true;
    private boolean initialized = false;
    private boolean supressCheck = false;

    private Patient patient = null;

    public PatientDialogFragment() {
        setCancelable(false);
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        updateFields();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_dialog, container);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        firstNameField = (EditText) view.findViewById(R.id.first_editText);
        lastNameField = (EditText) view.findViewById(R.id.last_editText);
        dobField = (EditText) view.findViewById(R.id.dob_editText);
        notesField = (EditText) view.findViewById(R.id.notes_editText);
        maleCheckBox = (CheckBox) view.findViewById(R.id.male_checkBox);
        femaleCheckBox = (CheckBox) view.findViewById(R.id.female_checkBox);

        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        saveButton = (Button) view.findViewById(R.id.save_button);

        cancelButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        maleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!supressCheck) {
                    supressCheck = true;
                    femaleCheckBox.setChecked(!isChecked);
                    supressCheck = false;
                }
            }
        });

        femaleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!supressCheck) {
                    supressCheck = true;
                    maleCheckBox.setChecked(!isChecked);
                    supressCheck = false;
                }
            }
        });

        initialized = true;
        updateFields();

        return view;
    }

    public void updateFields() {
        if (initialized) {
            if (patient != null) {
                firstNameField.setText(patient.getFirstName());
                lastNameField.setText(patient.getLastName());
                notesField.setText(patient.getNotes());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
                break;
            case R.id.save_button:
                onSaveAction();
                break;
        }
    }

    public void onSaveAction() {
        if (validateInformation() || !isForceValid()) {
            if (patient == null) {
                patient = new Patient();
            }
            patient.setFirstName(firstNameField.getText().toString().trim());
            patient.setLastName(lastNameField.getText().toString().trim());
            patient.setDob(dobField.getText().toString().trim());
            patient.setNotes(notesField.getText().toString().trim());
            patient.setGender(maleCheckBox.isChecked() ? "Male" : "Female");
            try {
                ParseInterface.get().uploadPatient(patient);
            } catch (ParseException e) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("Uploading Failed")
                        .show();
            }
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        }
    }

    private boolean validateInformation() {
        boolean valid = true;
        try {
            if (TextUtils.isEmpty(dobField.getText().toString())) {
                dobField.setError(getString(R.string.text_empty_error));
                valid = false;
            } else {
                dobField.setError(null);
            }
            if (TextUtils.isEmpty(firstNameField.getText().toString())) {
                firstNameField.setError(getString(R.string.text_empty_error));
                valid = false;
            } else {
                firstNameField.setError(null);
            }
            if (TextUtils.isEmpty(lastNameField.getText().toString())) {
                lastNameField.setError(getString(R.string.text_empty_error));
                valid = false;
            } else {
                lastNameField.setError(null);
            }
        } catch (Exception e) {
            return false;
        }
        return valid;
    }

    public boolean isForceValid() {
        return forceValid;
    }

    public void setForceValid(boolean forceValid) {
        this.forceValid = forceValid;
    }

    public Patient getPatient() {
        return patient;
    }
}
