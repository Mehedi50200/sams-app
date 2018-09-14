package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class RecyclerViewAdapterAttendance extends RecyclerView.Adapter<RecyclerViewAdapterAttendance.AttendanceViewHolder> {

    private Context mContext;
    private List<AttendanceModel> mData;

    public RecyclerViewAdapterAttendance() {} //Constructor

    public RecyclerViewAdapterAttendance(Context mContext, List<AttendanceModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View attendanceview;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        attendanceview = mInflater.inflate(R.layout.item_attendance_history, parent, false);
        return new AttendanceViewHolder(attendanceview);
    }


    @Override
    public void onBindViewHolder(final AttendanceViewHolder holder, final int position) {

        holder.Date.setText(mData.get(position).getAttendanceDate());
        holder.Week.setText(mData.get(position).getWeek());
        if(mData.get(position).getStatus().trim().equals("Present")){
            holder.IVStatus.setBackgroundResource(R.drawable.bgpresent);
        }else if (mData.get(position).getStatus().trim().equals("Absent")) {
            holder.IVStatus.setBackgroundResource(R.drawable.bgabsent);
        }else{
            holder.IVStatus.setBackgroundResource(R.drawable.bgfailed);
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView Date, Week;
        ImageView IVStatus;
        private AttendanceViewHolder(View itemView) {
            super(itemView);
            Date = itemView.findViewById(R.id.tvadate);
            Week = itemView.findViewById(R.id.tvaweek);
            IVStatus =itemView.findViewById(R.id.ivastatus);
        }
    }


}
