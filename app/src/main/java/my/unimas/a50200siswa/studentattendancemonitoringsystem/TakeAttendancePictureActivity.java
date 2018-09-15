package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adityaarora.liveedgedetection.activity.ScanActivity;
import com.adityaarora.liveedgedetection.constants.ScanConstants;
import com.adityaarora.liveedgedetection.util.ScanUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class TakeAttendancePictureActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = "Take Picture";
    private ImageView scannedImageView;

    Button btnProcess;
    Bitmap capturedpic;

    Layout actionbar;

    Button btnSignOut;
    TextView UserName;
    CircleImageView userProfileImage;

    Uri fileuri;
    String fname, data;
    String UserId,userName, CourseCode, CourseName,UserProfileImageUrl;

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
        setContentView(R.layout.activity_take_attendance_picture);


        scannedImageView = findViewById(R.id.scanned_image);
        btnProcess =findViewById(R.id.btnprocess);

        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        userProfileImage =findViewById(R.id.userprofileimg);

        /*----------------------------- Database Reference Elements ------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        /*----------------------------------------------------------------------------------------*/

        Intent intent = getIntent();
        UserId = intent.getExtras().getString("UserId");
        userName = intent.getExtras().getString("UserName");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        UserProfileImageUrl = intent.getExtras().getString("UserProfileImageUrl");

        UserName.setText(userName);

        GlideApp.with(this)
                .load(UserProfileImageUrl)
                .error(R.drawable.profilepic)
                .into(userProfileImage);

        if(intent.getData() != null){
            fileuri = intent.getData();

            try {
                capturedpic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileuri);
                scannedImageView.setImageBitmap(capturedpic);
                saveTempImage(capturedpic);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            startScan();
        }

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(TakeAttendancePictureActivity.this, ProcessesdResult.class);
                intent.putExtra("fname", fname);
                intent.putExtra("UserId", UserId );
                intent.putExtra("UserName", userName );
                intent.putExtra("CourseCode", CourseCode);
                intent.putExtra("CourseName", CourseName);
                intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);

                Pair[] pairs = new Pair[1];
                pairs[0]= new Pair<View, String>(scannedImageView, "tmainimage");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TakeAttendancePictureActivity.this, pairs);

                startActivity(intent, options.toBundle());
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
                    startActivity(new Intent(TakeAttendancePictureActivity.this, SignInActivity.class));
                }
            }
        };

    }

    /*------------------------------------ Edge Detection -----------------------------------------*/
    private void startScan() {
        Intent intent = new Intent(this,  ScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(null != data && null != data.getExtras()) {
                    String filePath = data.getExtras().getString(ScanConstants.SCANNED_RESULT);
                    capturedpic = ScanUtils.decodeBitmapFromFile(filePath, ScanConstants.IMAGE_NAME);
                    scannedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    scannedImageView.setImageBitmap(capturedpic);
                    saveTempImage(capturedpic);
                }
            } else if(resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }
    /*------------------------------------ ************* -----------------------------------------*/


    /*------------------------------------ Store Image -------------------------------------------*/
    public void saveTempImage(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        }else{
            Toast.makeText(this, "Please provide permission to write on the Storage!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/sams_images/" + CourseCode);

        if (! root.exists()){
            root.mkdirs();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

        String ImagePath = root.getPath().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMddHH").format(new Date());
        fname = CourseCode + "_" + timeStamp + ".jpg";

        File file = new File(ImagePath, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
   /*------------------------------------ ************* -----------------------------------------*/


}