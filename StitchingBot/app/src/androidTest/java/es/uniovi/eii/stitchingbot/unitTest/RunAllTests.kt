package es.uniovi.eii.stitchingbot.unitTest

import es.uniovi.eii.stitchingbot.unitTest.database.LogosDatabaseTest
import es.uniovi.eii.stitchingbot.unitTest.database.SewingMachineDatabaseTest
import es.uniovi.eii.stitchingbot.unitTest.translatorTest.TranslatorUnitTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    TranslatorUnitTest::class,
    LogosDatabaseTest::class,
    SewingMachineDatabaseTest::class
)
class RunAllTests