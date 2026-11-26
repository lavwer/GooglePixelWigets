package app.photowidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.photowidget.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnAddWidget.setOnClickListener { requestPin() }
    }

    override fun onResume() {
        super.onResume()
        val count = AppWidgetManager.getInstance(this).getAppWidgetIds(provider()).size
        b.tvCount.text = getString(R.string.widgets_count, count)
    }

    private fun provider() = ComponentName(this, GalleryWidgetProvider::class.java)

    private fun requestPin() {
        val manager = AppWidgetManager.getInstance(this)
        if (manager.isRequestPinAppWidgetSupported) {
            // Лаунчер сам покажет подтверждение и откроет экран настройки виджета
            manager.requestPinAppWidget(provider(), null, null)
        } else {
            Toast.makeText(this, R.string.pin_unsupported, Toast.LENGTH_LONG).show()
        }
    }
}
