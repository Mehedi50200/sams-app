package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;

public class ProcessesdResult extends AppCompatActivity {

    TextView tvProcessedText;
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processesd_result);

        Intent intenttakeattendance = getIntent();
        String fname = intenttakeattendance.getStringExtra("fname");

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);

        String photoPath = myDir+"/sams_images/"+ fname;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        image = BitmapFactory.decodeFile(photoPath, options);
        tvProcessedText = findViewById(R.id.tvprocessedtext);
        imageProcess(image);

    }



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
                tvProcessedText.setText(strBuilder.toString().substring(0, strBuilder.toString().length()));
        }
    }

    /*

    public void imageCropSave(Mat bitmap) {

        Mat img = bitmap;


    }

*/

}






