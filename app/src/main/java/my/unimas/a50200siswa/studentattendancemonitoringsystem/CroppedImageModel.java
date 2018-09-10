package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class CroppedImageModel {

    public String CroppedImagePath, StudentNo, StudentMatric, AttendanceRecord;

    public CroppedImageModel(){}  //Constructor

    public CroppedImageModel(String studentNo, String croppedImagePath, String studentMatric, String attendanceRecord) {
        StudentNo = studentNo;
        CroppedImagePath = croppedImagePath;
        StudentMatric = studentMatric;
        AttendanceRecord = attendanceRecord;
    }

    public String getStudentNo() {
        return StudentNo;
    }
    public String getCroppedImagePath() {
        return CroppedImagePath;
    }

    public void setStudentNo(String studentNo) {
        StudentNo = studentNo;
    }

    public void setCroppedImagePath(String croppedImagePath) {
        CroppedImagePath = croppedImagePath;
    }


    public String getStudentMatric(){return StudentMatric;}
    public String getAttendanceRecord(){return AttendanceRecord;}

    public void  setStudentMatric(String studentMatric){
        StudentMatric = studentMatric;
    }

    public void setAttendanceRecord(String attendanceRecord) {
        AttendanceRecord = attendanceRecord;
    }


}