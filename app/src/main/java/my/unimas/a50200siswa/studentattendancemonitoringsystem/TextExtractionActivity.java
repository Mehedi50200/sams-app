package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.opencv.android.OpenCVLoader;

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

    String processedtext, attendanceText;


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
        OpenCVLoader.initDebug();

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

        if (fileDirectory.isDirectory()) {
            listCroppedImages.clear();
            EmptyViewCroppedImage.setVisibility(View.GONE);
            RVCroppedImages.setVisibility(View.VISIBLE);
            listCroppedImages.clear();
            String PhotoPath[] = new String[100];
            String StudentMatric[] = new String[100];
            String AttendanceRecord[] = new String[100];

            for (int i = 1; i <= fileDirectory.listFiles().length; i++) {
                PhotoPath[i] = croppedImageDirectory + i + ".jpg";

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap croppedimageold = BitmapFactory.decodeFile(PhotoPath[i], options);
                Bitmap croppedimagenew = Bitmap.createScaledBitmap(croppedimageold, 400, 60, true);
                //   StudentMatric[i]=TextImageProcess(croppedimagenew);
                //   AttendanceRecord[i]=CircleDetection(croppedimagenew);

                //   listCroppedImages.add(new CroppedImageModel(String.valueOf(i),PhotoPath[i], StudentMatric[i], AttendanceRecord[i] ));
                listCroppedImages.add(new CroppedImageModel(String.valueOf(i), PhotoPath[i]));
            }
        } else {
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
