package kg.jarnama.ozgon.ui.promotion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PromotionViewModel : ViewModel() {
    var db: FirebaseFirestore = Firebase.firestore
    var promotionsRef = db.collection("promotions")
    var usersRef = db.collection("users")

    private val promotions = MutableLiveData<DocumentSnapshot>()
    private val user = MutableLiveData<DocumentSnapshot>()

    fun getDocument(id: String): LiveData<DocumentSnapshot> {
        promotionsRef.document(id).get().addOnSuccessListener {
            promotions.value = it
        }
        return promotions
    }

    fun getUser(reference: DocumentReference): LiveData<DocumentSnapshot> {
        reference.get().addOnSuccessListener {
            user.value = it
        }
        return user
    }

    fun deletePromotion(id: String): MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        promotionsRef.document(id).delete().addOnSuccessListener {
            data.value = true
        }.addOnFailureListener {
            data.value = false
            it.printStackTrace()
        }
        return data
    }
}