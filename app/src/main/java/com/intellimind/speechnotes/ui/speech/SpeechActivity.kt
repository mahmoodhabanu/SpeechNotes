package com.intellimind.speechnotes.ui.speech

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.intellimind.speechnotes.common.BounceInterpolator
import com.intellimind.speechnotes.common.PermissionListener
import com.intellimind.speechnotes.databinding.ActivitySpeechBinding
import com.intellimind.speechnotes.ui.base.BaseHandler
import com.intellimind.speechnotes.utils.DialogUtility
import com.intellimind.speechnotes.utils.PermissionUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.intellimind.speechnotes.R
import com.intellimind.speechnotes.remote.Speech
import com.intellimind.speechnotes.ui.base.BaseModel
import com.intellimind.speechnotes.ui.base.BaseRecyclerAdapter
import com.intellimind.speechnotes.utils.RxObservable
import java.lang.StringBuilder


class SpeechActivity : AppCompatActivity(), RecognitionListener, BaseHandler<BaseModel>, com.intellimind.speechnotes.common.Observer {

    private lateinit var binding: ActivitySpeechBinding
    var speech: SpeechRecognizer? = null
    var recognizerIntent: Intent? = null
    private var anim: Animation? = null
    private var adapter: BaseRecyclerAdapter<Speech>? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var isSuggestionClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_speech)
        binding.handler = this
        resetSpeechRecognizer()
        setRecogniserIntent()
        initAdapter()

        val editTextListener = RxObservable.editTextListener(binding.speechText)
        editTextListener.subscribe {
            binding.speechText.text?.run {
                if (binding.speechText.text.toString().isNotEmpty() && !isSuggestionClicked) {
                    Handler().postDelayed({getViewModel()?.getSuggestions(binding.speechText.text.toString())},1000)
                } else {
                    adapter?.updateList(ArrayList())
                }
            }
        }

        getViewModel()?.suggestions?.observe(this, androidx.lifecycle.Observer {
                adapter?.updateList(it)
            it?.run {
                if(it.isEmpty()) {
                    getViewModel()?.addSpeechText(binding.speechText.text.toString())
                }
            }
        })
    }


    override fun onClick(view: View, data: BaseModel?) {
        when (view.id) {
            R.id.img_send_mike -> {
                sendSpeechRequest()
            }
            R.id.suggestion_text -> {
                var speechData = data as Speech
                isSuggestionClicked = true
                binding.speechText.setText(speechData.speechText)
                adapter?.updateList(ArrayList())
                binding.speechText?.clearFocus()
                isSuggestionClicked = false
            }
        }
    }

    private fun getViewModel(): SpeechViewModel? {
        return ViewModelProviders.of(this).get(SpeechViewModel::class.java)
    }

    /**
     * This method initializes the BaseAdapter for SpeechRecycler.
     */
    private fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, true)
        (binding.recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        binding.recyclerView.layoutManager = linearLayoutManager
        adapter = BaseRecyclerAdapter(0, getViewModel(), this)
        binding.recyclerView.adapter = adapter
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
    }

    override fun onBufferReceived(p0: ByteArray?) {
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.e("Speech"," onPartialResults"+ p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION))
        val matches = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.size > 0) {
            binding.speechText.text.clear()
            binding.speechText.setText(matches[0])
        }
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onEndOfSpeech() {
        speech?.stopListening()
    }

    override fun onError(p0: Int) {
        resetSpeech()
    }

    override fun onResults(p0: Bundle?) {
        speech?.stopListening()

    }
    override fun onObserve(requestCode: Int, requestMessage: String?) {
    }

    private fun resetSpeech(){
        resetSpeechRecognizer()
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
