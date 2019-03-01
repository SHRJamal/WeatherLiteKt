package jamdevinc.weather.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.jetbrains.anko.*
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout

class LocationsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        supportActionBar?.hide()
        LocationsActivityUI().setContentView(this)

    }
}

class LocationsActivityUI : AnkoComponent<LocationsActivity> {
    override fun createView(ui: AnkoContext<LocationsActivity>): View =
            with(ui){
                coordinatorLayout {
                    appBarLayout {

                        toolbar {

                        }
                    }


                }
                relativeLayout {
                    editText {
                        hint = "location"
                    }
                    button("add")
                }


            }

}