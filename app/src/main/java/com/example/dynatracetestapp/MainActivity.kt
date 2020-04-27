package com.example.dynatracetestapp

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dynatrace.android.agent.Dynatrace
import com.dynatrace.android.agent.WebRequestTiming
import com.dynatrace.android.agent.util.Utility.readStream
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {


    private val LATITUDE = 51.75924850
    private val LONGITUDE = 19.45598330

    private var mHandler: Handler? = null

    private var mHandlerThread: HandlerThread? = null

    fun startHandlerThread() {
        mHandlerThread = HandlerThread("HandlerThread")
        mHandlerThread!!.start()
        mHandler = Handler(mHandlerThread!!.looper)

        mHandler!!.postDelayed({
//            testAll()
            testApi()
            //testWebRequest()
            // Your task goes here
        }, 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//        throw  Exception("rk test 123")

        fab.setOnClickListener { view ->

            startHandlerThread()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun testApi(){
       // val webAction = Dynatrace.enterAction("forecast")
       val client = ApiWeatherRestClient.getInstance()
           client.getWeather(LATITUDE,LONGITUDE)


    }

    private fun testWebRequest(){

        val webAction = Dynatrace.enterAction("techcrunch")

        var urlConnection: HttpURLConnection? = null
        var timing: WebRequestTiming? = null
        try {
            val url = URL("https://techcrunch.com/")
            urlConnection = url.openConnection() as HttpURLConnection
            // [2] Tag the web request automatically and receive a WebRequestTiming instance
            timing = Dynatrace.getWebRequestTiming(urlConnection)

            // [3] Call startWebRequestTiming() to begin the timing (and then handle the input stream from the connection)
            timing!!.startWebRequestTiming()
            val input: InputStream = BufferedInputStream(urlConnection.getInputStream())
            readStream(input)

            // [4] Once we're done reading, we can stop the timing
            timing!!.stopWebRequestTiming()
            //processData()
        } catch (exception: Exception) {
            // [5a] Finalize the timing when an error occurs
            timing?.stopWebRequestTiming()
            // [5b] Attach the exception to the action
            webAction.reportError("Exception", exception)

            // user-defined exception handling
            //handleError(exception)
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }

            // [6] Lastly finalize the timing of the action in the finally block.
            webAction.leaveAction()
        }
    }

    fun testAll(){
        reportError()
        reportEvent()
        reportValue()
    }

    fun reportEvent(){
        val action = Dynatrace.enterAction("reportEvent Action")
        action.reportEvent("Button clicked")
        action.leaveAction()
    }

    fun reportValue(){
        val action = Dynatrace.enterAction("reportValue Action")
        action.reportValue("reportValue Test",121)
        action.leaveAction()
    }

    fun reportError(){
        val action = Dynatrace.enterAction("reportError Action")
        action.reportError("reportError Test",9999)
        action.leaveAction()
        Dynatrace.reportError("reportError Test2",5555)
    }
}
