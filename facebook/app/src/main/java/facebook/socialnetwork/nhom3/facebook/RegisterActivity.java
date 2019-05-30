package facebook.socialnetwork.nhom3.facebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserEmail = (EditText) findViewById(R.id.editTextRegister_email);
        UserPassword = (EditText) findViewById(R.id.editTextRegister_password);
        UserConfirmPassword = (EditText) findViewById(R.id.editTextRegister_Confirm_password);
        CreateAccountButton = (Button) findViewById(R.id.btnRegister_Create_account);
    }
}
