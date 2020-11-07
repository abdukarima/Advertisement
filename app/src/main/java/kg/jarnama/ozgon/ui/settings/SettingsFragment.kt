package kg.jarnama.ozgon.ui.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.database.DataCache
import kg.jarnama.ozgon.helpers.Const
import kg.jarnama.ozgon.ui.MainActivity
import kg.jarnama.ozgon.ui.WelcomeActivity
import kg.jarnama.ozgon.ui.BalanceActivity
import kotlinx.android.synthetic.main.dialog_change_name.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var userHash: HashMap<String, Any> = HashMap()
    private lateinit var dataCache: DataCache
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        dataCache = DataCache.of(activity!!)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            // not logged in
            root.account_layout.visibility = View.GONE
            root.open_registration.setOnClickListener {
                startActivity(Intent(activity, WelcomeActivity::class.java))
            }

        } else {

            root.open_registration.visibility = View.GONE

            settingsViewModel.getUser(dataCache.getToken()!!).observe(viewLifecycleOwner, Observer {
                root.account_name.text = it.get(Const.userName).toString()
                root.account_num.text = it.get(Const.userNumber).toString()
                var coins = it.get(Const.userCoins)
                if (coins == null){
                    coins = "0"
                    settingsViewModel.createUserBalance(dataCache.getToken().toString())
                }
                root.account_balance.text = "$coins сом"
                dataCache.saveNum(it.get(Const.userNumber).toString())
            })
        }

        root.open_balance.setOnClickListener {
            startActivity(Intent(activity!!, BalanceActivity::class.java))
        }

        root.open_dialog.setOnClickListener {

            val dialog = Dialog(context!!)

            dialog.setContentView(R.layout.dialog_change_name)

            dialog.save_name_button.setOnClickListener {

                val name = dialog.name_change_edittext.text.toString()

                userHash = hashMapOf(
                    Const.userName to name,
                    Const.userNumber to dataCache.getNum()!!
                )

                root.account_name.text = name
                settingsViewModel.setUser(dataCache.getToken()!!, userHash)
                Toast.makeText(activity, "сакталды", Toast.LENGTH_LONG).show()
                dialog.cancel()
            }

            dialog.show()
        }

        root.exit_account.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)

            builder.setMessage(getString(R.string.logout_permission))
                .setPositiveButton("Ооба") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    dataCache.saveToken("")
                    startActivity(Intent(activity!!, MainActivity::class.java))
                    Thread{
                        Thread.sleep(2000)
                        activity!!.finish()
                    }.start()
                }

                .setNegativeButton(
                    "Жок"
                ) { _, _ -> }
            builder.create().show()
        }

        return root
    }
}
