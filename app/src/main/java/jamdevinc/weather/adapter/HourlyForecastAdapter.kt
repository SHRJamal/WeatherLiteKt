package jamdevinc.weather.adapter


import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jamdevinc.weather.R
import jamdevinc.weather.data.HourlyForecast
import jamdevinc.weather.utilities.LiteUtilities
import kotlinx.android.synthetic.main.hourly_forcast_list_item.view.*
import java.util.*


class HourlyForecastAdapter(private val hourlyForecastList: ArrayList<HourlyForecast>) :
        RecyclerView.Adapter<HourlyForecastAdapter.ItemViewHolder>() {

    var context : Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_forcast_list_item, parent, false)
        return ItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val hourlyForecast = hourlyForecastList[position]
        holder.bind(hourlyForecast)
    }

    override fun getItemCount(): Int =hourlyForecastList.size


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(hourlyForecast : HourlyForecast) {

            itemView.hourlyDateTv.text = LiteUtilities.formatHour(hourlyForecast.date)
            itemView.hourlyIcon.setImageResource(hourlyForecast.weatherIcon)
            itemView.hourlyTemptv.text = LiteUtilities.formatTemperature(PreferenceManager.getDefaultSharedPreferences(context),hourlyForecast.temp)
        }
    }
}
