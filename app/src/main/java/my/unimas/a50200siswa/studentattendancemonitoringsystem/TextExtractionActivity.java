package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TextExtractionActivity extends AppCompatActivity {

    List<CroppedImageModel> listCroppedImages;
    RecyclerView RVCroppedImages;
    RecyclerViewAdapterCroppedImages CroppedImageAdapter;

    Button btnSignOut;
    TextView UserName;
    CircleImageView userProfileImage;

    TextView EmptyViewCroppedImage;

    String UserId,userName,CourseCode, CourseName,UserProfileImageUrl;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


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
        setContentView(R.layout.activity_text_extraction);

        /*------------------------- Receive data From Previous Intent ----------------------------*/
        Intent intent = getIntent();
        String croppedImageDirectory = intent.getStringExtra("chunkedImagedDirectory");
        UserId = intent.getExtras().getString("UserId");
        userName = intent.getExtras().getString("UserName");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        UserProfileImageUrl = intent.getExtras().getString("UserProfileImageUrl");
        /*----------------------------------------------------------------------------------------*/

        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        userProfileImage =findViewById(R.id.userprofileimg);

        EmptyViewCroppedImage =findViewById(R.id.empty_view_croppedimage);
        RVCroppedImages = findViewById(R.id.rvcroppedimage);
        RVCroppedImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        /*----------------------------- Database Reference Elements ------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        /*----------------------------------------------------------------------------------------*/


        UserName.setText(userName);
        GlideApp.with(TextExtractionActivity.this)
                .load(UserProfileImageUrl)
                .error(R.drawable.profilepic)
                .into(userProfileImage);

        File fileDirectory = new File(croppedImageDirectory);

        listCroppedImages = new ArrayList<>();

        if(fileDirectory.isDirectory()){
            EmptyViewCroppedImage.setVisibility(View.GONE);
            RVCroppedImages.setVisibility(View.VISIBLE);


            String PhotoPath[]= new String[100];
            String AttendanceRecord[]= new String[100];

            for (int i=1; i <=fileDirectory.listFiles().length; i++){
                PhotoPath[i] = croppedImageDirectory+ i + ".jpg";
                listCroppedImages.add(new CroppedImageModel(PhotoPath[i], String.valueOf(i)));
            }

        }else{

            EmptyViewCroppedImage.setVisibility(View.VISIBLE);
            RVCroppedImages.setVisibility(View.GONE);
        }

        CroppedImageAdapter = new RecyclerViewAdapterCroppedImages(this,listCroppedImages);
        RVCroppedImages.setAdapter(CroppedImageAdapter);


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
                    startActivity(new Intent(TextExtractionActivity.this, SignInActivity.class));
                }
            }
        };



    }

}
