package facebook.socialnetwork.nhom3.facebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText username, userProfileName, userStatus, userCountry, userGender, userRelationship, userDob;
    private Button UpdateAccountSettingsButton;
    private CircleImageView  userProfileImage;

    private DatabaseReference SettingsUserRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);


        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        username = (EditText) findViewById(R.id.settings_username);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userProfileName = (EditText) findViewById(R.id.settings_profile_fullname);
        userCountry = (EditText) findViewById(R.id.settings_country);
        userGender = (EditText) findViewById(R.id.settings_gender);
        userRelationship = (EditText) findViewById(R.id.settings_relationship_status);
        userDob = (EditText) findViewById(R.id.settings_dob);

        userProfileImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        UpdateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings_button);


        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDob = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    username.setText(myUserName);
                    userProfileName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDob.setText(myDob);
                    userCountry.setText(myCountry);
                    userGender.setText(myGender);
                    userRelationship.setText(myRelationshipStatus);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
