package com.pradiptapa.elearning

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.pradiptapa.elearning.firebase.Firebase
import kotlinx.android.synthetic.main.activity_insert_voucher.*

class InsertVoucher : AppCompatActivity(){

    val bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_insert_voucher)

        btn_voucher.setOnClickListener {
            tv_voucher_salah.visibility = View.GONE
            pb_voucher.visibility = View.VISIBLE
            val voucher = et_voucher.text.toString()

            checkIfVoucherExist(voucher)


        }
    }

    fun checkIfVoucherExist(voucher: String){
        Firebase.getCollection("voucher")
            .whereEqualTo("voucher", voucher)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (ds in task.result!!) {
                        val occupation = ds.getString("occupation")
                        val school = ds.getString("school")
                        Log.d(Firebase.TAG, occupation+school)
                        bundle.putString(Firebase.school,school)
                        bundle.putString("occupation",occupation)
                        if(occupation.equals("student")){
                            val intent = Intent(this,RegisStudent::class.java)
                            intent.putExtra("school",school)
                            intent.putExtra("occupation",occupation)
                            startActivity(intent)
                            pb_voucher.visibility = View.GONE
                        }
                        if(occupation.equals("teacher")){
                            val intent = Intent(this,RegisTeacher::class.java)
                            intent.putExtra("school",school)
                            intent.putExtra("occupation",occupation)
                            startActivity(intent)
                            pb_voucher.visibility = View.GONE
                        }
                    }

                }
                //checking if task contains any payload. if no, then update
                if (task.result!!.size() == 0) {
                    try {
                        tv_voucher_salah.visibility = View.VISIBLE
                        Log.d(Firebase.TAG, "Error getting documents: ", task.exception)


                    } catch (e: NullPointerException) {
                        Log.e(Firebase.TAG, "NullPointerException: " + e.message)
                    }

                }
            }
    }






}