package com.pradiptapa.elearning

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.pradiptapa.elearning.adapter.Pager
import com.pradiptapa.elearning.firebase.Firebase
import com.pradiptapa.elearning.fragment.ListStudent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class InsertTask : AppCompatActivity() {

    val PDF : Int = 0
    lateinit var uri : Uri
    lateinit var mStorage : StorageReference
    var isAttached = false
    lateinit var school : String
    lateinit var celass : String





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        school = intent.getStringExtra("school")
        Log.d("intent.getString",school)


    }


    ////////////////////////////////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_task_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.task_send -> {
            celass = et_task_class.text.toString()
            if (et_task_title.text.isEmpty() || et_task_class.text.isEmpty() || et_task_content.text.isEmpty()) {
                Toast.makeText(this, "Tolong isi yang sesuai", Toast.LENGTH_SHORT).show()
            } else {

                Firebase
                    .getCollection("account")
                    .whereEqualTo("school", school)
                    .whereEqualTo("celass", celass)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.result!!.size() != 0 ) {
                            if (isAttached == true) {
                                Toast.makeText(this, "isAttached $isAttached", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(this, "isAttached $isAttached", Toast.LENGTH_SHORT).show()
                            }
                        }else {
                            Toast.makeText(this, "Tidak ada kelas $celass", Toast.LENGTH_SHORT).show()


                        }
                    }
            }
            true

        }
        R.id.task_attach ->{
            val intent = Intent()
            intent.type = "pdf/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
    ////////////////////////////////////////////////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PDF) {
                cv_task_link.visibility = View.VISIBLE
                uri = data!!.data
                val uriString = uri.toString()
                val fileName = uriString.substringAfterLast("/")
                tv_task_link.text = fileName
                isAttached = true

                img_task_x.setOnClickListener {
                    cv_task_link.visibility = View.GONE
                    isAttached = false
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload() {
        var mReference = mStorage.child(uri.lastPathSegment)
        try {
            mReference.putFile(uri).addOnSuccessListener {
                    taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.storage.downloadUrl
                Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

    }



}