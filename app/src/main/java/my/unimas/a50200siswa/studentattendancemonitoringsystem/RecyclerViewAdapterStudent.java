package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


class RecyclerViewAdapterStudent extends RecyclerView.Adapter<RecyclerViewAdapterStudent.StudentViewHolder> {

    private Context mContext;
    private List<StudentModel> mData;
    View divider ;

    public RecyclerViewAdapterStudent() {

    }

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
    public void onBindViewHolder(StudentViewHolder holder, final int position) {

        holder.studentId.setText(mData.get(position).getStudentId());
        holder.studentName.setText(mData.get(position).getStudentName());
        holder.studentSerial.setText(mData.get(position).getStudentSerial());

        holder.studentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, StudentProfileActivity.class);
                intent.putExtra("StudentId", mData.get(position).getStudentId());
                intent.putExtra("StudentName", mData.get(position).getStudentName());
                intent.putExtra("CourseCode", mData.get(position).getCourseCode());
                intent.putExtra("CourseName", mData.get(position).getCourseName());
                mContext.startActivity(intent);
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

        }
    }

}
