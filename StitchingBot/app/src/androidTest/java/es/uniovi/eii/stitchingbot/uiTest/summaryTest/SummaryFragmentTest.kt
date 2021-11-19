package es.uniovi.eii.stitchingbot.uiTest.summaryTest


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.uiTest.MockingHelper
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.uiTest.arduinoConfigurationTest.ArduinoConnectionTest
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import io.mockk.unmockkAll
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class SummaryFragmentTest {

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
        val ldc = LogoDatabaseConnection(context)
        ldc.open()
        ldc.deleteAllData()
        ldc.close()
    }

    @Test
    fun runTest() {
        navigateToScreen()

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.action_add), withContentDescription("Añadir"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val materialButton = onView(
            allOf(
                withId(R.id.btnDone), withText("Guardar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val recyclerView2 = onView(
            allOf(
                withId(R.id.rvLogoList),
                childAtPosition(
                    withClassName(CoreMatchers.`is`("androidx.core.widget.NestedScrollView")),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val materialButton2 = onView(
            allOf(
                withId(R.id.btnSew), withText("Coser"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val textView = onView(
            allOf(
                withText("Resumen de la ejecucion"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Resumen de la ejecucion")))

        val imageView = onView(
            allOf(
                withId(R.id.imgLogoSummary), withContentDescription("Logotipo seleccionado"),
                withParent(
                    allOf(
                        withId(R.id.cardViewLogo),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        val imageView2 = onView(
            allOf(
                withId(R.id.imgSewingMachineSummary),
                withContentDescription("Añadir máquina de coser"),
                withParent(
                    allOf(
                        withId(R.id.cardViewSewingMachine),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        imageView2.check(matches(isDisplayed()))

        val imageView3 = onView(
            allOf(
                withId(R.id.imgRobotSummary),
                withContentDescription("Estado de la conexión del arduino"),
                withParent(
                    allOf(
                        withId(R.id.cardViewArduino),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        imageView3.check(matches(isDisplayed()))

        val buttonTranslate = onView(
            allOf(
                withId(R.id.btnStartTranslate), withText("TRADUCIR"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        buttonTranslate.check(matches(isDisplayed()))

        val buttonStart = onView(
            allOf(
                withId(R.id.btnStartExecution), withText("COMENZAR"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        buttonStart.check(matches(allOf(isDisplayed(), not(isEnabled()))))

        val buttonPause = onView(
            allOf(
                withId(R.id.btnPauseExecution), withText("PAUSA"),
                isDisplayed()
            )
        )
        buttonPause.check(doesNotExist())

        val buttonResume = onView(
            allOf(
                withId(R.id.btnResumeExecution), withText("REANUDAR"),
                isDisplayed()
            )
        )
        buttonResume.check(doesNotExist())

        val buttonStop = onView(
            allOf(
                withId(R.id.btnStopExecution), withText("STOP"),
                isDisplayed()
            )
        )
        buttonStop.check(doesNotExist())

        val cardView = onView(
            allOf(
                withId(R.id.cardViewSewingMachine),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        cardView.perform(click())

        val textView2 = onView(
            allOf(
                withText("Máquinas de coser"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Máquinas de coser")))

        val actionMenuItemAddSewingMachine = onView(
            allOf(
                withId(R.id.action_add), withContentDescription("Añadir"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemAddSewingMachine.perform(click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.etxtSewingMachineName),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("test"))


        val materialButton3 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("Agregar"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        val recyclerView = onView(
            allOf(
                withId(R.id.rvMachinesList),
                childAtPosition(
                    withClassName(`is`("androidx.core.widget.NestedScrollView")),
                    0
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val textView3 = onView(
            allOf(
                withText("Resumen de la ejecucion"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Resumen de la ejecucion")))

        val textView4 = onView(
            allOf(
                withId(R.id.txtSewingMachineSummary), withText("test"),
                withParent(
                    allOf(
                        withId(R.id.cardViewSewingMachine),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("test")))

        val cardView2 = onView(
            allOf(
                withId(R.id.cardViewArduino),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        cardView2.perform(click())

        val textView5 = onView(
            allOf(
                withText("Configuración del robot"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("Configuración del robot")))

        ArduinoConnectionTest().testConnection()
        ArduinoConnectionTest().clickOnDone()

        buttonTranslate.check(matches(isDisplayed()))
        buttonTranslate.perform(click())

        buttonStart.check(matches(isEnabled()))
        buttonTranslate.check(matches(not(isEnabled())))

        buttonStart.perform(click())

        val progressBar = onView(
            allOf(
                withId(R.id.pbExecution),
                isDisplayed()
            )
        )
        progressBar.check(matches(isDisplayed()))

        buttonStart.check(doesNotExist())
        buttonTranslate.check(doesNotExist())
        buttonPause.check(matches(isDisplayed()))
        buttonStop.check(matches(isDisplayed()))

        buttonPause.perform(click())
        buttonPause.check(doesNotExist())
        buttonResume.check(matches(isDisplayed()))

        buttonResume.perform(click())
        buttonResume.check(doesNotExist())
        buttonPause.check(matches(isDisplayed()))

        buttonStop.perform(click())
        progressBar.check(doesNotExist())
        buttonPause.check(doesNotExist())
        buttonStop.check(doesNotExist())

        buttonTranslate.check(matches(isDisplayed()))
        buttonStart.check(matches(isDisplayed()))

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    private fun navigateToScreen() {
        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Open navigation drawer"),
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val navigationMenuItemView = onView(
            allOf(
                withId(R.id.nav_logo_list),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.nav_view),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())
    }

}
