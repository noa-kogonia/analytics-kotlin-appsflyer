package com.appsflyer.segment.app

import AnalyticsManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.segment.analytics.kotlin.destinations.appsflyer.AppsFlyerDestination

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var adapter: KeyValueAdapter? = null
    private var eventNameET: EditText? = null
    private var keyET: EditText? = null
    private var valueET: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).post {
        // set view objects
        setContentView(R.layout.activity_main)
        initListView()
        findViewById<View>(R.id.button_add).setOnClickListener(this)
        findViewById<View>(R.id.track_button).setOnClickListener(this)
        eventNameET = findViewById<View>(R.id.event_name_editText) as EditText
        keyET = findViewById<View>(R.id.key_text_editText) as EditText
        valueET = findViewById<View>(R.id.value_text_editText) as EditText
        valueET!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                adapter!!.addItem(keyET!!.text.toString(), valueET!!.text.toString())
                keyET!!.requestFocus()
            }
            false
        }
        // Confirm Analytics and AppsFlyerDestination readiness
        if (AnalyticsManager.analytics.find(AppsFlyerDestination::class) != null) {
            Log.d(TAG, "AppsFlyerDestination is ready")
        } else {
            Log.e(TAG, "AppsFlyerDestination is NOT ready")
        }
        // set AppsFlyer conversions listener
        Log.d(TAG, "MainActivity OnCreate - Initializing...")
        initConversionListener()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity OnResume - Strating the SDK")
    }

    private fun initConversionListener() {
        // Set the conversion listener
        AppsFlyerDestinationManager.appsFlyerDestination.conversionListener = object : AppsFlyerDestination.ExternalAppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                conversionData.forEach { (key, value) ->
                    Log.d(TAG, "attribute: $key = $value")
                }

                // Build the conversion data string for display
                val conversionDataString = buildString {
                    append("callbackType: Conversion Data\n")
                    append("Install Type: ${conversionData["af_status"]}\n")
                    append("Media Source: ${conversionData["media_source"]}\n")
                    append("Click Time(GMT): ${conversionData["click_time"]}\n")
                    append("Install Time(GMT): ${conversionData["install_time"]}\n")
                }

                // Update the UI on the main thread
                runOnUiThread {
                    val conversionTextView = findViewById<TextView>(R.id.conversionDataTextView)
                    if (conversionTextView != null) {
                        conversionTextView.gravity = Gravity.CENTER_VERTICAL
                        conversionTextView.text = conversionDataString
                    } else {
                        Log.d(TAG, "Could not load conversion data")
                    }
                }
            }

            override fun onConversionDataFail(errorMessage: String?) {
                Log.e(TAG, "Conversion Data Failure: $errorMessage")
            }

            override fun onAppOpenAttribution(attributionData: Map<String, String>) {
                attributionData.forEach { (key, value) ->
                    Log.d(TAG, "attribute: $key = $value")
                }
            }

            override fun onAttributionFailure(errorMessage: String?) {
                Log.e(TAG, "Attribution Failure: $errorMessage")
            }
        }
    }

    private fun initListView() {
        adapter = KeyValueAdapter()
        (findViewById<View>(R.id.listView_items) as ListView).adapter =
            adapter
    }

    override fun onClick(view: View) {
        if (view.id == R.id.track_button) {
            val eventName = eventNameET?.text.toString()
            val properties = mapOf("key" to "value")
            if (eventName.isNotEmpty()) {
                AnalyticsManager.analytics.track(eventName, properties)
            } else {
                AnalyticsManager.analytics.track("Default Event")
            }
        }
    }

    fun deleteClickHandler(v: View) {
        val vwParentRow = v.parent as RelativeLayout
        val keyValContainer = vwParentRow.getChildAt(0) as LinearLayout
        val key = (keyValContainer.getChildAt(0) as TextView).text.toString()
        adapter!!.mData.remove(key)
        adapter!!.mKeys.remove(key)
        adapter!!.notifyDataSetChanged()
    }

    class ViewHolder {
        var keyTextView: TextView? = null
        var valueTextView: TextView? = null
        var deleteButton: Button? = null
    }

    private inner class KeyValueAdapter : BaseAdapter {
        var mData: MutableMap<String, String> = LinkedHashMap()
        var mKeys: MutableList<String>
        private var mInflater: LayoutInflater

        @Suppress("unused")
        constructor(data: LinkedHashMap<String, String>) {
            mInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mData = data
            mKeys = ArrayList(mData.keys)
        }

        constructor() {
            mInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mKeys = ArrayList(mData.keys)
        }

        fun addItem(key: String, value: String) {
            mData[key] = value
            if (!mKeys.contains(key)) {
                mKeys.add(key)
            }
            notifyDataSetChanged()
            keyET!!.setText("")
            valueET!!.setText("")
        }

        override fun getCount(): Int {
            return mData.size
        }

        override fun getItem(position: Int): String {
            return mKeys[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            println("getView $position $convertView")
            val holder: ViewHolder
            if (convertView == null) {
                convertView = mInflater.inflate(
                    R.layout.item1,
                    parent,
                    false
                ) //http://stackoverflow.com/questions/14978296/unable-to-start-activityunsupportedoperationexception-addviewview-layoutpara
                holder = ViewHolder()
                holder.keyTextView = convertView.findViewById<View>(R.id.key_text_item) as TextView
                holder.valueTextView =
                    convertView.findViewById<View>(R.id.value_text_item) as TextView
                holder.deleteButton = convertView.findViewById<View>(R.id.remove_button) as Button
                convertView.setTag(holder)
            } else {
                holder = convertView.tag as ViewHolder
            }
            holder.keyTextView!!.text = mKeys[position]
            holder.valueTextView!!.text = mData[mKeys[position]]
            return convertView!!
        }
    }

    companion object {
        const val TAG = "AppsFlyer-Segment"
    }
}
