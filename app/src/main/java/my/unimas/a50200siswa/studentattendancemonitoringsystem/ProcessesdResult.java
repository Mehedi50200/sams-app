package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.chrisbanes.photoview.PhotoView;

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

public class ProcessesdResult extends AppCompatActivity {

    Bitmap image;
    String photoPath, chunkedImagedDirectory;
    Mat imageMat;
    PhotoView ProcessedImage;
    Button btnProcessText, btnTakePicture;
    File myDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OpenCVLoader.initDebug();

        setContentView(R.layout.activity_processesd_result);

        Intent intenttakeattendance = getIntent();
        String fname = intenttakeattendance.getStringExtra("fname");

        String root = Environment.getExternalStorageDirectory().toString();
        final File myDir = new File(root);

        photoPath = myDir+"/sams_images/"+ fname;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        image = BitmapFactory.decodeFile(photoPath, options);

        ProcessedImage = findViewById(R.id.pvprocessedimage);
        btnProcessText = findViewById(R.id.btnprocesstext);
        btnTakePicture = findViewById(R.id.btntakepictureagain);

        imageCropIntoPiecess(image);

        btnProcessText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent processedresultintent= new Intent(ProcessesdResult.this, TextExtractionActivity.class);
                processedresultintent.putExtra("chunkedImagedDirectory", chunkedImagedDirectory);
                startActivity(processedresultintent);
            }
        });

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent processedresultintent= new Intent(ProcessesdResult.this, TakeAttendanceActivity.class);
                startActivity(processedresultintent);
            }
        });

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




/*
    public void imageProcess(Bitmap bitmap){
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!txtRecognizer.isOperational()) {
            tvProcessedText.setText("Try Again");
        } else {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray items = txtRecognizer.detect(frame);
                StringBuilder strBuilder = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    TextBlock item = (TextBlock) items.valueAt(i);
                    strBuilder.append(item.getValue());
                    strBuilder.append("/");
                    for (Text line : item.getComponents()) {
                        //extract scanned text lines here
                        Log.v("lines", line.getValue());
                        for (Text element : line.getComponents()) {
                            //extract scanned text words here
                            Log.v("element", element.getValue());

                        }
                    }
                }
                tvProcessedText.setMovementMethod(new ScrollingMovementMethod());
                tvProcessedText.setText(strBuilder.toString().substring(0, strBuilder.toString().length()));
        }
    }

*/







}









