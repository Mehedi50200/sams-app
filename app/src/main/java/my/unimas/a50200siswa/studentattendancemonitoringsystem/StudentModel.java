package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class StudentModel {

    public String StudentId, StudentName, StudentProgram, StudentProfileImageUrl, StudentSerial, StudentAttendanceRecord, CourseCode,CourseName, UserProfileImageUrl;

    public StudentModel(){}

    public StudentModel(String studentId, String studentName, String studentProgram, String studentProfileImageUrl, String studentSerial, String studentAttendanceRecord, String courseCode, String courseName, String userProfileImageUrl) {
        StudentSerial = studentSerial;
        StudentId = studentId;
        StudentName = studentName;
        StudentProgram =studentProgram;
        StudentProfileImageUrl =studentProfileImageUrl;
        StudentAttendanceRecord =studentAttendanceRecord;
        CourseCode = courseCode;
        CourseName = courseName;
        UserProfileImageUrl = userProfileImageUrl;
    }

    public String getStudentId() {
        return StudentId;
    }
    public String getStudentName() {
        return StudentName;
    }
    public String getStudentProgram() {return StudentProgram;}

    public String getStudentProfileImageUrl() {  return StudentProfileImageUrl; }
    public String getStudentSerial(){
        return StudentSerial;
    }

    public String getStudentAttendanceRecord() {
        return StudentAttendanceRecord;
    }

    public String getCourseCode() {
        return CourseCode;
    }
    public String getCourseName(){
        return CourseName;
    }
    public String getUserProfileImageUrl(){
        return UserProfileImageUrl;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }
    public void setStudentName(String studentName) {
        StudentName = studentName;
    }
    public void setStudentProgram(String studentProgram) { StudentProgram = studentProgram; }
    public void setStudentProfileImageUrl(String studentProfileImageUrl) {StudentProfileImageUrl = studentProfileImageUrl;}
    public void setStudentSerial(String studentSerial) {
        StudentSerial = studentSerial;
    }

    public void setStudentAttendanceRecord(String studentAttendanceRecord) {
        StudentAttendanceRecord = studentAttendanceRecord;
    }

    public void setCourseCode (String courseCode) {
        CourseCode = courseCode;
    }
    public void setCourseName(String courseName){
        CourseName = courseName;
    }
    public void setUserProfileImageUrl(String userProfileImageUrl){ UserProfileImageUrl = userProfileImageUrl; }
}