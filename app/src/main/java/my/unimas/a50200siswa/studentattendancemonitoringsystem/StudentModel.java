package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class StudentModel {

    public String StudentId, StudentName, StudentSerial, CourseCode,CourseName;

    public StudentModel(){}

    public StudentModel(String studentId, String studentName, String studentSerial,String courseCode, String courseName) {
        StudentSerial = studentSerial;
        StudentId = studentId;
        StudentName = studentName;
        CourseCode = courseCode;
        CourseName = courseName;
    }

    public String getStudentId() {
        return StudentId;
    }
    public String getStudentName() {
        return StudentName;
    }
    public String getStudentSerial(){
        return StudentSerial;
    }
    public String getCourseCode() {
        return CourseCode;
    }
    public String getCourseName(){
        return CourseName;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }
    public void setStudentName(String studentName) {
        StudentName = studentName;
    }
    public void setStudentSerial(String studentSerial) {
        StudentSerial = studentSerial;
    }
    public void setCourseCode (String courseCode) {
        CourseCode = courseCode;
    }
    public void setCourseName(String courseName){
        CourseName = courseName;
    }
}