package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceName: String = "NasSyncService",
    val status: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
