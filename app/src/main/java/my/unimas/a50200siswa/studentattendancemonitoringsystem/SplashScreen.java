package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashScreen extends AppCompatActivity {
private ImageView IVViolet,IVPink,IVOrange, LogoBackground ;
private LinearLayout LOSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /*------------------------------ Declaration of UI elements ------------------------------*/
        LOSplash=findViewById(R.id.parentlayout_splash);
        LogoBackground =findViewById(R.id.logobackground);
        IVViolet = findViewById(R.id.violetball) ;
        IVPink = findViewById(R.id.pinkball) ;
        IVOrange = findViewById(R.id.orangeball) ;
        /*----------------------------------------------------------------------------------------*/

        Animation rotation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade);
        Animation bounce = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bounce);

        LogoBackground.startAnimation(rotation);
        IVViolet.startAnimation(bounce);
        IVPink.startAnimation(bounce);
        IVOrange.startAnimation(bounce);

        final Intent intent = new Intent (SplashScreen.this, SignInActivity.class);

        Thread timer =new Thread() {
            public void run (){
                try {
                    sleep (3000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();

    }
}
