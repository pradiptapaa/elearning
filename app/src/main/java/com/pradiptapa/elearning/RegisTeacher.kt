package com.pradiptapa.elearning

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.UploadTask
import com.pradiptapa.elearning.firebase.Firebase
import kotlinx.android.synthetic.main.activity_add_student.*
import kotlinx.android.synthetic.main.activity_add_teacher.*
import kotlinx.android.synthetic.main.activity_insert_voucher.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RegisTeacher : AppCompatActivity() {
    var school  = "none"
    var occupation = "true"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_teacher)

        school = intent.getStringExtra("school").toString()
        occupation = intent.getStringExtra("occupation").toString()
        Log.d(Firebase.TAG,school)


        //to change title of activity
        val actionBar = supportActionBar
        actionBar!!.title = occupation.capitalize()

        tv_regis_teacher_school.text = "@$school.com"


        btn_regis_teacher.setOnClickListener {
            pb_regis_teacher.visibility = View.VISIBLE
            attemptLogin()
        }

        img_regis_teacher_photo.setOnClickListener {
            chooseImage()
        }
    }

    fun attemptLogin(){

        val name = et_regis_teacher_name.text.toString()
        val email = et_regis_teacher_email.text.toString()+tv_regis_teacher_school.text.toString()
        val rawEmail = et_regis_teacher_email.text.toString()
        val password = et_regis_teacher_password.text.toString()
        val passCon = et_regis_teacher_password_confirm.text.toString()

        val education = et_regis_teacher_education.text.toString()
        val number = et_regis_teacher_number.text.toString()

        if(name.isEmpty() || rawEmail.isEmpty() || password.isEmpty() || passCon.isEmpty() || education.isEmpty() || number.isEmpty()){
            Toast.makeText(this, "Silahkan lengkapi terlebih dahulu", Toast.LENGTH_SHORT).show()
            pb_regis_teacher.visibility = View.GONE
        } else
            if (password != passCon){
                Toast.makeText(this,"Password tidak sama",Toast.LENGTH_SHORT).show()
                pb_regis_teacher.visibility = View.GONE

            }
        else{
                createUserWithNameEmailAndPassword(name,email,password,education,number,school,occupation,this)
            }


    }

    fun createNewUserWithNameClassOptionalNumber(name: String, education: String, number: String, school: String, occupation: String, context: Context) {

        val uid = Firebase.auth.uid.toString()
        Firebase.getCollection("account")
            .whereEqualTo("number", number)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (ds in task.result!!) {
                        val nameExist = ds.get("name")
                        Toast.makeText(context, "Nomor  sudah ada dengan nama $nameExist", Toast.LENGTH_SHORT).show()
                    }
                }
                //checking if task contains any payload. if no, then update
                if (task.result!!.size() == 0) {
                    try {

                            val refPhotoProfile = Firebase.storage.child("profile/" + FirebaseAuth.getInstance().currentUser!!.email + ".jpg") //akses path dan filename storage di firebase untuk menyimpan gambar

                            val bitmap = img_regis_teacher_photo.drawable.toBitmap() //convert imageview ke bitmap
                            Log.d(Firebase.TAG, "bitmap = $bitmap")
                            val baos = ByteArrayOutputStream()

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) //convert bitmap ke bytearray
                            val dataB = baos.toByteArray()
                            Log.d(Firebase.TAG, "dataB = $dataB")

                            val uploadTask =
                                refPhotoProfile.putBytes(dataB) //upload image yang sudah dalam bentuk bytearray ke firebase storage
                            uploadTask.addOnFailureListener(OnFailureListener {
                                // Handle unsuccessful uploads
                                Toast.makeText(this, "uploadFail", Toast.LENGTH_SHORT).show()
                            }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                val photoUrl =
                                    taskSnapshot.storage.downloadUrl //setelah selesai upload, ambil url gambar
                                Log.d(Firebase.TAG, "photoUrl = $photoUrl")
                                while (!photoUrl.isSuccessful);
                                val downloadUrl = photoUrl.result
                                Log.d(Firebase.TAG, "downloadUrl = $downloadUrl")

                                //push atau insert data ke firebase database

                                val insertTeacher = HashMap<String, Any>()
                                insertTeacher["name"] = name
                                insertTeacher["education"] = education
                                insertTeacher["uid"] = uid
                                insertTeacher["number"] = number
                                insertTeacher["school"] = school
                                insertTeacher["occupation"] = occupation
                                insertTeacher["pProfile"] = downloadUrl.toString()
                                startActivity(Intent(this, MainActivity::class.java))

                                Firebase.getCollection("account").document(uid).set(insertTeacher)

                                Toast.makeText(applicationContext, "Uploaded!", Toast.LENGTH_SHORT).show()
                            })


                            Log.d(Firebase.TAG, "Error getting documents: ", task.exception)


                    } catch (e: NullPointerException) {
                        Log.e(Firebase.TAG, "NullPointerException: " + e.message)
                    }

                }
            }
    }

    fun createUserWithNameEmailAndPassword(name: String, email:String, password:String, education: String, number: String, school : String, occupation : String, context: Context) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build()

                    Firebase.auth.currentUser!!.updateProfile(profileUpdates)
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        context, "Akun berhasil terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()

                    createNewUserWithNameClassOptionalNumber(name, education, number, school, occupation, context)

                    Log.d(Firebase.TAG, "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Firebase.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Daftar Gagal, email sudah terdaftar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    pb_regis_teacher.visibility = View.GONE

                }

                // ...
            }
    }

    fun chooseImage(){
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
            .folderMode(false) // folder mode (false by default)
            .toolbarFolderTitle("Folder") // folder selection title
            .toolbarImageTitle("Tap to select") // image selection title
            .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
            .single() // single mode
            .limit(1) // max images can be selected (99 by default)
            .showCamera(true) // show camera or not (true by default)
            .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
            .enableLog(false) // disabling log
            .start() // start image picker activity with request code

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) { // jika ada data dipilih
            val image = ImagePicker.getFirstImageOrNull(data) //ambil first image
            val imgFile = File(image.path) // dapatkan lokasi gambar yang dipilih
            if (imgFile.exists()) { //jika ditemukan
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath) //convert file ke bitmap
                img_regis_teacher_photo.setImageBitmap(myBitmap) //set imageview dengan gambar yang dipilih
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}