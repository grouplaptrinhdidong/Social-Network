package facebook.socialnetwork.nhom3.facebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRequestActivity extends AppCompatActivity {
    private RecyclerView friendList;
    private DatabaseReference FriendsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String  online_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_request);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendList = (RecyclerView) findViewById(R.id.friend_list);
        friendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendList.setLayoutManager(linearLayoutManager);
        DisplayAllFriend();
    }
    private void DisplayAllFriend() {
        FirebaseRecyclerAdapter<FriendsRequest, FriendsRequestActivity.FriendsRequestViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FriendsRequest, FriendsRequestActivity.FriendsRequestViewHolder>(
                FriendsRequest.class,
                R.layout.all_users_display_layout,
                FriendsRequestActivity.FriendsRequestViewHolder.class,
                FriendsRef
        ) {

            @Override
            protected void populateViewHolder(final FriendsRequestActivity.FriendsRequestViewHolder viewHolder, FriendsRequest model, int position) {
                viewHolder.setRequestType(model.getRequest_type());
                //Deplay all friends
                final String usersIDs = getRef(position).getKey();
                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            final String userName = dataSnapshot.child("fullname").getValue().toString();
                            final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                            final String type;

                            viewHolder.setFullname(userName);
                            viewHolder.setProfileimage(getApplicationContext(), profileImage);

                            //set 2 option: chat or view profile
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[] = new CharSequence[]{
                                            userName + "'s Profile",
                                            "Send Message"
                                    };
                                    AlertDialog.Builder builder =new AlertDialog.Builder(FriendsRequestActivity.this);
                                    builder.setTitle("Select Option");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            //option view profile
                                            if(which ==0){

                                                Intent profileintent =new Intent(FriendsRequestActivity.this, PersonProfileActivity.class);
                                                profileintent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileintent);

                                            }if(which==1){

                                                //option chat
                                                Intent Chatintent =new Intent(FriendsRequestActivity.this, ChatActivity.class);
                                                Chatintent.putExtra("visit_user_id",usersIDs);
                                                Chatintent.putExtra("userName",userName);
                                                startActivity(Chatintent);
                                            }
                                        }
                                    });

                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        friendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsRequestViewHolder extends RecyclerView.ViewHolder{
        View mView;
        //ImageView onlineStatusView;
        public FriendsRequestViewHolder(View itemView){

            super(itemView);
            mView = itemView;
            //onlineStatusView = (ImageView) itemView.findViewById(R.id.all_user_online_icon);
        }
        public void setProfileimage(Context ctx, String profileimage){
            CircleImageView myImage=(CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        public void setFullname(String fullname){

            TextView myName=(TextView) mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }
        public void setRequestType(String requestType){
            TextView RequestType =(TextView) mView.findViewById(R.id.all_users_status);
            RequestType.setText("Request type: " + requestType);
        }
    }
}
