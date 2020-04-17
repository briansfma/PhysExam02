package org.centum.android.ecg.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.centum.android.ecg.R;
import org.centum.android.ecg.graph.GraphSurfaceView;
import org.centum.android.ecg.hardware.DataListener;
import org.centum.android.ecg.hardware.SerialReader;
import org.centum.android.ecg.model.DataStore;
import org.centum.android.ecg.model.Patient;
import org.centum.android.ecg.SignalProcessing.RealTimeFilter;

/**
 * Created by Phani on 6/1/2014.
 */
/*
    This handles the actions on the screen that displays the ECG data.
 */
public class GraphFragment extends Fragment implements DataListener, View.OnClickListener {
//different colors for 8 of the ECG signals
    private static final int LINE_COLORS[] = new int[]{
            Color.parseColor("#F44336"), Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"), Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"),
            Color.parseColor("#CDDC39"), Color.parseColor("#FF9800")};
    //initialize the 8 graphs
    private GraphSurfaceView graphSurfaceViews[] = new GraphSurfaceView[8];
    private RealTimeFilter filters[] = new RealTimeFilter[8];
    private LinearLayout graphLinearLayout;
    private ImageButton recordButton;

    private boolean isRecording = false;

    private int[] updateCounter = new int[8];
    private DataStore currentDataStore;

    public GraphFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        LinearLayout leftGraphLayout = (LinearLayout) rootView.findViewById(R.id.LeftGraphLayout);
        LinearLayout rightGraphLayout = (LinearLayout) rootView.findViewById(R.id.RightGraphLayout);
        recordButton = (ImageButton) rootView.findViewById(R.id.recordButton);

        recordButton.setOnClickListener(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        //generates 8 graphs, and sets the color used for the ECG signal of the respective graph.
        for (int i = 0; i < 8; i+=2) {      // Handles 0,2,4,6 onto the Left side of screen
            graphSurfaceViews[i] = new GraphSurfaceView(getActivity());
            graphSurfaceViews[i].setLineColor(LINE_COLORS[i]);
            leftGraphLayout.addView(graphSurfaceViews[i], 0, params);

            filters[i] = new RealTimeFilter();
        }
        for (int i = 0; i < 8; i+=2) {      // Handles 1,3,5,7 onto the Right side of screen
            graphSurfaceViews[i] = new GraphSurfaceView(getActivity());
            graphSurfaceViews[i].setLineColor(LINE_COLORS[i]);
            rightGraphLayout.addView(graphSurfaceViews[i], 0, params);

            filters[i] = new RealTimeFilter();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getActionBar().show();
        }
        SerialReader.getInstance().addDataListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isRecording) {
            SerialReader.getInstance().removeDataListener(this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.graph, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    /*
        Executed when the user presses the record button
     */
    private void onRecordAction() {
        if (!isRecording) {
            if (!SerialReader.getInstance().isConnected()) {
                new AlertDialog.Builder(getActivity()).setTitle("No Hardware!")
                        .setMessage("No hardware is connected!").show();
            } else {
                startRecording();
            }
        } else {
            Toast.makeText(getActivity(), R.string.recording_in_progress, Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecording() {
        isRecording = true;
        currentDataStore = new DataStore();
        SerialReader.getInstance().addDataListener(currentDataStore);
        recordButton.setImageResource(R.drawable.ic_action_cancel);

        // Execute some code after 30 seconds have passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                onStopRecordAction();
            }
        }, 30000);
    }

    private void onStopRecordAction() {
        isRecording = false;
        SerialReader.getInstance().removeDataListener(currentDataStore);
        recordButton.setImageResource(R.drawable.ic_record);
        editPatientWithDataStore(currentDataStore);
    }

    private void editPatientWithDataStore(DataStore dataStore) {
        Patient patient = new Patient();
        patient.setDataCSV(dataStore.toString());
        patient.setMeasurementTime(dataStore.getCreationTime());

        PatientDialogFragment patientDialogFragment = new PatientDialogFragment();
        patientDialogFragment.setPatient(patient);
        patientDialogFragment.show(getFragmentManager(), "patient-dialog");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recordButton:
                if (isRecording) {
                    onStopRecordAction();
                } else {
                    onRecordAction();


                }
                break;
        }
    }
    //gets the data and displays it onto the graph.
    /*
        Note: Manipulate this line to incorporate filtering.
     */
    @Override
    public void dataRead(long time, int chan, int val) {
        updateCounter[chan]++;
        val = filters[chan].get(val);
        graphSurfaceViews[chan].add((int) (time % Integer.MAX_VALUE), val);
    }

}
