package com.diaz.project.casher

import com.diaz.project.casher.db.Record

class GraphModel(
    val date: String,
    val records: MutableList<Record>
)