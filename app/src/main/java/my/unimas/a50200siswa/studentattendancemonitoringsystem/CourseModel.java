package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class CourseModel {

    public String UserId, UserName, UserProfileImageUrl, CourseCode, CourseName, Day, Time;

    public CourseModel(){}  //Constructor

    public CourseModel(String userId, String userName, String userProfileImageUrl, String courseCode, String courseName, String day, String time) {
        UserId = userId;
        UserName = userName;
        UserProfileImageUrl =userProfileImageUrl;
        CourseCode = courseCode;
        CourseName = courseName;
        Day =day;
        Time=time;
    }

    public String getUserId() {
        return UserId;
    }
    public String getUserName() {
        return UserName;
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
    public void setUserName(String userName) {
        UserId = userName;
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