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
import android.widget.Button;
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
    String userID, userName;
    String CourseCode, CourseName, UserId, UserProfileImageUrl ;

    List<StudentModel> listStudent;

    TextView EmptyViewStudent;
    CircleImageView cvTakeAttendance, cvGallery, cvGeneratePdf;

    Button btnSignOut;
    TextView UserName;
    CircleImageView userProfileImage;

    private TextView coursecode,coursename;
    private static final int PICK_IMAGE_REQUEST = 1;


    /*------------------------- Firebase Database Element Declaration ----------------------------*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference userRef;
    RecyclerViewAdapterStudent studentAdapter;
    /*--------------------------------------------------------------------------------------------*/

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
        setContentView(R.layout.activity_course);

        /*------------------------- Receive data From Previous Intent ----------------------------*/
        Intent intent = getIntent();
        UserId = intent.getExtras().getString("UserId");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        UserProfileImageUrl = intent.getExtras().getString("UserProfileImageUrl");
        /*----------------------------------------------------------------------------------------*/

        cvTakeAttendance =findViewById(R.id.cvtakeattendance);
        cvGallery=findViewById(R.id.cvgallery);
        cvGeneratePdf = findViewById(R.id.cvgeneratepdf);

        coursecode =  findViewById(R.id.coursecode);
        coursename =  findViewById(R.id.coursename);

        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        userProfileImage =findViewById(R.id.userprofileimg);

        final RecyclerView RVStudent = findViewById(R.id.rvstudent);
        RVStudent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        EmptyViewStudent = findViewById(R.id.empty_view_student);


        coursecode.setText(CourseCode);
        coursename.setText(CourseName);

        GlideApp.with(CourseActivity.this)
                .load(UserProfileImageUrl)
                .error(R.drawable.profilepic)
                .into(userProfileImage);


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
                userName = dataSnapshot.child(userID).child("userName").getValue(String.class);
                UserName.setText(userName);

                String studentId[] = new String[50];
                String studentName[] = new String[50];
                String studentserial[] = new String[50];

                listStudent.clear();
                if (dataSnapshot.exists()) {
                    RVStudent.setVisibility(View.VISIBLE);
                    EmptyViewStudent.setVisibility(View.GONE);

                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").getChildren()) {
                        studentId[i]= dataSnapshot1.getKey();
                        studentName[i]=dataSnapshot.child(userID).child("Course").child(CourseCode).child("Students").child(studentId[i]).child("StudentName").getValue(String.class);
                        studentserial[i]= String.valueOf(i);

                        listStudent.add(new StudentModel(studentId[i],studentName[i], studentserial[i],CourseCode,CourseName, UserProfileImageUrl));
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

        cvGeneratePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, GenerateAttendanceSheetActivity.class);
                intent.putExtra("UserId", UserId );
                intent.putExtra("UserName", userName );
                intent.putExtra("CourseCode", CourseCode);
                intent.putExtra("CourseName", CourseName);
                intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);
                startActivity(intent);
            }
        });



        cvTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, TakeAttendancePictureActivity.class);
                intent.putExtra("UserId", UserId );
                intent.putExtra("UserName", userName );
                intent.putExtra("CourseCode", CourseCode);
                intent.putExtra("CourseName", CourseName);
                intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);
                startActivity(intent);
            }
        });

        cvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

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

            Intent intent= new Intent(CourseActivity.this, TakeAttendancePictureActivity.class);
            intent.setData(mImageUri);
            intent.putExtra("UserId", UserId );
            intent.putExtra("UserName", userName );
            intent.putExtra("CourseCode", CourseCode);
            intent.putExtra("CourseName", CourseName);
            intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);
            startActivity(intent);
        }
    }




}
