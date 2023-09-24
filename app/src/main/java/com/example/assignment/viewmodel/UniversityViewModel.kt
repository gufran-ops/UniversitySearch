package com.example.assignment.viewmodel

import androidx.lifecycle.ViewModel
import com.example.assignment.recyclerview.RowItem

class UniversityViewModel:ViewModel() {
    var itemList:MutableList<RowItem> = mutableListOf()

}