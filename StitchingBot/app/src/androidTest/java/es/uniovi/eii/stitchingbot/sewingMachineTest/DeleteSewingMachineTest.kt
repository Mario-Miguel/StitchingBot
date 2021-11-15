package es.uniovi.eii.stitchingbot.sewingMachineTest


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import es.uniovi.eii.stitchingbot.MainActivity
import es.uniovi.eii.stitchingbot.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class DeleteSewingMachineTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun deleteSewingMachineTest() {
        val linearLayout = onView(
            allOf(
                withParent(
                    allOf(
                        withId(R.id.cardMachine),
                    )
                ),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

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

        val textView = onView(
            allOf(
                withId(R.id.action_delete), withContentDescription("Eliminar"),
                withParent(withParent(withId(R.id.toolbar))),
                isDisplayed()
            )
        )
        textView.check(matches(isDisplayed()))

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

        val textView2 = onView(
            allOf(
                withId(android.R.id.message),
                withText("¿Está seguro de eliminar la máquina de coser?"),
                withParent(withParent(withId(R.id.scrollView))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("¿Está seguro de eliminar la máquina de coser?")))

        val materialButton2 = onView(
            allOf(
                withId(android.R.id.button2), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    2
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val editText2 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName), withText("test123"),
                withParent(withParent(withId(R.id.txtSewingMachineName))),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("test123")))

        val appCompatImageButton2 = onView(
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
        appCompatImageButton2.perform(click())

        val cardView2 = onView(
            allOf(
                withId(R.id.cardMachine),
                isDisplayed()
            )
        )
        cardView2.check(matches(isDisplayed()))

        val recyclerView2 = onView(
            allOf(
                withId(R.id.rvMachinesList),
                childAtPosition(
                    withClassName(`is`("androidx.core.widget.NestedScrollView")),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val actionMenuItemView3 = onView(
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
        actionMenuItemView3.perform(click())

        val textView3 = onView(
            allOf(
                withId(android.R.id.message),
                withText("¿Está seguro de eliminar la máquina de coser?"),
                withParent(withParent(withId(R.id.scrollView))),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("¿Está seguro de eliminar la máquina de coser?")))

        val materialButton3 = onView(
            allOf(
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
        materialButton3.perform(scrollTo(), click())

        val cardView = onView(
            allOf(
                withId(R.id.cardMachine),
                isDisplayed()
            )
        )
        cardView.check(doesNotExist())
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
