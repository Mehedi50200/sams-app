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

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.bitwise_not;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;

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
*/


class RecyclerViewAdapterCroppedImages extends RecyclerView.Adapter<RecyclerViewAdapterCroppedImages.CroppedimageViewHolder> {

    private Context mContext;
    private List<CroppedImageModel> mData;
    int x =0;

    String processedtext, attendanceText;

    public RecyclerViewAdapterCroppedImages() {
        OpenCVLoader.initDebug();
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

        try {
            holder.StudentId.setText(TextImageProcess(croppedimagenew));
            holder.StudentStatus.setText(CircleDetection(croppedimagenew));
            //  holder.StudentId.setText(mData.get(position).getStudentMatric());
            //  holder.StudentStatus.setText(mData.get(position).getAttendanceRecord());
        } catch (Exception e) {
            holder.StudentId.setText("wait");
            holder.StudentStatus.setText("wait");
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void myMotifyDataSetChanged() {
        this.notifyDataSetChanged();

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

    public String CircleDetection(Bitmap bitmap) {

        /*
        String timeStamp = new SimpleDateFormat("yyyyMMdd_mmHH").format(new Date());
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/sams_images" + "/" + timeStamp);
        myDir.mkdir();
        String chunkedImagedDirectory = myDir.toString() + "/";
        */

        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat gray = new Mat();

        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        Imgproc.medianBlur(gray, gray, 5);

        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double) gray.rows() / 16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 30); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles
        Mat mask = new Mat(src.rows(), src.cols(), CvType.CV_8U, Scalar.all(0));
        int radius = 0;
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0, 100, 100), 1, 8, 0);
            // circle outline
            radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255, 0, 255), 1, 8, 0);
            Imgproc.circle(mask, center, radius, new Scalar(255, 0, 255), 1, 8, 0);
        }

        //String circledetected = myDir.toString() + "_" + String.valueOf(radius) + "_" + "a.jpg";
        //Imgcodecs.imwrite(circledetected, src);

        Mat masked = new Mat();
        src.copyTo(masked, mask);

        Mat thresh = new Mat();
        Imgproc.threshold(mask, thresh, 1, 255, Imgproc.THRESH_BINARY);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat cropped = null;
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = boundingRect(contours.get(i));
            if (rect.width / rect.height > 0.8 && rect.width / rect.height < 1.2 && rect.width > 25 & rect.height > 25) {
                cropped = src.submat(rect);
            }
        }

        Mat localMat1 = cropped;
        Mat localMat2 = new Mat();
        Imgproc.GaussianBlur(localMat1, localMat2, new Size(5, 5), 7);
        Object localObject = new Mat();
        Imgproc.cvtColor(localMat2, (Mat) localObject, COLOR_RGB2GRAY);
        Mat cloneMat = ((Mat) localObject).clone();
        localMat2 = localMat1.clone();
        bitwise_not(cloneMat, cloneMat);
        Imgproc.threshold(cloneMat, localMat2, 127, 255, Imgproc.THRESH_OTSU);
        Mat thresh2 = localMat2.clone();

        List<MatOfPoint> contourscircles = new ArrayList<MatOfPoint>();

        Imgproc.findContours(localMat2, contourscircles, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Rect rectCrop = boundingRect(contourscircles.get(0));
        Mat imageROI = thresh2.submat(rectCrop);
        int total = countNonZero(imageROI);
        double pixel = total / contourArea(contourscircles.get(0)) * 100;
        if (pixel >= 70 && pixel <= 130) {
            attendanceText = "Present";
        /*    String chunkedfilename = chunkedImagedDirectory + "_" + "present" + "h" + rectCrop.height + "w" + rectCrop.width + ".jpg";
            Imgcodecs.imwrite(chunkedfilename, imageROI);   */
        } else {
            attendanceText = "Absent";
        /*    String chunkedfilename = chunkedImagedDirectory + "absent" + "h" + rectCrop.height + "w" + rectCrop.width + ".jpg";
            Imgcodecs.imwrite(chunkedfilename, imageROI); */
        }

        return attendanceText;

    }


}
