package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.List;


class RecyclerViewAdapterCroppedImages extends RecyclerView.Adapter<RecyclerViewAdapterCroppedImages.CroppedimageViewHolder> {

    private Context mContext;
    private List<CroppedImageModel> mData;

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
    public void onBindViewHolder(CroppedimageViewHolder holder, final int position) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap croppedimage  = BitmapFactory.decodeFile(mData.get(position).getCroppedImagePath(), options);
        croppedimage = Bitmap.createScaledBitmap(croppedimage, 400, 60, true);

        holder.StudentNo.setText(mData.get(position).getStudentNo());
        holder.CroppedImage.setImageBitmap(croppedimage);
       // imageProcess(croppedimage);

        holder.StudentId.setText(imageProcess(croppedimage));
        holder.StudentStatus.setText("waiting");


    }


    @Override
    public int getItemCount() {

        return mData.size();
    }

    class CroppedimageViewHolder extends RecyclerView.ViewHolder {
        TextView StudentNo, StudentId, StudentStatus;
        ImageView CroppedImage;

        private CroppedimageViewHolder(View itemView) {
            super(itemView);
            StudentNo = itemView.findViewById(R.id.tvtxtprocessstudentno);
            CroppedImage = itemView.findViewById(R.id.ivcroppedimage);
            StudentId = itemView.findViewById(R.id.tvtxtprocessstudentid);
            StudentStatus =itemView.findViewById(R.id.tvtxtprocessstudentstatus);
        }
    }

    public String imageProcess(Bitmap bitmap) {
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(mContext.getApplicationContext()).build();
        String processedtext = null;
        if (!txtRecognizer.isOperational()) {
            //   tvProcessedText.setText("Try Again");
        } else {

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray items = txtRecognizer.detect(frame);
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = (TextBlock) items.valueAt(i);
                strBuilder.append(item.getValue());
               // strBuilder.append("/");
                for (Text line : item.getComponents()) {
                    //extract scanned text lines here
                    Log.v("lines", line.getValue());
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        Log.v("element", element.getValue());

                    }
                }
            }

            processedtext =  strBuilder.toString().substring(0, strBuilder.toString().length());

        }

        return processedtext;

    }
}