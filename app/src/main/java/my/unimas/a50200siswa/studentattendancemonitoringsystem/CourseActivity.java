package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class CourseActivity extends AppCompatActivity {

    private TextView userid,coursecode,coursename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        userid = findViewById(R.id.userid);
        coursecode =  findViewById(R.id.coursecode);
        coursename =  findViewById(R.id.coursename);
        //img = (ImageView) findViewById(R.id.bookthumbnail);

        // Recieve data
        Intent intent = getIntent();
        String UserId = intent.getExtras().getString("UserId");
        String CourseCode = intent.getExtras().getString("CourseCode");
        String CourseName = intent.getExtras().getString("CourseName");

        // Setting values

        userid.setText(UserId);
        coursecode.setText(CourseCode);
        coursename.setText(CourseName);
       // img.setImageResource(image);


    }
}
