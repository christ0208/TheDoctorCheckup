package edu.bluejack181.thedoctorcheckups

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.tag_list_item.view.*

class TagsAdapter(val items: ArrayList<String>, val context: Context): RecyclerView.Adapter<TagViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TagViewHolder {
        return TagViewHolder(LayoutInflater.from(context).inflate(R.layout.tag_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: TagViewHolder, p1: Int) {
        var element = items.get(p1)
        p0.name.text = element
    }
}

class TagViewHolder(view: View): RecyclerView.ViewHolder(view){
    var name = view.txt_tag
}