package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opencv.core.Core.bitwise_not;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;


class RecyclerViewAdapterCroppedImages extends RecyclerView.Adapter<RecyclerViewAdapterCroppedImages.CroppedimageViewHolder> {

    private Context mContext;
    private List<CroppedImageModel> mData;
    int x =0;

    String processedtext, attendanceText;

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



            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap croppedimageold = BitmapFactory.decodeFile(mData.get(position).getCroppedImagePath(), options);
            Bitmap croppedimagenew = Bitmap.createScaledBitmap(croppedimageold, 400, 60, true);

            holder.StudentNo.setText(mData.get(position).getStudentNo());
            holder.CroppedImage.setImageBitmap(croppedimagenew);
            // imageProcess(croppedimage);


            holder.StudentId.setText(TextImageProcess(croppedimagenew));
            holder.StudentStatus.setText(CircleDetection(croppedimagenew));



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
            StudentStatus = itemView.findViewById(R.id.tvtxtprocessstudentstatus);
        }
    }



    public String TextImageProcess(Bitmap bitmap) {
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(mContext.getApplicationContext()).build();

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

            processedtext = strBuilder.toString().substring(0, strBuilder.toString().length());
        }

        return processedtext;

    }


    public String CircleDetection(Bitmap bitmap){

        String  timeStamp = new SimpleDateFormat("yyyyMMdd_mmHH").format(new Date());
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/sams_images"+"/"+timeStamp);
        myDir.mkdir();

        String chunkedImagedDirectory = myDir.toString() + "/";

        Mat localMat1 = new Mat();
        Utils.bitmapToMat(bitmap, localMat1);
        Mat localMat2 = new Mat();
        Imgproc.GaussianBlur(localMat1, localMat2, new Size(5, 5), 7);
        Object localObject = new Mat();
        Imgproc.cvtColor(localMat2, (Mat)localObject, COLOR_RGB2GRAY);
        Mat cloneMat= ((Mat) localObject).clone();
        localMat2 = localMat1.clone();
        bitwise_not(cloneMat,cloneMat);
        Imgproc.threshold(cloneMat,localMat2,127,255,Imgproc.THRESH_OTSU);
        Mat thresh=localMat2.clone();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(localMat2, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);



         for (int i =0 ; i < contours.size(); i++) {

            Rect rectCrop = boundingRect(contours.get(i));
            //creating crop of that contour from actual image


           /* if((rectCrop.height>=22 && rectCrop.height <= 32) && (rectCrop.width>=22 && rectCrop.width <= 32)) */
            if((rectCrop.height/rectCrop.width > 0.8) && (rectCrop.height/rectCrop.width < 1.2) && rectCrop.height > 30 && rectCrop.width >30){
                Mat imageROI= thresh.submat(rectCrop);
                //apply countnonzero method to that crop
                int total = countNonZero(imageROI);
                double pixel = total / contourArea(contours.get(i)) * 100;
                //pixel is in percentage of area that is filled
                if (pixel >= 100 && pixel <= 130) {
                    //counting filled circles
                    attendanceText = "Present";
                    String chunkedfilename = chunkedImagedDirectory +x+ "present" + "h" + rectCrop.height + "w" + rectCrop.width + ".jpg";
                    Imgcodecs.imwrite(chunkedfilename, imageROI);

                    x++;

                    break;
                } else {

                    attendanceText = "Absent";
                    String chunkedfilename = chunkedImagedDirectory +x+"absent" + "h" + rectCrop.height + "w" + rectCrop.width + ".jpg";
                    Imgcodecs.imwrite(chunkedfilename, imageROI);

                    x++;
                    break;
                }
            }
        }
        return attendanceText;

    }



}