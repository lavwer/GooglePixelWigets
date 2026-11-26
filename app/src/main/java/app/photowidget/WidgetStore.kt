package app.photowidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

object WidgetStore {

    private const val PREFS = "widget_prefs"
    private const val MAX_SOURCE_SIDE = 1280

    data class Settings(val fit: Boolean, val radiusDp: Int)

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun imageFile(c: Context, id: Int): File = File(c.filesDir, "widget_$id.jpg")

    fun pendingFile(c: Context, id: Int): File = File(c.cacheDir, "pending_$id.jpg")

    fun load(c: Context, id: Int): Settings = Settings(
        fit = prefs(c).getBoolean("fit_$id", false),
        radiusDp = prefs(c).getInt("radius_$id", 20)
    )

    fun save(c: Context, id: Int, s: Settings) {
        prefs(c).edit()
            .putBoolean("fit_$id", s.fit)
            .putInt("radius_$id", s.radiusDp)
            .apply()
    }

    fun delete(c: Context, id: Int) {
        imageFile(c, id).delete()
        pendingFile(c, id).delete()
        prefs(c).edit().remove("fit_$id").remove("radius_$id").apply()
    }

    fun decodeFile(file: File): Bitmap? =
        if (file.exists()) BitmapFactory.decodeFile(file.path) else null

    /** Декодирует фото из галереи, уменьшает, исправляет поворот и сохраняет во временный файл. */
    fun importToPending(c: Context, id: Int, uri: Uri): Bitmap? {
        val bmp = decodeUri(c, uri) ?: return null
        FileOutputStream(pendingFile(c, id)).use { bmp.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return bmp
    }

    private fun decodeUri(c: Context, uri: Uri): Bitmap? {
        val cr = c.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SOURCE_SIDE) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val raw = cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: return null

        val orientation = try {
            cr.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }

        val m = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                m.postRotate(90f)
                m.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                m.postRotate(-90f)
                m.postScale(-1f, 1f)
            }
        }

        val longest = max(raw.width, raw.height)
        if (longest > MAX_SOURCE_SIDE) {
            val s = MAX_SOURCE_SIDE / longest.toFloat()
            m.postScale(s, s)
        }
        if (m.isIdentity) return raw

        val out = Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, m, true)
        if (out !== raw) raw.recycle()
        return out
    }
}
