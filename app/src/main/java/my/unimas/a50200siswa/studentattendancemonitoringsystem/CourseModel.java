package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class CourseModel {

    public String UserId, CourseCode, CourseName;

    public CourseModel(){}

    public CourseModel(String userId, String courseCode, String courseName) {
        UserId = userId;
        CourseCode = courseCode;
        CourseName = courseName;
    }

    public String getUserId() {
        return UserId;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public String getCourseName() {
        return CourseName;
    }


    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

}
