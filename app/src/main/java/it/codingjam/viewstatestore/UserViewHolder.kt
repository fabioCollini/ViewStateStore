package it.codingjam.viewstatestore

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserViewHolder(
        view: View,
        private val viewModel: MainViewModel
) : RecyclerView.ViewHolder(view) {

    init {
        view.setOnClickListener {
            viewModel.toggleUserAsync(adapterPosition)
        }
    }

    fun bind(user: User) {
        (itemView as TextView).text = user.name
        itemView.setBackgroundColor(if (user.starred) Color.LTGRAY else 0)
    }
}