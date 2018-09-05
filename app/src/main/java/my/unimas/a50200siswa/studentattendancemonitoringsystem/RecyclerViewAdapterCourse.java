package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
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
    View divider ;
    private LinearLayout llCourse;

    public RecyclerViewAdapterCourse() {
    }   //Constructor


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
    public void onBindViewHolder(final CourseViewHolder holder, final int position) {

        holder.day.setText(mData.get(position).getDay());
        if(mData.get(position).getDay().trim().equals(getCurrentDay().trim())){
            llCourse.setBackgroundColor(Color.parseColor("#562235"));
            holder.courseName.setTextColor(Color.parseColor("#b23b66"));
        }else{
            llCourse.setBackgroundResource(R.drawable.home_bg);
            divider.setBackgroundColor(Color.parseColor("#c61e4a"));
        }

        holder.courseCode.setText(mData.get(position).getCourseCode());
        holder.courseName.setText(mData.get(position).getCourseName());
        holder.day.setText(mData.get(position).getDay());
        holder.time.setText(mData.get(position).getTime());

        holder.courseCardView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CourseActivity.class);
                // passing data to the book activity
                intent.putExtra("UserId", mData.get(position).getUserId());
                intent.putExtra("CourseCode", mData.get(position).getCourseCode());
                intent.putExtra("CourseName", mData.get(position).getCourseName());
                intent.putExtra("UserProfileImageUrl", mData.get(position).getUserProfileImageUrl());

                Pair[] pairs = new Pair[2];
                pairs[0]= new Pair<View, String>(holder.courseCardView, "tparent");
                pairs[1]= new Pair<View, String>(holder.courseName, "tcoursename");
                pairs[1]= new Pair<View, String>(holder.courseCode, "tcoursecode");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
                // start the activity
                mContext.startActivity(intent, options.toBundle());
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