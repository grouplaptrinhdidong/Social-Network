package facebook.socialnetwork.nhom3.facebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private Button btnlogin, btnRegister;
    private EditText editTextUsername, editTextPassword;
    private TextView forgetPasswordLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Boolean emailAddressChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        editTextUsername = (EditText) findViewById(R.id.editTextLogin_email);
        editTextPassword = (EditText) findViewById(R.id.editTextLogin_password);
        btnlogin = (Button) findViewById(R.id.btLogin_login);
        btnRegister = (Button) findViewById(R.id.btLogin_Create_account);
        loadingBar = new ProgressDialog(this);
        forgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowingUserToLogin();
            }
        });
    }

    private void AllowingUserToLogin() {
        String email = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please write your email", Toast.LENGTH_SHORT).show();
        }
        else{
            if (TextUtils.isEmpty(password)){

                Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
            }
            else{
                loadingBar.setTitle("Login");
                loadingBar.setMessage("Please wait, while we are allowing you to login into your account...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    VerifyEmailAddress();
                                    loadingBar.dismiss();
                                }
                                else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "Error occured:"+message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserToMainActivity();
            //SendUserToRegisterActivity();
            //SendUserToSetupActivity();
        }
    }
    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    //check email input
    private void VerifyEmailAddress(){
        FirebaseUser user = mAuth.getCurrentUser();
        emailAddressChecker = user.isEmailVerified();
        if(emailAddressChecker){
            SendUserToMainActivity();
        }
        else {
            //don't allow user to go to MainActivity
            Toast.makeText(this, "Please verify your account first...", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        //registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
    }
}
