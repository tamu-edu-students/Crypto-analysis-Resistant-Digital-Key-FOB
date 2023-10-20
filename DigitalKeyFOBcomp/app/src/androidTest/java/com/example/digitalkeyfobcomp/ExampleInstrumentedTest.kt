package com.example.digitalkeyfobcomp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//@RunWith(AndroidJUnit4::class)
//class ExampleInstrumentedTest {
//    @Test
//    fun useAppContext() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        assertEquals("com.example.digitalkeyfobcomp", appContext.packageName)
//    }
//}
@RunWith(AndroidJUnit4::class)
class ProfileDaoTest {

    private lateinit var profileDao: ProfileDao
    private lateinit var db: ProfileDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.databaseBuilder(
            context,
            ProfileDatabase::class.java,
            "test_profile_database" // Specify a different database name for testing
        ).build()
        profileDao = db.profileDao()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetProfile() = runBlocking {
        val profile = ProfileEntity(name = "Test", locked = false, engine = false, address = 1, sigid = 1L)
        profileDao.insertProfile(profile)
        val allProfiles = profileDao.getAllProfiles().waitForValue()
        assertEquals(allProfiles[0].name, profile.name)
    }
}


// Extension function to get value from LiveData
fun <T> LiveData<T>.waitForValue(): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            data = t
            latch.countDown()
            this@waitForValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)

    return data ?: throw AssertionError("LiveData value was null.")
}