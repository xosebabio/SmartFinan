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
                val newId = documentReference.id
                spend.spend_id = newId
                spendRef.document(newId).set(spend)
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

    fun deleteSpend(spend: Spend) {
        spendRef
            .document(spend.spend_id.toString())
            .delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }

}