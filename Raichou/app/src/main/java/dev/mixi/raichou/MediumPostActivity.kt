package dev.mixi.raichou

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MediumPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medium_post)
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, MediumPostActivity::class.java)
    }
}
