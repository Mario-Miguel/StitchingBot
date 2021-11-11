package es.uniovi.eii.stitchingbot


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreateSewingMachineTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createSewingMachineTest() {
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
                withId(R.id.nav_sewing_machines),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.nav_view),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

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

        val editText = onView(
            allOf(
                withId(R.id.etxtSewingMachineName),
                isDisplayed()
            )
        )
        editText.check(matches(isDisplayed()))

        val editText2 = onView(
            allOf(
                withId(R.id.etxtMotorSteps),
                isDisplayed()
            )
        )
        editText2.check(matches(isDisplayed()))

        val materialButton = onView(
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
        materialButton.perform(click())

        val editText3 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName),
                isDisplayed()
            )
        )
        editText3.check(matches(isDisplayed()))

        val textInputEditText =
            onView(
                allOf(
                    withClassName(Matchers.endsWith("EditText")),
                    withId(R.id.etxtSewingMachineName)
                )
            )
        textInputEditText.perform(replaceText("Maquina"))


        val textInputEditText2 =
            onView(
                allOf(
                    withClassName(Matchers.endsWith("EditText")),
                    withId(R.id.etxtMotorSteps)
                )
            )
        textInputEditText2.perform(replaceText(""))


        val materialButton2 = onView(
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
        materialButton2.perform(click())

        val editText4 = onView(
            allOf(
                withId(R.id.etxtMotorSteps),
                isDisplayed()
            )
        )
        editText4.check(matches(isDisplayed()))

        val textInputEditText4 =
            onView(
                allOf(
                    withClassName(Matchers.endsWith("EditText")),
                    withId(R.id.etxtSewingMachineName)
                )
            )
        textInputEditText4.perform(replaceText(""))


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

        val editText5 = onView(
            allOf(
                withId(R.id.etxtSewingMachineName),
                isDisplayed()
            )
        )
        editText5.check(matches(isDisplayed()))

        val textInputEditText7 =
            onView(
                allOf(
                    withClassName(Matchers.endsWith("EditText")),
                    withId(R.id.etxtSewingMachineName)
                )
            )
        textInputEditText7.perform(replaceText("Maquina"))

        val textInputEditText8 =
            onView(
                allOf(
                    withClassName(Matchers.endsWith("EditText")),
                    withId(R.id.etxtMotorSteps)
                )
            )
        textInputEditText8.perform(replaceText("1"))

        val materialButton4 = onView(
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
        materialButton4.perform(click())

        val recyclerView = onView(
            allOf(
                withId(R.id.rvMachinesList),
                withParent(withParent(withId(R.id.nav_host_fragment))),
                isDisplayed()
            )
        )
        recyclerView.check(matches(isDisplayed()))
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
