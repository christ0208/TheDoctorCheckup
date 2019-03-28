package edu.bluejack181.thedoctorcheckups


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_create_forum.*
import kotlinx.android.synthetic.main.fragment_create_forum.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
    private var databaseRefTags: DatabaseReference? = null
    private var list_tags: ArrayList<String> = ArrayList()
    private var list_confirmed_tags: ArrayList<String> = ArrayList()

    private lateinit var curr_view: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        curr_view = inflater.inflate(R.layout.fragment_create_forum, container, false)
        databaseRefForum = mDatabase.getReference("forum")
        databaseRefTags = mDatabase.getReference("tags")
        var insertBtn: Button = curr_view.findViewById(R.id.btn_insert)
        insertBtn.setOnClickListener {
            insertForum(curr_view)
        }
        getTags()

        return curr_view
    }

    private fun getTags() {
        databaseRefTags!!.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children){
                    var element = snapshot.getValue(ForumTag::class.java)!!
                    if(!list_tags.contains(element.name)) list_tags.add(element.name)
                }

                setAdapter()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun setAdapter() {
        val adapterSearch = ArrayAdapter<String>(curr_view.context, android.R.layout.simple_dropdown_item_1line, list_tags)
        val adapterTags = ConfirmSymptomsAdapter(list_confirmed_tags, curr_view.context)

        txt_tags.threshold = 1
        txt_tags.setAdapter(adapterSearch)
        txt_tags.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            list_confirmed_tags.add(selectedItem.toString())
            adapterTags.notifyDataSetChanged()
        }

        txt_tags.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length > 0) {
                    var lastChar = s.toString().substring(s!!.length - 1)
                    if (lastChar.equals(" ")) {
                        list_confirmed_tags.add(s.toString().substring(0, s!!.length - 1))
                        txt_tags.setText("")
//                        Toast.makeText(curr_view.context, s.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        btn_reset_tags.setOnClickListener {
            list_confirmed_tags.clear()
            adapterTags.notifyDataSetChanged()
        }

        rv_tags.layoutManager = LinearLayoutManager(curr_view.context)
        rv_tags.adapter = adapterTags
    }

    fun insertForum(view: View){
        var title:String = view.txt_title.text.toString()
        var question: String = view.txt_question.text.toString()
        var userId: String = mAuth.currentUser!!.uid

        var simpleDate = SimpleDateFormat("dd-MM-yyyy")
        var dateClass = Date()

        var dateIn: String = simpleDate.format(dateClass)

        var insertedData = ArrayList<String>()
        var insertedForumTag = ArrayList<String>()
        for (tag in list_tags){
            if(!tag.equals("")) insertedData.add(tag)
        }
        for (tag in list_confirmed_tags){
            if(!insertedData.contains(tag) && !tag.equals("")) {
                insertedData.add(tag)
                insertedForumTag.add(tag)
            }
        }

//        var arr_insertedData: Array<String?> = arrayOfNulls<String>(insertedData.size)
//        insertedData.toArray(arr_insertedData)
//        Arrays.toString(arr_insertedData)

        var newForum = databaseRefForum!!.push()
        newForum.setValue(Forum(title, question, userId, dateIn, insertedForumTag))

        databaseRefTags!!.removeValue()
        for (tag in insertedData){
            databaseRefTags!!.push().setValue(ForumTag(tag!!))
        }

        Toast.makeText(curr_view.context, "Success insert forum", Toast.LENGTH_SHORT).show()
        startActivity(Intent(curr_view.context, ForumActivity::class.java))
    }
}
