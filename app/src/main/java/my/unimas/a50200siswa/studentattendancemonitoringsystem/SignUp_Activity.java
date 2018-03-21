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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUp_Activity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignup;
    TextView btnLogin;
    TextView btnForgotPass;
    EditText input_email,input_pass,input_pass_confirm;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //View

        btnSignup = (Button)findViewById(R.id.signup_btn_register);
        btnLogin =(TextView)findViewById(R.id.signup_btn_login);
        btnForgotPass = (TextView)findViewById(R.id.signup_btn_forgot_pass);
        input_email = (EditText)findViewById(R.id.sign_up_email);
        input_pass = (EditText)findViewById(R.id.sign_up_password);
        input_pass_confirm= (EditText)findViewById(R.id.sign_up_password_confirm);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signup_btn_login){
            startActivity(new Intent(this,SignIn_Activity.class));
            finish();
        }
        else if(view.getId() == R.id.signup_btn_forgot_pass){
            startActivity(new Intent(SignUp_Activity.this,Forgot_Password_Activity.class));
            finish();
        }
        else if(view.getId() == R.id.signup_btn_register){

            if( input_email.getText().toString().length() == 0 ) {
                input_email.setError("Email is required!");
            }

            else if( input_pass.getText().toString().length() <=5  ) {
                input_pass.setError("Password should contain at least 6 Characters!");
            }

            else if(input_pass.getText().toString().length()!= input_pass_confirm.getText().toString().length()) {

                input_pass_confirm.setError("Password does not match!");
            }

            else{
                signUpUser(input_email.getText().toString(),input_pass.getText().toString());
            }



        }
    }

    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            input_email.setError("Email is not valid!");
                            Toast.makeText(SignUp_Activity.this,"Registration Unsuccessful", Toast.LENGTH_LONG).show();

                        }
                        else{
                            Toast.makeText(SignUp_Activity.this,"Registration Successful", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
