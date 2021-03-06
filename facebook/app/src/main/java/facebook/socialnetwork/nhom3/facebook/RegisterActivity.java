package facebook.socialnetwork.nhom3.facebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserEmail = (EditText) findViewById(R.id.editTextRegister_email);
        UserPassword = (EditText) findViewById(R.id.editTextRegister_password);
        UserConfirmPassword = (EditText) findViewById(R.id.editTextRegister_Confirm_password);
        CreateAccountButton = (Button) findViewById(R.id.btnRegister_Create_account);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAcount();
            }
        });
    }

    private void createNewAcount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmpassword = UserConfirmPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email!", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password!", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(confirmpassword)){
            Toast.makeText(this, "Please write your confirm password!", Toast.LENGTH_SHORT).show();
        }
        else  if(!password.equals(confirmpassword)){
            Toast.makeText(this, "Your password do not match with your confirm password!", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your new account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendEmailVerificationMessage();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error occured:" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserToMainActivity();
            //SendUserToRegisterActivity();
        }
    }
    private void SendEmailVerificationMessage()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            //Send email to user to verify
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Registration successfull, we've sent you a email. Please check and verify you account...", Toast.LENGTH_SHORT).show();
                        SendUserToLoginActivity();
                        mAuth.signOut();
                    }
                    else {
                        String mess = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error: " + mess, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
