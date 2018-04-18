package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class StudentModel {

    public String StudentId, StudentName, StudentSerial;

    public StudentModel(){}

    public StudentModel(String studentId, String studentName, String studentSerial) {
        StudentSerial = studentSerial;
        StudentId = studentId;
        StudentName = studentName;
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



    public void setStudentId(String studentId) {
        StudentId = studentId;
    }
    public void setStudentName(String studentName) {
        StudentName = studentName;
    }
    public void setStudentSerial(String studentSerial) {
        StudentSerial = studentSerial;
    }


}
