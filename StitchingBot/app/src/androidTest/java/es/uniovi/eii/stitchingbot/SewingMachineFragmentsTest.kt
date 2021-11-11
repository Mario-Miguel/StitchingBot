package es.uniovi.eii.stitchingbot


import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsAnything.anything
import org.hamcrest.core.IsInstanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class SewingMachineFragmentsTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_MEDIA_LOCATION",
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

    private lateinit var expectedIntent: Matcher<Intent>
    private lateinit var expectedIntent2: Matcher<Intent>


    @Before
    fun stubCameraIntent() {
        // Initializes Intents and begins recording intents.
        Intents.init()
        val intentResult = createGalleryPickActivityResultStub()
        val intentResult2 = createCameraTakeActivityResultStub()

        // Stub the Intent.
        expectedIntent = allOf(hasAction(Intent.ACTION_GET_CONTENT))
        expectedIntent2 = allOf(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
        intending(expectedIntent).respondWith(intentResult)
        intending(expectedIntent2).respondWith(intentResult2)
    }

    @After
    fun tearDown() {
        // Clears Intents state.
        Intents.release()
    }

    private fun clickOnGetImageFromGallery() {
        /*val expectedIntent: Matcher<Intent> = allOf(
            hasAction(Intent.ACTION_PICK),
            hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        )

        val activityResult = createGalleryPickActivityResultStub()
        intending(expectedIntent).respondWith(activityResult)*/

        //Execute and verify
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
            .atPosition(1)
        materialTextView.perform(click())
        //intended(expectedIntent)
    }

    private fun clickOnGetImageFromCamera() {
        //Execute and verify
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
            .atPosition(0)
        materialTextView.perform(click())
    }

    private fun createGalleryPickActivityResultStub(): ActivityResult {
        val resources: Resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
        val imageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    resources.getResourcePackageName(R.raw.inkstitch_logo) + "/" +
                    resources.getResourceTypeName(R.raw.inkstitch_logo) + "/" +
                    resources.getResourceEntryName(R.raw.inkstitch_logo)
        )

        val resultIntent = Intent()
        resultIntent.data = imageUri
        return ActivityResult(RESULT_OK, resultIntent)
    }

    private fun createCameraTakeActivityResultStub(): ActivityResult {
        // Put the drawable in a bundle.
        val bundle = Bundle()
        bundle.putParcelable(
            "7373", BitmapFactory.decodeResource(
                InstrumentationRegistry.getInstrumentation().targetContext.resources,
                R.raw.inkstitch_logo
            )
        )

        // Create the Intent that will include the bundle.
        val resultData = Intent()
        resultData.putExtras(bundle)

        // Create the ActivityResult with the Intent.
        return ActivityResult(RESULT_OK, resultData)

    }


    @Test
    fun sewingMachineFragmentsTest() {
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

        val linearLayout = onView(
            allOf(
                withId(R.id.txtSewingMachineName),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

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

        //Click en el botón de hacer imagen con la cámara
        clickOnGetImageFromGallery()
        //clickOnGetImageFromCamera()


        val textInputEditText = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withId(R.id.txtSewingMachineName),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("A"), closeSoftKeyboard())

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

        val cardView = onView(
            allOf(
                withId(R.id.cardMachine),
                withParent(
                    allOf(
                        withId(R.id.rvMachinesList),
                        withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        cardView.check(matches(isDisplayed()))

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
                withId(R.id.action_delete), withContentDescription("Eliminar"),
                withParent(withParent(withId(R.id.toolbar))),
                isDisplayed()
            )
        )
        textView3.check(matches(isDisplayed()))

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

        val textView4 = onView(
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
        textView4.check(doesNotExist())
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
