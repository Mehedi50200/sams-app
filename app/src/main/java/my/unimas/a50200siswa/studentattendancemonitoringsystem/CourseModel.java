package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class CourseModel {

    public String UserId, UserProfileImageUrl, CourseCode, CourseName, Day, Time;

    public CourseModel(){}  //Constructor

    public CourseModel(String userId, String userProfileImageUrl, String courseCode, String courseName, String day, String time) {
        UserId = userId;
        UserProfileImageUrl =userProfileImageUrl;
        CourseCode = courseCode;
        CourseName = courseName;
        Day =day;
        Time=time;
    }

    public String getUserId() {
        return UserId;
    }
    public String getUserProfileImageUrl() {
        return UserProfileImageUrl;
    }
    public String getCourseCode() {
        return CourseCode;
    }

    public String getCourseName() {
        return CourseName;
    }
    public String getDay() {return Day; }
    public String getTime() {
        return Time;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
    public void setUserProfileImageUrl(String userProfileImageUrl) {
        UserProfileImageUrl = userProfileImageUrl;
    }
    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }
    public void setCourseName(String courseName) {
        CourseName = courseName;
    }
    public void setDay(String day) {
        Day = day;
    }
    public void setTime(String time) {
        Time = time;
    }

}