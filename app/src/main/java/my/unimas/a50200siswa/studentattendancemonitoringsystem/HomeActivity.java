package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
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

public class HomeActivity extends AppCompatActivity {
    String userID;
    List<CourseModel> listCourse;
    List<NoteModel> listNote;
    TextView btnSignOut, UserName,EmptyViewCourse, EmptyViewNote;
    EditText ETNote;
    Button btnSaveNote, btnAddNote, btnp;
    Animation UpDown,DownUp,  RightToLeft;
    LinearLayout HomeUp, HomeDown;



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
        btnSignOut = findViewById(R.id.btnsignout_home);
        ETNote = findViewById(R.id.etnote);
        btnAddNote =findViewById(R.id.btnaddnote);
        btnSaveNote =findViewById(R.id.btnsavenote);
        UserName = findViewById(R.id.username);


        final RecyclerView RVCourse = findViewById(R.id.recyclerviewcourse);
        final RecyclerView rvnote = findViewById(R.id.rvnote);
        RVCourse.setLayoutManager(new GridLayoutManager(this,2));
        rvnote.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

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
                UserName.setText(userName);
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
                        listCourse.add(new CourseModel(userID,coursecode[i],coursename[i], day[i],time[i]));
                        i++;
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
                    rvnote.setVisibility(View.VISIBLE);
                    EmptyViewNote.setVisibility(View.GONE);

                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        noteid[i]= dataSnapshot1.getKey();
                        note[i]=dataSnapshot.child(noteid[i]).child("Note").getValue(String.class);
                        date[i]=dataSnapshot.child(noteid[i]).child("Date").getValue(String.class);
                        listNote.add(new NoteModel(noteid[i],note[i],date[i]));
                        i++;
                    }
                }else{
                    rvnote.setVisibility(View.GONE);
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
        rvnote.setAdapter(noteAdapter);
    }

    public String getCurrentDate(){
        String dateToday;
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateToday= dateFormat.format(today);
        return dateToday;
    }


}
