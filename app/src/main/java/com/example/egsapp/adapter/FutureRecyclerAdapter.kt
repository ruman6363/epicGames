package com.example.egsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.egsapp.Game
import com.example.egsapp.R
import com.squareup.picasso.Picasso

class FutureRecyclerAdapter(private val game: ArrayList<Game>): RecyclerView.Adapter<FutureRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){ //Инициализация объектов лайаута айтемов ресайклера
        var wallpaperImage: ImageView = itemView.findViewById(R.id.wallpaperImageFut)
        val titleText: TextView = itemView.findViewById(R.id.titleTextFut)
        val descText: TextView = itemView.findViewById(R.id.descTextFut)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { //Подвязка лайаута к адаптеру ресайклера
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.future_recycler_item, parent, false) //Определение лайаута
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) { //Запись в айтем данных в заготовленные textview
        Picasso.get().load(game[position].imageURL).into(holder.wallpaperImage) //Тянем с помощью Picasso из ссылки картинку игры
        holder.titleText.text = game[position].title
        holder.descText.text = game[position].description
    }

    override fun getItemCount(): Int {
        println("size of list ${game.size}")
        return game.size
    }
}