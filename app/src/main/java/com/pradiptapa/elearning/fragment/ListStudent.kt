package com.pradiptapa.elearning.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pradiptapa.elearning.R
import com.pradiptapa.elearning.firebase.Firebase
import com.pradiptapa.elearning.model.Student
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_student.view.*
import kotlinx.android.synthetic.main.layout_recyclerview.*


class ListStudent : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_recyclerview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getStudentWhereTheClassIs()




    }
    class CategoryHolder(val customView: View, var category: Student? = null) : RecyclerView.ViewHolder(customView) {

        fun bind(category: Student) {

            customView.tv_student_name.text =     category.name
            customView.tv_student_class.text =    category.celass
            customView.tv_student_number.text =   category.number

            Picasso.get().load(category.pProfile).into(itemView.iv_student_photo)
        }
    }

    fun getStudentWhereTheClassIs(){
        rv_main.setHasFixedSize(true)
        rv_main.layoutManager = LinearLayoutManager(activity)

        val query =
            Firebase
                .getCollection("class")
                .whereEqualTo(Firebase.auth.currentUser!!.uid, "true")


        val options = FirestoreRecyclerOptions.Builder<Student>()
            .setQuery(query, Student::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object : FirestoreRecyclerAdapter<Student, CategoryHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
                return CategoryHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_student, parent, false))
            }

            override fun onBindViewHolder(holder: CategoryHolder, position: Int, model: Student) {
                holder.bind(model)
            }

            override fun onDataChanged() {
                // Called each time there is a new data snapshot. You may want to use this method
                // to hide a loading spinner or check for the "no documents" state and update your UI.
                // ...

                if(itemCount == 0){
                    rv_main.visibility = View.GONE
                    tv_rv_state.visibility = View.VISIBLE
                    Toast.makeText(activity,itemCount.toString(),Toast.LENGTH_SHORT).show()

                } else{
                    rv_main.visibility = View.VISIBLE
                    tv_rv_state.visibility = View.GONE
                    Toast.makeText(activity,itemCount.toString(),Toast.LENGTH_SHORT).show()

                }


            }
        }

        rv_main.adapter = adapter

    }


}