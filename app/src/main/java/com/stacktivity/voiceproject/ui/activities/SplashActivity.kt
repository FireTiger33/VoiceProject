package com.stacktivity.voiceproject.ui.activities

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.quickblox.users.model.QBUser
import com.stacktivity.voiceproject.services.LoginService
import com.stacktivity.voiceproject.utils.SharedPrefsHelper
import com.stacktivity.voiceproject.R

private const val SPLASH_DELAY = 1500

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        fillVersion()
        Handler().postDelayed({
            val user: QBUser? = SharedPrefsHelper.getCurrentUser()
            if (user != null) {
                LoginService.start(this, user)
                OpponentsActivity.start(this)
            } else {
                LoginActivity.start(this)
            }
            finish()
        }, SPLASH_DELAY.toLong())
    }

    private fun fillVersion() {
        val appName = getString(R.string.app_name)
        findViewById<TextView>(R.id.text_splash_app_title).text = appName
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        findViewById<TextView>(R.id.text_splash_app_version).text = getString(R.string.splash_app_version, versionName)
    }
}