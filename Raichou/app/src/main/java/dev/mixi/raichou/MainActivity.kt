package dev.mixi.raichou

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dev.mixi.raichou.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.nav_fragment_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
}
