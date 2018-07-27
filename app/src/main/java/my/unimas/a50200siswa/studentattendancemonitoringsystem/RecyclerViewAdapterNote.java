package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

class RecyclerViewAdapterNote extends RecyclerView.Adapter<RecyclerViewAdapterNote.NoteViewHolder> {

    private Context mContext;
    private List<NoteModel> mData;
    FirebaseAuth mAuth;
    String userID;

    public RecyclerViewAdapterNote() {} //Constructor

    public RecyclerViewAdapterNote(Context mContext, List<NoteModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View noteview;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        noteview = mInflater.inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(noteview);
    }


    @Override
    public void onBindViewHolder(final NoteViewHolder holder, final int position) {

        holder.Note.setText(mData.get(position).getNote());
        holder.Date.setText(mData.get(position).getDate());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                userID = user.getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("Users").child(userID).child("Notes").child(mData.get(position).getNoteId()).removeValue();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView NoteId, Note,Date;
        Button btnRemove;

        private NoteViewHolder(View itemView) {
            super(itemView);
            Note = itemView.findViewById(R.id.tvnote);
            Date = itemView.findViewById(R.id.tvdate);
            btnRemove =itemView.findViewById(R.id.btnremove);


        }
    }


}
