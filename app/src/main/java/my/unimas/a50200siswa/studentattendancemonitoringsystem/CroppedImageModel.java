package my.unimas.a50200siswa.studentattendancemonitoringsystem;


import java.io.Serializable;

public class CroppedImageModel implements Serializable {

    public String CroppedImagePath, StudentNo, StudentMatric, AttendanceRecord;

    public CroppedImageModel(){}  //Constructor

    public CroppedImageModel(String studentno, String croppedImagePath) {
        StudentNo = studentno;
        CroppedImagePath = croppedImagePath;
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

    /*

    public String getStudentMatric(){return StudentMatric;}
    public String getAttendanceRecord(){return AttendanceRecord;}

    public void  setStudentMatric(String studentMatric){
        StudentMatric = studentMatric;
    }

    public void setAttendanceRecord(String attendanceRecord) {
        AttendanceRecord = attendanceRecord;
    }

    */


}