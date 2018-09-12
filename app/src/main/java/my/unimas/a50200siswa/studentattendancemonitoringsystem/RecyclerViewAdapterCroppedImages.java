package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/*
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

*/


class RecyclerViewAdapterCroppedImages extends RecyclerView.Adapter<RecyclerViewAdapterCroppedImages.CroppedimageViewHolder> {

    private Context mContext;
    private List<CroppedImageModel> mData;
    int x =0;
    //   private String StudentMatric, studentMatric, AttendanceStatus, attendanceStatus;

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

        final CroppedImageModel changedRecord = mData.get(position);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap croppedimageold = BitmapFactory.decodeFile(mData.get(position).getCroppedImagePath(), options);
        Bitmap croppedimagenew = Bitmap.createScaledBitmap(croppedimageold, 528, 80, true);


        holder.StudentNo.setText(mData.get(position).getStudentNo());
        holder.CroppedImage.setImageBitmap(croppedimagenew);
        holder.StudentMatric.setText(mData.get(position).getStudentMatric());
        holder.StudentStatus.setText(mData.get(position).getAttendanceRecord());

        if(mData.get(position).getAttendanceRecord().equals("Failed")){
            holder.LPData.setBackgroundResource(R.drawable.card_gradientpurple);
        }
        if(mData.get(position).getStudentMatric().length()<5){
            holder.LPData.setBackgroundResource(R.drawable.card_gradientblue);
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
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lEditStudentMatric.setVisibility(View.GONE);
            }
        });

        holder.StudentStatus.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class CroppedimageViewHolder extends RecyclerView.ViewHolder {
        TextView StudentNo, StudentMatric, StudentStatus;
        ImageView CroppedImage;
        LinearLayout LPData;
        EditText ETCorrectStudentMatric;
        Button btnSaveStudentMatric, btnCancel;
        LinearLayout lEditStudentMatric;

        private CroppedimageViewHolder(View itemView) {
            super(itemView);
            StudentNo = itemView.findViewById(R.id.tvtxtprocessstudentno);
            CroppedImage = itemView.findViewById(R.id.ivcroppedimage);
            StudentMatric = itemView.findViewById(R.id.tvtxtprocessstudentid);
            StudentStatus = itemView.findViewById(R.id.tvtxtprocessstudentstatus);
            LPData = itemView.findViewById(R.id.lpdata);
            ETCorrectStudentMatric = itemView.findViewById(R.id.etcstudentid);
            btnSaveStudentMatric =itemView.findViewById(R.id.btnsavestudentmatric);
            lEditStudentMatric = itemView.findViewById(R.id.leditstudentmatric);
            btnCancel = itemView.findViewById(R.id.btncanceleditingstudentmatric);

        }
    }

}
