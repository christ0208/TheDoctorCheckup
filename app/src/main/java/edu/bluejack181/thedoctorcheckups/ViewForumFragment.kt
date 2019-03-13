package edu.bluejack181.thedoctorcheckups


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_health_history.*
import kotlinx.android.synthetic.main.fragment_create_forum.view.*
import kotlinx.android.synthetic.main.fragment_view_forum.*
import kotlinx.android.synthetic.main.fragment_view_forum.view.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.random.Random


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ViewForumFragment : Fragment() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseRefForum: DatabaseReference
    private lateinit var databaseRefUser: DatabaseReference

    private var list_forum: ArrayList<Forum> = ArrayList()
    private var list_showed_forum: ArrayList<Forum> = ArrayList()
    private var list_user: ArrayList<User> = ArrayList()
    private var list_user_id: ArrayList<String> = ArrayList()

    private lateinit var curr_view: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        curr_view = inflater.inflate(R.layout.fragment_view_forum, container, false)
        var userId = mAuth.currentUser!!.uid
        databaseRefForum = mDatabase.getReference("forum")
        databaseRefUser = mDatabase.getReference("users")

        databaseRefForum.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children){
                    var element = snapshot.getValue(Forum::class.java)!!
                    if(!element.userId.equals(userId, true)){
                        list_forum.add(element)
                    }
                }

//                getRandomForum()
                getUsers()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        return curr_view
    }

    fun getRandomForum(){
        var selected_forum: ArrayList<Int> = ArrayList()
        var limit = selected_forum.size

        if(limit > 5) limit = 5

        for (i in 0 until limit){
            var isFound = false
            while(!isFound){
                var number = Random.nextInt(list_forum.size)
                for (selected in selected_forum){
                    if(number != selected) {
                        isFound = true
                    }
                }
            }
        }

        for (selected in selected_forum){
            list_showed_forum.add(list_forum.get(selected))
        }
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
