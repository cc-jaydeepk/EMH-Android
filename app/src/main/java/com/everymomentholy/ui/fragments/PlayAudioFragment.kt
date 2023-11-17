package com.everymomentholy.ui.fragments

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.ui.activity.MainActivity
import java.util.*
import java.util.concurrent.TimeUnit

class PlayAudioFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.activity_playaudio_new, container, false)

        (activity as MainActivity).toolbar.visibility = View.GONE

        val bundle = arguments
        val title = bundle!!.getString("title").toString()
        val audio = bundle!!.getString("audio").toString()
        Log.e("AUDIO", "onCreateView: " + audio)

        txtLiturgyName = view.findViewById(R.id.txtLiturgyName)
        ivPause = view.findViewById(R.id.ivPause)
        ivPlay = view.findViewById(R.id.ivPlay)
        ivLiturgyImage = view.findViewById(R.id.ivLiturgyImage)

        seekbar = view.findViewById(R.id.seekbar)
        volumeSeekbar = view.findViewById(R.id.volumeSeekbar)
        txtCurrentTime = view.findViewById(R.id.txtCurrentTime)
        txtTotalTime = view.findViewById(R.id.txtTotalTime)

        mediaPlayer = MediaPlayer()

        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekbar.setMax(maxVolume)
        volumeSeekbar.setProgress(currVolume)

        seekbar.progress = 0
        seekbar.max = mediaPlayer.duration

        //val liturgyTitle = intent.getStringExtra("title")
        //val liturgyAudio = intent.getStringExtra("audio")
        audioUrl = audio.toString()

       // Log.e("AUDIOURL", "onCreate: " + title)
        txtLiturgyName.text = title

        relativeClose = view.findViewById(R.id.ivClose)
        relativeClose.setOnClickListener {
            mediaPlayer.stop()
           // requireActivity().onBackPressed()
            (activity as MainActivity).toolbar.visibility = View.VISIBLE
            (activity as MainActivity).replaceFragment(MyLiturgiesFragment(), "My Liturgies")
        }

        ivPause.setOnClickListener {
            ivPlay.visibility = View.VISIBLE
            ivPause.visibility = View.GONE
            //  ivPause.setImageResource(R.drawable.ic_play_icon)
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

            //  mediaPlayer = null;
            seekbar.progress = 0
        }

        return view

    }

    private fun playAudio() {
        // ivPause.setImageResource(R.drawable.ic_play_icon)
        // audioUrl = "http://ec2-13-234-132-104.ap-south-1.compute.amazonaws.com/storage/liturgies/files/9781645552826_094_Every_Moment_Holy_A_Liturgy_for_Sundays_Table_Blessing.mp3"
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


                    // txtCurrentTime.text = formateDuration(mediaPlayer.currentPosition.toLong())

                } catch (e: java.lang.Exception) {
                }
            }
        }, 0, 1000)

    }

    override fun onPause() {
        super.onPause()
        // Log.d("lifecycle", "onPause invoked")
        mediaPlayer.stop()

    }
}