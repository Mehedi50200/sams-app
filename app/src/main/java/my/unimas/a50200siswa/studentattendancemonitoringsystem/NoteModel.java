package my.unimas.a50200siswa.studentattendancemonitoringsystem;

public class NoteModel {


    public String NoteId, Note, Date;

    public NoteModel(){}    //Constructor

    public NoteModel(String noteid, String note, String date) {
        NoteId = noteid;
        Note =note;
        Date= date;
    }

    public String getNoteId() {
        return NoteId;
    }
    public String getNote() {
        return Note;
    }
    public String getDate() {
        return Date;
    }

    public void setNoteId(String noteid) {
        NoteId = noteid;
    }
    public void setNote(String note) {
        Note = note;
    }
    public void setDate(String date) {
        Date = date;
    }

}
