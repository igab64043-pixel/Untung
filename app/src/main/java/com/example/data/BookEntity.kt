package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val category: String,
    val description: String,
    val pdfFileName: String = "",
    val totalPages: Int = 120,
    val isBorrowed: Boolean = false,
    val borrowerName: String? = null,
    val borrowDate: String? = null,
    val returnDueDate: String? = null,
    val coverImageRes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
