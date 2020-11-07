package kg.jarnama.ozgon.ui.promotion

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.database.DataCache
import kg.jarnama.ozgon.helpers.Const
import kotlinx.android.synthetic.main.activity_create_new_add.*
import java.io.InputStream
import java.util.ArrayList
import kotlin.math.floor


class CreateNewActivity : AppCompatActivity() {

    val PICK_IMAGE = 1
    private var selectedCategoryPosition = 0
    private var dataForImage: Uri? = null

    val db = Firebase.firestore
    val dataCache = DataCache.of(this)
    var user: DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_add)

        var imageUrl = "images/" + generateImageName()
        val imagesRef = FirebaseStorage.getInstance()
            .getReference(imageUrl)

        progressBar.visibility = View.VISIBLE

        var categories: List<DocumentSnapshot>? = null

        db.collection("users")
            .document(dataCache.getToken()!!)
            .get().addOnSuccessListener {
                user = it
                val name = user!!.get(Const.userName).toString()
                val num = user!!.get(Const.userNumber).toString()
                if (num.isEmpty() || name.isEmpty()){
                    Toast.makeText(this, getString(R.string.fill_profile), Toast.LENGTH_LONG).show()
                    onBackPressed()
                }
        }

        db.collection("categories")
            .get().addOnSuccessListener {
                categories = it.documents
                progressBar.visibility = View.GONE
                updateCategories(categories)
            }.addOnFailureListener{
                progressBar.visibility = View.GONE
                onBackPressed()
                makeToast(getString(R.string.account_not_ready_for_publish))
            }
        image_holder.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        submit.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val title = header.text
            val price = price_edit.text
            val body = body_edit.text
            if (price.isEmpty()){
                makeToast(getString(R.string.fill_price_for_promotion))
                return@setOnClickListener
            }
            if (title.isEmpty()){
                makeToast(getString(R.string.fill_title_of_promotion))
                return@setOnClickListener
            }

            if (dataForImage != null){
                val uploadTask = imagesRef.putFile( dataForImage!!)
                uploadTask.addOnFailureListener {
                    makeToast(getString(R.string.error_uploading_image))
                }.addOnSuccessListener {

                }
            } else {
                imageUrl = ""
            }

            val data = hashMapOf(
                "title" to title.toString(),
                "price" to price.toString(),
                "body" to body.toString(),
                "image" to imageUrl,
                "timestamp" to System.currentTimeMillis().toString(),
                "category" to categories!![selectedCategoryPosition].reference,
                "user" to user?.reference
            )

            db.collection("users")
                .document(dataCache.getToken().toString())
                .get().addOnSuccessListener {
                    db.collection("users")
                        .document(dataCache.getToken().toString() + "/" +dataCache.getToken().toString())
                        .set(Integer.parseInt(it.get(Const.userCoins).toString()) - 1)
                }

            db.collection("promotions")
                .add(data)
                .addOnSuccessListener {
                    makeToast(getString(R.string.promotion_added))
                    onBackPressed()
                }.addOnFailureListener {
                    makeToast(getString(R.string.errror_on_server))
                    onBackPressed()
                }
        }

        back.setOnClickListener {
            onBackPressed()
        }

        category.onItemSelectedListener =object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategoryPosition = position
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val inputStream: InputStream? =
                applicationContext.contentResolver.openInputStream(data?.data!!)

            image_holder.setImageBitmap(BitmapFactory.decodeStream(inputStream))
            dataForImage = data.data!!
        }
    }

    fun makeToast(text: String){
        Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()
    }

    fun generateImageName(): String{
        val a = "qwertyuiopasdfghjklzxcvbnm"
        var name = ""
        name += System.currentTimeMillis()
        name += a[floor(Math.random()*26).toInt()]
        name += a[floor(Math.random()*26).toInt()]
        name += a[floor(Math.random()*26).toInt()]
        name += ".jpg"
        return name
    }

    fun updateCategories(list: List<DocumentSnapshot>?){
        val newList = ArrayList<String>()
        list?.forEach {
             newList.add(it.get("name").toString())
        }
        category.adapter =
            ArrayAdapter<String>(applicationContext,
                android.R.layout.simple_spinner_item,
                newList)
    }
}
