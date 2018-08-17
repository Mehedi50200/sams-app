package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.adityaarora.liveedgedetection.activity.ScanActivity;
import com.adityaarora.liveedgedetection.constants.ScanConstants;
import com.adityaarora.liveedgedetection.util.ScanUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TakeAttendanceActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = "Take Picture";
    private ImageView scannedImageView;
    Button btnProcess;
    Bitmap capturedpic;
    String fname, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        scannedImageView = findViewById(R.id.scanned_image);
        btnProcess =findViewById(R.id.btnprocess);
        startScan();

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTempImage(capturedpic);
                Intent processedresultintent= new Intent(TakeAttendanceActivity.this, ProcessesdResult.class);
                processedresultintent.putExtra("fname", fname);
                startActivity(processedresultintent);
            }
        });

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

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/sams_images");

        if (! myDir.exists()){
            myDir.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

        String  timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        fname = timeStamp +".jpg";

        File file = new File(myDir, fname);
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
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
   /*------------------------------------ ************* -----------------------------------------*/


}