package com.shahin.redesign.second

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.*
import com.google.android.material.transition.MaterialContainerTransform
import com.shahin.redesign.R
import com.shahin.redesign.databinding.FragmentSecondBinding
import com.shahin.redesign.first.FirstViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val viewModel: FirstViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val navArgs: SecondFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        val changeBounds = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            setAllContainerColors(Color.TRANSPARENT)
//            duration = 400
        }
        sharedElementEnterTransition = changeBounds
//        sharedElementReturnTransition = transitionSet
        enterTransition = Fade().apply {
//            duration = 400
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })

        when (navArgs.containerTransitionName) {
            getString(R.string.container_transition_1) -> {
                binding.text1.setText(navArgs.word)
                binding.text2.setText("Spanish translation")
            }
            getString(R.string.container_transition_2) -> {
                binding.text1.setText("English translation")
                binding.text2.setText(navArgs.word)
            }
        }

        binding.cardView1.setOnClickListener {
            viewModel.updateMotionLayoutProgress(0.0f)
            navigateBack()
        }

        binding.cardView2.setOnClickListener {
            viewModel.updateMotionLayoutProgress(1.0f)
            navigateBack()
        }
    }

    private fun navigateBack() {
        viewModel.setIsReEntry(true)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
