package nz.zhang.aucklandtransportwear

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.ConfirmationActivity
import android.support.wearable.activity.WearableActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_city_picker.*
import nz.zhang.aucklandtransportwear.wakaapi.Cities

class CityPickerActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_picker)
        val adapter = ArrayAdapter<Cities>(this, android.R.layout.simple_list_item_1, Cities.values())
        cityList.adapter = adapter

        cityList.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, pos, p3 ->
            val city = Cities.values()[pos]
            DataStore.selectedCity = city
            DataStore.writeData()
            val intent = Intent(applicationContext, ConfirmationActivity::class.java)
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION)
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "City set to ${city.cityName}")
            startActivity(intent)
            onBackPressed()
        }
    }
}
