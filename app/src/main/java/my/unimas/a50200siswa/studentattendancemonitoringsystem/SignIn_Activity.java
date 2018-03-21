package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn_Activity extends AppCompatActivity {


    Button btnSignIn;
    Button btnGSignIn;

    EditText Email,Password;
    TextView btnSignUp,btnForgotPassword;

    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 50200;

    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        btnSignIn = (Button)findViewById(R.id.btn_Sign_In);
        btnGSignIn = (Button)findViewById(R.id.btn_G_Sign_In);
        btnSignUp = (TextView)findViewById(R.id.btn_Sign_Up);
        btnForgotPassword = (TextView)findViewById(R.id.btn_Forgot_Password);
        Email = (EditText)findViewById(R.id.Email);
        Password =(EditText)findViewById(R.id.Password);
        mAuth = FirebaseAuth.getInstance();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignIn_Activity.this,SignUp_Activity.class));
                finish();
                }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignIn_Activity.this,Forgot_Password_Activity.class));
                finish();
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( Email.getText().toString().trim().length() == 0 || Password.getText().toString().trim().length() <= 5) {
                    Email.setError( "Email is required!" );
                    Password.setError("Password Should Contain at least 6 Characters ");
                }
                else{
                    SignInUser( Email.getText().toString(),Password.getText().toString());
                }

            }
        });

        btnGSignIn.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View view){
                signIn();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() !=null){
                    startActivity(new Intent(SignIn_Activity.this, Home_Activity.class));

                }
            }
        };

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(SignIn_Activity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void SignInUser(String email, final String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override

                     public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(SignIn_Activity.this,"Sorry Wrong Email or Password!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            startActivity(new Intent(SignIn_Activity.this,Home_Activity.class));
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Authentication could not be performed!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });


    }


}
