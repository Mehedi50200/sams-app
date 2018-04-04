package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity
{
    /*---- Declaration of UI elements -----*/
    LinearLayout GetStartedUp, GetStartedDown;
    Animation UpDown, DownUp;
    Button btnSignUp, btnSignIn,btnGSignIn;
    EditText Email,Name, Password, PasswordConfirm;
    ImageView ImagePink, ImageOrange, ImageViolet;
    TextView AlreadyHasAccount, TVPageTitle;
    ProgressBar Progress;

    /*---- Firebase Database ----*/
    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 50200;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    /*-----------------------------------------*/


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        /* ------------- Transition animation of layout ---------------------
        GetStartedUp = (LinearLayout) findViewById(R.id.getstartedup);
        GetStartedDown = (LinearLayout) findViewById(R.id.getstarteddown);
        UpDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        DownUp = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        GetStartedUp.setAnimation(UpDown);
        GetStartedDown.setAnimation(DownUp);

        final ProgressBar simpleProgressBar =findViewById(R.id.simpleProgressBar);
        /*------------------------------------------------------------------*/

        /*---------------------- Finding View--------------------------*/
        btnSignUp = findViewById(R.id.btnsignup_signup);
        btnSignIn = findViewById(R.id.btnsignin_signup);
        btnGSignIn =findViewById(R.id.btngsignin_signup);
        AlreadyHasAccount = findViewById(R.id.tvalreadyhasaccount_signup);
        TVPageTitle =findViewById(R.id.tvpagetitle_signup);
        Email = findViewById(R.id.etemail_signup);
        Name = findViewById(R.id.etname_signup);
        Password = findViewById(R.id.etpassword_signup);
        PasswordConfirm = findViewById(R.id.etconfirmpassword_signup);


        ImageViolet=findViewById(R.id.imageviolet_signup);
        ImagePink=findViewById(R.id.imagepink_signup);
        ImageOrange=findViewById(R.id.imageorange_signup);
        /*------------------------------------------------------------------*/

        Progress = findViewById(R.id.progress_signup);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Name.getText().toString().length() == 0) {
                    Name.setError("Email is required!");
                } else if (Email.getText().toString().length() == 0) {
                    Email.setError("Email is required!");
                } else if (Password.getText().toString().length() <= 5) {
                    Password.setError("Password should contain at least 6 Characters!");
                } else if (Password.getText().toString().length() != PasswordConfirm.getText().toString().length()) {
                    PasswordConfirm.setError("Password does not match!");
                } else {
                    signUpUser(Name.getText().toString(), Email.getText().toString(),Password.getText().toString());
                }

            }

        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (SignUpActivity.this, SignInActivity.class);
                Pair[] pairs = new Pair[10];
                pairs[0]= new Pair<View, String>(btnSignIn, "signin");
                pairs[1]= new Pair<View, String>(Email, "email");
                pairs[2]= new Pair<View, String>(Password, "password");
                pairs[3]= new Pair<View, String>(btnGSignIn, "gsignin");
                pairs[4]= new Pair<View, String>(btnSignUp, "signup");
                pairs[5]= new Pair<View, String>(AlreadyHasAccount, "forget_already");
                pairs[6]= new Pair<View, String>(ImageViolet, "imageviolet");
                pairs[7]= new Pair<View, String>(ImagePink, "imagepink");
                pairs[8]= new Pair<View, String>(ImageOrange, "imageorange");
                pairs[9]= new Pair<View, String>(TVPageTitle, "pagetitle");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
                startActivity(intent, options.toBundle() );

            }
        });



        btnGSignIn.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View view){
                Progress.setVisibility(View.VISIBLE);
                signIn();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() !=null){
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
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

                        Toast.makeText(SignUpActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

                            String userID = mAuth.getCurrentUser().getUid();
                            String userName = mAuth.getCurrentUser().getDisplayName();
                            String userEmail = mAuth.getCurrentUser().getEmail();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                            Map newPost = new HashMap();
                            newPost.put("userName", userName);
                            newPost.put("userEmail", userEmail);
                            current_user_db.setValue(newPost);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            // updateUI(null);
                        }
                        // ...
                    }
                });
    }


    public void signUpUser(final String userName, final String userEmail, final String userPassword ) {

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Email.setError("Email is not valid!");
                    Toast.makeText(SignUpActivity.this,"Registration Unsuccessful", Toast.LENGTH_LONG).show();
                } else {
                    String userID = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                    Map newPost = new HashMap();
                    newPost.put("userName", userName);
                    newPost.put("userEmail", userEmail);
                    newPost.put("userPassword", userPassword);
                    current_user_db.setValue(newPost);
                    Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                }

            }

        });
    }

}

