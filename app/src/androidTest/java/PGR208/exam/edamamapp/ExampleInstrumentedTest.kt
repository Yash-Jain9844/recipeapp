package PGR208.exam.edamamapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

// Instrumented test class for testing on an Android device
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    // Test to verify the app's package name
    @Test
    fun useAppContext() {
        // Get the context of the app under test
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        // Assert that the package name is correct
        assertEquals("PGR208.exam.edamamapp", appContext.packageName)
    }
}