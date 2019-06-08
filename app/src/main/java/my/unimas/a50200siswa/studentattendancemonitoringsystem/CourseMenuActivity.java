package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseMenuActivity extends AppCompatActivity {
    String userID;

    Button btnSignOut;
    TextView UserName;
    CircleImageView userProfileImage;

    Animation UpDown,DownUp,  RightToLeft, LeftToRight;

    private TextView coursecode,coursename;

    /*------------------------------- Firebase Database stuff ------------------------------------*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference userRef, notesRef;

    String UserId,userName, CourseCode, CourseName,UserProfileImageUrl;
    CardView HomeworkCard, AttendanceCard, ExamResultCard;


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
        setContentView(R.layout.activity_course_menu);
        /*---------------------------------- Finding View ----------------------------------------*/

        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        userProfileImage =findViewById(R.id.userprofileimg);


        /*------------------------------------- Animation --------------------------------------- */


        HomeworkCard =findViewById(R.id.homeworkcard);

        AttendanceCard =findViewById(R.id.attendancecard);
        ExamResultCard =findViewById(R.id.examresultcard);
        RightToLeft = AnimationUtils.loadAnimation(this,R.anim.rightleft);
        LeftToRight = AnimationUtils.loadAnimation(this,R.anim.leftright);

        HomeworkCard.setAnimation(RightToLeft);
        ExamResultCard.setAnimation(LeftToRight);


        coursecode =  findViewById(R.id.mcoursecode);
        coursename =  findViewById(R.id.mcoursename);

        /* ------------------------------ Firebase Elements --------------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
        notesRef = rootRef.child("Users").child(userID).child("Notes");

        /*----------------------------------------------------------------------------------------*/

        Intent intent = getIntent();
        UserId = intent.getExtras().getString("UserId");
        userName = intent.getExtras().getString("UserName");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        UserProfileImageUrl = intent.getExtras().getString("UserProfileImageUrl");

        UserName.setText(userName);
        coursecode.setText(CourseCode);
        coursename.setText(CourseName);

        GlideApp.with(CourseMenuActivity.this)
                .load(UserProfileImageUrl)
                .error(R.drawable.profilepic)
                .into(userProfileImage);

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
                    startActivity(new Intent(CourseMenuActivity.this, SignInActivity.class));
                }
            }
        };


        AttendanceCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseMenuActivity.this, CourseActivity.class);
                // passing data to the book activity
                intent.putExtra("UserId", UserId);
                intent.putExtra("CourseCode", CourseCode);
                intent.putExtra("CourseName", CourseName);
                intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);
                startActivity(intent);
            }
        });

    }





    public void quit() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }

}
