package com.example.assignment.recyclerview

import androidx.recyclerview.widget.DiffUtil

class MyDiffUtil(
    private val oldList: MutableList<RowItem>,
    private val newList:MutableList<RowItem>
):DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if(oldList[oldItemPosition].name==newList[newItemPosition].name)
            return true
        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if(oldList[oldItemPosition].name!=newList[newItemPosition].name)
            return false
        if(oldList[oldItemPosition].url.getString(0)!=newList[newItemPosition].url.getString(0))
            return false
        if(oldList[oldItemPosition].code!=newList[newItemPosition].code)
            return false
        if(oldList[oldItemPosition].country!=newList[newItemPosition].country)
            return false
       if(oldList[oldItemPosition].domains!=newList[newItemPosition].domains)
           return false
        return true
    }

}