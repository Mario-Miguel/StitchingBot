package es.uniovi.eii.stitchingbot.sewingMachineTest


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class ModifySewingMachineTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun modifySewingMachineTest() {

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

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText(""))


        val materialButton2 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("Modificar"),
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
        materialButton2.perform(click())

        val button = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("MODIFICAR"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName),
                isDisplayed()
            )
        )
        textInputEditText3.perform(replaceText("test"))

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.etxtMotorSteps),
                isDisplayed()
            )
        )
        textInputEditText4.perform(replaceText(""))

        val materialButton3 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("Modificar"),
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

        val button2 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("MODIFICAR"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.etxtMotorSteps),
                isDisplayed()
            )
        )
        textInputEditText5.perform(replaceText("467"))


        val materialButton4 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("Modificar"),
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
        materialButton4.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.txtSewingMachineName), withText("test"),
                withParent(withParent(withId(R.id.cardMachine))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("test")))

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

        val editText = onView(
            allOf(
                withId(R.id.etxtSewingMachineName), withText("test"),
                withParent(withParent(withId(R.id.txtSewingMachineName))),
                isDisplayed()
            )
        )
        editText.check(matches(withText("test")))

        val textInputEditText11 = onView(
            allOf(
                withId(R.id.etxtMotorSteps), withText("467"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.txtMotorSteps),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText11.perform(replaceText("1"))

        val textInputEditText12 = onView(
            allOf(
                withId(R.id.etxtMotorSteps), withText("1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.txtMotorSteps),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText12.perform(closeSoftKeyboard())

        val materialButton5 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("Modificar"),
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
        materialButton5.perform(click())

        val recyclerView3 = onView(
            allOf(
                withId(R.id.rvMachinesList),
                childAtPosition(
                    withClassName(`is`("androidx.core.widget.NestedScrollView")),
                    0
                )
            )
        )
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val editText2 = onView(
            allOf(
                withId(R.id.etxtMotorSteps), withText("1"),
                withParent(withParent(withId(R.id.txtMotorSteps))),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("1")))

        val textInputEditText13 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName), withText("test"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.txtSewingMachineName),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText13.perform(replaceText("test123"))

        val textInputEditText14 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName), withText("test123"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.txtSewingMachineName),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText14.perform(closeSoftKeyboard())

        val textInputEditText15 = onView(
            allOf(
                withId(R.id.etxtMotorSteps), withText("1"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.txtMotorSteps),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText15.perform(replaceText("100"))

        val textInputEditText16 = onView(
            allOf(
                withId(R.id.etxtMotorSteps), withText("100"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.txtMotorSteps),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText16.perform(closeSoftKeyboard())

        val materialButton6 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("Modificar"),
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
        materialButton6.perform(click())

        val textView2 = onView(
            allOf(
                withId(R.id.txtSewingMachineName), withText("test123"),
                withParent(withParent(withId(R.id.cardMachine))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("test123")))

        val recyclerView4 = onView(
            allOf(
                withId(R.id.rvMachinesList),
                childAtPosition(
                    withClassName(`is`("androidx.core.widget.NestedScrollView")),
                    0
                )
            )
        )
        recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val editText3 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName), withText("test123"),
                withParent(withParent(withId(R.id.txtSewingMachineName))),
                isDisplayed()
            )
        )
        editText3.check(matches(withText("test123")))

        val editText4 = onView(
            allOf(
                withId(R.id.etxtMotorSteps), withText("100"),
                withParent(withParent(withId(R.id.txtMotorSteps))),
                isDisplayed()
            )
        )
        editText4.check(matches(withText("100")))

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
