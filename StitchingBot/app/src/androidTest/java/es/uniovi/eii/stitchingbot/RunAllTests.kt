package es.uniovi.eii.stitchingbot

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import es.uniovi.eii.stitchingbot.arduinoConfigurationTest.ArduinoConfigurationFragmentTest
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import es.uniovi.eii.stitchingbot.logoTest.LogoFragmentsTest
import es.uniovi.eii.stitchingbot.sewingMachineTest.SewingMachinesFragmentTest
import es.uniovi.eii.stitchingbot.summaryTest.SummaryFragmentTest
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class RunAllTests {

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
        val mock = MockingHelper()
        mock.mockBluetoothService()
        mock.mockTranslator()
        mock.mockArduinoCommands()
    }

    @After
    fun afterTests() {
        unmockkAll()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        LogoDatabaseConnection(context).deleteAllData()
        SewingMachineDatabaseConnection(context).deleteAllData()
    }

    @Test
    fun run() {
        LogoFragmentsTest().runTest()
        SewingMachinesFragmentTest().runAllTests()
        ArduinoConfigurationFragmentTest().runAllTests()
        SummaryFragmentTest().runTest()
    }
}