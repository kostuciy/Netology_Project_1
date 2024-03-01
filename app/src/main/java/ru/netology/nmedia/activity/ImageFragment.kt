package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.view.loadImageAttachment

class ImageFragment : Fragment() {
    val arguments: ImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentImageBinding.inflate(inflater, container, false)

        val imageUrl = arguments.imageUrl
        if (imageUrl != null) {
            binding.errorText.isGone = true
            binding.image.loadImageAttachment(imageUrl)
            binding.image.isVisible = true
        } else {
            binding.errorText.isVisible = true
            binding.image.isGone = true
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}