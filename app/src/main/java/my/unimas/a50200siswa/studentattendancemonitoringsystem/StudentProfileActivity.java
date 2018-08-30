package my.unimas.a50200siswa.studentattendancemonitoringsystem;


import android.content.Intent;
import android.graphics.Color;
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


public class StudentProfileActivity extends AppCompatActivity {

    private static final String TAG = "StudentProfile" ;
    String userID;
    String CourseCode;
    String CourseName;
    String StudentName;
    String StudentId;
    String NumberOfPresence;
    String NumberOfAbsence;
    String Percentage;
    TextView btnSignOut, UserName,NoAbsence,NoPresence, NoPercentage, NoClass, EmptyViewAteendance;
    Button btnNotifyStudent;
    private TextView studentName,studentId;
    CircleImageView ProfileImage;
    ProgressBar AttendanceProgress;
    RecyclerViewAdapterAttendance attendanceAdapter;

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

        final RecyclerView RVAttendance = findViewById(R.id.rvattendancehistory);
        RVAttendance.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        /*----------------------------------- Receiving data ------------------------------------ */
        Intent intent = getIntent();
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        StudentName = intent.getExtras().getString("StudentName");
        StudentId = intent.getExtras().getString("StudentId");

        /*---------------------------------- Setting values --------------------------------------*/
        studentName.setText(StudentName);
        studentId.setText(StudentId);

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

        /* ------------------------------- Firebase Elements -------------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");

        storageReference = FirebaseStorage.getInstance().getReference();
        profileImageReference =storageReference.child("StudentsPic/" +StudentId+".jpg");


        ProfileImage = findViewById(R.id.civprofileimage);



            GlideApp.with(this /* context */)
                    .load(profileImageReference)
                    .error(R.drawable.profilepic)
                    .into(ProfileImage);

        /*----------------------------------------------------------------------------------------*/


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
                    int nop = 0;
                    int noa = 0;

                    for (DataSnapshot dataSnapshot1 :dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(StudentId).child("Attendance").getChildren()) {

                        attendanceId[i] = dataSnapshot1.getKey();
                        attendacedate[i] = dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(StudentId).child("Attendance").child(attendanceId[i]).child("Date").getValue(String.class);
                        status[i] = dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(StudentId).child("Attendance").child(attendanceId[i]).child("Status").getValue(String.class);
                        week[i] = "Week " + String.valueOf(i + 1);

                        listAttendance.add(new AttendanceModel(attendanceId[i], attendacedate[i], status[i], week[i]));

                        if (status[i].equals("Present")) {
                            nop++;
                        } else {
                            noa++;
                        }
                        i++;
                    }
                    if(listAttendance.size()==0){
                        RVAttendance.setVisibility(View.GONE);
                        EmptyViewAteendance.setVisibility(View.VISIBLE);
                    }else{

                        NumberOfAbsence = String.valueOf(noa);
                        NoAbsence.setText(NumberOfAbsence);

                        NumberOfPresence = String.valueOf(nop);
                        NoPresence.setText(NumberOfPresence);

                        NoClass.setText(String.valueOf(noa+nop));


                        Percentage = String.valueOf(AttendancePercentage(i,nop));
                        if (AttendancePercentage(i,nop)<=60)
                        {
                            NoPercentage.setTextColor(Color.RED);
                        }
                        NoPercentage.setText(Percentage + " %");
                        AttendanceProgress.setProgress(AttendancePercentage(i,nop));

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

    }

    public int AttendancePercentage(int classTaken, int classPresence){
        int Percentage;
        Percentage = classPresence * 100 / classTaken;
        return Percentage;
    }

}