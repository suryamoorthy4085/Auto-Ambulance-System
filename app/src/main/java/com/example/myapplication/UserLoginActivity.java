package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class UserLoginActivity extends AppCompatActivity {

    private CountryCodePicker ccp;

    private EditText userEditText;
    private ConstraintLayout phoneLayout;
    private PinView firstPinView;
    private String selected_country_code = "+91";
    private static final int CREDENTIAL_PICKER_REQUEST =120 ;
    private ProgressBar progressBar;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        ccp =(CountryCodePicker) findViewById(R.id.ccp);
        userEditText =(EditText) findViewById(R.id.editTextTextPersonName);
        firstPinView = (PinView) findViewById(R.id.firstPinView);
        phoneLayout = (ConstraintLayout) findViewById(R.id.phoneLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();


        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });

        userEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().length()==10){
                    Toast.makeText(UserLoginActivity.this, "Enter 6 digit PIN", Toast.LENGTH_SHORT).show();
                    phoneLayout.setVisibility(View.GONE);
                    firstPinView.setVisibility(View.VISIBLE);
                    sendOtp();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        firstPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().length()==6){
                    progressBar.setVisibility(View.VISIBLE);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,firstPinView.getText().toString().trim());
                    signInWithAuthCredentials(credential);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        try {
            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build();


            PendingIntent intent = Credentials.getClient(UserLoginActivity.this).getHintPickerIntent(hintRequest);
            try {
                startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, new Bundle());
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if(code != null){
                    firstPinView.setText(code);

                    signInWithAuthCredentials(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(UserLoginActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.VISIBLE);
                firstPinView.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(@NonNull String verficationId, @NonNull PhoneAuthProvider.ForceResendingToken token){
                super.onCodeSent(verficationId, token);

                mVerificationId = verficationId;
                mResentToken = token;

                Toast.makeText(UserLoginActivity.this, "6 digit OTP sent", Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.GONE);
                firstPinView.setVisibility(View.VISIBLE);

            }
        };

    }

    private void sendOtp() {

        progressBar.setVisibility(View.VISIBLE);

        String phoneNumber = selected_country_code+userEditText.getText().toString();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setTimeout(60L, TimeUnit.SECONDS)
                .setPhoneNumber(phoneNumber)
                .setActivity(UserLoginActivity.this).setCallbacks(callbacks).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {

            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);

            userEditText.setText(credentials.getId().substring(3));

        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {

            Toast.makeText(UserLoginActivity.this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }
    private void signInWithAuthCredentials(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(UserLoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UserLoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(UserLoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UserLoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Animatoo.animateSlideRight(UserLoginActivity.this);

                }

            }
        });
    }

}