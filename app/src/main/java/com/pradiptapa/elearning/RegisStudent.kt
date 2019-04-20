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
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.HashMap

class RegisStudent : AppCompatActivity() {
    var school  = "none"
    var occupation = "true"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_student)

        school = intent.getStringExtra("school").toString()
        occupation = intent.getStringExtra("occupation").toString()
        Log.d(Firebase.TAG,school)


        //to change title of activity
        val actionBar = supportActionBar
        actionBar!!.title = occupation.capitalize()

        tv_regis_student_school.text = "@$school.com"



        btn_regis_student.setOnClickListener {
            pb_regis_student.visibility = View.VISIBLE
            /*val name = et_regis_student_name.text.toString()
            val email = et_regis_student_email.text.toString()+tv_regis_student_school.text.toString()
            val password = et_regis_student_password.text.toString()

            val celass = et_regis_student_class.text.toString()
            val optional = et_regis_student_optional.text.toString()
            val number = et_regis_student_number.text.toString()


            createUserWithNameEmailAndPassword(name,email,password,this,celass,optional,number, school)
            startActivity(Intent(this,MainActivity::class.java))
            */
            attempLogin()

        }


        img_regis_student_photo.setOnClickListener {
            chooseImage()
        }
    }

    fun attempLogin() {

        val name = et_regis_student_name.text.toString()
        val emailg = et_regis_student_email.text.toString()
        val email = et_regis_student_email.text.toString() + tv_regis_student_school.text.toString()
        val password = et_regis_student_password.text.toString()
        val conPass = et_regis_student_password_confirm.text.toString()

        val celass = et_regis_student_class.text.toString()
        val optional = et_regis_student_optional.text.toString()
        val number = et_regis_student_number.text.toString()

        if (name.isEmpty() || emailg.isEmpty() || password.isEmpty() || conPass.isEmpty() || celass.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Silahkan lengkapi terlebih dahulu", LENGTH_SHORT).show()
            pb_regis_student.visibility = View.GONE

        } else
            if (password != conPass) {
                Toast.makeText(this, "Password tidak sama", LENGTH_SHORT).show()
                pb_regis_student.visibility = View.GONE
            }
        else {
                createUserWithNameEmailAndPassword(name,email,password,this,celass,optional,number,school,occupation)
                pb_regis_student.visibility = View.GONE

            }
    }


    fun createNewUserWithNameClassOptionalNumber(
        name: String,
        celass: String,
        optional: String,
        number: String,
        school: String,
        context: Context,
        occupation : String
    ) {
        val uid = Firebase.auth.uid.toString()

        Firebase.getCollection("account")
            .whereEqualTo("celass", celass + optional)
            .whereEqualTo("number", number)
            .whereEqualTo("school", school)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (ds in task.result!!) {
                        val nameExist = ds.get("name")
                        Toast.makeText(context, "Nomor  sudah ada dengan nama $nameExist", LENGTH_SHORT).show()
                    }
                }
                //checking if task contains any payload. if no, then update
                if (task.result!!.size() == 0) {
                    try {

                            val refPhotoProfile =
                                Firebase.storage.child("profile/" + FirebaseAuth.getInstance().currentUser!!.email + ".jpg") //akses path dan filename storage di firebase untuk menyimpan gambar

                            val bitmap = img_regis_student_photo.drawable.toBitmap() //convert imageview ke bitmap
                            Log.d(Firebase.TAG, "bitmap = $bitmap")
                            val baos = ByteArrayOutputStream()

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) //convert bitmap ke bytearray
                            val dataB = baos.toByteArray()
                            Log.d(Firebase.TAG, "dataB = $dataB")

                            val uploadTask =
                                refPhotoProfile.putBytes(dataB) //upload image yang sudah dalam bentuk bytearray ke firebase storage
                            uploadTask.addOnFailureListener(OnFailureListener {
                                // Handle unsuccessful uploads
                                Toast.makeText(this, "Upload Gambar Gagal", LENGTH_SHORT).show()
                            }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                val photoUrl =
                                    taskSnapshot.storage.downloadUrl //setelah selesai upload, ambil url gambar
                                Log.d(Firebase.TAG, "photoUrl = $photoUrl")
                                while (!photoUrl.isSuccessful);
                                val downloadUrl = photoUrl.result
                                Log.d(Firebase.TAG, "downloadUrl = $downloadUrl")


                                val insertStudent = HashMap<String, Any>()
                                insertStudent["name"] = name
                                insertStudent["uid"] = uid
                                insertStudent["celass"] = celass + optional
                                insertStudent["number"] = number
                                insertStudent["school"] = school
                                insertStudent["occupation"] = occupation
                                insertStudent["pProfile"] = downloadUrl.toString()
                                Firebase.getCollection("account").document(uid).set(insertStudent)
                                startActivity(Intent(this, MainActivity::class.java))

                                Log.d(Firebase.TAG, "Error getting documents: ", task.exception)

                            })

                    } catch (e: NullPointerException) {
                        Log.e(Firebase.TAG, "NullPointerException: " + e.message)
                    }

                }
            }
    }

    fun createUserWithNameEmailAndPassword(
        name: String,
        email: String,
        password: String,
        context: Context,
        celass: String,
        optional: String,
        number: String,
        school: String,
        occupation: String
    ) {
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

                    createNewUserWithNameClassOptionalNumber(name, celass, optional, number, school, context,occupation)

                    Log.d(Firebase.TAG, "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Firebase.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Daftar Gagal, email sudah terdaftar.",
                        Toast.LENGTH_SHORT
                    ).show()
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
                img_regis_student_photo.setImageBitmap(myBitmap) //set imageview dengan gambar yang dipilih
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}