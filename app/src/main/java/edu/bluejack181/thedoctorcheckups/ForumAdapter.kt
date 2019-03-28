package edu.bluejack181.thedoctorcheckups

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        var title = element.title
        if(title.indexOf(" [Recommend]") != -1){
            title = title.substring(0, element.title.indexOf(" [Recommend]"))
        }

        p0.title.text = title
        p0.date.text = element.dateIn
        p0.user.text = element.userId

        var list_tags = element.tags
        var adapter = TagsAdapter(list_tags, context)
        p0.view_tags.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        p0.view_tags.adapter = adapter

        if(!element.title.contains("[Recommend]")){
            p0.recommended.visibility = View.INVISIBLE
        }

//        Toast.makeText(context, list_tags.size, Toast.LENGTH_SHORT).show()

        p0.area.setOnClickListener {
            getDetail()
        }
    }

    fun getDetail(){
        Toast.makeText(context, "Detail of forum", Toast.LENGTH_SHORT).show()
    }
}

class ForumViewHolder(view: View): RecyclerView.ViewHolder(view){
    var area = view.item_forum
    var title = view.lbl_title
    var user = view.lbl_user
    var date = view.lbl_date
    var view_tags = view.rv_tags
    var recommended = view.recommended_view
}
