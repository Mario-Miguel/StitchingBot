package es.uniovi.eii.stitchingbot.translatorTest

import android.graphics.Bitmap
import android.graphics.Color
import es.uniovi.eii.stitchingbot.arduinoCommunication.Translator
import org.junit.Assert
import org.junit.Test

class TranslatorUnitTest {

    @Test
    fun emptyLogoTranslationTest() {
        val numOfPaintedRows = 0
        val image = getCustomBitmap(numOfPaintedRows, 375, 375)
        val translator = Translator
        translator.image = image
        translator.run {}

        val firstCoord = translator.translation[0]
        val assertFistCoord = Translator.calculateAdjustedCoord(Pair(0, 0))

        Assert.assertEquals(assertFistCoord, firstCoord)
        Assert.assertEquals(1, translator.translation.size)
    }

    @Test
    fun logoTranslationTest() {
        val numOfPaintedRows = 5
        val image = getCustomBitmap(numOfPaintedRows, 375, 375)
        val translator = Translator
        translator.image = image
        translator.run {}

        val firstCoord = translator.translation[0]
        val assertFistCoord = Translator.calculateAdjustedCoord(Pair(0, 0))

        Assert.assertEquals(assertFistCoord, firstCoord)
        Assert.assertEquals((375 * numOfPaintedRows / 2) - 1, translator.translation.size)
    }

    private fun getCustomBitmap(numOfRows: Int, height: Int, width: Int): Bitmap {
        val pixels = IntArray(height * width)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val startPixel = 0
        val endPixel = startPixel + (374 * numOfRows)
        for (i in startPixel..endPixel) {
            pixels[i] = Color.BLACK
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return bitmap
    }
}