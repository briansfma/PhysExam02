package org.centum.android.ecg.network;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.centum.android.ecg.model.Patient;

/**
 * Created by Phani on 8/13/2014.
 */
public class ParseInterface {

    private static final String APPLICATION_ID = "Nw4MT2M1mzEaUTBmHCHi7ENYAvEohqAp9tibXWoT";
    private static final String CLIENT_KEY = "nc76PnaUla931bpSXyrDbElQfr6J5sFKm3vghSoo";

    private static ParseInterface instance = null;
    private Context context;

    private ParseObject p = null;

    private ParseInterface(Context context) {
        this.context = context;
        Parse.initialize(context, APPLICATION_ID, CLIENT_KEY);
    }

    public void uploadPatient(final Patient patient) throws ParseException {
        p = null;
        //If the patient already has a parse ID, pull that object to update
        if (patient.getParseObjectId() != null) {
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Patient");
                p = query.get(patient.getParseObjectId());
                patient.populateParseObject(p);
            } catch (ParseException e) {
                p = null;
            }
        }

        //New parse object if none exists
        if (p == null) {
            p = patient.getParseObject();
        }
        p.saveEventually();
        patient.setParseObjectId(p.getObjectId());
    }

    public static ParseInterface get() {
        return instance;
    }

    public static void init(Context context) {
        instance = new ParseInterface(context);
    }

}
