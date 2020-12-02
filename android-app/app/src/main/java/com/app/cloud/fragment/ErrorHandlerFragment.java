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

public class ErrorHandlerFragment extends DialogFragment implements View.OnClickListener {

    Button submit;
    String message;

    public ErrorHandlerFragment(String msg){
        this.message = msg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_error_message, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submit = view.findViewById(R.id.submit_btn);
        TextView msg = view.findViewById(R.id.error_message);
        submit.setOnClickListener(this);
        msg.setText(message);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}