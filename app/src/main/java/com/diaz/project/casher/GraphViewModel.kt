package com.diaz.project.casher

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.diaz.project.casher.db.Record
import com.diaz.project.casher.db.RecordRepo

class GraphViewModel(application: Application) : AndroidViewModel(application) {
    private val recordRepo = RecordRepo(application)

    fun getAllPengeluaran(): LiveData<List<Record>>? {
        return recordRepo.getAllPengeluaran()
    }

    fun getAllPemasukan(): LiveData<List<Record>>? {
        return recordRepo.getAllPemasukan()
    }
}