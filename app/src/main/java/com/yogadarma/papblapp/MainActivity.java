package com.yogadarma.papblapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RegisterFragment.RegisterCallback, LoginFragment.LoginCallback {

    private Button btnGoLogin, btnGoRegister;
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private AlertDialog alertDialogVerif, alertDialogEmailNotVerif;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    public final int RC_SIGN_IN = 9001;
    public final String TAG = "GoogleActivity";
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        btnGoLogin = findViewById(R.id.btn_go_login);
        btnGoRegister = findViewById(R.id.btn_go_register);

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        loginFragment.setLoginCallback(this);
        registerFragment.setRegisterCallback(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        alertDialogVerif = builder.create();
        alertDialogEmailNotVerif = builder.create();

        progressDialog = new ProgressDialog(this);

        btnGoLogin.setOnClickListener(this);
        btnGoRegister.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            if (user.isEmailVerified()) {
                startActivity(new Intent(this, MapsActivity.class));
                finish();
            } else {
                firebaseAuth.signOut();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go_login:
                loginFragment.show(getSupportFragmentManager(), loginFragment.getTag());
                break;

            case R.id.btn_go_register:
                registerFragment.show(getSupportFragmentManager(), registerFragment.getTag());
                break;
        }
    }

    @Override
    public void onLoginButtonClick(String email, String password) {
        loginFragment.dismiss();
        loginUser(email, password);
    }

    @Override
    public void onGoogleButtonClick() {
        loginFragment.dismiss();
        loginGoogle();
    }

    @Override
    public void onRegisterButtonClick(String name, String email, String password) {
        registerFragment.dismiss();
        registerUser(name, email, password);
    }

    private void sendEmailVerifikasi(final String email) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    showDialogVerifikasi(email);
                } else {
                    Toast.makeText(MainActivity.this, "Gagal mengirim email verifikasi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialogVerifikasi(String email) {
        alertDialogVerif.setTitle("Verifikasi Email");
        alertDialogVerif.setMessage("Email verifikasi telah dikirimkan ke email\n" + email + "\nSilahkan periksa email anda");
        alertDialogVerif.setButton(Dialog.BUTTON_POSITIVE, "Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogVerif.dismiss();
            }
        });
        alertDialogVerif.show();
    }

    private void showDialogEmailNotVerified(final String email) {
        alertDialogEmailNotVerif.setTitle("Email belum diverifikasi");
        alertDialogEmailNotVerif.setMessage("Mohon verifikasi email terlebih dahulu");
        alertDialogEmailNotVerif.setButton(Dialog.BUTTON_NEUTRAL, "Kirim ulang", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmailVerifikasi(email);
                alertDialogEmailNotVerif.dismiss();
            }
        });
        alertDialogEmailNotVerif.setButton(Dialog.BUTTON_POSITIVE, "Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogEmailNotVerif.dismiss();
            }
        });
        alertDialogEmailNotVerif.show();
    }

    private void loginUser(final String email, String password) {
        progressDialog.setTitle("Proses masuk");
        progressDialog.setMessage("Tunggu sebentar...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser.isEmailVerified()) {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                        finishAffinity();
                    } else {
                        progressDialog.dismiss();
                        showDialogEmailNotVerified(email);

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Email dan password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(final String name, final String email, String password) {
        progressDialog.setTitle("Proses daftar");
        progressDialog.setMessage("Tunggu sebentar...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    progressDialog.dismiss();
                    firebaseUser = firebaseAuth.getCurrentUser();

                    UserProfileChangeRequest updateProfileUser = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    firebaseUser.updateProfile(updateProfileUser);

                    sendEmailVerifikasi(email);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Email sudah terdaftar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginGoogle() {
        progressDialog.setTitle("Proses masuk");
        progressDialog.setMessage("Tunggu sebentar...");
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, MapsActivity.class));
                            finishAffinity();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
