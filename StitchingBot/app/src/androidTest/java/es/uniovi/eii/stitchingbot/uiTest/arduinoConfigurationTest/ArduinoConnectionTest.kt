package es.uniovi.eii.stitchingbot.uiTest.arduinoConfigurationTest

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.TreeIterables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class ArduinoConnectionTest {

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

    @Test
    fun runTest() {
        navigateToScreen()
        testConnection()
        clickOnDone()
    }

    private fun navigateToScreen() {
        val appCompatImageButton = onView(
            allOf(
                ViewMatchers.withContentDescription("Open navigation drawer"),
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            ViewMatchers.withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                            0
                        )
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val navigationMenuItemView = onView(
            allOf(
                withId(R.id.nav_arduino_connection),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.nav_view),
                            0
                        )
                    ),
                    3
                ),
                ViewMatchers.isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())
    }

    fun testConnection() {
        val imageButton = onView(
            allOf(
                withId(R.id.fabDiscoverDevices),
                ViewMatchers.withContentDescription("Botón de búsqueda"),
                ViewMatchers.withParent(ViewMatchers.withParent(IsInstanceOf.instanceOf(FrameLayout::class.java))),
                ViewMatchers.isDisplayed()
            )
        )
        imageButton.check(matches(ViewMatchers.isDisplayed()))

        val floatingActionButton = onView(
            allOf(
                withId(R.id.fabDiscoverDevices),
                ViewMatchers.withContentDescription("Botón de búsqueda"),
                childAtPosition(
                    childAtPosition(
                        ViewMatchers.withClassName(`is`("android.widget.FrameLayout")),
                        0
                    ),
                    2
                ),
                ViewMatchers.isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        onView(ViewMatchers.isRoot()).perform(waitForView(R.id.txtDeviceName))
        clickOnRow(0)

        val textView2 = onView(
            AllOf.allOf(
                ViewMatchers.withText("Configuración de Arduino"),
                ViewMatchers.withParent(
                    AllOf.allOf(
                        withId(R.id.toolbar),
                        ViewMatchers.withParent(IsInstanceOf.instanceOf(LinearLayout::class.java))
                    )
                ),
                ViewMatchers.isDisplayed()
            )
        )
        textView2.check(matches(ViewMatchers.withText("Configuración de Arduino")))
    }

    fun clickOnDone() {
        val button = onView(
            allOf(
                withId(R.id.btnAxisDone), ViewMatchers.withText("¡HECHO!"),
                ViewMatchers.withParent(ViewMatchers.withParent(IsInstanceOf.instanceOf(FrameLayout::class.java))),
                ViewMatchers.isDisplayed()
            )
        )
        button.check(matches(ViewMatchers.isDisplayed()))

        val materialButton = onView(
            allOf(
                withId(R.id.btnAxisDone), ViewMatchers.withText("¡Hecho!"),
                isCompletelyDisplayed()
            )
        )
        materialButton.check(matches(isCompletelyDisplayed()))
        materialButton.perform(click())
    }

    fun goBack() {
        //Ir hacia atrás.
        val appCompatImageButton2 = onView(
            allOf(
                ViewMatchers.withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.toolbar),
                        childAtPosition(
                            ViewMatchers.withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                            0
                        )
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatImageButton2.perform(click())
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

    private fun waitForView(viewId: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with id $viewId; during 5000 millis."
            }

            override fun perform(uiController: UiController, rootView: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + 5000
                val viewMatcher = withId(viewId)
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(rootView)) {
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }
                    uiController.loopMainThreadForAtLeast(100)
                } while (System.currentTimeMillis() < endTime)

            }
        }
    }

    private fun clickOnRow(position: Int) {
        onView(withId(R.id.rvDevicesList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>
                (position, ClickOnView())
        )
    }

    inner class ClickOnView : ViewAction {
        private var click = click()

        override fun getConstraints(): Matcher<View> {
            return click.constraints
        }

        override fun getDescription(): String {
            return " click on custom button view"
        }

        override fun perform(uiController: UiController, view: View) {
            click.perform(uiController, view.findViewById(R.id.txtDeviceName))
        }
    }
}