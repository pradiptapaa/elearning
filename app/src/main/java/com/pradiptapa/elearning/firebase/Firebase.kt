package com.pradiptapa.elearning.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage


class Firebase{
    companion object {
        const val TAG = "FirebaseTest"

        val user = "user"
        val school = "school"
        val student = "student"
        val teacher = "teacher"
        val voucher = "voucher"
        /////////////////////////////////
        val auth = FirebaseAuth.getInstance()!!
        val currUser = auth.currentUser

        val fTeacher = FirebaseFirestore.getInstance().collection(teacher)
        val fStudent = FirebaseFirestore.getInstance().collection(student)
        val fUser = FirebaseFirestore.getInstance().collection(user)
        val fVoucher = FirebaseFirestore.getInstance().collection(voucher)
        val storage = FirebaseStorage.getInstance().reference

        val refEmail = FirebaseFirestore.getInstance().collection("account").document(FirebaseAuth.getInstance().currentUser?.email.toString())


        fun getCollection(collection : String) : CollectionReference{
            return FirebaseFirestore.getInstance().collection(collection)
        }

        fun setHMapInCollection( hashmap: HashMap<String,Any>, collection : String){
            FirebaseFirestore.getInstance().collection(collection).document().set(hashmap)
        }

        fun getDocument(collection : String, document : String){
            FirebaseFirestore.getInstance().collection(collection).document(document)
        }









        /**From this point, it's fail code*/
        fun createUserWithNameEmailAndPassword(name: String, email: String, password: String, context: Context) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build()

                        auth.currentUser!!.updateProfile(profileUpdates)
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            context, "Akun berhasil terdaftar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // ...
                }
        }

        fun getUserRefWhereTheSchoolIs(school: String): Query{
            val query = fUser.whereEqualTo("school",school)

            return query
        }

        /*fun getSchoolWhereTheEmailIs(email: String): String{
            val query = FirebaseFirestore.getInstance().collection("account").document(email)
            val document = Tasks.await(query.get())

            var schoolString = "0"

            if(document.exists()){
                schoolString = document.get(school).toString()


            } else null

            return schoolString

        }
        */

    }
}
