package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class StudentIdModel {

    public String StudentId, StudentName, StudentSerial, CourseCode,CourseName, UserProfileImageUrl;

    public StudentIdModel(){}

    public StudentIdModel(String studentId) {
        StudentId = studentId;
    }

    public String getStudentId() {
        return StudentId;
    }


    public void setStudentId(String studentId) {
        StudentId = studentId;
    }
}