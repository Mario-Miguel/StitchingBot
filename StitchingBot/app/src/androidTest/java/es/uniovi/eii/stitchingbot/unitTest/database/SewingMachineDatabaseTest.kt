package es.uniovi.eii.stitchingbot.unitTest.database

import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.controller.SewingMachineController
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import es.uniovi.eii.stitchingbot.model.SewingMachine
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class SewingMachineDatabaseTest {

    private val sewingMachineController = SewingMachineController()
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
        val smDC = SewingMachineDatabaseConnection(context)
        smDC.open()
        smDC.deleteAllData()
        smDC.close()
    }

    @Test
    fun insertMachineTest() {
        val machine = SewingMachine()

        //Add a predefined sewing machine
        sewingMachineController.setSewingMachine(machine)

        sewingMachineController.addSewingMachine(context)
        var insertedMachine = sewingMachineController.getAllSewingMachines(context)[0]
        assertEquals(machine, insertedMachine)

        //Add machine with name
        machine.name = "Test"
        sewingMachineController.setSewingMachine(machine)
        sewingMachineController.addSewingMachine(context)

        insertedMachine = sewingMachineController.getAllSewingMachines(context)[1]
        assertEquals(machine, insertedMachine)

        //Add machine with name, url
        machine.imgUrl = "imgUrlTest"
        sewingMachineController.setSewingMachine(machine)
        sewingMachineController.addSewingMachine(context)

        insertedMachine = sewingMachineController.getAllSewingMachines(context)[2]
        assertEquals(machine, insertedMachine)

        //Add machine with name, url, motorSteps
        machine.motorSteps = 3
        sewingMachineController.setSewingMachine(machine)
        sewingMachineController.addSewingMachine(context)

        insertedMachine = sewingMachineController.getAllSewingMachines(context)[3]
        assertEquals(machine, insertedMachine)

        //Add machine with specific id
        machine.id = 25
        sewingMachineController.setSewingMachine(machine)
        sewingMachineController.addSewingMachine(context)

        insertedMachine = sewingMachineController.getAllSewingMachines(context)[4]
        assertNotEquals(machine.id, insertedMachine.id)
        assertEquals(5, insertedMachine.id)

    }

    @Test
    fun updateMachineTest() {
        val machine = SewingMachine(1, "Test", "url", 100)

        //Add a predefined sewing machine
        sewingMachineController.setSewingMachine(machine)

        sewingMachineController.addSewingMachine(context)
        val insertedMachine = sewingMachineController.getAllSewingMachines(context)[0]
        assertEquals(machine, insertedMachine)

        //Update name
        insertedMachine.name = "Test123"
        sewingMachineController.setSewingMachine(insertedMachine)
        sewingMachineController.updateSewingMachine(context)

        var updatedMachineList = sewingMachineController.getAllSewingMachines(context)
        assertEquals(insertedMachine, updatedMachineList[0])
        assertEquals(1, updatedMachineList.size)

        //Update url
        insertedMachine.imgUrl = "urlTest123"
        sewingMachineController.setSewingMachine(insertedMachine)
        sewingMachineController.updateSewingMachine(context)

        updatedMachineList = sewingMachineController.getAllSewingMachines(context)
        assertEquals(insertedMachine, updatedMachineList[0])
        assertEquals(1, updatedMachineList.size)

        //Update motorSteps
        insertedMachine.motorSteps = 5000
        sewingMachineController.setSewingMachine(insertedMachine)
        sewingMachineController.updateSewingMachine(context)

        updatedMachineList = sewingMachineController.getAllSewingMachines(context)
        assertEquals(insertedMachine, updatedMachineList[0])
        assertEquals(1, updatedMachineList.size)

        //Update id
        insertedMachine.id = 3000
        sewingMachineController.setSewingMachine(insertedMachine)
        sewingMachineController.updateSewingMachine(context)

        updatedMachineList = sewingMachineController.getAllSewingMachines(context)
        assertEquals(insertedMachine, updatedMachineList[0])
        assertEquals(1, updatedMachineList[0].id)


    }

    @Test
    fun deleteMachineTest() {
        val machine = SewingMachine(1, "Test", "url", 100)

        //Add a sewing machine
        sewingMachineController.setSewingMachine(machine)
        sewingMachineController.addSewingMachine(context)
        val insertedMachine = sewingMachineController.getAllSewingMachines(context)[0]

        //Delete sewing machine
        sewingMachineController.setSewingMachine(insertedMachine)
        sewingMachineController.deleteSewingMachine(context)

        var sewingMachinesListSize = sewingMachineController.getAllSewingMachines(context).size
        assertEquals(0, sewingMachinesListSize)

        //Delete sewingmachine without any in database
        sewingMachineController.deleteSewingMachine(context)
        sewingMachinesListSize = sewingMachineController.getAllSewingMachines(context).size
        assertEquals(0, sewingMachinesListSize)

    }
}