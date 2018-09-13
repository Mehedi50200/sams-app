package my.unimas.a50200siswa.studentattendancemonitoringsystem;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Integer.parseInt;


public class StudentProfileActivity extends AppCompatActivity {

    private static final String TAG = "StudentProfile" ;
    String userID ;
    String CourseCode, CourseName;
    String StudentName, StudentId;
    String NumberOfPresence, NumberOfAbsence, TotalClass;
    String Percentage;
    String UserProfileImageUrl;
    TextView btnSignOut, UserName,NoAbsence,NoPresence, NoPercentage, NoClass, EmptyViewAteendance;
    Button btnNotifyStudent;

    CircleImageView ProfileImage, userProfileImage;
    ProgressBar AttendanceProgress;
    RecyclerViewAdapterAttendance attendanceAdapter;

    private TextView studentName,studentId;


    List<AttendanceModel> listAttendance;

    /*-------------------------------- Firebase Database stuff -----------------------------------*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference rootRef,userRef;

    StorageReference storageReference ;
    StorageReference profileImageReference;


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
        setContentView(R.layout.activity_student_profile);

        btnNotifyStudent =findViewById(R.id.btnnotifystudent);
        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        studentName =  findViewById(R.id.studentname);
        studentId =  findViewById(R.id.studentmatric);
        NoPresence= findViewById(R.id.noP);
        NoAbsence= findViewById(R.id.noA);
        NoPercentage = findViewById(R.id.noPer);
        NoClass =findViewById(R.id.noClass);
        AttendanceProgress =findViewById(R.id.attendanceprogressBar);
        EmptyViewAteendance =findViewById(R.id.empty_view_attendance);

        userProfileImage =findViewById(R.id.userprofileimg);
        ProfileImage = findViewById(R.id.civprofileimage);

        final RecyclerView RVAttendance = findViewById(R.id.rvattendancehistory);
        RVAttendance.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));


        /*----------------------------------- Receiving data ------------------------------------ */
        Intent intent = getIntent();
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        StudentName = intent.getExtras().getString("StudentName");
        StudentId = intent.getExtras().getString("StudentId");
        UserProfileImageUrl = intent.getExtras().getString("UserProfileImageUrl");

        /*---------------------------------- Setting values --------------------------------------*/
        studentName.setText(StudentName);
        studentId.setText(StudentId);

        GlideApp.with(this)
                .load(UserProfileImageUrl)
                .error(R.drawable.profilepic)
                .into(userProfileImage);

        /* ------------------------------- Firebase Elements -------------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");

        storageReference = FirebaseStorage.getInstance().getReference();
        profileImageReference =storageReference.child("StudentsPic/" +StudentId+".jpg");
        /*----------------------------------------------------------------------------------------*/

        GlideApp.with(this /* context */)
                .load(profileImageReference)
                .error(R.drawable.profilepic)
                .into(ProfileImage);


        /*------------------------------- Attendance Fetching ------------------------------------*/

        listAttendance = new ArrayList<>();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(userID).child("userName").getValue(String.class);
                UserName.setText(userName);

                String attendanceId[] = new String[35];
                String attendacedate[] = new String[35];
                String status[] = new String[35];
                String week[] = new String[16];

                listAttendance.clear();
                if (dataSnapshot.exists()) {

                    RVAttendance.setVisibility(View.VISIBLE);
                    EmptyViewAteendance.setVisibility(View.GONE);
                    int i = 0;

                    for (DataSnapshot dataSnapshot1 :dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(StudentId).child("Attendance").getChildren()) {

                        attendanceId[i] = dataSnapshot1.getKey();
                        attendacedate[i] = dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(StudentId).child("Attendance").child(attendanceId[i]).child("Date").getValue(String.class);
                        status[i] = dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(StudentId).child("Attendance").child(attendanceId[i]).child("Status").getValue(String.class);
                        week[i] = "Week " + String.valueOf(i + 1);

                        listAttendance.add(new AttendanceModel(attendanceId[i], attendacedate[i], status[i], week[i]));
                    }
                    if(listAttendance.size()==0){
                        RVAttendance.setVisibility(View.GONE);
                        EmptyViewAteendance.setVisibility(View.VISIBLE);
                    }else{

                        DataSnapshot dataSnapshot2 = dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(StudentId).child("AttendanceRecord");

                        NumberOfAbsence = dataSnapshot2.child("ClassMissed").getValue(String.class);
                        NoAbsence.setText(NumberOfAbsence);

                        NumberOfPresence = dataSnapshot2.child("ClassAttended").getValue(String.class);
                        NoPresence.setText(NumberOfPresence);

                        TotalClass = dataSnapshot2.child("TotalClass").getValue(String.class);
                        NoClass.setText(TotalClass);

                        Percentage = dataSnapshot2.child("Percentage").getValue(String.class);
                        NoPercentage.setText(Percentage + " %");
                        AttendanceProgress.setProgress(parseInt(Percentage));

                    }

                }else{
                    RVAttendance.setVisibility(View.GONE);
                    EmptyViewAteendance.setVisibility(View.VISIBLE);
                }
                attendanceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        attendanceAdapter = new RecyclerViewAdapterAttendance(this,listAttendance);
        RVAttendance.setAdapter(attendanceAdapter);

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

    }

}