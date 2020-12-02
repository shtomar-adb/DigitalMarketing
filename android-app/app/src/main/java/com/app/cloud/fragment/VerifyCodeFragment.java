package com.app.cloud.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.cloud.R;
import com.app.cloud.listeners.DialogListener;

public class VerifyCodeFragment extends DialogFragment implements View.OnClickListener {

    DialogListener dialogListener;
    EditText verificationCode;
    Button submit;

    public VerifyCodeFragment(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_verify_code, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verificationCode = view.findViewById(R.id.verify_edit_text);
        submit = view.findViewById(R.id.submit_btn);
        submit.setOnClickListener(this);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onClick(View v) {
        dialogListener.submit(verificationCode.getText().toString());
    }
}
