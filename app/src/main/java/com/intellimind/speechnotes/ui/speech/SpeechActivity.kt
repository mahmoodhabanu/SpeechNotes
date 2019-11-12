package com.intellimind.speechnotes.ui.speech

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.intellimind.speechnotes.common.BounceInterpolator
import com.intellimind.speechnotes.common.Observer
import com.intellimind.speechnotes.common.PermissionListener
import com.intellimind.speechnotes.databinding.ActivitySpeechBinding
import com.intellimind.speechnotes.ui.base.BaseHandler
import com.intellimind.speechnotes.utils.DialogUtility
import com.intellimind.speechnotes.utils.PermissionUtil
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.Editable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intellimind.speechnotes.R
import java.lang.StringBuilder


class SpeechActivity : AppCompatActivity(), RecognitionListener,Observer, BaseHandler<String> {

    private lateinit var binding: ActivitySpeechBinding
    var speech: SpeechRecognizer? = null
    var recognizerIntent: Intent? = null
    private var anim: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_speech)
        binding.handler = this
        resetSpeechRecognizer()
        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO),
                PermissionUtil.MY_PERMISSIONS_RECORD_AUDIO
            )
            return
        }
        setRecogniserIntent()
    }

    override fun onClick(view: View, data: String?) {
        when (view.id) {
            R.id.img_send_mike -> sendSpeechRequest()
        }
    }
    /**
     * This method sends the request from Microphone
     */
    private fun sendSpeechRequest() {
        if (PermissionUtil.checkIsAudioPermissionEnabled(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtil.checkPermission(this, Manifest.permission.RECORD_AUDIO, object :
                    PermissionListener {
                    override fun onPermissionAsk() {
                        PermissionUtil.requestRecordAudioAccessPermission(this@SpeechActivity)
                    }

                    override fun onPermissionPreviouslyDenied() {
                        PermissionUtil.requestRecordAudioAccessPermission(this@SpeechActivity)

                    }

                    override fun onPermissionDisabled() {
                        DialogUtility.showDialogue(this@SpeechActivity, getString(R.string.microphone_allow), this@SpeechActivity)
                    }

                    override fun onPermissionGranted() {
                        addAnimationView()
                    }

                })
            }

        } else {
            addAnimationView()
        }
    }

    fun resetSpeechRecognizer() {
        speech?.destroy()
        speech = SpeechRecognizer.createSpeechRecognizer(this)
        if (SpeechRecognizer.isRecognitionAvailable(this))
            speech?.setRecognitionListener(this)
        else
            finish()
    }

    private fun setRecogniserIntent() {
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent?.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            "en")
        recognizerIntent?.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        recognizerIntent?.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
    }
    /**
     * This method adds the Animation item to trigger Listening
     */
    private fun addAnimationView() {
        addAnimationItem()
        resetSpeechRecognizer()
        speech?.startListening(recognizerIntent)

    }

    /**
     * This method adds the Animation item from Voice command
     */
    fun addAnimationItem() {
        anim = AnimationUtils.loadAnimation(this, R.anim.bounce)
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        val interpolator = BounceInterpolator(0.2, 30.0)
        anim?.interpolator = interpolator
        binding.imgSendMike.startAnimation(anim)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionUtil.MY_PERMISSIONS_RECORD_AUDIO -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addAnimationView()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        resetSpeechRecognizer()
        speech?.startListening(recognizerIntent)
    }

    override fun onPause() {
        super.onPause()
        speech?.stopListening()
    }

    override fun onStop() {
        super.onStop()
        speech?.destroy()

    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.e("Speech"," OnReady")
    }

    override fun onRmsChanged(p0: Float) {
        Log.e("Speech"," onRmsChanged")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.e("Speech"," onBufferReceived")
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.e("Speech"," onPartialResults"+ p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION))
        val matches = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.size > 0) {
            binding.speechText.setText(matches[0])
        }
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.e("Speech"," onEvent")
    }

    override fun onBeginningOfSpeech() {
        Log.e("Speech"," onBeginningOfSpeech")
    }

    override fun onEndOfSpeech() {
        Log.e("Speech"," onEndOfSpeech")
        speech?.stopListening()
    }

    override fun onError(p0: Int) {
        resetSpeech()
        Log.e("Speech"," onError " +getErrorText(p0))
    }

    override fun onResults(p0: Bundle?) {
        Log.e("Speech"," onResults" +  p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION))

    }
    override fun onObserve(requestCode: Int, requestMessage: String?) {
    }

    private fun resetSpeech(){
        resetSpeechRecognizer()
        //speech?.startListening(recognizerIntent)
    }
    fun getErrorText(errorCode: Int): String {
        val message: String
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> message = "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> message = "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"
            else -> message = "Didn't understand, please try again."
        }
        return message
    }
}