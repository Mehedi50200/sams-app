package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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


public class ForgotPasswordActivity extends AppCompatActivity {

    EditText Email;
    Button btnResetPass, btnSignIn, btnGSignIn;
    TextView TVIRemembered, TVPageTitle, TVResetPasswordMassage;
    ImageView ImagePink, ImageOrange, ImageViolet;
    ProgressBar Progress;
    Animation LeftRight;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);



        ImageViolet=findViewById(R.id.imageviolet_forgetpassword);
        ImagePink=findViewById(R.id.imagepink_forgetpassword);
        ImageOrange=findViewById(R.id.imageorange_forgetpassword);


        Email = findViewById(R.id.etemail_forgotpassword);
        btnResetPass =  findViewById(R.id.btnresetpassword_forgetpassword);
        btnSignIn = findViewById(R.id.btnsignin_forgotpassword);
        btnGSignIn =findViewById(R.id.btngsignin_forgotpassword);
        TVIRemembered =findViewById(R.id.tviremembered_forgotpassword);
        TVResetPasswordMassage=findViewById(R.id.tvresetpasswordmassage);
        TVPageTitle=findViewById(R.id.pagetitle_forgotpassword);

        /* ------------- Transition animation of layout ---------------------*/

        LeftRight = AnimationUtils.loadAnimation(this,R.anim.leftright);
        Progress = findViewById(R.id.progress_forgotpassword);
        /*------------------------------------------------------------------*/


        mAuth = FirebaseAuth.getInstance();



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent (ForgotPasswordActivity.this, SignInActivity.class);
                Pair[] pairs = new Pair[9];
                pairs[0]= new Pair<View, String>(btnSignIn, "signin");
                pairs[1]= new Pair<View, String>(Email, "email");
                pairs[2]= new Pair<View, String>(btnGSignIn, "gsignin");
                pairs[3]= new Pair<View, String>(btnResetPass, "signup");
                pairs[4]= new Pair<View, String>(TVIRemembered, "forget_already");
                pairs[5]= new Pair<View, String>(ImageViolet, "imageviolet");
                pairs[6]= new Pair<View, String>(ImagePink, "imagepink");
                pairs[7]= new Pair<View, String>(ImageOrange, "imageorange");
                pairs[8]= new Pair<View, String>(TVPageTitle, "pagetitle");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);
                startActivity(intent, options.toBundle() );
            }

        });


        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = Email.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {

                    TVResetPasswordMassage.setText("Enter Your Registered Email address");
                    TVResetPasswordMassage.setAnimation(LeftRight);
                    TVResetPasswordMassage.setVisibility(View.VISIBLE);
                    return;
                }
                Progress.setVisibility(View.VISIBLE);
                TVResetPasswordMassage.setVisibility(View.GONE);
                mAuth.sendPasswordResetEmail(email)

                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    TVResetPasswordMassage.setText("We have sent you instructions to reset your password to the Email ID: " + email);
                                    TVResetPasswordMassage.setAnimation(LeftRight);
                                    TVResetPasswordMassage.setVisibility(View.VISIBLE);

                                } else {
                                    TVResetPasswordMassage.setText("Failed to send reset instructions. Either "+email + " is not registered or there is a problem in establishing connection with server");
                                    TVResetPasswordMassage.setAnimation(LeftRight);
                                    TVResetPasswordMassage.setVisibility(View.VISIBLE);

                                }

                                Progress.setVisibility(View.GONE);
                            }
                        });
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
                    startActivity(new Intent(ForgotPasswordActivity.this, HomeActivity.class));
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

                        Toast.makeText(ForgotPasswordActivity.this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show();
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
                            current_user_db.updateChildren(newPost);
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(ForgotPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            // updateUI(null);
                        }
                        // ...
                    }
                });
    }


}
