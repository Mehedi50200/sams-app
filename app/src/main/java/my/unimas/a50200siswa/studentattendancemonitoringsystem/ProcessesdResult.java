package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    Button btnProcessText;

    PhotoView ProcessedImage;
    TextView UserName;

    Button btnSignOut;
    CircleImageView userProfileImage;

    String fname;
    String photoPath, chunkedImagedDirectory;
    String UserId,userName, CourseCode, CourseName,UserProfileImageUrl;

    CircleImageView cvPRHome, cvPRTakeAttendance;

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

        /*----------------------------------------------------------------------------------------*/

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"/sams_images/" + CourseCode);
        photoPath = root.getPath().toString()+"/" + fname;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        image = BitmapFactory.decodeFile(photoPath, options);

        ProcessedImage = findViewById(R.id.pvprocessedimage);
        btnProcessText = findViewById(R.id.btnprocesstext);

        cvPRHome = findViewById(R.id.cvprhome);
        cvPRTakeAttendance = findViewById(R.id.cvprtakeattendance);

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


        final View Scannerbar = findViewById(R.id.scannerbar);
        final Animation ImageprocessAnimation = AnimationUtils.loadAnimation(ProcessesdResult.this, R.anim.imagescanners);



        ImageprocessAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Scannerbar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        btnProcessText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

            Scannerbar.setVisibility(View.VISIBLE);
            Scannerbar.startAnimation(ImageprocessAnimation);

            final Intent intent= new Intent(ProcessesdResult.this, TextExtractionActivity.class);
            intent.putExtra("chunkedImagedDirectory", chunkedImagedDirectory);
            intent.putExtra("UserId", UserId );
            intent.putExtra("UserName", userName );
            intent.putExtra("CourseCode", CourseCode);
            intent.putExtra("CourseName", CourseName);
            intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);


            Thread timer =new Thread() {
                public void run (){
                    try {
                        sleep (2000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        startActivity(intent);
                        finish();
                    }
                }
            };
            timer.start();

            }
        });


        cvPRHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProcessesdResult.this);
                View view = (ProcessesdResult.this).getLayoutInflater().inflate(R.layout.alert_goback, null);
                final Button btnYes = view.findViewById(R.id.btnhyes);
                final Button btnNo = view.findViewById(R.id.btnhno);
                mBuilder.setView(view);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProcessesdResult.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        cvPRTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProcessesdResult.this, TakeAttendancePictureActivity.class);
                intent.putExtra("UserId", UserId );
                intent.putExtra("UserName", userName );
                intent.putExtra("CourseCode", CourseCode);
                intent.putExtra("CourseName", CourseName);
                intent.putExtra("UserProfileImageUrl", UserProfileImageUrl);
                startActivity(intent);
                finish();
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

        String timeStamp =  new SimpleDateFormat("ddMMyyyy-HH:mm").format(new Date());

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/sams_images/"+ CourseCode +"/" + timeStamp);
        root.mkdirs();

        chunkedImagedDirectory = root.getPath().toString() + "/";

        int n=1;

        imageMat=new Mat();

        Utils.bitmapToMat(bitmap,imageMat);
        Mat imgSource=imageMat.clone();

        Mat imageHSV = new Mat(imgSource.size(), CvType.CV_8UC4);
        Mat imageBlurr = new Mat(imgSource.size(),CvType.CV_8UC4);
        Mat imageA = new Mat(imgSource.size(), CvType.CV_32F);

        Imgproc.cvtColor(imgSource, imageHSV, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(1,1), 0);
        Imgproc.adaptiveThreshold(imageBlurr, imageA, 255,Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV,7, 5);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imageA, contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);


        Vector<Mat> rectangles = new Vector<Mat>();
        for(int i=0; i< contours.size();i++)
        {
            if (Imgproc.contourArea(contours.get(i)) > 100)
            {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                if ((rect.height > 30 && rect.height < 55) && (rect.width > 200 && rect.width < 400))
                {
                    Rect rec = new Rect(rect.x, rect.y, rect.width, rect.height);

                    Mat roi = imageMat.submat(rec);
                   // String chunkedfilename = chunkedImagedDirectory + n+ "_w" + rect.width + "_h" + rect.height + ".jpg";
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










