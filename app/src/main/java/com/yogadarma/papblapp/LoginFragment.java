package com.yogadarma.papblapp;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextView tvForgetPass;
    private Dialog mDialog;
    private ForgetPasswordFragment forgetPasswordFragment;
    private LoginCallback loginCallback;
    private CheckBox cbPasswordVisibilityLogin;
    private LinearLayout btnLoginGoogle;

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

        View contentView = View.inflate(getContext(), R.layout.fragment_login, null);
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

        etEmailLogin = view.findViewById(R.id.et_email_login);
        etPasswordLogin = view.findViewById(R.id.et_password_login);
        tvForgetPass = view.findViewById(R.id.tv_forget_pass);
        btnLogin = view.findViewById(R.id.btn_login);
        cbPasswordVisibilityLogin = view.findViewById(R.id.cb_password_visibility_login);
        btnLoginGoogle = view.findViewById(R.id.btn_login_google);
        btnLogin.setOnClickListener(this);
        tvForgetPass.setOnClickListener(this);
        btnLoginGoogle.setOnClickListener(this);

        forgetPasswordFragment = new ForgetPasswordFragment();

        cbPasswordVisibilityLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                final String email = etEmailLogin.getText().toString();
                String password = etPasswordLogin.getText().toString();
                if (!(email.isEmpty() && password.isEmpty())) {
                    loginCallback.onLoginButtonClick(email, password);
                } else {
                    Toast.makeText(getActivity(), "Lengkapi data", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_forget_pass:
                mDialog.dismiss();
                forgetPasswordFragment.show(getActivity().getSupportFragmentManager(), forgetPasswordFragment.getTag());
                break;

            case R.id.btn_login_google:
                loginCallback.onGoogleButtonClick();
                break;
        }
    }

    public void setLoginCallback(LoginCallback callback) {
        loginCallback = callback;
    }

    public interface LoginCallback {
        void onLoginButtonClick(String email, String password);

        void onGoogleButtonClick();
    }

}
