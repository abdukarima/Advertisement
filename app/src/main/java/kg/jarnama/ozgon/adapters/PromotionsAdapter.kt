package kg.jarnama.ozgon.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.helpers.Const
import kg.jarnama.ozgon.ui.promotion.PromotionActivity
import kotlinx.android.synthetic.main.item_promotion.view.*
import java.text.SimpleDateFormat
import java.util.*


class PromotionsAdapter(private var mDataset: List<DocumentSnapshot>, var context: Fragment) : RecyclerView.Adapter<PromotionsAdapter.MyViewHolder>() {

    class MyViewHolder(var frame: CardView) : RecyclerView.ViewHolder(frame)

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_promotion, parent, false) as CardView
        return MyViewHolder(v)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val document = mDataset[position]

        holder.frame.promotion_title.text = document.get("title").toString()
        holder.frame.promotion_body.text = document.get("body").toString()

        val dtf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val date = Date(document.get("timestamp").toString().toLong())
        holder.frame.promotion_publish_time.text =  dtf.format(date)

        holder.frame.promotion_price.text = document.get("price").toString()
        val storageRef = FirebaseStorage.getInstance().reference
        val image = document.get("image").toString()
        println(image)
        if (image.isNotEmpty()){
            val url = storageRef.child(image).downloadUrl
            url.addOnCompleteListener {
                if (it.isSuccessful){
                    Glide.with(context).load(it.result).into(holder.frame.image_add)
                }
            }
        }

        var clicked = false
        holder.frame.setOnClickListener {
            if (!clicked){
                clicked = true
                holder.frame.setBackgroundColor(context.activity!!.getColor(R.color.light_grey))
                val i = Intent(context.activity, PromotionActivity::class.java)
                i.putExtra(Const.promotionExtraName, document.id)
                context.startActivity(i)

                Thread{
                    Thread.sleep(1000)
                    clicked = false
                    holder.frame.setBackgroundColor(context.activity!!.getColor(R.color.white))
                }.start()
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size

    }

    fun update(myDataset: List<DocumentSnapshot>) {
        mDataset = myDataset
        notifyDataSetChanged()
    }

}