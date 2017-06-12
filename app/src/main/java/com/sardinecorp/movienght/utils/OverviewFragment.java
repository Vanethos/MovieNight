package com.sardinecorp.movienght.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Gonçalo on 11/06/2017.
 */

public class OverviewFragment extends DialogFragment {

    public static OverviewFragment newInstance(String title, String overview) {
        OverviewFragment frag = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("overview", overview);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String overview = getArguments().getString("overview");
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(overview)
                .setPositiveButton("Ok", null)
                .create();
    }
}
