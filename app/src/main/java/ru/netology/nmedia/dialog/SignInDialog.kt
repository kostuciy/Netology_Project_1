package ru.netology.nmedia.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R

class SignInDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage(getString(R.string.dialog_sign_in))
                .setPositiveButton(getString(R.string.dialog_sign_in_yes)) { dialog, id ->
//                    findNavController().popBackStack(R.id.signInFragment, true)
                    findNavController().navigate(R.id.signInFragment)
                }
                .setNegativeButton(getString(R.string.dialog_sign_in_no)) { dialog, id ->
                    dismiss()
//                    findNavController().navigateUp() TODO: check if needed
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}