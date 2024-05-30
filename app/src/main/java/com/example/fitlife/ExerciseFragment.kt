package com.example.fitlife

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExerciseFragment : Fragment(R.layout.fragment_exercise) {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var exerciseList: ArrayList<Exercises>
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerViewExercise)
        firebaseRef = FirebaseDatabase.getInstance().getReference("exercise")
        exerciseList = arrayListOf()
        fetchData()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }
        view.findViewById<ImageView>(R.id.iconL).setOnClickListener {
            changeFragment(Information())
        }
    }
    private fun fetchData(){
        firebaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                exerciseList.clear()
                if (snapshot.exists()){
                    for (exerciseSnap in snapshot.children){
                        val exe=exerciseSnap.getValue(Exercises::class.java)
                        exerciseList.add(exe!!)
                    }
                }
                recyclerView.adapter = context?.let { ExerciseAdapter(it, exerciseList) }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"error ${error}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

}