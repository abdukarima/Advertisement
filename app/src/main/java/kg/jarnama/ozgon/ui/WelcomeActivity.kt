package kg.jarnama.ozgon.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kg.jarnama.ozgon.R
import kg.jarnama.ozgon.database.DataCache
import kg.jarnama.ozgon.helpers.Const
import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.concurrent.TimeUnit

class WelcomeActivity : AppCompatActivity() {

    val TAG = "welcome"
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var currentNum : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("ky")

        val user = auth.currentUser
        if (user != null){
            goToMainActivity()
        }else{
            cardView.visibility = View.VISIBLE
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
                welcome_progress_bar.visibility = View.GONE
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(applicationContext, "Работа приложения нарушена", Toast.LENGTH_LONG).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(applicationContext, "Разработчик ошибся", Toast.LENGTH_LONG).show()
                }
                welcome_progress_bar.visibility = View.GONE

            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                cardView.visibility = View.GONE
                cardView_check.visibility = View.VISIBLE
                check_sms.text = currentNum
                welcome_progress_bar.visibility = View.GONE
            }
        }

        submit_button.setOnClickListener {
            val text = editText.text.toString()
            welcome_progress_bar.visibility = View.VISIBLE
            if (isPhoneNumber(text)){
                currentNum = "+996 $text"
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+996$text",
                    60,
                    TimeUnit.SECONDS,
                    this,
                    callbacks)
            }else{
                welcome_progress_bar.visibility = View.GONE
                Toast.makeText(applicationContext, "Ката", Toast.LENGTH_LONG).show()
            }
        }

        find_code.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code_edit.text.toString())
            signInWithPhoneAuthCredential(credential)
            welcome_progress_bar.visibility = View.VISIBLE
        }

        change_num.setOnClickListener {
            cardView.visibility = View.VISIBLE
            cardView_check.visibility = View.GONE
        }
    }

    fun isPhoneNumber(number: String): Boolean{
        if (number[0] == '0'){
            return false
        }
        if (number.length != 9){
            return false
        }
        return true
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = task.result?.user
                    DataCache.of(applicationContext).saveToken(user!!.uid)

                    welcome_progress_bar.visibility = View.GONE

                    val db: FirebaseFirestore = Firebase.firestore
                    val usersRef = db.collection("users")

                    usersRef.document(user.uid).get().addOnSuccessListener {
                        if (it.exists()){
                            usersRef.document(user.uid + "/" + Const.userNumber).set(currentNum)
                        }else {
                            val userHash = hashMapOf(
                                Const.userName to "",
                                Const.userNumber to currentNum,
                                Const.userCoins to 10,
                                Const.userPromotionsCount to 0
                            )
                            usersRef.document(user.uid).set(userHash)
                        }
                    }

                    goToMainActivity()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                }
            }
    }

    private fun goToMainActivity(){
        startActivity(Intent(applicationContext, MainActivity::class.java))
        Thread{
            Thread.sleep(2000)
            finish()
        }.start()
    }
}
