package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    /*------------------------------- Declaration of UI elements ---------------------------------*/
    Button btnSignIn, btnSignUp, btnGSignIn;
    EditText Email,Password;
    TextView TVForgotPassword, TVPageTitle;
    ImageView ImagePink, ImageOrange, ImageViolet;
    ProgressBar simpleProgressBar;
    /*--------------------------------------------------------------------------------------------*/

    /*---------------------------------- Firebase Database ---------------------------------------*/
    FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 50200;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    /*--------------------------------------------------------------------------------------------*/


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ImageViolet=findViewById(R.id.imageviolet_signin);
        ImagePink=findViewById(R.id.imagepink_signin);
        ImageOrange=findViewById(R.id.imageorange_signin);

        /* ------------------------------- Finding View ------------------------------------------*/
        btnSignIn = findViewById(R.id.btnsignin_signin);
        btnGSignIn = findViewById(R.id.btngsignin_signin);
        btnSignUp = findViewById(R.id.btnsignup_signin);
        TVForgotPassword = findViewById(R.id.tvforgotpassword_signin);
        TVPageTitle = findViewById(R.id.pagetitle_signin);
        Email = findViewById(R.id.etemail_signin);
        Password =findViewById(R.id.etpassword_signin);
        simpleProgressBar =findViewById(R.id.progress_signin);
        /*----------------------------------------------------------------------------------------*/

        mAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if( Email.getText().toString().trim().length() == 0 || Password.getText().toString().trim().length() <= 5) {
                    Email.setError( "Email is required!" );
                    Email.setBackgroundTintList(getResources().getColorStateList(R.color.colorMistakeRed));
                    Password.setError("Password Should Contain at least 6 Characters ");
                    Password.setBackgroundTintList(getResources().getColorStateList(R.color.colorMistakeRed));
                }
                else{
                    SignInUser( Email.getText().toString().trim(),Password.getText().toString().trim());
                }

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (SignInActivity.this, SignUpActivity.class);
                Pair[] pairs = new Pair[10];
                pairs[0]= new Pair<View, String>(btnSignIn, "signin");
                pairs[1]= new Pair<View, String>(Email, "email");
                pairs[2]= new Pair<View, String>(Password, "password");
                pairs[3]= new Pair<View, String>(btnGSignIn, "gsignin");
                pairs[4]= new Pair<View, String>(btnSignUp, "signup");
                pairs[5]= new Pair<View, String>(TVForgotPassword, "forget_already");
                pairs[6]= new Pair<View, String>(ImageViolet, "imageviolet");
                pairs[7]= new Pair<View, String>(ImagePink, "imagepink");
                pairs[8]= new Pair<View, String>(ImageOrange, "imageorange");
                pairs[9]= new Pair<View, String>(TVPageTitle, "pagetitle");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this, pairs);
                startActivity(intent, options.toBundle() );
            }
        });

        TVForgotPassword.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (SignInActivity.this, ForgotPasswordActivity.class);
                Pair[] pairs = new Pair[9];
                pairs[0]= new Pair<View, String>(btnSignIn, "signin");
                pairs[1]= new Pair<View, String>(Email, "email");
                pairs[2]= new Pair<View, String>(btnGSignIn, "gsignin");
                pairs[3]= new Pair<View, String>(btnSignUp, "signup");
                pairs[4]= new Pair<View, String>(TVForgotPassword, "forget_already");
                pairs[5]= new Pair<View, String>(ImageViolet, "imageviolet");
                pairs[6]= new Pair<View, String>(ImagePink, "imagepink");
                pairs[7]= new Pair<View, String>(ImageOrange, "imageorange");
                pairs[8]= new Pair<View, String>(TVPageTitle, "pagetitle");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this, pairs);
                startActivity(intent, options.toBundle() );
            }
        });

        btnGSignIn.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View view){
                simpleProgressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() !=null){
                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
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

                        Toast.makeText(SignInActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
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
        simpleProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(SignInActivity.this,"Sorry Wrong Email or Password!", Toast.LENGTH_LONG).show();
                            simpleProgressBar.setVisibility(View.GONE);

                        }
                        else{
                            simpleProgressBar.setVisibility(View.GONE);
                            startActivity(new Intent(SignInActivity.this,HomeActivity.class));
                            finish();
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
                assert account != null;
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

                            String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            String userName = mAuth.getCurrentUser().getDisplayName();
                            String userEmail = mAuth.getCurrentUser().getEmail();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            Map newPost = new HashMap();
                            newPost.put("userName", userName);
                            newPost.put("userEmail", userEmail);
                            current_user_db.updateChildren(newPost);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            simpleProgressBar.setVisibility(View.GONE);
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();

                            // updateUI(null);
                        }
                        // ...
                    }
                });
    }

}


