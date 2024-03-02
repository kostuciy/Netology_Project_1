package ru.netology.nmedia.activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel

class SignInFragment : Fragment() {
    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentSignInBinding.inflate(inflater, container, false)

        binding.apply {
            showPassword.setOnClickListener {
                password.transformationMethod =
                    if (!showPassword.isChecked) PasswordTransformationMethod.getInstance()
                    else HideReturnsTransformationMethod.getInstance()
            }
            signIn.setOnClickListener {
                viewModel.toDefault()
                viewModel.authenticate(
                    login.text.toString(),
                    password.text.toString()
                )
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { state ->
                    when {
                        state.loading -> {
                            binding.signIn.isClickable = false
                            binding.password.isEnabled = false
                            binding.login.isEnabled = false
                        }
                        state.error -> {
                            binding.signIn.isClickable = true
                            binding.password.text.clear()
                            binding.hint.text = getString(R.string.error_sign_in)
                            binding.password.isEnabled = true
                            binding.login.isEnabled = true
                        }
                        state.signedIn -> {
//                            findNavController().navigate(R.id.feedFragment)
                            findNavController().navigateUp()
                            viewModel.toDefault()
                        }
                    }
                }
            }
        }

        return binding.root
    }


}