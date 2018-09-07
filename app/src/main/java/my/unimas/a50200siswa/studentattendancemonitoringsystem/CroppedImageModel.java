package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class CroppedImageModel {

    public String CroppedImagePath, StudentNo; //AttendanceRecord;

    public CroppedImageModel(){}  //Constructor

    public CroppedImageModel(String croppedImagePath, String studentno/*, String attendanceRecord */) {
        CroppedImagePath = croppedImagePath;
        StudentNo = studentno;
      //  AttendanceRecord = attendanceRecord;
    }

    public String getCroppedImagePath() {
        return CroppedImagePath;
    }
    public String getStudentNo() {
        return StudentNo;
    }



    public void setCroppedImagePath(String croppedImagePath) {
        CroppedImagePath = croppedImagePath;
    }

    public void setStudentNo(String studentNo) {
        StudentNo = studentNo;
    }

    /*
    public String getAttendanceRecord() {
        return AttendanceRecord;
    }

    public void setAttendanceRecord(String attendanceRecord) {
        AttendanceRecord = attendanceRecord;
    }

    */
}