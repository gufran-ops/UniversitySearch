package com.example.assignment

import android.content.Context
import android.content.Intent
import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.assignment.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.recyclerview.ItemAdapter
import com.example.assignment.recyclerview.RowItem
import com.example.assignment.syncservice.RunningService
import com.example.assignment.viewmodel.UniversityViewModel
import com.example.assignment.webactivity.WebActivity
import org.json.JSONException
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var mQueue: RequestQueue
    private lateinit var binding: ActivityMainBinding
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var itemList: MutableList<RowItem>
    private lateinit var tempItemList: MutableList<RowItem>
    private var searchView: SearchView ?=null
    private lateinit var progressBar: ProgressBar
    private lateinit var universityViewModel: UniversityViewModel
    private lateinit var adapter: ItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.mytoolbar))
        newRecyclerView = binding.recyclerView
        newRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        universityViewModel = ViewModelProvider(this)[UniversityViewModel::class.java]


        itemList = mutableListOf()
        tempItemList = mutableListOf()
        mQueue = Volley.newRequestQueue(this)
        progressBar = binding.progressBar

        adapter = ItemAdapter()
        initializeAdapter()
        receiveDataUpdate()

        if (!isForegroundServiceRunning()) {
            Intent(applicationContext, RunningService::class.java).also {
                it.action = RunningService.Actions.START.toString()
                startService(it)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu?.findItem(R.id.seach_action)
        searchView = item?.actionView as SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                onSearchRefresh(newText)
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun onSearchRefresh(curSearchText: String?){
        tempItemList.clear()
        val searchText = curSearchText!!.lowercase(Locale.getDefault())
        if(searchText.isNotEmpty()){

            itemList.forEach {
                if(it.name.lowercase(Locale.getDefault()).contains(searchText)){
                    tempItemList.add(it)
                }
            }
        }
        else
        {
            tempItemList.addAll(itemList)
        }

        adapter.setData(tempItemList)
        adapter.setOnItemClickListner(object : ItemAdapter.OnItemClickListner {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, WebActivity::class.java)
                intent.putExtra("URL", tempItemList[position].url.getString(0))
                intent.putExtra("NAME", tempItemList[position].name)
                startActivity(intent)
            }

        })
        newRecyclerView.adapter = adapter
    }

    private fun isForegroundServiceRunning(): Boolean {
        val activityManager: ActivityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices((Int.MAX_VALUE))) {
            if (RunningService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun receiveDataUpdate() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "data-sync") {
                    val jsonArrayString = intent.getStringExtra("response")
                    if (jsonArrayString != null) {
                        try {
                            val response = JSONArray(jsonArrayString)
                            refreshAdaptor(response)
                        }
                        catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        val filter = IntentFilter("data-sync")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)

    }

    private fun initializeAdapter(){
        if(universityViewModel.itemList.isEmpty())
            return

        progressBar.visibility = View.GONE

        itemList.addAll(universityViewModel.itemList)
        adapter.setData(itemList)
        adapter.setOnItemClickListner(object : ItemAdapter.OnItemClickListner {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, WebActivity::class.java)
                intent.putExtra("URL", itemList[position].url.getString(0))
                intent.putExtra("NAME", itemList[position].name)
                startActivity(intent)
            }

        })
        newRecyclerView.adapter = adapter
    }

    private fun refreshAdaptor(response : JSONArray){
        itemList.clear()
        for (i in 0 until response.length()) {
            val jsonObject: JSONObject = response.getJSONObject(i)
            val item = RowItem(
                jsonObject.getString("name"),
                jsonObject.getJSONArray("domains"),
                jsonObject.getString("country"),
                jsonObject.getString("alpha_two_code"),
                jsonObject.getJSONArray("web_pages")
            )
            itemList.add(item)

        }
        universityViewModel.itemList.addAll(itemList)
        val searchText = searchView!!.query.toString()
        onSearchRefresh(searchText)
        progressBar.visibility = View.GONE
    }

}