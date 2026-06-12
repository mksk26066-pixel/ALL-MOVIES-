package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val filePath: String,
    val thumbnailPath: String,
    val views: Int = 0,
    val duration: String = "00:00",
    val uploadDate: Long = System.currentTimeMillis()
)
