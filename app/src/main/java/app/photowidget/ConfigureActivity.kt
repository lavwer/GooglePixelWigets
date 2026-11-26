package app.photowidget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.photowidget.databinding.ActivityConfigureBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class ConfigureActivity : AppCompatActivity() {

    private lateinit var b: ActivityConfigureBinding
    private lateinit var preset: SizePreset
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var previewSrc: Bitmap? = null

    private val picker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val bmp = withContext(Dispatchers.IO) {
                    try {
                        WidgetStore.importToPending(applicationContext, widgetId, uri)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (bmp == null) {
                    Toast.makeText(this@ConfigureActivity, R.string.load_error, Toast.LENGTH_SHORT).show()
                } else {
                    previewSrc = bmp
                    refreshPreview()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // По умолчанию — отмена: если пользователь выйдет, виджет не будет добавлен
        setResult(RESULT_CANCELED)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        setResult(RESULT_CANCELED, resultIntent())

        b = ActivityConfigureBinding.inflate(layoutInflater)
        setContentView(b.root)

        preset = SizePreset.forWidget(this, widgetId)
        b.tvSizeInfo.text = getString(R.string.size_info, preset.cols, preset.rows)
        b.toolbar.setNavigationOnClickListener { finish() }

        val settings = WidgetStore.load(this, widgetId)
        b.toggleShape.check(
            when (settings.shape) {
                ShapeType.CIRCLE -> R.id.btn_shape_circle
                ShapeType.CAPSULE -> R.id.btn_shape_capsule
                else -> R.id.btn_shape_rounded
            }
        )
        b.sliderRadius.value = settings.radiusDp.coerceIn(0, 40).toFloat()

        val pending = WidgetStore.pendingFile(this, widgetId)
        previewSrc = if (pending.exists()) {
            WidgetStore.decodeFile(pending)
        } else {
            WidgetStore.decodeFile(WidgetStore.imageFile(this, widgetId))
        }

        b.btnPick.setOnClickListener { launchPicker() }
        b.toggleShape.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) refreshPreview()
        }
        b.sliderRadius.addOnChangeListener { _, _, _ -> refreshPreview() }
        b.btnSave.setOnClickListener { save() }

        refreshPreview()

        // Плавный сценарий: при первом добавлении сразу открываем выбор фото
        if (savedInstanceState == null && previewSrc == null) {
            b.root.post { launchPicker() }
        }
    }

    private fun launchPicker() {
        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun resultIntent() = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

    private fun currentShape(): Int = when (b.toggleShape.checkedButtonId) {
        R.id.btn_shape_circle -> ShapeType.CIRCLE
        R.id.btn_shape_capsule -> ShapeType.CAPSULE
        else -> ShapeType.ROUNDED
    }

    private fun refreshPreview() {
        val shape = currentShape()
        val radius = b.sliderRadius.value.toInt()
        val rounded = shape == ShapeType.ROUNDED

        b.sliderRadius.isEnabled = rounded
        b.rowRadius.alpha = if (rounded) 1f else 0.38f
        b.tvRadiusValue.text = getString(R.string.radius_value, radius)
        b.btnPick.setText(if (previewSrc != null) R.string.replace_photo else R.string.pick_photo)

        val src = previewSrc
        if (src == null) {
            b.imgPreview.visibility = View.GONE
            b.tvEmpty.visibility = View.VISIBLE
            return
        }

        // Превью в тех же пропорциях, что и выбранный размер виджета
        val scale = 720f / maxOf(preset.nominalWidthDp, preset.nominalHeightDp)
        val w = (preset.nominalWidthDp * scale).roundToInt()
        val h = (preset.nominalHeightDp * scale).roundToInt()
        b.imgPreview.setImageBitmap(WidgetRenderer.drawBitmap(src, w, h, shape, radius * scale))
        b.imgPreview.visibility = View.VISIBLE
        b.tvEmpty.visibility = View.GONE
    }

    private fun save() {
        val pending = WidgetStore.pendingFile(this, widgetId)
        val target = WidgetStore.imageFile(this, widgetId)

        if (pending.exists()) {
            pending.copyTo(target, overwrite = true)
            pending.delete()
        } else if (!target.exists()) {
            Toast.makeText(this, R.string.pick_first, Toast.LENGTH_SHORT).show()
            return
        }

        WidgetStore.save(
            this,
            widgetId,
            WidgetStore.Settings(shape = currentShape(), radiusDp = b.sliderRadius.value.toInt())
        )
        WidgetRenderer.update(this, AppWidgetManager.getInstance(this), widgetId)

        setResult(RESULT_OK, resultIntent())
        finish()
    }

    override fun onDestroy() {
        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID && isFinishing) {
            WidgetStore.pendingFile(this, widgetId).delete()
        }
        super.onDestroy()
    }
}
