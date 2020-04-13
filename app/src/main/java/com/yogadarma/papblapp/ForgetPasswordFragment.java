package com.yogadarma.papblapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordFragment extends BottomSheetDialogFragment implements View.OnClickListener{

    private Dialog mDialog;
    private FirebaseAuth firebaseAuth;
    private EditText etEmailForget;
    private Button btnSendEmailReset;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        mDialog = dialog;

        View contentView = View.inflate(getContext(), R.layout.fragment_forget_password, null);
        dialog.setContentView(contentView);

        ((View) contentView.getParent()).setBackgroundColor(Color.TRANSPARENT);
//        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppTheme_BottomSheetDialogTheme);

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetBehavior) behavior);
            bottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        initView(contentView);
    }

    private void initView(View view){
        firebaseAuth = FirebaseAuth.getInstance();

        etEmailForget = view.findViewById(R.id.et_email_forget);
        btnSendEmailReset = view.findViewById(R.id.btn_send_email_reset);
        btnSendEmailReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_email_reset){
            String emailReset = etEmailForget.getText().toString();

            if (emailReset.isEmpty()){
                Toast.makeText(getActivity(), "Isikan alamat email", Toast.LENGTH_SHORT).show();
            } else {
                sendEmailNewPassword(emailReset);
            }
        }
    }

    private void sendEmailNewPassword(String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Email reset password telah dikirim, silahkan cek email", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Email tidak terdaftar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
