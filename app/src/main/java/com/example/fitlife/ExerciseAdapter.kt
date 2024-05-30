package com.example.fitlife

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView

class ExerciseAdapter(private val context: Context,private val exerciseList: List<Exercises>): RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view_exercise, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = "${exerciseList[position].name}"
        val image = "${exerciseList[position].photo}"
        Glide.with(context)
            .load(image)
            .into(holder.image)
        holder.itemText.text = name
    }

    @SuppressLint("SuspiciousIndentation")
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        var image: ImageView
        var itemText: TextView
        init {
            image = view.findViewById(R.id.imageExercise)
            itemText = view.findViewById(R.id.nameExercise)

            view.setOnClickListener {
                val name = exerciseList[position].name
                val description = exerciseList[position].description
                val amount = exerciseList[position].amount
                val series = exerciseList[position].series
                val photo = exerciseList[position].photo

                val manager = (view.context as FragmentActivity).supportFragmentManager
                val fragmentTransaction = manager.beginTransaction()
                    fragmentTransaction.replace(
                        R.id.container,
                        DetailsFragment.newInstance(name.toString(),description.toString(),amount.toString().toInt(),series.toString(),photo.toString(),"exercise")
                    )

                fragmentTransaction.commit()

            }
        }
    }
}