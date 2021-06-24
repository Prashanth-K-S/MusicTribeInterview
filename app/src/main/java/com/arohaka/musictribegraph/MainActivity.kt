package com.arohaka.musictribegraph

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.arohaka.musictribegraph.activities.GraphActivity
import com.arohaka.musictribegraph.activities.NFCActivity
import com.arohaka.musictribegraph.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.tbMain.musicToolbar.title = getString(R.string.options)
        setSupportActionBar(mBinding.tbMain.musicToolbar)

        mBinding.cvAccelerometerGraph.setOnClickListener {
            startActivity(Intent(this@MainActivity, GraphActivity::class.java))
        }

        mBinding.cvNfc.setOnClickListener {
            startActivity(Intent(this@MainActivity, NFCActivity::class.java))
        }

    }
}