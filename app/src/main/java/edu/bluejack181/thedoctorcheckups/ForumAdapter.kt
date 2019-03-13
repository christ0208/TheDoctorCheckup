package edu.bluejack181.thedoctorcheckups

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.forum_list_item.view.*

class ForumAdapter(val items: ArrayList<Forum>, val context: Context): RecyclerView.Adapter<ForumViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ForumViewHolder {
        return ForumViewHolder(LayoutInflater.from(context).inflate(R.layout.forum_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ForumViewHolder, p1: Int) {
        val element = items.get(p1)
        p0.title.text = element.title
        p0.date.text = element.dateIn
        p0.user.text = element.userId

        p0.area.setOnClickListener {
            getDetail()
        }
    }

    fun getDetail(){

    }
}

class ForumViewHolder(view: View): RecyclerView.ViewHolder(view){
    var area = view.item_forum
    var title = view.lbl_title
    var user = view.lbl_user
    var date = view.lbl_date
}
