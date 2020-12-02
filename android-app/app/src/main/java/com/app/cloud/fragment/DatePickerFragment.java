package com.app.cloud.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.cloud.R;
import com.app.cloud.listeners.DialogListener;

public class DatePickerFragment extends DialogFragment implements View.OnClickListener {

    DatePicker datePicker;
    Button done;
    DialogListener dialogListener;

    public DatePickerFragment(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        datePicker = view.findViewById(R.id.date_picker);
        done = view.findViewById(R.id.done_btn);
        done.setOnClickListener(this);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        String sDay;
        String sMonth;
        int day =  datePicker.getDayOfMonth();
        int month = datePicker.getMonth()+1;
        if(day < 10){
            sDay = String.format("%02d", day) +"/";
        }else sDay = day+"/";

        if(month < 10){
            sMonth = String.format("%02d", month) +"/";
        }else sMonth = month+"/";

        String date = sDay+ sMonth +datePicker.getYear();
        dialogListener.setDate(date);
        dismiss();
    }
}