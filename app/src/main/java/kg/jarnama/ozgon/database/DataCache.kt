package kg.jarnama.ozgon.database

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DataCache(var context: Context) {
    private val gson = Gson()
    private val prefsNode = "prefs"
    private val userTokenNode = "userToken"
    private val favoritesNode = "favorites"
    private val numberNode = "number"
    private val promoNode = "promo"

    fun saveToken(token: String){
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE).edit()
        myPrefs.putString(userTokenNode, token)
        myPrefs.apply()
    }

    fun getToken(): String? {
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE)
        return myPrefs.getString(userTokenNode, "")
    }

    fun saveNum(num: String){
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE).edit()
        myPrefs.putString(numberNode, num)
        myPrefs.apply()
    }

    fun getNum(): String? {
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE)
        return myPrefs.getString(numberNode, "")
    }

    @SuppressLint("CommitPrefEdits")
    fun saveChoices(list: List<DocumentSnapshot>){
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE).edit()
        myPrefs.putString(favoritesNode, gson.toJson(list))
        myPrefs.apply()
    }

    fun getChoices(): ArrayList<DocumentSnapshot>{
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE)
        val listType: Type = object : TypeToken<ArrayList<DocumentSnapshot>>() {}.type
        val listJson = myPrefs.getString(favoritesNode, "")
        if (listJson == ""){
            return arrayListOf()
        }
        return gson.fromJson(listJson, listType)
    }

    fun addPollChoice(choice: DocumentSnapshot){
        val l  = getChoices()
        l.add(choice)
        saveChoices(l)
    }

    @SuppressLint("CommitPrefEdits")
    fun savePromo(list: List<String>){
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE).edit()
        myPrefs.putString(promoNode, gson.toJson(list))
        myPrefs.apply()
    }

    fun getPromo(): ArrayList<String>{
        val myPrefs = context.getSharedPreferences(prefsNode, Context.MODE_PRIVATE)
        val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
        val listJson = myPrefs.getString(promoNode, "")
        if (listJson == ""){
            return arrayListOf()
        }
        return gson.fromJson(listJson, listType)
    }

    fun addPromo(choice: String){
        val l  = getPromo()
        l.add(choice)
        savePromo(l)
    }

    fun findPromo(promo: String): String? {
        val l  = getPromo()
        l.forEach {
            if(promo == it){
                return  it
            }
        }
        return null
    }

    companion object{
        fun of (context: Context): DataCache {
            return DataCache(context)
        }
    }
}