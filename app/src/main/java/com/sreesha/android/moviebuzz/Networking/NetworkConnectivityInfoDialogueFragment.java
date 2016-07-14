package com.sreesha.android.moviebuzz.Networking;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Sreesha on 18-10-2015.
 */
public class NetworkConnectivityInfoDialogueFragment extends DialogFragment {
    private Bundle arguments;

    public NetworkConnectivityInfoDialogueFragment() {

    }

    public interface ConfirmationDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    ConfirmationDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ConfirmationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        arguments = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            builder.setMessage(arguments.get("message").toString());
        } catch (NullPointerException e) {
            builder.setMessage("Error in Code");
        }
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(NetworkConnectivityInfoDialogueFragment.this);
            }
        });
        return builder.create();
    }
}
