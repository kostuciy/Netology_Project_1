package ru.netology.nmedia.activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignViewModel

class SignUpFragment : Fragment() {
    private val viewModel: SignViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)

        fun checkPasswords(): Boolean =
            binding.password.text.toString() == binding.passwordConfirm.text.toString()

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == ImagePicker.RESULT_ERROR) {
                    Toast
                        .makeText(requireContext(), R.string.error_empty_content, Toast.LENGTH_LONG)
                        .show()
                    return@registerForActivityResult
                }

                val uri = result.data?.data ?: return@registerForActivityResult
                viewModel.setImage(uri)
            }


        binding.apply {
            showPassword.setOnClickListener {
                password.transformationMethod =
                    if (!showPassword.isChecked) PasswordTransformationMethod.getInstance()
                    else HideReturnsTransformationMethod.getInstance()
            }
            signUp.setOnClickListener {
                viewModel.toDefault()
                if (!checkPasswords()) {
                    passwordConfirm.text.clear()
                    return@setOnClickListener
                }
                viewModel.register(
                    login.text.toString(),
                    password.text.toString(),
                    username.text.toString(),
                    viewModel.avatar.value
                )
            }
            camera.setOnClickListener {
                ImagePicker.with(this@SignUpFragment)
                    .cameraOnly()
                    .crop()
                    .compress(2_048)
                    .createIntent(launcher::launch)
            }
            gallery.setOnClickListener {
                ImagePicker.with(this@SignUpFragment)
                    .galleryOnly()
                    .crop()
                    .compress(2_048)
                    .createIntent(launcher::launch)
            }
        }

        viewModel.apply {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    avatar.collect { uri ->
                        if (uri == null) {
                            val drawable =
                                AppCompatResources.getDrawable(
                                    requireContext(),
                                    com.github.dhaval2404.imagepicker.R.drawable.ic_photo_black_48dp
                                ) ?: throw NullPointerException()
                            binding.avatar.setImageDrawable(drawable)
                        } else binding.avatar.setImageURI(uri)
                    }
                }
            }
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    state.collect { state ->
                        Log.d("GUG", "---------------------")
                        when {
                            state.loading -> {
                                binding.signUp.isClickable = false
                                binding.password.isEnabled = false
                                binding.passwordConfirm.isEnabled = false
                                binding.login.isEnabled = false
                                binding.username.isEnabled = false
                            }
                            state.error -> {
                                binding.signUp.isClickable = true
                                binding.password.text.clear()
                                binding.hint.text = getString(R.string.error_sign_in)
                                binding.password.isEnabled = true
                                binding.passwordConfirm.isEnabled = true
                                binding.login.isEnabled = true
                                binding.username.isEnabled = true
                            }
                            state.signedIn -> {
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
            }
        }



        return binding.root
    }



}