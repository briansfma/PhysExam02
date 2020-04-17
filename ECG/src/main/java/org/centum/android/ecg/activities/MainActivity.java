package org.centum.android.ecg.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.physicaloid.lib.Physicaloid;

import org.centum.android.ecg.R;
import org.centum.android.ecg.fragments.ConnectFragment;
import org.centum.android.ecg.fragments.GraphFragment;
import org.centum.android.ecg.hardware.ConnectionListener;
import org.centum.android.ecg.hardware.SerialReader;
import org.centum.android.ecg.network.ParseInterface;
import org.centum.android.ecg.utils.Fonts;

/**
 * Entry point for the app, contains the different fragments (connect, graph)
 */
public class MainActivity extends Activity implements ConnectionListener {

    private Fragment[] fragments = new Fragment[]
            {new ConnectFragment(), new GraphFragment()};
    private int currentFragment = 0;

    /**
     * Initialize everything, including singletons
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SerialReader.getInstance(this).addConnectionListener(this);
        Fonts.loadFonts(this);
        ParseInterface.init(this);
        switchToConnectFragment();
    }

    public void switchToConnectFragment() {
        switchToFragment(0, true);
    }

    public void switchToGraphFragment() {
        switchToFragment(1, true);
    }

    private void switchToFragment(int i, boolean forward) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragments[i])
                .setCustomAnimations(forward ? R.anim.slide_in_right : R.anim.slide_in_left,
                        forward ? R.anim.slide_out_left : R.anim.slide_out_right)
                .commitAllowingStateLoss();
        currentFragment = i;
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(i > 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment > 0) {
            switchToFragment(currentFragment - 1, false);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Callback for when a device is connected to Physicaloid from SerialReader
     * Graph fragment is automatically shown when connected
     *
     * @param physicaloid
     */
    @Override
    public void onConnect(Physicaloid physicaloid) {
        switchToGraphFragment();
    }

    /**
     * Disconnect callback for the serial reader
     *
     * @param physicaloid
     */
    @Override
    public void onDisconnect(Physicaloid physicaloid) {

    }

}
