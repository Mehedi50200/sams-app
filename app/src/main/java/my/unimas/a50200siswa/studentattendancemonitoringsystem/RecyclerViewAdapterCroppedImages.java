package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

class RecyclerViewAdapterCroppedImages extends RecyclerView.Adapter<RecyclerViewAdapterCroppedImages.CroppedimageViewHolder> {

    private Context mContext;
    private List<CroppedImageModel> mData;
    int x =0;

    public RecyclerViewAdapterCroppedImages() {
    }   //Constructor


    public RecyclerViewAdapterCroppedImages(Context mContext, List<CroppedImageModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public CroppedimageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View croppedimageview;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        croppedimageview = mInflater.inflate(R.layout.item_cropped_image, parent, false);
        return new CroppedimageViewHolder(croppedimageview);
    }

    @Override
    public void onBindViewHolder(final CroppedimageViewHolder holder, final int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        String CourseCode = mData.get(position).getCourseCode();
        String StudentMatric = mData.get(position).getStudentMatric();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference StudentsRef = rootRef.child("Users").child(userID).child("Course").child(CourseCode).child("Students");

        Query query = StudentsRef.orderByKey().equalTo(StudentMatric);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    holder.LPData.setBackgroundResource(R.drawable.card_gradientyellow);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

        final CroppedImageModel changedRecord = mData.get(position);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap croppedimageold = BitmapFactory.decodeFile(mData.get(position).getCroppedImagePath(), options);
        Bitmap croppedimagenew = Bitmap.createScaledBitmap(croppedimageold, 528, 80, true);

        holder.StudentNo.setText(mData.get(position).getStudentNo());

        holder.CroppedImage.setImageBitmap(croppedimagenew);
        holder.StudentMatric.setText(mData.get(position).getStudentMatric());
        holder.StudentStatus.setText(mData.get(position).getAttendanceRecord());

        if(mData.get(position).getAttendanceRecord().equals("Present")){
            holder.IVattendance.setBackgroundResource(R.drawable.presentdot);
        }else if(mData.get(position).getAttendanceRecord().equals("Absent")){
            holder.IVattendance.setBackgroundResource(R.drawable.absentdot);
        }else if(mData.get(position).getAttendanceRecord().equals("Failed")){
            holder.IVattendance.setBackgroundResource(R.drawable.faileddot);
        }

        if(mData.get(position).getAttendanceRecord().equals("Failed")){
            holder.LPData.setBackgroundResource(R.drawable.card_gradientpurple);
        }else if (mData.get(position).getAttendanceRecord().equals("Present")) {
            holder.LPData.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if (mData.get(position).getAttendanceRecord().equals("Absent")) {
            holder.LPData.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.StudentMatric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lEditStudentMatric.setVisibility(View.VISIBLE);
            }

        });

        holder.btnSaveStudentMatric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.get(position).setStudentMatric(holder.ETCorrectStudentMatric.getText().toString());
                correctionAttendance(position, changedRecord);
                holder.lEditStudentMatric.setVisibility(View.GONE);
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lEditStudentMatric.setVisibility(View.GONE);
            }
        });

        holder.btnItemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                View view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.alert_delete, null);
                final Button btnYes = view.findViewById(R.id.btnyes);
                final Button btnNo = view.findViewById(R.id.btnno);
                mBuilder.setView(view);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(position);
                        dialog.dismiss();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        /*

        holder.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
                holder.LRemoveItem.setVisibility(View.GONE);
            }
        });

        holder.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.LRemoveItem.setVisibility(View.GONE);
            }
        });

        */


        holder.CroppedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mData.get(position).getAttendanceRecord())
                {
                    case "Failed":
                        mData.get(position).setAttendanceRecord("Present");
                        correctionAttendance(position, changedRecord);//code
                        break;

                    case "Present":
                        mData.get(position).setAttendanceRecord("Absent");
                        correctionAttendance(position, changedRecord);
                        break;

                    case "Absent":
                        mData.get(position).setAttendanceRecord("Present");
                        correctionAttendance(position, changedRecord);
                        break;

                    default:
                        break;
                }
            }
        });


        holder.IVattendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (mData.get(position).getAttendanceRecord())
                {
                    case "Failed":
                        mData.get(position).setAttendanceRecord("Present");
                        correctionAttendance(position, changedRecord);//code
                        break;

                    case "Present":
                        mData.get(position).setAttendanceRecord("Absent");
                        correctionAttendance(position, changedRecord);
                        break;

                    case "Absent":
                        mData.get(position).setAttendanceRecord("Present");
                        correctionAttendance(position, changedRecord);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    public void correctionAttendance(int position, CroppedImageModel attendanceRecord ) {
        mData.set(position, attendanceRecord);
        notifyItemChanged(position);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class CroppedimageViewHolder extends RecyclerView.ViewHolder {
        TextView StudentNo, StudentMatric, StudentStatus;
        ImageView CroppedImage, IVattendance;
        LinearLayout LPData, LRemoveItem;
        EditText ETCorrectStudentMatric;
        Button btnSaveStudentMatric, btnCancel, btnItemRemove; //, btnYes,btnNo;
        LinearLayout lEditStudentMatric;
        CardView CardViewCroppedImage;

        private CroppedimageViewHolder(View itemView) {
            super(itemView);
            LPData = itemView.findViewById(R.id.lpdata);
            lEditStudentMatric = itemView.findViewById(R.id.leditstudentmatric);


            IVattendance =itemView.findViewById(R.id.ivattendance);

            btnSaveStudentMatric =itemView.findViewById(R.id.btnsavestudentmatric);
            btnCancel = itemView.findViewById(R.id.btncanceleditingstudentmatric);

            btnItemRemove =itemView.findViewById(R.id.btnitemremove);
            //   LRemoveItem = itemView.findViewById(R.id.lremoveitem);
            //    btnYes = itemView.findViewById(R.id.btnritemYes);
            //    btnNo =itemView.findViewById(R.id.btnritemNo);

            StudentNo = itemView.findViewById(R.id.tvtxtprocessstudentno);
            CroppedImage = itemView.findViewById(R.id.ivcroppedimage);
            StudentMatric = itemView.findViewById(R.id.tvtxtprocessstudentid);
            StudentStatus = itemView.findViewById(R.id.tvtxtprocessstudentstatus);
            ETCorrectStudentMatric = itemView.findViewById(R.id.etcstudentid);
            CardViewCroppedImage = itemView.findViewById(R.id.cardviewcroppedimage);
        }
    }

}
