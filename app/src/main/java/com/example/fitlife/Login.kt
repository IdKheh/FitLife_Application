package com.example.fitlife

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : Fragment(R.layout.fragment_login) {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")

        view.findViewById<Button>(R.id.login).setOnClickListener {
            checkData(view)
        }
        view.findViewById<Button>(R.id.register).setOnClickListener {
            changeFragment(Registration())
        }
        val pizza = view.findViewById<ImageView>(R.id.pizza)
        animatePizza(pizza)
    }

    private fun checkData(view: View) {
        val email = view.findViewById<EditText>(R.id.emailLogin).text.toString()
        val password = view.findViewById<EditText>(R.id.passwordLogin).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Podaj dane!", Toast.LENGTH_SHORT).show()
        } else {
            firebaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (user in snapshot.children) {
                            val userData = user.getValue(User::class.java)
                            if (userData != null && userData.email == email && userData.password == password) {
                                sharedPreferences = requireActivity().getSharedPreferences("com.example.fitlife", Context.MODE_PRIVATE)
                                sharedPreferences.edit().putString("user", email).apply()
                                changeFragment(Information())
                            } else {
                                Toast.makeText(context, "Błędny email lub hasło!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Nie znaleziono użytkownika!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Błąd bazy danych: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    private fun animatePizza(view: View) {
        val pizzaAnimation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).apply {
            duration = 5000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        pizzaAnimation.start()
    }
}
