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
    TextView btnSignOut, UserName, CourseName, CourseName2, CourseName3, CourseName4, CourseCode,CourseCode2,CourseCode3,CourseCode4;

    /*---- Firebase Database stuff ----*/
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

      //  CourseCode();

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


        CourseName = findViewById(R.id.courseName);
        CourseName2 = findViewById(R.id.courseName2);
        CourseName3 = findViewById(R.id.courseName3);
        CourseName4 = findViewById(R.id.courseName4);
        CourseCode= findViewById(R.id.courseCode);
        CourseCode2= findViewById(R.id.courseCode2);
        CourseCode3= findViewById(R.id.courseCode3);
        CourseCode4= findViewById(R.id.courseCode4);
        /* ----------------- Firebase Elements -----------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        myRef = rootRef.child("Users");
    /*------------------------------------------------------------------*/


        listCourse = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userName = dataSnapshot.child(userID).child("userName").getValue(String.class);
                UserName.setText(userName);
                String coursecode[] = new String[10];
                String coursename[] = new String[10];


                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(userID).child("Course").getChildren()) {
                        int i = 1;
                        coursecode[i]= dataSnapshot1.getKey();
                        coursename[i]=dataSnapshot.child(userID).child("Course").child(coursecode[i]).child("CourseName").getValue(String.class);
                        listCourse.add(new CourseModel(userID,coursecode[i],coursename[i]));


                        /*
                        CourseCode.setText(coursecode[1]);
                        CourseCode2.setText(coursecode[2]);
                        CourseCode3.setText(coursecode[3]);
                        CourseCode4.setText(coursecode[4]);

                        CourseName.setText(coursename[1]);
                        CourseName2.setText(coursename[2]);
                        CourseName3.setText(coursename[3]);
                        CourseName4.setText(coursename[4]); */
                        i++;

                        }

                    }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        RecyclerView myrv = findViewById(R.id.recyclerviewcourse);
        RecyclerViewAdapterCourse myAdapter = new RecyclerViewAdapterCourse(this,listCourse);
        myrv.setLayoutManager(new GridLayoutManager(this,2));
        myrv.setAdapter(myAdapter);


    }


}
