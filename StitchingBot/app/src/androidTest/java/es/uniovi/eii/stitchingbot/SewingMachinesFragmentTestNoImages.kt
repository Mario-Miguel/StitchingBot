package es.uniovi.eii.stitchingbot


import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewInteraction
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*

import es.uniovi.eii.stitchingbot.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SewingMachinesFragmentTestNoImages {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_MEDIA_LOCATION",
            "android.permission.CAMERA"
        )

    @Test
    fun sewingMachinesFragmentTestNoImages() {
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

        val textView = onView(
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
        textView.check(matches(withText("Máquinas de coser")))

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

        val imageView = onView(
            allOf(
                withId(R.id.imgSewingMachineDetails),
                withContentDescription("Imagen de la máquina de coser"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        val button = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("AGREGAR"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val appCompatImageView = onView(
            allOf(
                withId(R.id.imgSewingMachineDetails),
                withContentDescription("Imagen de la máquina de coser"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.FrameLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        val textView2 = onView(
            allOf(
                withId(R.id.alertTitle), withText("Escoge la foto de tu máquina de coser"),
                withParent(
                    allOf(
                        withId(R.id.title_template),
                        withParent(withId(R.id.topPanel))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Escoge la foto de tu máquina de coser")))

        val textView3 = onView(
            allOf(
                withId(android.R.id.text1), withText("Sacar foto"),
                withParent(
                    allOf(
                        withId(R.id.select_dialog_listview),
                        withParent(withId(R.id.contentPanel))
                    )
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Sacar foto")))

        val textView4 = onView(
            allOf(
                withId(android.R.id.text1), withText("Escoger de la galería"),
                withParent(
                    allOf(
                        withId(R.id.select_dialog_listview),
                        withParent(withId(R.id.contentPanel))
                    )
                ),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("Escoger de la galería")))

        val materialTextView = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.select_dialog_listview),
                    childAtPosition(
                        withId(R.id.contentPanel),
                        0
                    )
                )
            )
            .atPosition(2)
        materialTextView.perform(click())

        val textInput =
            onView(allOf(withClassName(endsWith("EditText")), withId(R.id.etxtSewingMachineName)))
        textInput.perform(replaceText("Maquina"))

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

        val recyclerView = onView(
            allOf(
                withId(R.id.rvMachinesList),
                withParent(withParent(withId(R.id.nav_host_fragment))),
                isDisplayed()
            )
        )
        recyclerView.check(matches(isDisplayed()))

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

        val textView5 = onView(
            allOf(
                withId(R.id.action_delete), withContentDescription("Eliminar"),
                withParent(withParent(withId(R.id.toolbar))),
                isDisplayed()
            )
        )
        textView5.check(matches(isDisplayed()))

        val editText = onView(
            allOf(
                withText("Maquina"),
                withParent(withParent(withId(R.id.txtSewingMachineName))),
                isDisplayed()
            )
        )
        editText.check(matches(withText("Maquina")))

        val button2 = onView(
            allOf(
                withId(R.id.btnSewingMachineAction), withText("MODIFICAR"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

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

        val linearLayout = onView(
            allOf(
                withParent(
                    allOf(
                        withId(R.id.cardMachine),
                        withParent(withId(R.id.rvMachinesList))
                    )
                ),
                isDisplayed()
            )
        )
        linearLayout.check(doesNotExist())
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
