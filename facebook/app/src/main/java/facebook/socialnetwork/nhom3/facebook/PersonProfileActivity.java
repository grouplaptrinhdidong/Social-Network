package facebook.socialnetwork.nhom3.facebook;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {


    private TextView username, userProfileName, userStatus, userCountry, userGender, userRelationship, userDob;
    private CircleImageView userProfileImage;
    private Button AddFriendbtn, Declinebtn;

    private DatabaseReference FriendRequestRef, UsersRef, FriendRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId, CURRENT_STATE,  saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);


        mAuth=FirebaseAuth.getInstance();

        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendRef =  FirebaseDatabase.getInstance().getReference().child("Friends");

        IntializeFields();


        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myDob = dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationshipstatus").getValue().toString();


                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    username.setText("@" + myUserName);
                    userProfileName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDob.setText("DOB: " + myDob);
                    userCountry.setText("Country: " + myCountry);
                    userGender.setText("Gender: " + myGender);
                    userRelationship.setText("Relationship: " + myRelationshipStatus);

                    MaintananceofButtons();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Declinebtn.setVisibility(View.INVISIBLE);
        Declinebtn.setEnabled(false);

        if (!senderUserId.equals(receiverUserId)){

            AddFriendbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddFriendbtn.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")){

                        AddFriendToAPerson();
                    }
                    if (CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")){
                        UnfriendExitingFriend();
                    }
                }
            });
        }
        else {
            Declinebtn.setVisibility(View.INVISIBLE);
            AddFriendbtn.setVisibility(View.INVISIBLE);
        }
    }

    private void UnfriendExitingFriend() {
        FriendRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            FriendRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                AddFriendbtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                AddFriendbtn.setText("Add friend");
                                                Declinebtn.setVisibility(View.INVISIBLE);
                                                Declinebtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calFordDate=Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate=currentDate.format(calFordDate.getTime());
        FriendRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FriendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if (task.isSuccessful()){
                                                                                        AddFriendbtn.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        AddFriendbtn.setText("Unfriend");
                                                                                        Declinebtn.setVisibility(View.INVISIBLE);
                                                                                        Declinebtn.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                AddFriendbtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                AddFriendbtn.setText("Add friend");
                                                Declinebtn.setVisibility(View.INVISIBLE);
                                                Declinebtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintananceofButtons() {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(receiverUserId)){
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type")
                                    .getValue().toString();

                            if (request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                AddFriendbtn.setText("Delete request");
                                Declinebtn.setVisibility(View.INVISIBLE);
                                Declinebtn.setEnabled(false);

                            }
                            else  if (request_type.equals("received")){
                                CURRENT_STATE = "request_received";
                                AddFriendbtn.setText("Accept request");
                                Declinebtn.setVisibility(View.VISIBLE);
                                Declinebtn.setEnabled(true);
                                Declinebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });

                            }
                        }
                        else {
                            FriendRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserId)){
                                                CURRENT_STATE = "friends";
                                                AddFriendbtn.setText("Unfriend");
                                                Declinebtn.setVisibility(View.INVISIBLE);
                                                Declinebtn.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void AddFriendToAPerson() {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                AddFriendbtn.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                AddFriendbtn.setText("Delete request");
                                                Declinebtn.setVisibility(View.INVISIBLE);
                                                Declinebtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void IntializeFields() {
        username = (TextView) findViewById(R.id.person_profile_username);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userProfileName = (TextView) findViewById(R.id.person_profile_full_name);
        userCountry = (TextView) findViewById(R.id.person_profile_country);
        userGender = (TextView) findViewById(R.id.per_profile_gender);
        userRelationship = (TextView) findViewById(R.id.person_profile_relation);
        userDob = (TextView) findViewById(R.id.person_profile_dob);

        userProfileImage = (CircleImageView) findViewById(R.id.person_profile_pic);

        AddFriendbtn = (Button) findViewById(R.id.person_send_friend_request_btn);
        Declinebtn = (Button) findViewById(R.id.person_decline_friend_request_btn);

        CURRENT_STATE = "not_friends";
    }
}
