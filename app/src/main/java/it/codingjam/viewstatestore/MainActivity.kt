package it.codingjam.viewstatestore

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
            loading.visible = it.loading
            error.visible = it.error != null
            retry.visible = it.error != null
            recycler.visible = !it.loading && it.error == null
            adapter.list = it.users
        }
    }
}

var View.visible
    get() = visibility == View.VISIBLE
    set(value) = if (value) visibility = View.VISIBLE else visibility = View.GONE