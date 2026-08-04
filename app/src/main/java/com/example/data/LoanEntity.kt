package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val bookTitle: String,
    val borrowerName: String,
    val borrowDate: String,
    val returnDueDate: String,
    val isReturned: Boolean = false,
    val returnDate: String? = null
)
