package kg.jarnama.ozgon.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.models.Add
import kg.jarnama.ozgon.ui.home.ImpSelections
import kotlinx.android.synthetic.main.item_category.view.*
import java.util.*

class CategoriesAdapter(private var mDataset: List<DocumentSnapshot>, var parent: ImpSelections) :
    RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    class MyViewHolder(var frame: CardView) : RecyclerView.ViewHolder(frame)

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false) as CardView
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val f = mDataset[position]
        holder.frame.categoryname.text = f.get("name").toString()
        holder.frame.setOnClickListener {
            parent.selectCategory(f)
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