package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


class RecyclerViewAdapterStudent extends RecyclerView.Adapter<RecyclerViewAdapterStudent.StudentViewHolder> {

    private Context mContext;
    private List<StudentModel> mData;
    View divider ;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();



    public RecyclerViewAdapterStudent(TextExtractionActivity mContext, ArrayList<Bitmap> listCroppedImages) {}  //Constructor

    public RecyclerViewAdapterStudent(Context mContext, List<StudentModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View studentview;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        studentview = mInflater.inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(studentview);
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, final int position) {

        StorageReference profileImageReference =storageReference.child("StudentsPic/" +mData.get(position).getStudentId()+".jpg");

        holder.studentId.setText(mData.get(position).getStudentId());
        holder.studentName.setText(mData.get(position).getStudentName());
        holder.studentSerial.setText(mData.get(position).getStudentSerial());

        GlideApp.with(mContext /* context */)
                .load(profileImageReference)
                .error(R.drawable.profilepic)
                .into(holder.studentImage);

        holder.studentCardView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StudentProfileActivity.class);
                intent.putExtra("StudentId", mData.get(position).getStudentId());
                intent.putExtra("StudentName", mData.get(position).getStudentName());
                intent.putExtra("CourseCode", mData.get(position).getCourseCode());
                intent.putExtra("CourseName", mData.get(position).getCourseName());
                intent.putExtra("UserProfileImageUrl", mData.get(position).getUserProfileImageUrl());

                Pair[] pairs = new Pair[4];
                pairs[0]= new Pair<View, String>(holder.studentId, "tstudentmatric");
                pairs[1]= new Pair<View, String>(holder.studentName, "tstudentname");
                pairs[2]= new Pair<View, String>(holder.studentImage, "tstudentpic");
                pairs[3]= new Pair<View, String>(holder.studentCardView, "tcard");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
                mContext.startActivity(intent, options.toBundle() );


            }
        });

    }


    @Override
    public int getItemCount() {

        return mData.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentId, studentName, studentSerial;
        CardView studentCardView;
        CircleImageView studentImage;

        private StudentViewHolder(View itemView) {
            super(itemView);
            studentId = itemView.findViewById(R.id.tvstudentmatric);
            studentName = itemView.findViewById(R.id.tvstudentname);
            studentSerial =itemView.findViewById(R.id.tvstudentserial);
            studentCardView = itemView.findViewById(R.id.studentcardview);
            studentImage = itemView.findViewById(R.id.studentimage);

        }
    }

}
