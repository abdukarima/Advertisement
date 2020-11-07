package kg.jarnama.ozgon.ui.promotion

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.database.DataCache
import kg.jarnama.ozgon.helpers.Const
import kotlinx.android.synthetic.main.activity_promotion.*
import java.text.SimpleDateFormat
import java.util.*


class PromotionActivity : AppCompatActivity() {

    private lateinit var promotionViewModel: PromotionViewModel
    private lateinit var dataCache: DataCache
    val db = Firebase.firestore

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion)
        promotionViewModel = ViewModelProvider(this ).get(PromotionViewModel::class.java)
        dataCache = DataCache.of(this)
        val id = intent.getStringExtra(Const.promotionExtraName)
        val userRef = db.document("/users/" + dataCache.getToken())
        promotionViewModel.getDocument(id!!).observe(this, Observer {
            if (it.get("title") == null){
                promotion_page_title.text = getString(R.string.turned_off)
                return@Observer
            }

            if (userRef == (it.get("user") as DocumentReference)) {
                delete_promotion.visibility = View.VISIBLE
            }

            promotion_page_progress_bar.visibility = View.GONE
            // set date
            val dtf = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val date = Date(it.get("timestamp").toString().toLong())
            date_of_promotion.text =  dtf.format(date)

            // set data
            promotion_page_price.text = it.get("price").toString()
            promotion_page_title.text = it.get("title").toString()
            promotion_page_body.text = it.get("body").toString()

            val storageRef = FirebaseStorage.getInstance().reference

            val image = it.get("image").toString()
            if (image.isNotEmpty()){
                val url = storageRef.child(image).downloadUrl
                url.addOnCompleteListener {uri ->
                   if (uri.isSuccessful){
                       Glide.with(this).load(uri.result).into(promotion_page_image)
                   }
                }
            }

            promotionViewModel.getUser(it.get("user") as DocumentReference)
                .observe(this, Observer { user ->
                    user_name_view.text = user.get(Const.userName).toString()
                    user_number_view.text = user.get(Const.userNumber).toString()
            })
        })

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        delete_promotion.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.delete_dialog)
                .setPositiveButton("Ооба") { _, _ ->
                    promotionViewModel.deletePromotion(id).observe(this, Observer {
                        if (it) {
                            onBackPressed()
                            Toast.makeText(this, "Жарыя очурулду", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this, "Ката", Toast.LENGTH_LONG).show()
                        }
                    })
                }
                .setNegativeButton(
                    "Жок"
                ) { _, _ -> }
            builder.create().show()
        }
    }

}
