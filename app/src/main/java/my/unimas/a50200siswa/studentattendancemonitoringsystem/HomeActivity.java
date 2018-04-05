package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    TextView btnSignOut;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    
    TextView UserName;

    FirebaseUser user;
    
    
    
    @Override
    protected void onStart(){

        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnSignOut =(TextView) findViewById(R.id.btnsignout_home);
        mAuth =FirebaseAuth.getInstance();

        user=FirebaseAuth.getInstance().getCurrentUser();


        mAuthListner = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                }
            }
        };

        btnSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mAuth.signOut();
            }


        });

        UserName = findViewById(R.id.username);
        UserName.setText(user.getDisplayName());





    }
}


