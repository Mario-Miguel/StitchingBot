package es.uniovi.eii.stitchingbot.arduinoConfigurationTest

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test

class ArduinoDisconnectionTest {

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
        val appCompatImageButton = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withContentDescription("Open navigation drawer"),
                childAtPosition(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.toolbar),
                        childAtPosition(
                            ViewMatchers.withClassName(Matchers.`is`("com.google.android.material.appbar.AppBarLayout")),
                            0
                        )
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        appCompatImageButton.perform(ViewActions.click())

        val navigationMenuItemView = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.nav_arduino_connection),
                childAtPosition(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.design_navigation_view),
                        childAtPosition(
                            ViewMatchers.withId(R.id.nav_view),
                            0
                        )
                    ),
                    3
                ),
                ViewMatchers.isDisplayed()
            )
        )
        navigationMenuItemView.perform(ViewActions.click())

        val textView3 = Espresso.onView(
            AllOf.allOf(
                ViewMatchers.withText("Configuración de Arduino"),
                ViewMatchers.withParent(
                    AllOf.allOf(
                        ViewMatchers.withId(R.id.toolbar),
                        ViewMatchers.withParent(IsInstanceOf.instanceOf(LinearLayout::class.java))
                    )
                ),
                ViewMatchers.isDisplayed()
            )
        )
        textView3.check(ViewAssertions.matches(ViewMatchers.withText("Configuración de Arduino")))

        val materialButton = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.btnDisconnect), ViewMatchers.withText("Desconectar")
            )
        )
        materialButton.perform(ViewActions.click())

        val imageButton = Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.fabDiscoverDevices),
                ViewMatchers.withContentDescription("Botón de búsqueda"),
                ViewMatchers.withParent(ViewMatchers.withParent(IsInstanceOf.instanceOf(FrameLayout::class.java))),
                ViewMatchers.isDisplayed()
            )
        )
        imageButton.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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
}