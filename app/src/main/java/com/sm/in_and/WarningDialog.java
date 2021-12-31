package com.sm.in_and;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class WarningDialog extends DialogFragment {
    private TextView tvMessage;
    private TextView tvWarning;

    private WarningDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.warning_dialog_layout, null);

        tvMessage = view.findViewById(R.id.tvMessage);
        tvWarning = view.findViewById(R.id.tvWarning);

        String message = getArguments().getString("message");
        if(message == null){
            tvMessage.setVisibility(View.INVISIBLE);
        }else{
            tvMessage.setText(message);
        }
        message = getArguments().getString("warning");
        if(message == null){
            tvWarning.setVisibility(View.INVISIBLE);
        }else{
            tvWarning.setText(message);
        }
        int position = getArguments().getInt("position");

        builder.setView(view)
                .setTitle("Potwierdzenie wykonania działania")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.WarningDialogConfirmed(position);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (WarningDialogListener) context;
    }

    public interface  WarningDialogListener{
        void WarningDialogConfirmed(int position);
    }
}
