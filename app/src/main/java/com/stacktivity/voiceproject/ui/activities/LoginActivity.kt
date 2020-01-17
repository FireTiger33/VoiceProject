package com.stacktivity.voiceproject.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.stacktivity.voiceproject.services.LoginService
import com.stacktivity.voiceproject.util.signInUser
import com.stacktivity.voiceproject.util.signUp
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import com.stacktivity.voiceproject.R
import com.stacktivity.voiceproject.data.db.User
import com.stacktivity.voiceproject.utils.*
import kotlinx.android.synthetic.main.activity_login.*

const val ERROR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422

class LoginActivity : BaseActivity() {

    private lateinit var user: QBUser

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUI()
    }

    private fun initUI() {
        supportActionBar?.title = getString(R.string.title_login_activity)

        btn_login_done.setOnClickListener {
            if (isEnteredUserNameValid() && isEnteredRoomNameValid()) {
                hideKeyboard()
                val user = createUserWithEnteredData()
                signUpNewUser(user)
            }
        }
    }

    private fun isEnteredUserNameValid(): Boolean {
        return isLoginValid(this, userLoginEditText)
    }

    private fun isEnteredRoomNameValid(): Boolean {
        return isFoolNameValid(this, userFullNameEditText)
    }

    private fun hideKeyboard() {
        hideKeyboard(userLoginEditText)
        hideKeyboard(userFullNameEditText)
    }

    private fun signUpNewUser(newUser: User) {
        val newQBUser = newUser.getQbUser()
        showProgressDialog(R.string.dlg_creating_new_user)
        signUp(newQBUser, object : QBEntityCallback<QBUser> {
            override fun onSuccess(result: QBUser, params: Bundle) {
                SharedPrefsHelper.saveCurrentUser(newQBUser)
                loginToChat(result)
            }

            override fun onError(e: QBResponseException) {
                if (e.httpStatusCode == ERROR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                    signInCreatedUser(newQBUser)
                } else {
                    hideProgressDialog()
                    longToast(R.string.sign_up_error)
                }
            }
        })
    }

    private fun loginToChat(qbUser: QBUser) {
        qbUser.password = userPassEditText.text.toString()  // TODO remove
        user = qbUser
        startLoginService(qbUser)
    }

    private fun createUserWithEnteredData(): User {
        val user = User(
            userLoginEditText.text.toString(),
            userPassEditText.text.toString(),
            userFullNameEditText.text.toString()
        )

        return user
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == EXTRA_LOGIN_RESULT_CODE) {
            hideProgressDialog()

            var isLoginSuccess = false
            data?.let {
                isLoginSuccess = it.getBooleanExtra(EXTRA_LOGIN_RESULT, false)
            }

            var errorMessage = getString(R.string.unknown_error)
            data?.let {
                errorMessage = it.getStringExtra(EXTRA_LOGIN_ERROR_MESSAGE)
            }

            if (isLoginSuccess) {
                SharedPrefsHelper.saveCurrentUser(user)
                signInCreatedUser(user)
            } else {
                longToast(getString(R.string.login_chat_login_error) + errorMessage)
                userLoginEditText.setText(user.login)
                userFullNameEditText.setText(user.fullName)
            }
        }
    }

    private fun signInCreatedUser(user: QBUser) {
        signInUser(user, object : QBEntityCallback<QBUser> {
            override fun onSuccess(result: QBUser, params: Bundle) {
                SharedPrefsHelper.saveCurrentUser(user)
                updateUserOnServer(user)
            }

            override fun onError(responseException: QBResponseException) {
                hideProgressDialog()
                longToast(R.string.sign_in_error)
            }
        })
    }

    private fun updateUserOnServer(user: QBUser) {
        user.password = null
        QBUsers.updateUser(user).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(updUser: QBUser?, params: Bundle?) {
                hideProgressDialog()
                OpponentsActivity.start(this@LoginActivity)
                finish()
            }

            override fun onError(responseException: QBResponseException?) {
                hideProgressDialog()
                longToast(R.string.update_user_error)
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }

    private fun startLoginService(qbUser: QBUser) {
        val tempIntent = Intent(this, LoginService::class.java)
        val pendingIntent = createPendingResult(EXTRA_LOGIN_RESULT_CODE, tempIntent, 0)
        LoginService.start(this, qbUser, pendingIntent)
    }
}