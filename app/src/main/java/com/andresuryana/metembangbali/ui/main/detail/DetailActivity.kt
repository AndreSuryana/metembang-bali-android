package com.andresuryana.metembangbali.ui.main.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.adapter.LyricsAdapter
import com.andresuryana.metembangbali.data.model.Tembang
import com.andresuryana.metembangbali.databinding.ActivityDetailBinding
import com.andresuryana.metembangbali.dialog.LoadingDialogFragment
import com.andresuryana.metembangbali.helper.AnimationHelper.animateFadeIn
import com.andresuryana.metembangbali.helper.AnimationHelper.animateFadeOut
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.helper.Helpers.formatDate
import com.andresuryana.metembangbali.utils.Ext.toMusicTimeline
import com.andresuryana.metembangbali.utils.event.TembangDetailEvent
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    // Layout binding
    private lateinit var binding: ActivityDetailBinding

    // View model
    private val viewModel: DetailViewModel by viewModels()

    // Loading dialog
    private val loadingDialog = LoadingDialogFragment()

    // Lyrics adapter
    private var lyricsAdapter: LyricsAdapter? = null

    // Popup menu
    private var popupMenu: PopupMenu? = null

    // Current tembang uid
    private var currentTembang: Tembang? = null

    // Exo player
    private var exoPlayer: ExoPlayer? = null

    // Exo player state & histories
    private var isMuted: Boolean = false
    private var currentVolume: Float? = null
    private var playerHistory = ArrayDeque<Tembang>()

    // Exo player polling timeline runnable & handler
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup exo player
        setupExoPlayer()

        // Setup seekbar
        setupSeekbar()

        // Setup popup menu
        setupPopupMenu()

        // Setup adapter
        lyricsAdapter = LyricsAdapter()

        // Setup recycler view
        binding.lyricsContainer.rvLyrics.layoutManager = LinearLayoutManager(this)

        // Get tembang uid from intent
        intent?.getStringExtra(EXTRA_TEMBANG_UID).let {
            if (it == null) throw IllegalArgumentException()
            else viewModel.getTembangDetail(it)
        }

        // Observe tembang
        viewModel.tembang.observe(this, this::tembangObserver)

        // Setup button listener
        setupButtonListener()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Clear exo player instance
        clearExoPlayer()
    }

    private fun setupExoPlayer() {
        // Init exo player
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.apply {
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
            shuffleModeEnabled = false
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        runnable = Runnable {
                            runOnUiThread {
                                exoPlayer?.let {
                                    binding.tvCurrentDuration.text =
                                        it.currentPosition.toMusicTimeline()
                                    binding.sbAudio.progress =
                                        ((it.currentPosition * 100) / it.duration).toInt()
                                }
                                handler.postDelayed(runnable, 1000)
                            }
                        }
                        handler.postDelayed(runnable, 1000L)
                    } else {
                        handler.removeCallbacks(runnable)
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        binding.tvMaxDuration.text = exoPlayer?.duration?.toMusicTimeline()
                    } else if (playbackState == Player.STATE_ENDED) {
                        resetExoPlayer()
                    }
                }
            })
        }
    }

    private fun setupSeekbar() {
        // Seekbar change listener
        binding.sbAudio.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                exoPlayer?.let {
                    val progressMs = (it.duration / 100) * (seekBar?.progress ?: 0)
                    binding.tvCurrentDuration.text = progressMs.toMusicTimeline()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                exoPlayer?.let {
                    val progressMs = (it.duration / 100) * (seekBar?.progress ?: 0)
                    it.seekTo(progressMs)
                }
            }
        })
    }

    private fun setupPopupMenu() {
        popupMenu = PopupMenu(this, binding.btnMore, Gravity.END).apply {
            inflate(R.menu.menu_detail)
            setOnMenuItemClickListener {
                onMenuItemClicked(it)
                true
            }
        }
    }

    private fun setupButtonListener() {
        // Button back listener
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Button more listener
        binding.btnMore.setOnClickListener {
            popupMenu?.show()
        }

        // Button play pause listener
        binding.btnPlayPause.setOnClickListener {
            if (exoPlayer?.isPlaying == true) {
                // If exo player is playing then set button state -> true
                exoPlayer?.pause()
                setButtonPlayState(false)
            } else {
                // Otherwise set button state -> false
                exoPlayer?.play()
                setButtonPlayState(true)
            }
        }

        // Button stop listener
        binding.btnStop.setOnClickListener {
            exoPlayer?.apply {
                // Pause and reset exo player
                pause()
                resetExoPlayer()
            }
            binding.btnPlayPause.setImageResource(R.drawable.ic_play_circle)
        }

        // Button mute listener
        binding.btnMute.setOnClickListener {
            if (exoPlayer?.isPlaying == true) {
                if (isMuted) {
                    // If exo player is muted then restore current volume
                    currentVolume?.let { exoPlayer?.volume = it }
                    isMuted = false
                    setButtonMuteState(false)
                } else {
                    // Otherwise store current volume and set exo volume to 0
                    currentVolume = exoPlayer?.volume
                    exoPlayer?.volume = 0f
                    isMuted = true
                    setButtonMuteState(true)
                }
            }
        }

        // Button next listener
        binding.btnNext.setOnClickListener {
            // Add tembang to stack
            playerHistory.addLast(currentTembang)

            // Release exo player
            exoPlayer?.release()

            // Get next tembang
            viewModel.getNextTembang()
        }

        // Button previous listener
        binding.btnPrevious.setOnClickListener {
            if (playerHistory.isNotEmpty()) {
                // If player history is not empty then pop from stack
                val tembang = playerHistory.removeLast()
                viewModel.getTembangDetail(tembang.id)
            } else {
                // Otherwise show error message
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_no_previous_tembang),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Button translate listener
        binding.lyricsContainer.btnTranslate.setOnClickListener {
            toggleTranslateTextState()
            toggleBtnTranslateState()
        }
    }

    private fun onMenuItemClicked(menu: MenuItem) {
        when (menu.itemId) {
            R.id.menu_lyrics -> {
                // Toggle lyrics container visibility
                toggleLyricsContainer()
            }
            R.id.menu_info_detail -> {
                // Toggle info detail container visibility
                toggleInfoDetailContainer()
            }
        }
    }

    private fun toggleLyricsContainer() {
        if (binding.detailContainer.root.visibility == View.VISIBLE) {
            toggleInfoDetailContainer()
        }

        if (binding.lyricsContainer.root.visibility == View.VISIBLE) {
            // If lyrics container visible then toggle visibility to gone
            binding.lyricsContainer.root.visibility = View.GONE

            // Start fade out animation
            binding.lyricsContainer.root.animateFadeOut(this, duration = 500L)

            // Update menu lyrics title
            popupMenu?.menu?.findItem(R.id.menu_lyrics)?.setTitle(R.string.menu_lyrics_show)
        } else {
            // Otherwise toggle visibility to visible
            binding.lyricsContainer.root.visibility = View.VISIBLE

            // Start fade in animation
            binding.lyricsContainer.root.animateFadeIn(this, duration = 500L)

            // Update menu lyrics title
            popupMenu?.menu?.findItem(R.id.menu_lyrics)?.setTitle(R.string.menu_lyrics_hide)
        }
    }

    private fun toggleInfoDetailContainer() {
        if (binding.lyricsContainer.root.visibility == View.VISIBLE) {
            toggleLyricsContainer()
        }

        if (binding.detailContainer.root.visibility == View.VISIBLE) {
            // If lyrics container visible then toggle visibility to gone
            binding.detailContainer.root.visibility = View.GONE

            // Start fade out animation
            binding.detailContainer.root.animateFadeOut(this, duration = 500L)

            // Update menu lyrics title
            popupMenu?.menu?.findItem(R.id.menu_info_detail)
                ?.setTitle(R.string.menu_info_detail_show)
        } else {
            // Otherwise toggle visibility to visible
            binding.detailContainer.root.visibility = View.VISIBLE

            // Start fade in animation
            binding.detailContainer.root.animateFadeIn(this, duration = 500L)

            // Update menu lyrics title
            popupMenu?.menu?.findItem(R.id.menu_info_detail)
                ?.setTitle(R.string.menu_info_detail_hide)
        }
    }

    private fun tembangObserver(event: TembangDetailEvent) {
        when (event) {
            is TembangDetailEvent.Success -> {
                loadingDialog.dismiss()

                // Reset media player info & state
                resetExoPlayer()
                binding.tvCurrentDuration.text = getString(R.string.duration_placeholder)
                binding.tvMaxDuration.text = getString(R.string.duration_placeholder)

                // Set tembang data info
                currentTembang = event.tembang
                binding.tvTembangTitle.text = event.tembang.title
                binding.tvCategory.text = Helpers.formatCategory(event.tembang)

                // Set tembang data detail info
                binding.detailContainer.apply {
                    tvAuthor.text = event.tembang.author ?: ("-")
                    tvCategory.text = event.tembang.category
                    tvSubCategory.text = event.tembang.subCategory ?: ("-")
                    tvMood.text = if (event.tembang.mood != null) event.tembang.mood.toString() else "-"
                    tvMeaning.text = event.tembang.meaning ?: ("-")
                    tvCoverSource.text = event.tembang.coverSource ?: ("-")
                    tvRule.text = if (event.tembang.rule != null) event.tembang.rule.toString() else "-"
                    tvUsage.text = if (event.tembang.usage?.isNotEmpty() == true) {
                        event.tembang.usage.joinToString {
                            it.activity
                        }
                    } else "-"
                    tvDateCreated.text = formatDate(event.tembang.createdAt) ?: ("-")
                }

                // Set lyrics adapter list
                lyricsAdapter?.setList(event.tembang.lyrics, event.tembang.lyricsIDN)
                binding.lyricsContainer.rvLyrics.adapter = lyricsAdapter

                // Button translate state
                binding.lyricsContainer.btnTranslate.isEnabled =
                    event.tembang.lyricsIDN?.isNotEmpty() == true

                // Inflate cover if exists
                Glide.with(binding.root)
                    .load(event.tembang.coverUrl)
                    .placeholder(R.drawable.ic_cover_placeholder)
                    .error(R.drawable.ic_cover_placeholder)
                    .centerCrop()
                    .into(binding.ivCover)

                // Prepare audio data if exists
                if (!event.tembang.audioUrl.isNullOrBlank()) {
                    // Update media button state
                    setMediaButtonState(true)

                    // Prepare media item for exo player
                    exoPlayer?.apply {
                        setMediaItem(
                            MediaItem.fromUri(event.tembang.audioUrl)
                        )
                        prepare()
                    }
                } else {
                    // Update media button state
                    setMediaButtonState(false)
                }
            }
            is TembangDetailEvent.Error -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                Timer().schedule(1000L) {
                    finish()
                }
            }
            is TembangDetailEvent.NetworkError -> {
                loadingDialog.dismiss()
                Helpers.snackBarError(
                    binding.root,
                    getString(R.string.error_default_network_error),
                    Snackbar.LENGTH_SHORT
                ).show()
                Timer().schedule(1000L) {
                    finish()
                }
            }
            is TembangDetailEvent.Loading -> {
                if (!loadingDialog.isAdded) {
                    loadingDialog.show(
                        supportFragmentManager,
                        LoadingDialogFragment::class.java.simpleName
                    )
                }
            }
        }
    }

    private fun setMediaButtonState(enabled: Boolean) {
        binding.apply {
            btnStop.isEnabled = enabled
            btnPlayPause.isEnabled = enabled
            btnMute.isEnabled = enabled
            sbAudio.isEnabled = enabled

            if (enabled) {
                btnStop.alpha = 1f
                btnPlayPause.alpha = 1f
                btnMute.alpha = 1f
            } else {
                btnStop.alpha = 0.5f
                btnPlayPause.alpha = 0.5f
                btnMute.alpha = 0.5f
            }
        }
    }

    private fun setButtonPlayState(isPlaying: Boolean) {
        if (isPlaying) binding.btnPlayPause.setImageResource(R.drawable.ic_pause_circle)
        else binding.btnPlayPause.setImageResource(R.drawable.ic_play_circle)
    }

    private fun setButtonMuteState(isMuted: Boolean) {
        if (isMuted) binding.btnMute.setImageResource(R.drawable.ic_volume_off)
        else binding.btnMute.setImageResource(R.drawable.ic_volume)
    }

    private fun toggleBtnTranslateState() {
        if (binding.lyricsContainer.btnTranslate.alpha == 0.5f) {
            binding.lyricsContainer.btnTranslate.apply {
                imageTintList =
                    ContextCompat.getColorStateList(this@DetailActivity, R.color.color_primary)
                alpha = 1f
            }
        } else {
            binding.lyricsContainer.btnTranslate.apply {
                imageTintList =
                    ContextCompat.getColorStateList(this@DetailActivity, R.color.color_secondary)
                alpha = 0.5f
            }
        }
    }

    private fun toggleTranslateTextState() {
        val itemCount = binding.lyricsContainer.rvLyrics.childCount
        for (i in 0 until (itemCount)) {
            val holder = binding.lyricsContainer.rvLyrics.findViewHolderForAdapterPosition(i)
            val tvLyricsIdn =
                holder?.itemView?.findViewById<MaterialTextView>(R.id.tv_lyrics_idn)
            tvLyricsIdn?.visibility =
                if (tvLyricsIdn?.visibility != View.VISIBLE) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun clearExoPlayer() {
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun resetExoPlayer() {
        exoPlayer?.seekTo(0)
        binding.sbAudio.progress = 0
    }

    companion object {
        const val EXTRA_TEMBANG_UID = "tembang_uid"
    }
}