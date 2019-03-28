package edu.bluejack181.thedoctorcheckups


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_search_forum.*
import kotlinx.android.synthetic.main.fragment_search_forum.view.*
import java.util.*
import kotlin.concurrent.schedule


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SearchForumFragment : Fragment() {
    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseRefForum: DatabaseReference
    private lateinit var databaseRefUser: DatabaseReference

    private lateinit var curr_view: View
    private lateinit var txt_search: EditText
    private var list_forum: ArrayList<Forum> = ArrayList()
    private var list_user: ArrayList<User> = ArrayList()
    private var list_user_id: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        curr_view = inflater.inflate(R.layout.fragment_search_forum, container, false)
        var userId = mAuth.currentUser!!.uid
        databaseRefForum = mDatabase.getReference("forum")
        databaseRefUser = mDatabase.getReference("users")

        txt_search = curr_view.txt_search
        curr_view.btn_search.setOnClickListener {
            searchThread(txt_search.text.toString())
        }

        return curr_view
    }

    private fun searchThread(searchKey: String) {
        databaseRefForum.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                list_forum.clear()
                for (snapshot in p0.children) {
                    var element = snapshot.getValue(Forum::class.java)!!
                    if(element.title.contains(searchKey) || element.question.contains(searchKey) || element.tags.contains(searchKey))
                        list_forum.add(element)
                }

                Timer("User", true).schedule(3000){
                    getUsers()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun getUsers(){
        databaseRefUser.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children){
                    var element = snapshot.getValue(User::class.java)!!
                    var elementId = snapshot.key!!
                    list_user.add(element)
                    list_user_id.add(elementId)
                }

                changeUserIdToUserName()
                showForum()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun showForum(){
//        Toast.makeText(curr_view.context.applicationContext, list_forum.get(0).tags.size, Toast.LENGTH_SHORT).show()
//        Log.d("LogLog", list_forum.get(0).tags.size.toString())
        var adapterForum = ForumAdapter(list_forum, curr_view.context)
        list_forums.layoutManager = LinearLayoutManager(curr_view.context)
        list_forums.adapter = adapterForum
    }

    fun changeUserIdToUserName(){
        for (count in 0 until list_forum.size){
            var forum = list_forum.get(count)
            for (i in 0 until list_user.size){
                var userId = list_user_id.get(i)
                if(forum.userId.equals(userId)){
                    var user = list_user.get(i)
                    forum.userId = user.name
                    list_forum.set(count, forum)
                    break
                }
            }
        }
    }
}
