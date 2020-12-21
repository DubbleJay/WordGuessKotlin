package com.dubblej.wordguesskotlin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.animation.AlphaAnimation
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.view.*


class MainActivity : AppCompatActivity(), EndGameFragment.OnInputListener {

    private lateinit var game: Game
    private lateinit var soundPool: SoundPool
    private var correctSoundId : Int = 0
    private var wrongSoundId : Int = 0
    private var endGameSoundId : Int = 0
    private val KEY_GAME = "GAME"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null) {
            game = savedInstanceState.getSerializable(KEY_GAME)as Game
        }

        else {
            game = Game(this)
            newGameAnimation()
        }

        soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        correctSoundId = soundPool.load(this, R.raw.correctsoundeffect, 1)
        wrongSoundId = soundPool.load(this, R.raw.wrondsoundeffect, 1)
        endGameSoundId = soundPool.load(this, R.raw.endgamesound, 1)

        playerWordTextView.text = game.playerWord

        lettersRecyclerView.adapter = LetterSquareAdapter()

    }

    override fun sendInput(userResponse: Int) {

        if (userResponse == Activity.RESULT_OK) {
            game.startNewGame()
            newGameAnimation()
        }

        else
            finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(KEY_GAME, game)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val autoStartMenuItem = menu.getItem(0)
        autoStartMenuItem.isChecked = game.autoStartNewGame
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.auto_start -> {
                game.autoStartNewGame = !game.autoStartNewGame
                item.isChecked = !item.isChecked
            }

            R.id.start_new_game -> {
                if (!game.isGameComplete()) {
                    game.startNewGame()
                    newGameAnimation()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun newGameAnimation () {

        val animation : LayoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        lettersRecyclerView.layoutAnimation = animation
        lettersRecyclerView.adapter = LetterSquareAdapter()

        val textViewAnimation : Animation = AnimationUtils.loadAnimation(this, R.anim.text_view_from_left)
        playerWordTextView.text = game.playerWord
        playerWordTextView.animation = textViewAnimation
    }

    private fun endGameAnimation() {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500
        anim.start()
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = 10
        playerWordTextView.startAnimation(anim)

        soundPool.play(endGameSoundId, 1f, 1f, 0, 0, 1f)


        Handler().postDelayed( {

            if(game.autoStartNewGame) {
                game.startNewGame()
                newGameAnimation()
            }
            else {
                val endGameFragment = EndGameFragment()
                endGameFragment.show(supportFragmentManager, "MyCustomDialog")
            }
        }, 5000)
    }

    private inner class LetterSquareAdapter : RecyclerView.Adapter<LetterSquareHolder>() {

        private val letters = Array(26) { i -> ('A' + i)}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterSquareHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.letter_square, parent, false)
            return LetterSquareHolder(view)
        }

        override fun onBindViewHolder(holder: LetterSquareHolder, position: Int) {
            holder.bindLetterSquare(letters[position])
        }

        override fun getItemCount(): Int {
            return letters.size
        }
    }

    private inner  class LetterSquareHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var letterTextView = itemView.findViewById<TextView>(R.id.letterTextView)
        private val redXTextView = itemView.findViewById<TextView>(R.id.redXTextView)
        private val greenCheckImageView = itemView.findViewById<ImageView>(R.id.greenCheckImageView)

        fun bindLetterSquare(letter : Char) {
            itemView.setOnClickListener(this)
            letterTextView.text = letter.toString()

            if (game.lettersMap.containsKey(letter)) {

                if (game.lettersMap.get(letter) == true)
                    greenCheckImageView.isVisible = true

                else
                    redXTextView.isVisible = true
            }
        }

        override fun onClick(v: View) {

            if (!game.lettersMap.containsKey(letterTextView.text[0]) && !game.isGameComplete()) {

                if (game.checkUserLetter(letterTextView.text[0])) {
                    greenCheckImageView.isVisible = true
                    playerWordTextView.text = game.playerWord
                    soundPool.play(correctSoundId, 1f ,1f, 0, 0, 1f)
                    if(game.isGameComplete()) {
                        endGameAnimation()
                    }

                } else {
                    redXTextView.isVisible = true
                    soundPool.play(wrongSoundId, 1f, 1f, 0, 0, 1f)
                }
            }

            else
                return

        }
    }
}
