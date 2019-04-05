package edu.bluejack181.thedoctorcheckups

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import java.util.*
import kotlin.concurrent.schedule

class DeleteForumAdapter(val items: ArrayList<Forum>, val context: Context): RecyclerView.Adapter<ForumViewHolder>(){
    private val mDatabase = FirebaseDatabase.getInstance()
    private var databaseRefForum: DatabaseReference = mDatabase.getReference("forum")
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

        p0.btn_delete.setOnClickListener {
            deleteThread(element)
        }
    }

    private fun deleteThread(element: Forum) {
        databaseRefForum.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children){
                    var current = snapshot.getValue(Forum::class.java)!!
                    if(current.title.equals(element.title)) {
                        deleteProcess(snapshot.key)
                        notifyDataSetChanged()
                        items.remove(element)
                        break
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun deleteProcess(key: String?) {
        databaseRefForum.child(key!!).removeValue()
    }

}