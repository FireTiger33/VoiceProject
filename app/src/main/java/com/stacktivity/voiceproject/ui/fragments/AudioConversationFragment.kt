package com.stacktivity.voiceproject.ui.fragments

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.quickblox.core.helper.StringifyArrayList
import com.stacktivity.voiceproject.ui.activities.CallActivity
import com.stacktivity.voiceproject.utils.SharedPrefsHelper
import com.stacktivity.voiceproject.utils.getColorCircleDrawable
import com.quickblox.users.model.QBUser
import com.quickblox.videochat.webrtc.AppRTCAudioManager
import com.stacktivity.voiceproject.R
import kotlin.collections.ArrayList

const val SPEAKER_ENABLED = "is_speaker_enabled"

class AudioConversationFragment : BaseConversationFragment(), CallActivity.OnChangeAudioDevice {

    private lateinit var audioSwitchToggleButton: ToggleButton
    private lateinit var firstOpponentNameTextView: TextView
    private lateinit var otherOpponentsTextView: TextView

    override fun onStart() {
        super.onStart()
        conversationFragmentCallback?.addOnChangeAudioDeviceListener(this)
    }

    override fun configureOutgoingScreen() {
        val context: Context = activity as Context
        outgoingOpponentsRelativeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        allOpponentsTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color_outgoing_opponents_names_audio_call))
        ringingTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color_call_type))
    }

    override fun configureToolbar() {
        val context: Context = activity as Context
        toolbar.visibility = View.VISIBLE
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        toolbar.setTitleTextColor(ContextCompat.getColor(context, R.color.toolbar_title_color))
        toolbar.setSubtitleTextColor(ContextCompat.getColor(context, R.color.toolbar_subtitle_color))
    }

    override fun configureActionBar() {
        actionBar.subtitle = String.format(getString(R.string.subtitle_text_logged_in_as), currentUser.fullName)
    }

    override fun initViews(view: View?) {
        super.initViews(view)
        if (view == null) {
            return
        }
        timerCallText = view.findViewById(R.id.timer_call)

        firstOpponentNameTextView = view.findViewById(R.id.text_caller_name)
        otherOpponentsTextView = view.findViewById(R.id.text_other_inc_users)


        opponentsLiveData.observe(this, Observer<ArrayList<QBUser>> { opponents: ArrayList<QBUser> ->
            val firstOpponentAvatarImageView = view.findViewById<ImageView>(R.id.image_caller_avatar)
            firstOpponentAvatarImageView?.setBackgroundDrawable(getColorCircleDrawable(opponents[0].id))

            view.findViewById<TextView>(R.id.text_also_on_call).visibility = if (amountOpponents < 2) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            firstOpponentNameTextView.text = opponents[0].fullName

            otherOpponentsTextView.text = makeStringFromOtherUsersFullNames(opponents)
        })

        audioSwitchToggleButton = view.findViewById(R.id.toggle_speaker)
        audioSwitchToggleButton.visibility = View.VISIBLE
        audioSwitchToggleButton.isChecked = SharedPrefsHelper.get(SPEAKER_ENABLED, true)
        actionButtonsEnabled(false)

        if (conversationFragmentCallback?.isCallState() == true) {
            onCallStarted()
        }
    }

    private fun makeStringFromOtherUsersFullNames(allUsers: ArrayList<QBUser>): String {
        val usersNames = StringifyArrayList<String>()
        for (i in 1 until allUsers.size) {
            if (allUsers[i].fullName != null) {
                usersNames.add(allUsers[i].fullName)
            } else if (allUsers[i].id != null) {
                usersNames.add(allUsers[i].id.toString())
            }
        }
        return usersNames.itemsAsString.replace(",", ", ")
    }

    override fun onStop() {89269764697
        super.onStop()
        conversationFragmentCallback?.removeOnChangeAudioDeviceListener(this)
    }

    override fun initButtonsListener() {
        super.initButtonsListener()
        audioSwitchToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPrefsHelper.save(SPEAKER_ENABLED, isChecked)
            conversationFragmentCallback?.onSwitchAudio()
        }
    }

    override fun actionButtonsEnabled(inability: Boolean) {
        super.actionButtonsEnabled(inability)
        audioSwitchToggleButton.isActivated = inability
    }

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_audio_conversation
    }

    override fun onCallTimeUpdate(time: String) {
        timerCallText.text = time
    }

    override fun audioDeviceChanged(newAudioDevice: AppRTCAudioManager.AudioDevice) {
        audioSwitchToggleButton.isChecked = newAudioDevice != AppRTCAudioManager.AudioDevice.SPEAKER_PHONE
    }
}