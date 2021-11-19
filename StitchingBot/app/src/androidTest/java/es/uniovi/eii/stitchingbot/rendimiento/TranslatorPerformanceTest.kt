package es.uniovi.eii.stitchingbot.rendimiento

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.test.filters.LargeTest
import es.uniovi.eii.stitchingbot.arduinoCommunication.Translator
import org.junit.Test

@LargeTest
class TranslatorPerformanceTest {

    //Medir tiempo de traducir el dibujo en negro completamente -> 5387ms
    //Hacer que haya un pixel dibujado cada 2 -> peor de los casos -> 266683ms

    @Test
    fun measureFull() {
        var mediaFullLogo: Long = 0
        for (i in 0..10) {
            mediaFullLogo += fullLogoTranslationTest()
        }

        mediaFullLogo /= 10
        Log.i("MEASURE_TIME", "Full logo measure: $mediaFullLogo millis")

        println()
    }

    @Test
    fun measureWorst() {

        var mediaWorstLogo: Long = 0
        for (i in 0..5) {
            mediaWorstLogo+=worstLogoTranslationTest()
        }

        mediaWorstLogo/=5
        Log.i("MEASURE_TIME", "Worst logo measure: $mediaWorstLogo millis")
        println()
    }

    private fun fullLogoTranslationTest(): Long {
        val image = getFullBitmap(375, 375)
        val translator = Translator
        translator.image = image

        return measureExecutionTime(translator, "Full logo")
    }

    private fun worstLogoTranslationTest(): Long {
        val image = getWorstBitmap(375, 375)
        val translator = Translator
        translator.image = image

        return measureExecutionTime(translator, "Worst logo")
    }

    private fun measureExecutionTime(translator: Translator, testName: String): Long {
        val startMillis = System.currentTimeMillis()
        translator.run {}
        val endMillis = System.currentTimeMillis()

        val measureTime = endMillis - startMillis
        return measureTime
    }

    private fun getFullBitmap(height: Int, width: Int): Bitmap {
        val pixels = IntArray(height * width)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (i in pixels.indices) {
            pixels[i] = Color.BLACK
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmap
    }

    private fun getWorstBitmap(height: Int, width: Int): Bitmap {
        val pixels = IntArray(height * width)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (i in pixels.indices) {
            if (i % 2 == 0)
                pixels[i] = Color.BLACK
            else {
                pixels[i] = Int.MAX_VALUE
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmap
    }
}