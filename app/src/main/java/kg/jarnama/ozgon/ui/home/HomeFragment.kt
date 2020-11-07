package kg.jarnama.ozgon.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.adapters.PromotionsAdapter
import kg.jarnama.ozgon.adapters.CategoriesAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() , ImpSelections{

    private var recyclerView: RecyclerView? = null
    private var horizontalRecyclerView: RecyclerView? = null
    private var mAdapter: PromotionsAdapter? = null
    private var viewManager: RecyclerView.LayoutManager? = null
    private var horizontalViewManager: RecyclerView.LayoutManager? = null
    private var categoriesAdapter: CategoriesAdapter? = null

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        viewManager = LinearLayoutManager(context)
        mAdapter = PromotionsAdapter(listOf(), this)
        categoriesAdapter = CategoriesAdapter(arrayListOf(), this)

        horizontalViewManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        horizontalRecyclerView = root.findViewById<RecyclerView>(R.id.horizontalRecyclerView).apply {
            setHasFixedSize(true)
            layoutManager = horizontalViewManager
            adapter = categoriesAdapter
        }

        recyclerView = root.findViewById<RecyclerView>(R.id.main_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter
        }

        homeViewModel.getCategories().observe(viewLifecycleOwner, Observer {
            categoriesAdapter!!.update(it)
        })

        homeViewModel.promotions.observe(viewLifecycleOwner, Observer {
            mAdapter!!.update(it)
            home_progress_bar.visibility = View.GONE
            if (it.isEmpty()){
                root.not_found.visibility = View.VISIBLE
            }
        })

        homeViewModel.getPromotions()

        return root
    }

    override fun selectCategory(category: DocumentSnapshot) {
        home_progress_bar.visibility = View.VISIBLE
        homeViewModel.getPromotionsByCategory(category.id)
        not_found.visibility = View.GONE
    }

}
