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
import com.shahin.redesign.TransitionMode
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

        when (navArgs.mode) {
            TransitionMode.English -> {
                binding.cardView1.transitionName = getString(R.string.container_transition_1)
            }
            TransitionMode.Spanish -> {
                binding.cardView2.transitionName = getString(R.string.container_transition_1)
            }
        }

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

        when (navArgs.mode) {
            TransitionMode.English -> {
                binding.text1.text = navArgs.word
                binding.text2.text = "Spanish translation"
            }
            TransitionMode.Spanish -> {
                binding.text1.text = "English translation"
                binding.text2.text = navArgs.word
            }
        }

        binding.cardView1.setOnClickListener {
            binding.cardView1.transitionName = getString(R.string.container_transition_1)
            binding.cardView2.transitionName = null
            viewModel.updateMotionLayoutProgress(0.0f)
            navigateBack()
        }

        binding.cardView2.setOnClickListener {
            binding.cardView1.transitionName = null
            binding.cardView2.transitionName = getString(R.string.container_transition_1)
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
