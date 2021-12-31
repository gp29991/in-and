package com.sm.in_and;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SortOptionsDialogCategory extends DialogFragment {
    private RadioGroup rgSortBy;
    private RadioGroup rgSortType;

    private SortOptionsDialogCategoryListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.sort_options_dialog_category, null);

        rgSortBy = view.findViewById(R.id.rgSortBy);
        rgSortType = view.findViewById(R.id.rgSortType);

        builder.setView(view)
                .setTitle("Opcje sortowania")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sortBy = "";
                        String sortType = "";
                        int checkedId = rgSortBy.getCheckedRadioButtonId();
                        if(checkedId == R.id.rbCategory){
                            sortBy = "categoryname";
                        }else if(checkedId == R.id.rbAmount){
                            sortBy = "amount";
                        }
                        checkedId = rgSortType.getCheckedRadioButtonId();
                        if(checkedId == R.id.rbAsc){
                            sortType = "asc";
                        }else if(checkedId == R.id.rbDesc){
                            sortType = "desc";
                        }
                        listener.setSortOptions(sortBy, sortType);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (SortOptionsDialogCategoryListener) context;
    }

    public interface SortOptionsDialogCategoryListener{
        void setSortOptions(String sortBy, String sortType);
    }
}
