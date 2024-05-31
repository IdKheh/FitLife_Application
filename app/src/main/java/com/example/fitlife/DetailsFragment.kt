package com.example.fitlife

import CountdownTimerFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailsFragment : Fragment(R.layout.fragment_details) {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var productList: ArrayList<Product>
    private lateinit var fieldText : TextView
    companion object {
        fun newInstance(name: String, description: String, amount: Int, series: String, photo: String, type: String): Fragment {
            val fragment = DetailsFragment()
            val bundle = Bundle()
            bundle.putString("name", name)
            bundle.putString("description", description)
            bundle.putInt("amount", amount)
            bundle.putString("series", series)
            bundle.putString("photo", photo)
            bundle.putString("type", type)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fieldText=view.findViewById(R.id.something)

        val name = arguments?.getString("name")
        val description = arguments?.getString("description")
        val amount = arguments?.getInt("amount")
        val series = arguments?.getString("series")
        val photo = arguments?.getString("photo")
        val type = arguments?.getString("type")

        context?.let { Glide.with(it).load(photo).into(view.findViewById(R.id.imageDetails)) }
        if(type=="recipe"){
            firebaseRef = FirebaseDatabase.getInstance().getReference("Recipe/${series}/Products")
            productList = arrayListOf()
            fetchData()
        }

        val iconL = view.findViewById<ImageView>(R.id.iconL)
        iconL.setOnClickListener{
            if (type == "exercise") {
                changeFragment(ExerciseFragment())
            }
            else {
                changeFragment(RecipeFragment())
            }
        }


        view.findViewById<TextView>(R.id.place).text = name
        view.findViewById<TextView>(R.id.description).text = description
        if(type == "exercise"){
            fieldText.text = "Liczba powtórzeń: ${amount} \nSerie: ${series}"
            view.findViewById<FloatingActionButton>(R.id.fabStoper).setOnClickListener {
                changeFragmentStoper(CountdownTimerFragment())
            }
        }
        else{
            fieldText.text = "Wartość energetyczna: ${amount} kcal\n\nSkładniki:\n\n"
            view.findViewById<FloatingActionButton>(R.id.fabStoper).visibility = View.INVISIBLE
        }
    }

    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    fun changeFragmentStoper(fragment: Fragment){

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container3, fragment)
        fragmentTransaction.commit()
    }
    private fun fetchData(){
        firebaseRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot){

                productList.clear()
                if (snapshot.exists()){
                    for (exerciseSnap in snapshot.children){
                        val exe=exerciseSnap.getValue(Product::class.java)
                        productList.add(exe!!)
                        var some = fieldText.text
                        fieldText.text = "${some}${exe.name.toString()} ${exe.amount.toString()}\n"
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"error ${error}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}