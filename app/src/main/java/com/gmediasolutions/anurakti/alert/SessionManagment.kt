package com.gmediasolutions.anurakti.alert


import java.util.HashMap

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import com.gmediasolutions.anurakti.base.LoginActivity

class SessionManagment(internal var _context: Context) {
    internal var pref: SharedPreferences
    internal var editor: Editor
    internal var PRIVATE_MODE = 0

    val userDetails: HashMap<String, String>
        get() {
            val user = HashMap<String, String>()
            user[USER_ID] = pref.getString(USER_ID, "null")
            user[USER_TOKEN] = pref.getString(USER_TOKEN, "null")

            return user
        }

    val UserisLoggedIn: Boolean
        get() {
            val logged = pref.getBoolean(USER_IS_LOGIN, false)
            return logged
        }

    init {

        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun createLoginSession(UserId: String, UserToken: String/*,AcceptDecline:String*/) {
        editor.putBoolean(USER_IS_LOGIN, true)
        editor.putString(USER_ID, UserId)
        editor.putString(USER_TOKEN, UserToken)
        editor.commit()
    }

    fun checkLogin() {
        Log.i("session_Login : ", UserisLoggedIn.toString())
        if (!this.UserisLoggedIn) {
            val i = Intent(_context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            _context.startActivity(i)
        }
    }

    fun logoutUser() {
        editor.clear()
        editor.commit()
        val i = Intent(_context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        _context.startActivity(i)
    }

    companion object {
        private val PREF_NAME = "AnuraktiPref"
        private val USER_IS_LOGIN = "UserIsLoggedIn"
        val USER_ID = "UserId"
        val USER_TOKEN = "UserToken"
    }
}