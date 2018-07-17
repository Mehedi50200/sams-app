package my.unimas.a50200siswa.studentattendancemonitoringsystem;

        import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentProfileActivity extends AppCompatActivity {


    String userID;
    String CourseCode;
    String CourseName;
    String StudentName;
    String StudentMatric;
    String UserId;
    TextView btnSignOut, UserName,EmptyViewStudent;
    Button btnNotifyStudent;
    private TextView studentName,studentId;



    /*---- Firebase Database stuff ----*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        btnNotifyStudent =findViewById(R.id.btnnotifystudent);
        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        studentName =  findViewById(R.id.studentname);
        studentId =  findViewById(R.id.studentmatric);




        // Recieve data
        Intent intent = getIntent();
        UserId = intent.getExtras().getString("UserId");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        StudentName = intent.getExtras().getString("StudentName");
        StudentMatric = intent.getExtras().getString("StudentId");

        // Setting values

        studentName.setText(StudentName);
        studentId.setText(StudentMatric);


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
                    startActivity(new Intent(StudentProfileActivity.this, SignInActivity.class));
                }
            }
        };


        /* ----------------- Firebase Elements -----------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");







        //  courseRef = rootRef.child("Users").child(userID).child("Course").child(CourseCode);
        /*------------------------------------------------------------------*/


        /*---------------------- Course List Fetch---------------------------------*/


        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

    }
}
