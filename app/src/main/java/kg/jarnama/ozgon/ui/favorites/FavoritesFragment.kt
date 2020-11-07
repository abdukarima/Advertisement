package kg.jarnama.ozgon.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.adapters.PromotionsAdapter
import kg.jarnama.ozgon.database.DataCache
import kg.jarnama.ozgon.ui.WelcomeActivity
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_favorites.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class FavoritesFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var viewManager: RecyclerView.LayoutManager? = null
    private var mAdapter: PromotionsAdapter? = null
    private var dataCache: DataCache? = null

    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        favoritesViewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        mAdapter = PromotionsAdapter(arrayListOf(), this)
        viewManager = LinearLayoutManager(context)

        dataCache = DataCache.of(activity!!)
        recyclerView = root.findViewById<RecyclerView>(R.id.my_items_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter
        }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            // not logged in
            root.my_items_recycler_view.visibility = View.GONE
            root.open_registration_activity.setOnClickListener {
                startActivity(Intent(activity, WelcomeActivity::class.java))
            }
        } else {
            // logged in
            root.open_registration_activity.visibility = View.GONE
            favoritesViewModel.getPromotions(dataCache!!.getToken()!!)
                .observe(viewLifecycleOwner, Observer {
                    mAdapter!!.update(it)
                    if (it.isEmpty()){
                        root.no_promotion_text.visibility = View.VISIBLE
                    }
                })
        }

        return root
    }
}
