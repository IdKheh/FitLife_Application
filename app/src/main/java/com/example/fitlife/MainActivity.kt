package com.example.fitlife

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fitlife.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val home = Information()
        val exercise =  ExerciseFragment()
        val recipe = RecipeFragment()

        findViewById<BottomNavigationView>(R.id.bottomNav).setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    changeFragment(home)
                    true
                }
                R.id.exercise -> {
                    changeFragment(exercise)
                    true
                }
                R.id.recipe -> {
                    changeFragment(recipe)
                    true
                }

                else -> {true}
            }
        }

        sharedPreferences = getSharedPreferences("com.example.fitlife", Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("user","")
        if(user==""){
            findViewById<BottomNavigationView>(R.id.bottomNav).visibility= View.INVISIBLE
        }
        else{
            findViewById<BottomNavigationView>(R.id.bottomNav).visibility= View.VISIBLE
        }

        changeFragment(Information())
    }

    fun changeFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }
}