package com.pradiptapa.elearning

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pradiptapa.elearning.adapter.Pager
import com.pradiptapa.elearning.firebase.Firebase
import com.pradiptapa.elearning.fragment.ListStudent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_insert_voucher.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add_student.*
import kotlinx.android.synthetic.main.item_student.view.*
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    private val bundle = Bundle()
    private var school = "none"
    lateinit var name : String
    lateinit var occupation : String
    lateinit var number : String
    lateinit var pProfile : String

    companion object {
        val fragmentKey = "fragmen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getUserNameAndTitle()
        showFragment()
        fab_add_student.setOnClickListener {
            showDialog(this)
        }

        fab_add_task.setOnClickListener {
            val moveToTask = Intent(this,InsertTask::class.java)
            moveToTask.putExtra("school",school)
             startActivity(moveToTask)
        }


    }

    private fun showDialog(context: Context) {
        var dialogs = Dialog(context)
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true)
        dialogs.setContentView(R.layout.dialog_add_student)
        val celass = dialogs.findViewById(R.id.et_add_class) as EditText
        val confirm = dialogs.findViewById(R.id.btn_add_student) as TextView
        confirm.setOnClickListener {
            val kelas = celass.text.toString()
            addStudentWithClass(kelas)
            dialogs.dismiss()
        }
        dialogs.show()

    }

    fun addStudentWithClass(celass: String){
        val id = Firebase.auth.currentUser!!.uid

        Firebase
            .getCollection("account")
            .whereEqualTo("school",school)
            .whereEqualTo("celass",celass)
            .get()
            .addOnCompleteListener { task ->
            if(task.isSuccessful){
                for(ds in task.result!!){
                    val name = ds.getString("name")
                    val occupation = ds.getString("occupation")
                    val number = ds.getString("number")
                    val teacher = Firebase.auth.currentUser!!.displayName
                    val photo = ds.getString("pProfile")
                    val celass = ds.getString("celass")

                    val insertStudentToClass = HashMap<String, Any>()
                    insertStudentToClass["name"] = name.toString()
                    insertStudentToClass["occupation"] = occupation.toString()
                    insertStudentToClass["number"] = number.toString()
                    insertStudentToClass["teacher"] = teacher.toString()
                    insertStudentToClass["pProfile"] = photo.toString()
                    insertStudentToClass["school"] = school
                    insertStudentToClass["celass"] = celass.toString()
                    insertStudentToClass[id] = "true"
                    Firebase.getCollection("class").document().set(insertStudentToClass)
                    Toast.makeText(this,"Murid berhasil dimasukkan",Toast.LENGTH_SHORT).show()

                }
            }
                //checking if task contains any payload. if no, then update
                if (task.result!!.size() == 0) {
                    try {
                        Toast.makeText(this,"Tidak ada murid dengan kelas $celass di $school",Toast.LENGTH_SHORT).show()
                        Log.d(Firebase.TAG, "Error getting documents: ", task.exception)


                    } catch (e: NullPointerException) {
                        Log.e(Firebase.TAG, "NullPointerException: " + e.message)
                    }

                }

            }

    }

    fun getUserNameAndTitle(){
        Firebase.getCollection("account")
            .whereEqualTo("uid", Firebase.auth.currentUser!!.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (ds in task.result!!) {
                        name        = ds.getString("name").toString()
                        occupation  = ds.getString("occupation").toString()
                        number      = ds.getString("number").toString()
                        pProfile    = ds.getString("pProfile").toString()
                        school = ds.getString("school").toString()
                        if(occupation.equals("student")){
                            tv_main_number.visibility = View.VISIBLE
                            tv_main_number.text = number
                            tv_main_name.text = name
                            tv_main_title.text = occupation
                        } else{
                            tv_main_number.visibility = View.GONE
                            tv_main_name.text = name
                            tv_main_title.text = occupation
                        }
                        Picasso.get().load(pProfile).into(img_main_photo)


                    }
                }
            }
    }

    ////////////////////////////////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.option_logout -> {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            true

        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
    ////////////////////////////////////////////////////////

    fun showFragment() {

        val f1 = ListStudent()
        f1.arguments = bundle

        val f2 = ListStudent()
        f2.arguments = bundle



        val fragmentAdapter = Pager(supportFragmentManager)

        fragmentAdapter.populateFragment(f1, "Murid")
        fragmentAdapter.populateFragment(f2, "Tugas")


        vp_main.adapter = fragmentAdapter
        tl_main_activity.setupWithViewPager(vp_main)


    }


}