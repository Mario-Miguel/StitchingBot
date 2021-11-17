package es.uniovi.eii.stitchingbot.unitTest.database

import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.controller.LogoController
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.model.Logo
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LogosDatabaseTest {

    private val logoController = LogoController()
    private lateinit var context: Context

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun beforeTest() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        deleteAllData()
    }

    @After
    fun runAfter() {
        deleteAllData()
    }

    private fun deleteAllData() {
        val ldc = LogoDatabaseConnection(context)
        ldc.open()
        ldc.deleteAllData()
        ldc.close()
    }

    @Test
    fun insertLogoTest() {
        val logo = Logo()

        //Add a predefined logo
        logoController.setLogo(logo)

        logoController.addLogo(context)
        var insertedLogo = logoController.getSavedLogos(context)[0]
        Assert.assertEquals(logo, insertedLogo)

        //Add logo with url
        logo.imgUrl = "Test"
        logoController.setLogo(logo)
        logoController.addLogo(context)

        insertedLogo = logoController.getSavedLogos(context)[1]
        Assert.assertEquals(logo, insertedLogo)

        //Add logo with specific id
        logo.id = 25
        logoController.setLogo(logo)
        logoController.addLogo(context)

        insertedLogo = logoController.getSavedLogos(context)[2]
        Assert.assertNotEquals(logo.id, insertedLogo.id)
        Assert.assertEquals(3, insertedLogo.id)

    }

    @Test
    fun updateLogoTest() {
        val logo = Logo(1, "url")

        //Add a predefined logo
        logoController.setLogo(logo)
        logoController.addLogo(context)
        val insertedLogo = logoController.getSavedLogos(context)[0]
        Assert.assertEquals(logo, insertedLogo)

        //Update url
        insertedLogo.imgUrl = "urlTest123"
        logoController.setLogo(insertedLogo)
        logoController.updateLogo(context)

        var updatedLogoList = logoController.getSavedLogos(context)
        Assert.assertEquals(insertedLogo, updatedLogoList[0])
        Assert.assertEquals(1, updatedLogoList.size)

        //Update id
        insertedLogo.id = 3000
        logoController.setLogo(insertedLogo)
        logoController.updateLogo(context)

        updatedLogoList = logoController.getSavedLogos(context)
        Assert.assertEquals(insertedLogo, updatedLogoList[0])
        Assert.assertEquals(1, updatedLogoList[0].id)


    }

    @Test
    fun deleteLogoTest() {
        val logo = Logo(1, "url")

        //Add a logo
        logoController.setLogo(logo)
        logoController.addLogo(context)
        val insertedLogo = logoController.getSavedLogos(context)[0]

        //Delete logo
        logoController.setLogo(insertedLogo)
        logoController.deleteLogo(context)

        var logosListSize = logoController.getSavedLogos(context).size
        Assert.assertEquals(0, logosListSize)

        //Delete logo without any in database
        logoController.deleteLogo(context)
        logosListSize = logoController.getSavedLogos(context).size
        Assert.assertEquals(0, logosListSize)

    }
}