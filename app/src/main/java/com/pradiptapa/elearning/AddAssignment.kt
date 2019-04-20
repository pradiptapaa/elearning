package com.pradiptapa.elearning

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.pradiptapa.elearning.firebase.Firebase
import kotlinx.android.synthetic.main.activity_add_student.*
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.activity_insert_voucher.*
import java.util.HashMap

class AddAssignment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_add_task)

        btn_voucher.setOnClickListener {




         //   createNewUserWithNameClassOptionalNumber(name,celass,optional,number, school)


        }
    }

    //////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_task_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.task_send -> {

            sendTask()
            startActivity(Intent(this, MainActivity::class.java))
            true

        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
    /////

    fun sendTask(){

        val title = et_task_title.text.toString()
        val toClass = et_task_class.text.toString()
        val content = et_task_content.text.toString()

        val insertTask = HashMap<String, Any>()
        insertTask["title"] = title
        insertTask["toClass"] = toClass
        insertTask["content"] = content

        Firebase.setHMapInCollection(insertTask,"task")

    }


}