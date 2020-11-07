package kg.jarnama.ozgon.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kg.jarnama.ozgon.helpers.Const

class SettingsViewModel : ViewModel() {
    var db: FirebaseFirestore = Firebase.firestore
    var usersRef = db.collection("users")

    private val user = MutableLiveData<DocumentSnapshot>()

    fun getUser(token: String): MutableLiveData<DocumentSnapshot> {
        usersRef.document(token).get().addOnSuccessListener {
            user.value = it
        }.addOnFailureListener {
            user.value = null
        }
        return user
    }

    fun setUser(token: String, userHash: HashMap<String, Any>) {
        usersRef.document(token).update(userHash)
    }

    fun createUserBalance(token: String){
        usersRef.document(token).set(hashMapOf(Const.userCoins to 0), SetOptions.merge())
    }
}