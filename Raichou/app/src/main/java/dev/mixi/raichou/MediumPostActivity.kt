package dev.mixi.raichou

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mixi.raichou.databinding.ActivityMediumPostBinding

class MediumPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medium_post)
        val binding = DataBindingUtil.setContentView<ActivityMediumPostBinding>(this, R.layout.activity_medium_post)
        binding.list.adapter = MyAdapter()
        binding.list.layoutManager = GridLayoutManager(this, 3)
        binding.list.setHasFixedSize(true)
    }

    companion object {
        fun createIntent(context: Context) = Intent(context, MediumPostActivity::class.java)
    }
}

class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class MyAdapter : RecyclerView.Adapter<MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medium, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}