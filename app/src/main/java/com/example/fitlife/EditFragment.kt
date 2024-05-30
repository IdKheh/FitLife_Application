package com.example.fitlife

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class EditFragment : Fragment(R.layout.fragment_edit) {
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: DBLocalHelper
    private lateinit var userId: String

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DBLocalHelper(requireContext(), null)
        firebaseRef = FirebaseDatabase.getInstance().getReference("users")
        sharedPreferences = requireActivity().getSharedPreferences("com.example.fitlife", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("IDuser", "") ?: ""

        nameEditText = view.findViewById(R.id.FirstNameEdit)
        surnameEditText = view.findViewById(R.id.LastNameEdit)
        emailEditText = view.findViewById(R.id.emailEdit)
        passwordEditText = view.findViewById(R.id.passwordEdit)
        ageEditText = view.findViewById(R.id.AgeEdit)
        weightEditText = view.findViewById(R.id.weightEdit)
        heightEditText = view.findViewById(R.id.heightEdit)


        view.findViewById<ImageView>(R.id.iconL).setOnClickListener {
            changeFragment(Information())
        }

        view.findViewById<Button>(R.id.edit).setOnClickListener {
            editData()
        }

        fetchUserData()
    }

    private fun fetchUserData() {
        firebaseRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    nameEditText.setText(it.name)
                    surnameEditText.setText(it.surname)
                    emailEditText.setText(it.email)
                    passwordEditText.setText(it.password)
                    ageEditText.setText(it.age.toString())
                    weightEditText.setText(it.weight.toString())
                    heightEditText.setText(it.height.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun editData() {
        val name = nameEditText.text.toString()
        val surname = surnameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val age = ageEditText.text.toString()
        val weight = weightEditText.text.toString()
        val height = heightEditText.text.toString()

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
            Toast.makeText(context, "Podaj dane!", Toast.LENGTH_SHORT).show()
        } else {
            val ageVal = age.toInt()
            val weightVal = weight.toInt()
            val heightVal = height.toInt()
            val bmiVal = BMI(heightVal, weightVal)
            val user = User(userId, name, surname, email, password, ageVal, weightVal, heightVal)

            firebaseRef.child(userId).setValue(user)
                .addOnCompleteListener {
                    Toast.makeText(context, "Sukces!", Toast.LENGTH_SHORT).show()
                    changeFragment(Information())
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            dbHelper.addRecord(ageVal, heightVal, weightVal, bmiVal)
        }
    }
    private fun BMI(height: Int, weight: Int): Float {
        return weight / ((height / 100.0) * (height / 100.0)).toFloat()
    }
    private fun changeFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
