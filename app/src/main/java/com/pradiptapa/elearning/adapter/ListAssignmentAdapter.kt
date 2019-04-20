package com.pradiptapa.elearning.adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pradiptapa.elearning.R
import com.pradiptapa.elearning.model.Assignment
import com.pradiptapa.elearning.model.Student
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_assignment.view.*
import kotlinx.android.synthetic.main.item_student.view.*

class ListAssignmentAdapter(options: FirestoreRecyclerOptions<Assignment>) : FirestoreRecyclerAdapter<Assignment, ListAssignmentAdapter.ViewHolder>(options) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Assignment) {
        holder.bind(model)
    }

    private val TAG: String = "Adapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false));
    }

    override fun onDataChanged() {
        super.onDataChanged()
        Log.v(TAG, "onDataChanged")
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        fun bind(model: Assignment) {
            itemView.tv_ass_title.text = model.assTitle
            itemView.tv_ass_sent.text = model.assSent
            itemView.tv_ass_deadline.text = model.assDeadline
            itemView.tv_ass_by.text = model.assBy
        }

    }
}