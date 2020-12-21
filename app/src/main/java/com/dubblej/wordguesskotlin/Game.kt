package com.dubblej.wordguesskotlin

import android.annotation.SuppressLint
import android.content.Context
import java.util.*
import kotlin.collections.HashMap
import java.io.Serializable


class Game (context: Context) : Serializable {

    lateinit var word : String
    val playerWord : StringBuilder = StringBuilder("")
    var lettersMap = HashMap<Char, Boolean?>()
    private val words = arrayListOf<String>()
    var autoStart : Boolean = false

    init {

        val  inputStream = context.resources.openRawResource(R.raw.words)
        
        val scanner = Scanner(inputStream)

        while (scanner.hasNextLine()) {
            val word = scanner.nextLine()
            if (word.length > 3 && !word.contains("-")) {
                words.add(word)
            }
        }

        scanner.close()

        startNewGame()

    }

    @SuppressLint("DefaultLocale")
    fun startNewGame() {
        val num  : Int = (Math.random() * words.size).toInt()
        word = words[num].toUpperCase()
        playerWord.delete(0, playerWord.length)
        for (i in word.indices)
            playerWord.append('*')
        lettersMap.clear()
        println(lettersMap.keys)
    }

    fun checkUserLetter(letter : Char) : Boolean {

        var inWord = false

        for (i in word.indices) {
           if(word[i].toUpperCase() == letter) {
                playerWord.setCharAt(i, word[i])
                inWord = true
                lettersMap.put(letter, inWord)
            } else {
                lettersMap.put(letter, inWord)
            }
        }
        return inWord
    }

    fun isGameComplete() : Boolean {
        return playerWord.toString() == word
    }


}