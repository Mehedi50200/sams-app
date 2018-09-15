package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GenerateAttendanceSheetActivity extends AppCompatActivity {

    List<StudentIdModel> listStudentId;
    String CourseCode, CourseName, UserId, UserName, UserProfileImageUrl;

    Button btnSignOut;
    TextView TVUserName;
    CircleImageView userProfileImage;
    private TextView coursecode, coursename;
    String AttendanceSheetPath;
    PDFView pdfViewAttendanceSheet;



    /*------------------------- Firebase Database Element Declaration ----------------------------*/
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference StudentRef;
    /*--------------------------------------------------------------------------------------------*/

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
        setContentView(R.layout.activity_generate_attendance_sheet);

        /*------------------------- Receive data From Previous Intent ----------------------------*/
        Intent intent = getIntent();
        UserId = intent.getExtras().getString("UserId");
        UserName = intent.getExtras().getString("UserName");
        CourseCode = intent.getExtras().getString("CourseCode");
        CourseName = intent.getExtras().getString("CourseName");
        UserProfileImageUrl = intent.getExtras().getString("UserProfileImageUrl");
        /*----------------------------------------------------------------------------------------*/

        /*----------------------------- Database Reference Elements ------------------------------*/
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        UserId = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        StudentRef = rootRef.child("Users").child(UserId).child("Course").child(CourseCode).child("Students");
        /*----------------------------------------------------------------------------------------*/


        btnSignOut = findViewById(R.id.btnsignout_home);
        TVUserName = findViewById(R.id.username);
        userProfileImage = findViewById(R.id.userprofileimg);

        coursecode = findViewById(R.id.generatecoursecode);
        coursename = findViewById(R.id.generatecoursename);

        coursecode.setText(CourseCode);
        coursename.setText(CourseName);
        TVUserName.setText(UserName);

        pdfViewAttendanceSheet = (PDFView) findViewById(R.id.pdfView);


        GlideApp.with(GenerateAttendanceSheetActivity.this)
                .load(UserProfileImageUrl)
                .error(R.drawable.profilepic)
                .into(userProfileImage);

        listStudentId = new ArrayList<>();
        StudentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String studentId[] = new String[50];

                listStudentId.clear();
                if (dataSnapshot.exists()) {
                 //   EmptyViewStudent.setVisibility(View.GONE);
                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        studentId[i]= dataSnapshot1.getKey();
                        listStudentId.add(new StudentIdModel(studentId[i]));
                        i++;
                    }

                    if(listStudentId.size()==0){

                    }
                }else{

                }

                try {
                    Document document = new Document();
                    File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                            "/sams_images/AttendanceSheet");
                    root.mkdirs();
                    AttendanceSheetPath = root.getPath().toString() + "/"+CourseCode+ ".pdf";
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(AttendanceSheetPath));

                    document.open();
                    PdfContentByte canvas = writer.getDirectContent();
                    ColumnText ct = new ColumnText(canvas);
                    ct.setSimpleColumn(200,750,400,780);
                    String documentTitle = CourseCode + " " + CourseName;
                    Paragraph paragraph = new Paragraph(new Phrase(2,documentTitle, FontFactory.getFont(FontFactory.TIMES_BOLD, 15)));//add ur string here
                    ct.addElement(paragraph);
                    ct.setAlignment(Element.ALIGN_CENTER);
                    ct.go();
                    writeData(writer, document);

                } catch (Exception e) {
                    Log.v("PDFcreation", e.toString());
                }

                pdfViewAttendanceSheet.fromUri(Uri.fromFile(new File(AttendanceSheetPath))).load();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value", error.toException());
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
                    startActivity(new Intent(GenerateAttendanceSheetActivity.this, SignInActivity.class));
                }
            }
        };

    }

    public void writeData(PdfWriter writer, Document document) throws DocumentException {

        int lrectllx = 70;
        int lrectlly = 720;
        int lrecturx = 280;
        int lrectury = 750;

        int rrectllx = 320;
        int rrectlly = 720;
        int rrecturx = 530;
        int rrectury = 750;

        String StudentId[] = new String[50];

        double cr= 9.50;

        for(int i=0; i<listStudentId.size(); i++){

            /* if dataIndex [i] is even number will create box at left side and for odd number create box at right side*/

            if (i%2 == 0){
                PdfContentByte canvas = writer.getDirectContent();
                /*-------------------------- Creating circle -------------------------------------*/
                canvas.circle(lrecturx- 30, (lrectlly+lrectury)/2, cr);
                canvas.setColorStroke(BaseColor.BLACK);

                /*-------------------------- Creating Rectangle -------------------------------------*/
                Rectangle rectangle = new Rectangle(lrectllx, lrectlly, lrecturx, lrectury);
                rectangle.setBorder(Rectangle.BOX);
                canvas.setColorStroke(BaseColor.BLACK);
                rectangle.setBorderWidth(1);
                canvas.rectangle(rectangle);

                /*--------------------------- Appending Text -------------------------------------*/
                ColumnText ct = new ColumnText(canvas);
                ct.setSimpleColumn(rectangle);
                StudentId[i] = listStudentId.get(i).getStudentId();
                Paragraph studentid = new Paragraph(new Phrase(20,StudentId[i], FontFactory.getFont(FontFactory.TIMES, 15)));//add ur string here
                studentid.setAlignment(Element.ALIGN_LEFT);
                studentid.setIndentationLeft(20);
                ct.addElement(studentid);
                ct.go();

                lrectlly = lrectlly - 42;
                lrectury = lrectury - 42;
               // lcx = lrecturx- 30 ;
               // lcy = (lrectlly+lrectury)/2;

            }else{
                PdfContentByte canvas = writer.getDirectContent();

                /*-------------------------- Creating circle -------------------------------------*/
                canvas.circle(rrecturx- 30, (rrectlly+rrectury)/2, cr);
                canvas.setColorStroke(BaseColor.BLACK);

                /*-------------------------- Creating Rectangle -------------------------------------*/
                Rectangle rectangle = new Rectangle(rrectllx, rrectlly, rrecturx, rrectury);
                rectangle.setBorder(Rectangle.BOX);
                rectangle.setBorderWidth(1);
                canvas.setColorStroke(BaseColor.BLACK);
                canvas.rectangle(rectangle);

                /*-------------------------- Appending Text ---------------------------------------*/
                ColumnText ct = new ColumnText(canvas);
                ct.setSimpleColumn(rectangle);
                StudentId[i] = listStudentId.get(i).getStudentId();
                Paragraph studentid = new Paragraph(new Phrase(20,StudentId[i], FontFactory.getFont(FontFactory.TIMES, 15)));//add ur string here
                studentid.setAlignment(Element.ALIGN_LEFT);
                studentid.setIndentationLeft(20);
                ct.addElement(studentid);
                ct.go();

                rrectlly = rrectlly - 42;
                rrectury = rrectury - 42;
            //    rcx = rrecturx- 30 ;
            //    rcy = (rrectlly+rrectury)/2;
            }
        }

        document.close();

    }


}
