package com.example.assignment.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R

class ItemAdapter() : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    private lateinit var mListener: OnItemClickListner
    private var oldRowItemList:MutableList<RowItem> = mutableListOf()

    interface OnItemClickListner{
        fun onItemClick(position:Int)
    }

    fun setOnItemClickListner(listner: OnItemClickListner){
        mListener = listner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent,false)
        return ViewHolder(itemView, mListener)
    }

    override fun getItemCount(): Int {
        return oldRowItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentItem  =oldRowItemList[position]

        holder.name.text = currentItem.name
        holder.domains.text = ""
        for(i in 0 until currentItem.domains.length()){
            val dom:String = currentItem.domains.getString(i)
            if(i!=currentItem.domains.length()-1)
                holder.domains.append(dom+"\n")
            else
                holder.domains.append(dom)
        }
        holder.code.text = currentItem.code
        holder.country.text = currentItem.country

        holder.setIsRecyclable(false)



    }

    class ViewHolder(itemView: View, listner: OnItemClickListner) : RecyclerView.ViewHolder(itemView){
        val name:TextView = itemView.findViewById(R.id.name)
        val domains:TextView = itemView.findViewById(R.id.domains)
        val country:TextView = itemView.findViewById(R.id.country)
        val code:TextView = itemView.findViewById(R.id.code)
        private var webButton: Button = itemView.findViewById(R.id.website_buttom)

        init {
            webButton.setOnClickListener{
                listner.onItemClick(bindingAdapterPosition)
            }
        }
    }

    fun setData(newRowItemList:MutableList<RowItem>){
        val diffUtil = MyDiffUtil(oldRowItemList,newRowItemList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        oldRowItemList = newRowItemList
        diffResults.dispatchUpdatesTo((this))
    }
}