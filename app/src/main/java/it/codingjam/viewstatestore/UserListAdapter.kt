package it.codingjam.viewstatestore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class UserListAdapter(
        private val viewModel: MainViewModel
) : RecyclerView.Adapter<UserViewHolder>() {

    var list by Delegates.observable(emptyList<User>()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.user_row, parent, false)
        return UserViewHolder(view, viewModel)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(list[position])
    }
}