package org.centum.android.ecg.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.centum.android.ecg.R;
import org.centum.android.ecg.activities.MainActivity;
import org.centum.android.ecg.hardware.SerialReader;

import java.io.IOException;

/**
 * Created by Phani on 6/1/2014.
 */
/* This class handles the operations on the first screen that asks to connect hardware.
 */
public class ConnectFragment extends Fragment implements View.OnClickListener {

    private ImageButton connectImageButton;
    private TextView messageTextView;
    private TextView tapToContinueTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        messageTextView = (TextView) rootView.findViewById(R.id.message_textView);
        tapToContinueTextView = (TextView) rootView.findViewById(R.id.skip_textView);
        connectImageButton = (ImageButton) rootView.findViewById(R.id.connect_imageButton);

        connectImageButton.setOnClickListener(this);
        tapToContinueTextView.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_imageButton:
                onConnectAction();
                break;
            case R.id.skip_textView:
                if (getActivity() != null)
                    ((MainActivity) getActivity()).switchToGraphFragment();
                break;
        }
    }

    private void onConnectAction() {
        onConnectAction(true);
    }
    /*
        This method is executed when the user presses the "connect buttion"
     */
    private void onConnectAction(boolean updateText) {
        try {
            if (SerialReader.getInstance().attemptConnect()) {
                if (updateText) {
                    messageTextView.setText(R.string.connected);
                    messageTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            } else if (updateText) {
                messageTextView.setText(R.string.failed_to_connect);
                messageTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
