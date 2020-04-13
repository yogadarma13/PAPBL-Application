package com.yogadarma.papblapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText etNameRegis, etEmailRegis, etPasswordRegis, etRepasswordRegis;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Dialog mDialog;
    RegisterCallback registerCallback;
    private CheckBox cbPasswordVisibilityRegister;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                mDialog.dismiss();
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

        View contentView = View.inflate(getContext(), R.layout.fragment_register, null);
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

    private void initView(View view) {
        firebaseAuth = FirebaseAuth.getInstance();

        etNameRegis = view.findViewById(R.id.et_name_register);
        etEmailRegis = view.findViewById(R.id.et_email_register);
        etPasswordRegis = view.findViewById(R.id.et_password_register);
        etRepasswordRegis = view.findViewById(R.id.et_repassword_register);
        cbPasswordVisibilityRegister = view.findViewById(R.id.cb_password_visibility_register);
        btnRegister = view.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);

        cbPasswordVisibilityRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    etPasswordRegis.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etRepasswordRegis.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPasswordRegis.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etRepasswordRegis.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                final String name = etNameRegis.getText().toString();
                final String email = etEmailRegis.getText().toString();
                String password = etPasswordRegis.getText().toString();
                String rePassword = etRepasswordRegis.getText().toString();
                if (!(name.isEmpty() && email.isEmpty() && password.isEmpty() && rePassword.isEmpty())) {
                    if (password.equals(rePassword)) {
                        registerCallback.onRegisterButtonClick(name, email, password);
                    } else {
                        Toast.makeText(getActivity(), "Password tidak sama", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Lengkapi data", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void setRegisterCallback(RegisterCallback callback){
        registerCallback = callback;
    }

    public interface RegisterCallback {
        void onRegisterButtonClick(String name, String email, String password);
    }
}
