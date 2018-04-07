package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    String userID;
    List<CourseModel> listCourse;
    TextView btnSignOut, UserName;
    RecyclerView courseRecycler;

    String CourseCode, CourseName;

    /*---- Firebase Database stuff ----*/
    FirebaseDatabase mFirebaseDatabase;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myRef;




    @Override
    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*-------Finding View---------*/
        btnSignOut = (TextView) findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        courseRecycler = findViewById(R.id.recyclerviewcourse);

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }

        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                }
            }
        };


        /* ----------------- Firebase Elements -----------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Users");





        /*------------------------------------------------------------------*/


        listCourse = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String userName = dataSnapshot.child(userID).child("userName").getValue(String.class);
                UserName.setText(userName);

                for (DataSnapshot dataSnapshot1 : dataSnapshot.child(userID).child("Course").getChildren()) {
                    String coursecode = dataSnapshot1.getChildren().toString();
                    String userid = userID;

                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("CourseName").getChildren()) {
                        String coursename = dataSnapshot2.getValue(String.class);
                        listCourse.add(new CourseModel(userid,coursecode,coursename));
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

/*
        listCourse = new ArrayList<>();
        listCourse.add(new CourseModel("122345","TMN1234","System Programming"));
        listCourse.add(new CourseModel("122345","TMN1234","System Programming"));
        listCourse.add(new CourseModel("122345","TMN1234","System Programming"));
        listCourse.add(new CourseModel("122345","TMN1234","System Programming"));

        */

        RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerviewcourse);
        RecyclerViewAdapterCourse myAdapter = new RecyclerViewAdapterCourse(this,listCourse);
        myrv.setLayoutManager(new GridLayoutManager(this,2));
        myrv.setAdapter(myAdapter);

    }
}
