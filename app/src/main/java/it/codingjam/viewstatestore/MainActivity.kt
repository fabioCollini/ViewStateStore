package it.codingjam.viewstatestore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val viewModel: MainViewModel = ViewModelProviders.of(this, MainViewModelFactory).get()

    recycler.layoutManager = LinearLayoutManager(this)
    val adapter = UserListAdapter(viewModel)
    recycler.adapter = adapter

    retry.setOnClickListener {
        viewModel.loadData()
    }

    viewModel.store.observe(this) {
        loading.isVisible = it.loading
        error.isVisible = it.error != null
        retry.isVisible = it.error != null
        recycler.isVisible = !it.loading && it.error == null
        adapter.list = it.users
    }
}
}
