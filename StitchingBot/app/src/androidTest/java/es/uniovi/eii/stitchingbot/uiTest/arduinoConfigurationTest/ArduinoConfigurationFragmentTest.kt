package es.uniovi.eii.stitchingbot.uiTest.arduinoConfigurationTest

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.uiTest.MockingHelper
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class ArduinoConfigurationFragmentTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.BLUETOOTH"
        )

    @Before
    fun beforeTests() {
        MockingHelper().mockBluetoothService()
    }

    @Test
    fun runAllTests() {
        ArduinoConnectionTest().runTest()
        ArduinoDisconnectionTest().runTest()
    }


    @After
    fun afterTests() {
        unmockkAll()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val ldc = LogoDatabaseConnection(context)
        ldc.open()
        ldc.deleteAllData()
        ldc.close()
    }
}