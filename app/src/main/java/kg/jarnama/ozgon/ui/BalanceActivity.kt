package kg.jarnama.ozgon.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.database.DataCache
import kg.jarnama.ozgon.helpers.Const
import kotlinx.android.synthetic.main.activity_balance.*
import kotlinx.android.synthetic.main.dialog_promo.*

class BalanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance)
        val dataCache = DataCache.of(this)
        supportActionBar?.title = "Балансты толуктоо"


        val ref = Firebase.firestore.collection("promo")
        val refUser = Firebase.firestore.collection("users")

        promo.setOnClickListener {
            val dialog = Dialog(this)

            dialog.setContentView(R.layout.dialog_promo)
            dialog.promo_activate.setOnClickListener {
                dialog.promo_progress_bar.visibility = View.VISIBLE
                val promocode = dialog.promo_edit_text.text.toString()
                ref.document(promocode).get().addOnSuccessListener {

                    val p = dataCache.findPromo(promocode)
                    if (p != null){
                        Toast.makeText(this, "Бул промокодду иштеткенсиз" , Toast.LENGTH_LONG).show()
                        dialog.cancel()
                        return@addOnSuccessListener
                    }
                    if (it.exists()){
                        val balance: Long = it.get("balance") as Long
                        if (balance > 0){
                            ref.document(promocode).set(hashMapOf("balance" to balance - 7))
                            refUser.document(dataCache.getToken().toString())
                                .get().addOnSuccessListener {user ->
                                    val c = user.get(Const.userCoins)
                                    var coins: Long = 0
                                    if (c != null){
                                        coins = c as Long
                                    }
                                    val hash: HashMap<String, Any> = hashMapOf(Const.userCoins to coins+balance)
                                    refUser.document(dataCache.getToken().toString())
                                        .set(hash, SetOptions.merge())
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Балансыныз толукталды" , Toast.LENGTH_LONG).show()
                                            dialog.cancel()
                                            dialog.promo_progress_bar.visibility = View.GONE
                                            dataCache.addPromo(promocode)
                                        }.addOnFailureListener{
                                            Toast.makeText(this, "Ката", Toast.LENGTH_LONG).show()
                                            dialog.cancel()
                                            dialog.promo_progress_bar.visibility = View.GONE
                                        }
                            }
                        }else{
                            Toast.makeText(this, "Кодтун балансы 0дон томон", Toast.LENGTH_LONG).show()
                            dialog.promo_progress_bar.visibility = View.GONE
                        }
                    }else{
                        Toast.makeText(this, "Код туура эмес", Toast.LENGTH_LONG).show()
                        dialog.promo_progress_bar.visibility = View.GONE
                    }
                }
            }

            dialog.show()
        }
    }
}
