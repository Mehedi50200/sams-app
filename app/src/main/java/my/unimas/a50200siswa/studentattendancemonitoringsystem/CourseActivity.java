package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CourseActivity extends AppCompatActivity {
    String userID;
    String CourseCode;
    String CourseName;
    String UserId;
    List<StudentModel> listStudent;
    TextView btnSignOut, UserName,EmptyViewStudent;
    CircleImageView cvTakeAttendance, cvGallery;
    private TextView coursecode,coursename;

    private static final int PICK_IMAGE_REQUEST = 1;


    /*------------------------- Firebase Database Element Declaration ----------------------------*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference userRef, studentpicdatabaseref;
    RecyclerViewAdapterStudent studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        cvTakeAttendance =findViewById(R.id.cvtakeattendance);
        cvGallery=findViewById(R.id.cvgallery);
        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        coursecode =  findViewById(R.id.coursecode);
        coursename =  findViewById(R.id.coursename);

        final RecyclerView RVStudent = findViewById(R.id.rvstudent);
        RVStudent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        EmptyViewStudent = findViewById(R.id.empty_view_student);

        /*--------------------------------- Receive data -----------------------------------------*/
        Intent intent = getIntent();
        UserId = intent.getExtras().getString("UserId");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");

        coursecode.setText(CourseCode);
        coursename.setText(CourseName);

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
                    startActivity(new Intent(CourseActivity.this, SignInActivity.class));
                }
            }
        };

        /*----------------------------- Database Reference Elements ------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
        /*----------------------------------------------------------------------------------------*/


        /*------------------------------ Course List Fetch ---------------------------------------*/
        listStudent = new ArrayList<>();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(userID).child("userName").getValue(String.class);
                UserName.setText(userName);
                String studentId[] = new String[20];
                String studentName[] = new String[20];
                String studentserial[] = new String[20];

                listStudent.clear();
                if (dataSnapshot.exists()) {
                    RVStudent.setVisibility(View.VISIBLE);
                    EmptyViewStudent.setVisibility(View.GONE);

                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").getChildren()) {
                        studentId[i]= dataSnapshot1.getKey();
                        studentName[i]=dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(studentId[i]).child("StudentName").getValue(String.class);
                        studentserial[i]= String.valueOf(i);

                        listStudent.add(new StudentModel(studentId[i],studentName[i], studentserial[i],CourseCode,CourseName));
                        i++;
                    }

                    if(listStudent.size()==0){
                        RVStudent.setVisibility(View.GONE);
                        EmptyViewStudent.setVisibility(View.VISIBLE);
                    }


                }else{
                    RVStudent.setVisibility(View.GONE);
                    EmptyViewStudent.setVisibility(View.VISIBLE);
                }
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value", error.toException());
            }
        });

        studentAdapter = new RecyclerViewAdapterStudent(this,listStudent);
        RVStudent.setAdapter(studentAdapter);

        cvTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CourseActivity.this, TakeAttendanceActivity.class));

            }
        });

        cvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri mImageUri = data.getData();
            Intent processedresultintent= new Intent(CourseActivity.this, TakeAttendanceActivity.class);
            processedresultintent.setData(mImageUri);
            startActivity(processedresultintent);


        }
    }


}
