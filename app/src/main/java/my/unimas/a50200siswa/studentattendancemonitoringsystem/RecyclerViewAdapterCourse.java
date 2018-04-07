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

class RecyclerViewAdapterCourse extends RecyclerView.Adapter<RecyclerViewAdapterCourse.CourseViewHolder> {

    private Context mContext ;
    private List<CourseModel> mData ;

    public RecyclerViewAdapterCourse() {

    }

    public RecyclerViewAdapterCourse(Context mContext, List<CourseModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_course,parent,false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, final int position) {
        holder.userID.setText(mData.get(position).getUserId());
        holder.courseCode.setText(mData.get(position).getCourseCode());
        holder.courseName.setText(mData.get(position).getCourseName());

        holder.courseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,CourseModel.class);

                // passing data to the book activity
                intent.putExtra("User ID",mData.get(position).getUserId());
                intent.putExtra("Course Code",mData.get(position).getCourseCode());
                intent.putExtra("Course Name",mData.get(position).getCourseName());
                // start the activity
                mContext.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
       return mData.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView userID,courseCode,courseName;
        CardView courseCardView;


        public CourseViewHolder(View itemView) {
            super(itemView);
            userID =  itemView.findViewById(R.id.userid);
            courseCode= itemView.findViewById(R.id.coursecode);
            courseName=  itemView.findViewById(R.id.coursename);
            courseCardView=  itemView.findViewById(R.id.coursecardview);
        }
    }


}
