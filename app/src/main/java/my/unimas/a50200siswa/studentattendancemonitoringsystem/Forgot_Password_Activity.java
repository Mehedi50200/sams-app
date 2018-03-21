package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password_Activity extends AppCompatActivity implements View.OnClickListener {


    private EditText input_email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button btnResetPass;
        TextView btnBack;
        input_email = (EditText)findViewById(R.id.forgot_email);
        btnResetPass = (Button)findViewById(R.id.forgot_btn_reset);
        btnBack = (TextView)findViewById(R.id.forgot_btn_login);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.forgot_btn_login)
        {
            startActivity(new Intent(this,SignIn_Activity.class));
            finish();
        }
        else  if(view.getId() == R.id.forgot_btn_reset)
        {
            if( input_email.getText().toString().length() == 0 )
                input_email.setError( "Email is required!" );
            else
            resetPassword(input_email.getText().toString());
        }
    }

    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Forgot_Password_Activity.this,"We have sent password to email: "+email, Toast.LENGTH_LONG).show();

                        }

                        else{

                            Toast.makeText(Forgot_Password_Activity.this,"The Email you provided is no Registered!!", Toast.LENGTH_LONG).show();

                        }
                    }



                });
    }
}