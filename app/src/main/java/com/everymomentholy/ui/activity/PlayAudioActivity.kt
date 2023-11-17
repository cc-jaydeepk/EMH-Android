package com.everymomentholy.ui.activity

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.everymomentholy.R
import java.lang.Boolean.getBoolean
import java.util.*
import java.util.concurrent.TimeUnit


class PlayAudioActivity : AppCompatActivity() {

    lateinit var seekbar: SeekBar
    lateinit var volumeSeekbar: SeekBar
    lateinit var mediaPlayer: MediaPlayer
    lateinit var audioUrl: String

    lateinit var btnReset: Button

    lateinit var ivLiturgyImage: ImageView

    var len = 0

    lateinit var relativeClose: ImageView
    lateinit var txtLiturgyName: TextView
    lateinit var ivPause: ImageView
    lateinit var ivPlay: ImageView
    lateinit var txtCurrentTime: TextView
    lateinit var txtTotalTime: TextView

    lateinit var audioManager: AudioManager


    //https://www.youtube.com/watch?v=Z4DQdeMAJRE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_playaudio)
        setContentView(R.layout.activity_playaudio_new)

        txtLiturgyName = findViewById(R.id.txtLiturgyName)
        ivPause = findViewById(R.id.ivPause)
        ivPlay = findViewById(R.id.ivPlay)
        ivLiturgyImage = findViewById(R.id.ivLiturgyImage)
        seekbar = findViewById(R.id.seekbar)
        volumeSeekbar = findViewById(R.id.volumeSeekbar)
        txtCurrentTime = findViewById(R.id.txtCurrentTime)
        txtTotalTime = findViewById(R.id.txtTotalTime)
        relativeClose = findViewById(R.id.ivClose)


        mediaPlayer = MediaPlayer()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekbar.setMax(maxVolume)
        volumeSeekbar.setProgress(currVolume)

        seekbar.progress = 0
        seekbar.max = mediaPlayer.duration

        val liturgyTitle = intent.getStringExtra("title")
        val liturgyAudio = intent.getStringExtra("audio")

        audioUrl = liturgyAudio.toString()
        Log.e("audioUrl", "onCreate: " + audioUrl)

        txtLiturgyName.text = liturgyTitle


        /*mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        relativeClose.setOnClickListener {
            mediaPlayer.stop()
            onBackPressed()
        }

        ivPause.setOnClickListener {
            ivPlay.visibility = View.VISIBLE
            ivPause.visibility = View.GONE

            /* mediaPlayer.setAudioAttributes(
                 AudioAttributes.Builder()
                     .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                     .setUsage(AudioAttributes.USAGE_MEDIA)
                     .build()
             )
             try {
                 mediaPlayer.reset();
                 mediaPlayer.setDataSource(audioUrl)
                 mediaPlayer.prepare()
             } catch (e: Exception) {
                 e.printStackTrace()
             }*/
            playAudio()
        }

        ivPlay.setOnClickListener {
            ivPlay.setImageResource(R.drawable.ic_pausess)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                len = mediaPlayer.getCurrentPosition();
            } else {
                ivPlay.setImageResource(R.drawable.ic_play)
                mediaPlayer.currentPosition
                mediaPlayer.start()

            }
        }

        mediaPlayer.setOnPreparedListener {
            seekbar.max = mediaPlayer!!.duration
        }


        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, pos: Int, changed: Boolean) {
                if (mediaPlayer != null) {
                    if (changed) {
                        mediaPlayer.seekTo(pos)
                        seekBar!!.setProgress(pos)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        mediaPlayer.setOnCompletionListener {
            ivPause.setImageResource(R.drawable.ic_pausess)
            // mediaPlayer.release();

            //seekbar.progress = 0
            seekbar.progress = mediaPlayer.duration
        }

        // http://ec2-13-234-132-104.ap-south-1.compute.amazonaws.com/api/getLiturgies?appUserId=623&deviceId=4429d6c3ecabfd84&bookId=21
    }


    private fun playAudio() {

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        try {

            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepare()
            mediaPlayer.start()

            seekbar.progress = 0
            seekbar.max = mediaPlayer.duration

        } catch (e: Exception) {
            e.printStackTrace()
        }

//        mediaPlayer.start()
//        seekbar.progress = 0
//        seekbar.max = mediaPlayer.duration

        val hms = String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mediaPlayer.duration.toLong()),
            TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.duration.toLong()) % TimeUnit.HOURS.toMinutes(
                1
            ),
            TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.duration.toLong()) % TimeUnit.MINUTES.toSeconds(
                1
            )
        )
        txtTotalTime.text = hms


        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                try {
                    seekbar.setProgress(mediaPlayer.currentPosition)

                    val hms = String.format(
                        "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(mediaPlayer.currentPosition.toLong()),
                        TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.currentPosition.toLong()) % TimeUnit.HOURS.toMinutes(
                            1
                        ),
                        TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.currentPosition.toLong()) % TimeUnit.MINUTES.toSeconds(
                            1
                        )
                    )
                    txtCurrentTime.text = hms
                } catch (e: java.lang.Exception) {
                }
            }
        }, 0, 1000)

    }

    fun formateMilliSeccond(milliseconds: Int): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString
    }

    fun formateDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val second = (TimeUnit.SECONDS.convert(
            duration,
            TimeUnit.MILLISECONDS
        ) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.SECONDS))
        return String.format("%2d:%2d", minutes, second)
    }

    override fun onPause() {
        super.onPause()
        // Log.d("lifecycle", "onPause invoked")
        mediaPlayer.stop()
    }
}