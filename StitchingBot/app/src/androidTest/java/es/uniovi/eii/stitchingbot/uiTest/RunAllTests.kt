package es.uniovi.eii.stitchingbot.uiTest

import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import es.uniovi.eii.stitchingbot.uiTest.arduinoConfigurationTest.ArduinoConfigurationFragmentTest
import es.uniovi.eii.stitchingbot.uiTest.logoTest.LogoFragmentsTest
import es.uniovi.eii.stitchingbot.uiTest.sewingMachineTest.SewingMachinesFragmentTest
import es.uniovi.eii.stitchingbot.uiTest.summaryTest.SummaryFragmentTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Suite

@LargeTest
@RunWith(Suite::class)
@Suite.SuiteClasses(
    LogoFragmentsTest::class,
    SewingMachinesFragmentTest::class,
    ArduinoConfigurationFragmentTest::class,
    SummaryFragmentTest::class
)
class RunAllTests {
    @Before
    fun beforeTesting() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val ldc = LogoDatabaseConnection(context)
        ldc.open()
        ldc.deleteAllData()
        ldc.close()
    }
}