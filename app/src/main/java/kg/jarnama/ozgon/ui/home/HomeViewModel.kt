package kg.jarnama.ozgon.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {
    var db: FirebaseFirestore = Firebase.firestore
    var categoriesRef = db.collection("categories")
    var promotionsRef = db.collection("promotions")

    private val categories = MutableLiveData<List<DocumentSnapshot>>()
    private val _promotions = MutableLiveData<List<DocumentSnapshot>>()

    val promotions: LiveData<List<DocumentSnapshot>> =  _promotions

    fun getCategories(): MutableLiveData<List<DocumentSnapshot>> {
        categoriesRef.get().addOnSuccessListener {
            categories.value = it.documents
        }.addOnFailureListener{

        }
        return categories
    }

    fun getPromotions() {
        promotionsRef.orderBy("timestamp").limit(100)
            .get().addOnSuccessListener {
                _promotions.value = it.documents
            }
    }

    fun getPromotionsByCategory(category: String) {
        val ref = db.document("categories/$category")
        promotionsRef
            .whereEqualTo("category", ref)
            .orderBy("timestamp")
            .limit(100)
            .get().addOnSuccessListener {
                _promotions.value = it.documents
            }
    }
}