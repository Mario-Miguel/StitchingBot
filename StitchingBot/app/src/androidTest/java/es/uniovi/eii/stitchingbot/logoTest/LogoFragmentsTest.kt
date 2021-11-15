package es.uniovi.eii.stitchingbot.logoTest


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.R
import es.uniovi.eii.stitchingbot.database.LogoDatabaseConnection
import es.uniovi.eii.stitchingbot.database.SewingMachineDatabaseConnection
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class LogoFragmentsTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @After
    fun afterTesting() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        LogoDatabaseConnection(context).deleteAllData()
        SewingMachineDatabaseConnection(context).deleteAllData()
    }

    @Test
    fun runTest() {
        val textView = onView(
            allOf(
                withText("Logotipos"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Logotipos")))

        val textView2 = onView(
            allOf(
                withId(R.id.action_add), withContentDescription("Añadir"),
                withParent(withParent(withId(R.id.toolbar))),
                isDisplayed()
            )
        )
        textView2.check(matches(isDisplayed()))

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

        val view = onView(
            allOf(
                withId(R.id.canvasView),
                withParent(
                    allOf(
                        withId(R.id.linearLayout2),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        view.check(matches(isDisplayed()))

        val linearLayout = onView(
            allOf(
                withId(R.id.buttonToolbar),
                withParent(
                    allOf(
                        withId(R.id.linearLayout2),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

        val button = onView(
            allOf(
                withId(R.id.btnDone), withText("GUARDAR"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val button2 = onView(
            allOf(
                withId(R.id.btnSew), withText("COSER"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

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

        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Navigate up"),
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

        val recyclerView = onView(
            allOf(
                withId(R.id.rvLogoList),
                withParent(withParent(withId(R.id.nav_host_fragment))),
                isDisplayed()
            )
        )
        recyclerView.check(matches(isDisplayed()))

        val textView3 = onView(
            allOf(
                withText("Logotipos"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Logotipos")))

        val recyclerView2 = onView(
            allOf(
                withId(R.id.rvLogoList),
                childAtPosition(
                    withClassName(`is`("androidx.core.widget.NestedScrollView")),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val view2 = onView(
            allOf(
                withId(R.id.canvasView),
                withParent(
                    allOf(
                        withId(R.id.linearLayout2),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        view2.check(matches(isDisplayed()))

        val textView4 = onView(
            allOf(
                withId(R.id.action_delete), withContentDescription("Eliminar"),
                withParent(withParent(withId(R.id.toolbar))),
                isDisplayed()
            )
        )
        textView4.check(matches(isDisplayed()))

        val actionMenuItemView2 = onView(
            allOf(
                withId(R.id.action_delete), withContentDescription("Eliminar"),
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
        actionMenuItemView2.perform(click())

        val materialButton3 = onView(
            Matchers.allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        materialButton3.perform(ViewActions.scrollTo(), click())

        val textView5 = onView(
            allOf(
                withText("Logotipos"),
                withParent(
                    allOf(
                        withId(R.id.toolbar),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("Logotipos")))
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
