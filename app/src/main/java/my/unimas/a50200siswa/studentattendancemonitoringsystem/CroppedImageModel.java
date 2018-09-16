package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class CroppedImageModel {

    public String CroppedImagePath, StudentNo, StudentMatric, AttendanceRecord, CourseCode;

    public CroppedImageModel(){}  //Constructor

    public CroppedImageModel(String studentNo, String croppedImagePath, String studentMatric, String attendanceRecord, String courseCode) {
        StudentNo = studentNo;
        CroppedImagePath = croppedImagePath;
        StudentMatric = studentMatric;
        AttendanceRecord = attendanceRecord;
        CourseCode =courseCode;
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
    public String getCourseCode() {
        return CourseCode;
    }

    public void  setStudentMatric(String studentMatric){
        StudentMatric = studentMatric;
    }

    public void setAttendanceRecord(String attendanceRecord) {
        AttendanceRecord = attendanceRecord;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }
}