package com.pradiptapa.elearning.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.pradiptapa.elearning.R
import com.pradiptapa.elearning.firebase.Firebase
import com.pradiptapa.elearning.model.Student

import kotlinx.android.synthetic.main.layout_recyclerview.*

class ListAssignment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.layout_recyclerview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // getDreamListWithSchoolIs(school)

    }

}