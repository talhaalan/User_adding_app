package com.example.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class FormAdapter(val userList : ArrayList<User>, val context: Context) : RecyclerView.Adapter<FormAdapter.ViewHolder>() {

    lateinit var onActionListener : OnActionListener

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName : AppCompatTextView
        val textViewPassword : AppCompatTextView
        val textViewDate : AppCompatTextView
        val textViewSize : AppCompatTextView
        val textViewKilos : AppCompatTextView
        val buttonDelete : AppCompatImageButton
        val cardView : CardView

        init {
            textViewName = itemView.findViewById(R.id.textViewName)
            buttonDelete = itemView.findViewById(R.id.buttonDelete)
            textViewPassword = itemView.findViewById(R.id.textViewPassword)
            cardView = itemView.findViewById(R.id.cardView)
            textViewDate = itemView.findViewById(R.id.textViewDate)
            textViewSize = itemView.findViewById(R.id.textViewSize)
            textViewKilos = itemView.findViewById(R.id.textViewKilos)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_holder,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy")
        val dateString = simpleDateFormat.format(userList[position].date)

        holder.textViewName.text = context.getString(R.string.kullanici_adi) + userList[position].name
        holder.textViewPassword.text = context.getString(R.string.sifre) + userList[position].password
        holder.textViewDate.text = dateString.toString()
        holder.textViewSize.text = context.getString(R.string.user_size) + userList[position].size
        holder.textViewKilos.text = context.getString(R.string.user_kilos) + userList[position].kilos

        holder.cardView.setOnClickListener {
            onActionListener.let {
                it.onItemClick(userList[position])
            }
        }

        holder.buttonDelete.setOnClickListener {
            onActionListener.let {
                it.onRemoveClick(userList[position])
            }

        }
        //holder.textViewPassword.text = data[position]

    }
    override fun getItemCount(): Int {
        return userList.size
    }

    interface OnActionListener {
        fun onItemClick(user: User)
        fun onRemoveClick(user: User)
    }


}