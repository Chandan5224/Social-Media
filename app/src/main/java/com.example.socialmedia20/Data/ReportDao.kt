package com.example.socialmedia20.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReportDao {
    private val db = FirebaseFirestore.getInstance()
    private val reportCollection = db.collection("reports")

    fun addReport(report: Report){
        GlobalScope.launch(Dispatchers.IO) {
            reportCollection.document(report.reportUid).set(report)
        }
    }
}