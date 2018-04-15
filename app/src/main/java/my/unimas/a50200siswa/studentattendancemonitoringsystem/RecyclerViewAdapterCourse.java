package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


class RecyclerViewAdapterCourse extends RecyclerView.Adapter<RecyclerViewAdapterCourse.CourseViewHolder> {

    private Context mContext;
    private List<CourseModel> mData;
    View divider;
    private LinearLayout llCourse;



    public RecyclerViewAdapterCourse() {

    }


    public RecyclerViewAdapterCourse(Context mContext, List<CourseModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View courseview;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        courseview = mInflater.inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(courseview);
    }


    @Override
    public void onBindViewHolder(CourseViewHolder holder, final int position) {

        holder.day.setText(mData.get(position).getDay());
        if(mData.get(position).getDay().trim().equals(getCurrentDay().trim())){
            llCourse.setBackgroundColor(Color.parseColor("#af1444"));
            divider.setBackgroundColor(Color.parseColor("#f68a5a"));
            holder.courseCode.setTextColor(Color.parseColor("#b58594"));
            holder.courseName.setTextColor(Color.parseColor("#fcc3d4"));

        }else{

            llCourse.setBackgroundResource(R.drawable.cardview_bg);

        }

      //  holder.userID.setText(mData.get(position).getUserId());
        holder.courseCode.setText(mData.get(position).getCourseCode());
        holder.courseName.setText(mData.get(position).getCourseName());
        holder.day.setText(mData.get(position).getDay());
        holder.time.setText(mData.get(position).getTime());

        holder.courseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, CourseActivity.class);

                // passing data to the book activity
                intent.putExtra("UserId", mData.get(position).getUserId());
                intent.putExtra("CourseCode", mData.get(position).getCourseCode());
                intent.putExtra("CourseName", mData.get(position).getCourseName());
                // start the activity
                mContext.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {

        return mData.size();
    }



    class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView userID, courseCode,courseName, day, time;
        CardView courseCardView;



        private CourseViewHolder(View itemView) {
            super(itemView);
           // userID = itemView.findViewById(R.id.userid);
            courseCode = itemView.findViewById(R.id.coursecode);
            courseName = itemView.findViewById(R.id.coursename);
            day=itemView.findViewById(R.id.tvday);
            time = itemView.findViewById(R.id.tvtime);
            courseCardView = itemView.findViewById(R.id.coursecardview);
            divider=itemView.findViewById(R.id.divider);
            llCourse =itemView.findViewById(R.id.llcourse);


        }
    }


    private String getCurrentDay(){

        String today;
        Date now = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        today= dayFormat.format(now);
        return today;


    }

}
