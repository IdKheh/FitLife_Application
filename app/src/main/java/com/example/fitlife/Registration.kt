package com.example.fitlife

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Registration : Fragment(R.layout.fragment_registration) {
    private lateinit var firebaseRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")

        view.findViewById<Button>(R.id.register1).setOnClickListener {
            addData(view)
        }
        view.findViewById<ImageView>(R.id.iconL).setOnClickListener {
            val manager = (view.context as FragmentActivity).supportFragmentManager
            val fragmentTransaction = manager.beginTransaction()

            fragmentTransaction.replace(R.id.container, Login())
            fragmentTransaction.commit()
        }
    }
    fun addData(view: View){
        val name = view.findViewById<EditText>(R.id.FirstName)
        val surname = view.findViewById<EditText>(R.id.LastName)
        val email = view.findViewById<EditText>(R.id.emailRegister)
        val password = view.findViewById<EditText>(R.id.passwordRegister)
        val age = view.findViewById<EditText>(R.id.AgeRegister)
        val weight = view.findViewById<EditText>(R.id.weightRegister)
        val height = view.findViewById<EditText>(R.id.heightRegister)

        if(name.text.isEmpty() || surname.text.isEmpty() || email.text.isEmpty() ||
            password.text.isEmpty() || age.text.isEmpty() || weight.text.isEmpty() ||
            height.text.isEmpty()){
            Toast.makeText(context,"Podaj dane!",Toast.LENGTH_SHORT).show()
        }
        else{
            val userId = firebaseRef.push().key!!
            val ageVal = Integer.parseInt(age.getText().toString())
            val weightVal = Integer.parseInt(weight.getText().toString())
            val heightVal = Integer.parseInt(height.getText().toString())
            val users = User(userId,name.text.toString(),surname.text.toString(),email.text.toString(),password.text.toString(),ageVal,weightVal,heightVal)
            firebaseRef.child(userId).setValue(users)
                .addOnCompleteListener{
                    Toast.makeText(context,"Sukces!",Toast.LENGTH_SHORT).show()
                    changeFragment(Login())
                }
                .addOnFailureListener{
                    Toast.makeText(context,"error ${it.message}",Toast.LENGTH_SHORT).show()
                }
        }
    }
    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }
}