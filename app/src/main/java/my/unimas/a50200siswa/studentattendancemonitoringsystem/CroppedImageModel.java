package my.unimas.a50200siswa.studentattendancemonitoringsystem;


public class CroppedImageModel {

    public String CroppedImagePath, StudentNo;

    public CroppedImageModel(){}  //Constructor

    public CroppedImageModel(String croppedImagePath, String studentno) {
        CroppedImagePath = croppedImagePath;
        StudentNo = studentno;
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
        CroppedImagePath = studentNo;
    }

}