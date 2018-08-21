package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class AttendanceModel {

    public String AttendanceId, AttendanceDate, Status, Week;

    public AttendanceModel(){}  //Constructor

    public AttendanceModel(String attendanceid, String attendancedate, String status, String week) {
        AttendanceId = attendanceid;
        AttendanceDate = attendancedate;
        Status = status;
        Week = week;
    }

    public String getAttendanceId() {
        return AttendanceId;
    }
    public String getAttendanceDate() {
        return AttendanceDate;
    }
    public String getStatus() {
        return Status;
    }

    public String getWeek() {
        return Week;
    }

    public void setAttendanceId(String attendanceId) {
        AttendanceId = attendanceId;
    }
    public void setAttendanceDateDate(String attendancedate) {
        AttendanceDate = attendancedate;
    }
    public void setStatus(String status) {
        Status = status;
    }

    public void setWeek(String week) {
        Week = week;
    }
}