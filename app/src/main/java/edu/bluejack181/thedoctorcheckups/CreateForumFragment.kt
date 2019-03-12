package edu.bluejack181.thedoctorcheckups


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_create_forum.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CreateForumFragment : Fragment() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mDatabase = FirebaseDatabase.getInstance()
    private var databaseRefForum: DatabaseReference? = null

    private lateinit var curr_view: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        curr_view = inflater.inflate(R.layout.fragment_create_forum, container, false)
        databaseRefForum = mDatabase.getReference("forum")
        var insertBtn: Button = curr_view.findViewById(R.id.btn_insert)
        insertBtn.setOnClickListener {
            insertForum(curr_view)
        }
        return curr_view
    }

    fun insertForum(view: View){
        var title:String = view.txt_title.text.toString()
        var question: String = view.txt_question.text.toString()
        var userId: String = mAuth.currentUser!!.uid

        var newForum = databaseRefForum!!.push()
        newForum.setValue(Forum(title, question, userId))

        Toast.makeText(curr_view.context, "Success insert forum", Toast.LENGTH_SHORT).show()
    }
}
