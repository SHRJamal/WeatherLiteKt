package jamdevinc.weather.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.android.gms.ads.MobileAds
import jamdevinc.weather.R

class SplashScreenActivity : AppCompatActivity() {

    private val DELAYED_TIME = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        MobileAds.initialize(this, "ca-app-pub-4206484200435863~4146255380")

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            this@SplashScreenActivity.finish()
        },
                DELAYED_TIME)

    }



}
