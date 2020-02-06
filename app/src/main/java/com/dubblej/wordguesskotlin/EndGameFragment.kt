package com.dubblej.wordguesskotlin

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class EndGameFragment : DialogFragment() {

    interface OnInputListener {
        fun sendInput(userResponse: Int)
    }

    lateinit var onInputListener : OnInputListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).setTitle("Start a New Game?")

            .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    onInputListener.sendInput(Activity.RESULT_OK)
                }
            })
            .setNegativeButton("Quit", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    onInputListener.sendInput(Activity.RESULT_CANCELED)
                }
            }).create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            onInputListener = activity as OnInputListener
        } catch (ex : ClassCastException) {
            throw RuntimeException("$context must implement interface listener")
        }
    }
}