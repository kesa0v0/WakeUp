package com.kesa.wakeup

import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val turnOnBtm = findViewById<Button>(R.id.recordOn)
        turnOnBtm.setOnClickListener{
            DoAsync {
                onRecord()
            }
        }
    }

    private fun onRecord(){
        var movement = listOf<Int>()
    }
}

class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {    // 비동기
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}