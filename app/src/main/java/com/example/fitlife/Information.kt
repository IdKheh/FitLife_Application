package com.example.fitlife

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.round


class Information : Fragment(R.layout.fragment_information) {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var dbHelper: DBLocalHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DBLocalHelper(requireContext(), null)

        sharedPreferences = requireActivity().getSharedPreferences("com.example.fitlife", Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("user","")
        if(user==""){
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).visibility= View.INVISIBLE
            changeFragment(Login())
        }
        else{
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).visibility= View.VISIBLE
            view.findViewById<ImageView>(R.id.iconL).setOnClickListener {
                sharedPreferences.edit().putString("user", "").apply()
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav).visibility= View.INVISIBLE
                dbHelper.dropData()
                changeFragment(Login())
            }
            view.findViewById<ImageView>(R.id.iconR).setOnClickListener {
                changeFragment(EditFragment())
            }
            firebaseRef = FirebaseDatabase.getInstance().getReference("users")
            if (user != null) {
                fetchData(view, user)
            }
            displayHistory(view)
        }
    }
    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }
    fun bmi(view: View,height:Int, weight:Int){
        val valueBmi = weight / ((height / 100.0) * (height / 100.0))
        view.findViewById<TextView>(R.id.bmi).text = round(valueBmi.toFloat()).toString()

        val textMessage = view.findViewById<TextView>(R.id.message)

        if(valueBmi<16) textMessage.text ="Wygłodzenie"
        else if(16<=valueBmi && valueBmi<18.5) textMessage.text ="Niedowaga"
        else if(18.5<=valueBmi && valueBmi<24.9) textMessage.text ="Waga prawidłowa"
        else if(25<=valueBmi && valueBmi<29.9) textMessage.text ="Nadwaga"
        else if(30<=valueBmi) textMessage.text ="Otyłość"
    }
    fun display(view: View, user: User){
        view.findViewById<TextView>(R.id.helloUser).text="Witaj, ${user.name} ${user.surname}"
        view.findViewById<TextView>(R.id.age).text = "${user.age}"
        view.findViewById<TextView>(R.id.weight).text= "${user.weight} kg"
        view.findViewById<TextView>(R.id.height).text ="${user.height} cm"
        user.height?.let { user.weight?.let { it1 -> bmi(view,it.toInt(), it1.toInt()) } }
    }
    @SuppressLint("SimpleDateFormat")
    private fun displayHistory(view: View) {
        val historyTextView = view.findViewById<TextView>(R.id.history)
        val cursor: Cursor? = dbHelper.getAllRecords()
        val historyStringBuilder = StringBuilder()

        cursor?.let {
            while (cursor.moveToNext()) {
                val height = cursor.getInt(cursor.getColumnIndexOrThrow(DBLocalHelper.HEIGHT_COL))
                val weight = cursor.getInt(cursor.getColumnIndexOrThrow(DBLocalHelper.WEIGHT_COL))
                val bmi = round( cursor.getFloat(cursor.getColumnIndexOrThrow(DBLocalHelper.BMI_COL)))
                val data = cursor.getLong(cursor.getColumnIndexOrThrow(DBLocalHelper.DATA_COL))
                val dateFormat = SimpleDateFormat("dd:MM:yyyy")
                val date = dateFormat.format(Date(data))
                historyStringBuilder.append("Data: $date, Wzrost: $height cm, Waga: $weight kg, BMI: $bmi\n")
            }
        }

        historyTextView.text = historyStringBuilder.toString()
        cursor?.close()
    }
    private fun fetchData(view: View, userEmail:String){
        firebaseRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (user in snapshot.children) {
                        val userData = user.getValue(User::class.java)
                        if (userData != null && userData.email == userEmail) {
                            display(view, userData)
                            sharedPreferences.edit().putString("IDuser", userData.id).apply()
                        } else {
                            Toast.makeText(context, "Błędny email lub hasło!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Nie znaleziono użytkownika!", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"error ${error}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}