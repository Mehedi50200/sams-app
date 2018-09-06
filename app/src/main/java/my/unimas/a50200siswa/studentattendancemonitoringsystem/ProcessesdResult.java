package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProcessesdResult extends AppCompatActivity {

    Bitmap image;
    Mat imageMat;

    Button btnProcessText, btnTakePicture;
    File myDir;

    PhotoView ProcessedImage;

    Button btnSignOut;
    TextView UserName;
    CircleImageView userProfileImage;

    String fname;
    String photoPath, chunkedImagedDirectory;
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

        OpenCVLoader.initDebug();

        setContentView(R.layout.activity_processesd_result);

        /*------------------------- Receive data From Previous Intent ----------------------------*/
        Intent intent = getIntent();
        fname = intent.getStringExtra("fname");
        UserId = intent.getExtras().getString("UserId");
        userName = intent.getExtras().getString("UserName");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        UserProfileImageUrl = intent.getExtras().getString("UserProfileImageUrl");
        /*------------------------- Receive data From Previous Intent ----------------------------*/

        String root = Environment.getExternalStorageDirectory().toString();
        final File myDir = new File(root);
        photoPath = myDir+"/sams_images/"+ fname;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        image = BitmapFactory.decodeFile(photoPath, options);

        ProcessedImage = findViewById(R.id.pvprocessedimage);
        btnProcessText = findViewById(R.id.btnprocesstext);
        btnTakePicture = findViewById(R.id.btntakepictureagain);

        btnSignOut = findViewById(R.id.btnsignout_home);
        UserName = findViewById(R.id.username);
        userProfileImage =findViewById(R.id.userprofileimg);

        /*----------------------------- Database Reference Elements ------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        /*----------------------------------------------------------------------------------------*/

        UserName.setText(userName);
        GlideApp.with(this)
                .load(UserProfileImageUrl)
                .error(R.drawable.profilepic)
                .into(userProfileImage);

        imageCropIntoPiecess(image);

        btnProcessText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ProcessesdResult.this, TextExtractionActivity.class);
                intent.putExtra("chunkedImagedDirectory", chunkedImagedDirectory);
                intent.putExtra("UserId", UserId );
                intent.putExtra("UserName", userName );
                intent.putExtra("CourseCode", CourseCode);
                intent.putExtra("CourseName", CourseName);
                intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);

                Pair[] pairs = new Pair[1];
                pairs[0]= new Pair<View, String>(ProcessedImage, "tmainlayout");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProcessesdResult.this, pairs);

                startActivity(intent, options.toBundle());
            }
        });

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent processedresultintent= new Intent(ProcessesdResult.this, TakeAttendancePictureActivity.class);
                startActivity(processedresultintent);
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
                    startActivity(new Intent(ProcessesdResult.this, SignInActivity.class));
                }
            }
        };

    }


    public void imageCropIntoPiecess(Bitmap bitmap){

        String  timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/sams_images"+"/"+timeStamp);
        myDir.mkdir();

        chunkedImagedDirectory = myDir.toString() + "/";

        int n=1;

        imageMat=new Mat();

        Utils.bitmapToMat(bitmap,imageMat);
        Mat imgSource=imageMat.clone();

        Mat imageHSV = new Mat(imgSource.size(), CvType.CV_8UC4);
        Mat imageBlurr = new Mat(imgSource.size(),CvType.CV_8UC4);
        Mat imageA = new Mat(imgSource.size(), CvType.CV_32F);

        Imgproc.cvtColor(imgSource, imageHSV, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(5,5), 0);
        Imgproc.adaptiveThreshold(imageBlurr, imageA, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV,7, 5);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imageA, contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);


        Vector<Mat> rectangles = new Vector<Mat>();
        for(int i=0; i< contours.size();i++)
        {
            if (Imgproc.contourArea(contours.get(i)) > 100)
            {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                if ((rect.height > 30 && rect.height < 120) && (rect.width > 120 && rect.width < 500))
                {
                    Rect rec = new Rect(rect.x, rect.y, rect.width, rect.height);

                    Mat roi = imageMat.submat(rec);
                    String chunkedfilename = chunkedImagedDirectory + n + ".jpg";
                    Imgcodecs.imwrite(chunkedfilename, roi);

                    rectangles.add(new Mat(imgSource, rec));
                    Imgproc.rectangle(imgSource, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255));
                    n++;
                }
            }
        }

        Bitmap analyzed=Bitmap.createBitmap(imgSource.cols(),imgSource.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imgSource,analyzed);
        ProcessedImage.setImageBitmap(analyzed);

    }



}










