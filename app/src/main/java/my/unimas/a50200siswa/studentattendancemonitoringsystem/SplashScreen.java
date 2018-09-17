package my.unimas.a50200siswa.studentattendancemonitoringsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView IVViolet,IVPink,IVOrange, LogoBackground ;

        /*------------------------------ Declaration of UI elements ------------------------------*/
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
                    sleep (2500);

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