package kg.jarnama.ozgon.ui.favorites

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavoritesViewModel : ViewModel() {
    var db: FirebaseFirestore = Firebase.firestore
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var promotionsRef = db.collection("promotions")
    private val promotions = MutableLiveData<List<DocumentSnapshot>>()

    fun getPromotions(token: String): LiveData<List<DocumentSnapshot>> {
        val ref = db.document("/users/$token")
        promotionsRef.whereEqualTo("user", ref)
            .get().addOnSuccessListener {
                promotions.value = it.documents
            }.addOnFailureListener {
                println("fail to load $it")
            }

        return promotions
    }

}