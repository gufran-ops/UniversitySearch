package com.example.assignment.syncservice

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R

class RunningService:Service() {
    private lateinit var handlerThread:HandlerThread
    private lateinit var handler:Handler
    private lateinit var dataFetchTask: Runnable
    private lateinit var mQueue: RequestQueue
    private val FETCH_INTERVAL:Long = 10 * 1000 // Fetch data every 10 seconds
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        handlerThread = HandlerThread("DataFetchThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        mQueue = Volley.newRequestQueue(this)

        dataFetchTask = object : Runnable {
            override fun run() {
                broadcastResponse()
                handler.postDelayed(this, FETCH_INTERVAL)
            }
        }

        super.onCreate()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){
            Actions.START.toString()->start()
            Actions.STOP.toString()->stopService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val notification=NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Data Sync")
            .setContentText("Syncing data from API every 10 sec!")
            .build()
        startForeground(1, notification)

        handler.post(dataFetchTask)
    }


    private fun stopService() {
        handler.removeCallbacks(dataFetchTask)
        handlerThread.quitSafely()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }


    private fun broadcastResponse() {
        var url = "http://universities.hipolabs.com/search"
        var request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val intent = Intent("data-sync")
                    intent.putExtra("response", response.toString())
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Log.e("TAG", "Response is $error")
                Toast.makeText(this@RunningService, "Failed To Get Respond", Toast.LENGTH_LONG).show()
            })

        mQueue.add(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(dataFetchTask)
        handlerThread.quitSafely()
    }

    enum class Actions{
        START, STOP
    }

}