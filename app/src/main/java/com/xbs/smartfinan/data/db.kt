package com.xbs.smartfinan.data

import com.google.firebase.firestore.FirebaseFirestore
import com.xbs.smartfinan.view.Spend

class db {

    // Obtén una instancia de FirebaseFirestore
    val db = FirebaseFirestore.getInstance()
    val spendRef = db.collection("spends")

    fun addSpend(spend: Spend) {
        // Add a new document with a generated ID
        db.collection("spends")
            .add(spend)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun getSpend(): MutableList<Spend> {
        val spends = mutableListOf<Spend>()
        spendRef
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val spend = Spend.fromFirestore(document)
                    spends.add(spend)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
        return spends
    }
}