package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    String userID;
    String userProfileImageUrl;
    List<CourseModel> listCourse;
    List<NoteModel> listNote;

    Button btnSignOut;
    TextView UserName;
    CircleImageView userProfileImage;

    TextView EmptyViewCourse, EmptyViewNote;
    EditText ETNote;
    Button btnSaveNote, btnAddNote;

    Animation UpDown,DownUp,  RightToLeft;
    LinearLayout HomeUp, HomeDown;
    CircleImageView CVHomeTakeAttendance;



    /*------------------------------- Firebase Database stuff ------------------------------------*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference userRef, notesRef;
    RecyclerViewAdapterCourse courseAdapter;
    RecyclerViewAdapterNote noteAdapter;

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
        /*---------------------------------- Finding View ----------------------------------------*/

        ETNote = findViewById(R.id.etnote);
        btnAddNote =findViewById(R.id.btnaddnote);
        btnSaveNote =findViewById(R.id.btnsavenote);

        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        userProfileImage =findViewById(R.id.userprofileimg);

        CVHomeTakeAttendance =findViewById(R.id.cvhometakeattendance);

        final RecyclerView RVCourse = findViewById(R.id.recyclerviewcourse);
        final RecyclerView RVNote = findViewById(R.id.rvnote);
        RVCourse.setLayoutManager(new GridLayoutManager(this,2));
        RVNote.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        EmptyViewNote = findViewById(R.id.empty_view_note);
        EmptyViewCourse = findViewById(R.id.empty_view_course);

        /*------------------------------------- Animation --------------------------------------- */

        HomeUp =findViewById(R.id.hometop);
        HomeDown=findViewById(R.id.homebottom);
        RightToLeft = AnimationUtils.loadAnimation(this,R.anim.rightleft);
        UpDown =AnimationUtils.loadAnimation(this,R.anim.uptodown);
        DownUp =AnimationUtils.loadAnimation(this,R.anim.downtoup);
        HomeUp.setAnimation(UpDown);
        HomeDown.setAnimation(DownUp);



        /* ------------------------------ Firebase Elements --------------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
        notesRef = rootRef.child("Users").child(userID).child("Notes");

        /*----------------------------------------------------------------------------------------*/



        /*-------------------------------- Course List Fetch -------------------------------------*/
        listCourse = new ArrayList<>();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child(userID).child("userName").getValue(String.class);
                userProfileImageUrl = dataSnapshot.child(userID).child("userProfileImageUrl").getValue(String.class);
                UserName.setText(userName);


                GlideApp.with(HomeActivity.this)
                        .load(userProfileImageUrl)
                        .error(R.drawable.profilepic)
                        .into(userProfileImage);

                String coursecode[] = new String[10];
                String coursename[] = new String[10];
                String day[] = new String[10];
                String time[] = new String[10];

                listCourse.clear();
                if (dataSnapshot.exists()) {
                    RVCourse.setVisibility(View.VISIBLE);
                    EmptyViewCourse.setVisibility(View.GONE);

                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(userID).child("Course").getChildren()) {
                        coursecode[i]= dataSnapshot1.getKey();
                        coursename[i]=dataSnapshot.child(userID).child("Course").child(coursecode[i]).child("CourseName").getValue(String.class);
                        day[i]=dataSnapshot.child(userID).child("Course").child(coursecode[i]).child("Routine").child("Day").getValue(String.class);
                        time[i]=dataSnapshot.child(userID).child("Course").child(coursecode[i]).child("Routine").child("Time").getValue(String.class);
                        listCourse.add(new CourseModel(userID,userProfileImageUrl,coursecode[i],coursename[i], day[i],time[i]));
                        i++;
                    }
                    if(listCourse.size()==0){
                        RVCourse.setVisibility(View.GONE);
                        EmptyViewCourse.setVisibility(View.VISIBLE);
                    }else{
                        RVCourse.setVisibility(View.VISIBLE);
                        EmptyViewCourse.setVisibility(View.GONE);
                    }

                }else{
                    RVCourse.setVisibility(View.GONE);
                    EmptyViewCourse.setVisibility(View.VISIBLE);
                }
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        courseAdapter = new RecyclerViewAdapterCourse(this,listCourse);
        RVCourse.setAdapter(courseAdapter);

        /*----------------------------------------------------------------------------------------*/


        /*--------------------------------------- Notes ------------------------------------------*/

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


        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveNote.setAnimation(RightToLeft);
                ETNote.setAnimation(RightToLeft);
                btnSaveNote.setVisibility(View.VISIBLE);
                ETNote.setVisibility(View.VISIBLE);
                btnAddNote.setVisibility(View.GONE);
            }
        });

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ETNote.getText().toString().length() == 0) {
                    ETNote.setError("Type Something");
                } else if (ETNote.getText().toString().length() >= 100) {
                    ETNote.setError("Note should be less than 100 characters");
                } else {
                    btnAddNote.setAnimation(RightToLeft);
                    btnAddNote.setVisibility(View.VISIBLE);
                    btnSaveNote.setVisibility(View.GONE);
                    ETNote.setVisibility(View.GONE);

                    String note = ETNote.getText().toString().trim();
                    DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Notes").push();
                    noteRef.child("Note").setValue(note);
                    noteRef.child("Date").setValue(getCurrentDate());
                    Toast.makeText(HomeActivity.this, "Note Saved", Toast.LENGTH_LONG).show();
                    ETNote.setText("");
                }
            }
        });

        listNote = new ArrayList<>();
        notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String noteid[] = new String[25];
                String note[] = new String[25];
                String date[] = new String[25];

                listNote.clear();
                if (dataSnapshot.exists()) {
                    RVNote.setVisibility(View.VISIBLE);
                    EmptyViewNote.setVisibility(View.GONE);

                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        noteid[i]= dataSnapshot1.getKey();
                        note[i]=dataSnapshot.child(noteid[i]).child("Note").getValue(String.class);
                        date[i]=dataSnapshot.child(noteid[i]).child("Date").getValue(String.class);
                        listNote.add(new NoteModel(noteid[i],note[i],date[i]));
                        i++;
                    }
                    if(listNote.size()==0){
                        RVNote.setVisibility(View.GONE);
                        EmptyViewNote.setVisibility(View.VISIBLE);
                    }else{
                        RVNote.setVisibility(View.VISIBLE);
                        EmptyViewNote.setVisibility(View.GONE);
                    }
                }else{
                    RVNote.setVisibility(View.GONE);
                    EmptyViewNote.setVisibility(View.VISIBLE);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        noteAdapter = new RecyclerViewAdapterNote(this,listNote);
        RVNote.setAdapter(noteAdapter);


        if (!isNetworkConnected(HomeActivity.this)) {
            EmptyViewCourse.setVisibility(View.VISIBLE);
            EmptyViewCourse.setText("It Seems You do Not Have Internet Connection. However, You Still Can Take Picture of the Attendance Sheet and Process it Later");
            CVHomeTakeAttendance.setVisibility(View.VISIBLE);
            RVCourse.setVisibility(View.GONE);

            CVHomeTakeAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, TakeAttendancePictureActivity.class);
                    intent.putExtra("UserId", "UserOfline");
                    intent.putExtra("UserName", "User Ofline");
                    intent.putExtra("CourseCode", "UndefinedCourseCode");
                    intent.putExtra("CourseName", "UndefinedCourseName");
                    intent.putExtra("UserProfileImageUrl", "No User Image");
                    startActivity(intent);
                }
            });
        }

    }

    public String getCurrentDate(){
        String dateToday;
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateToday= dateFormat.format(today);
        return dateToday;
    }

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
