package com.example.fitlife

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val context: Context,private val recipeList: List<Recipes>): RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view_recipe, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = "${recipeList[position].name}"
        val image = "${recipeList[position].photo}"
        Glide.with(context)
            .load(image)
            .into(holder.image)
        holder.itemText.text = name
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView
        var itemText: TextView

        init {
            image = view.findViewById(R.id.imageRecipe)
            itemText = view.findViewById(R.id.nameRecipe)
            view.setOnClickListener {
                val name = recipeList[position].name
                val description = recipeList[position].description
                val kcal = recipeList[position].kcal
                val id = recipeList[position].id
                val photo = recipeList[position].photo

                val manager = (view.context as FragmentActivity).supportFragmentManager
                val fragmentTransaction = manager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.container,
                    DetailsFragment.newInstance(name.toString(),description.toString(),kcal.toString().toInt(),id.toString(),photo.toString(),"recipe")
                )

                fragmentTransaction.commit()

            }
        }
    }
}