package com.app.cloud.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.cloud.R;
import com.app.cloud.listeners.PushDialogListener;

public class NotificationFragment extends DialogFragment implements View.OnClickListener {

    Button interested_btn;
    Button not_interested_btn;
    String message;
    String titleText;
    PushDialogListener listener;

    public NotificationFragment(String title ,String msg , PushDialogListener pushDialogListener){
        this.message = msg;
        this.titleText = title;
        listener = pushDialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        interested_btn = view.findViewById(R.id.interested_btn);
        not_interested_btn = view.findViewById(R.id.not_interested_btn);
        TextView title = view.findViewById(R.id.notificationTitle);
        TextView msg = view.findViewById(R.id.push_message);
        interested_btn.setOnClickListener(this);
        not_interested_btn.setOnClickListener(this);
        msg.setText(message);
        title.setText(titleText);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.interested_btn:
                listener.sendInterested();
                break;
            case R.id. not_interested_btn:
                listener.sendNotInterested();
                break;
        }
        dismiss();
    }
}