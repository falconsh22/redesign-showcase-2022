package com.shahin.redesign.first

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.shahin.redesign.R
import com.shahin.redesign.databinding.FragmentFirstBinding
import com.shahin.redesign.extensions.setRootLayoutBottomMargin
import com.shahin.redesign.extensions.setVisibleOrGone

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val viewModel: FirstViewModel by activityViewModels()

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            if (imeHeight == 0 /*invisible keyboard*/) {
                if (blockOnReEntry(byPassCondition = viewModel.fastReEntry.value == false)) {
                    return@setOnApplyWindowInsetsListener insets
                }
                binding.guidelineHorizontal.setGuidelinePercent(0.6f)
                setEditTextViewMarginsAndPaddings(resources.getDimensionPixelSize(R.dimen.dimen_16))
                binding.cardViewOfflineMode.setVisibleOrGone(visible = true)
            } else {
                binding.guidelineHorizontal.setGuidelinePercent(1.0f)
                setEditTextViewMarginsAndPaddings(0)
                binding.cardViewOfflineMode.setVisibleOrGone(visible = false)
            }
            binding.root.setRootLayoutBottomMargin(imeHeight)
            return@setOnApplyWindowInsetsListener insets
        }

        // ---------------------------------------------------------------------------
        binding.editText.imeOptions = EditorInfo.IME_ACTION_GO
        binding.editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.editText.setOnEditorActionListener(
            TextView.OnEditorActionListener { textView, actionId, event ->
                if (textView?.text == null || textView.text.isBlank()) {
                    return@OnEditorActionListener false
                }
                when (actionId) {
                    EditorInfo.IME_ACTION_GO -> {
                        navigateToSecondActivity(textView.text.toString().trim())
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        )

        // ---------------------------------------------------------------------------

        binding.cardViewDialectSwitcher.root.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                viewModel.updateMotionLayoutProgress(progress)
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {}

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}

        })

        // ---------------------------------------------------------------------------

        binding.offlineMode.airplaneModeSwitch
            .setOnCheckedChangeListener { _, isChecked ->
                viewModel.setFastReEntry(isChecked)
            }
    }

    private fun blockOnReEntry(byPassCondition: Boolean): Boolean {
        if (byPassCondition) {
            return false
        }
        return if (viewModel.isReEntry.value == true) {
            binding.guidelineHorizontal.setGuidelinePercent(1.0f)
            setEditTextViewMarginsAndPaddings(0)
            binding.cardViewOfflineMode.setVisibleOrGone(visible = false)
            updateMotionLayoutState()
            viewModel.setIsReEntry(false)
            true
        } else {
            false
        }
    }

    private fun setEditTextViewMarginsAndPaddings(margin: Int) {
        val viewParams = binding.editTextLayout.layoutParams as ConstraintLayout.LayoutParams
        viewParams.topMargin = margin
        viewParams.marginStart = margin
        viewParams.marginEnd = margin
        binding.editTextLayout.layoutParams = viewParams
    }

    private fun navigateToSecondActivity(word: String) {
        var containerTransitionName = ""
        if (viewModel.motionLayoutProgress.value == 0.0f) {
            // animation is on start mode - English is at the left
            containerTransitionName = getString(R.string.container_transition_1)
            binding.editTextLayout.transitionName =  containerTransitionName
        } else {
            containerTransitionName = getString(R.string.container_transition_2)
            binding.editTextLayout.transitionName = containerTransitionName
        }
        findNavController().navigate(
            directions = FirstFragmentDirections.actionFirstFragmentToSecondFragment(
                word = word,
                containerTransitionName = containerTransitionName
            ),
            navigatorExtras = FragmentNavigatorExtras(
                binding.editTextLayout to containerTransitionName,
//                binding.editText to editTexTransitionName
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.fastReEntry.value == false) {
            requestEditTextFocus()
            updateMotionLayoutState()
        }
    }

    private fun requestEditTextFocus() {
        val text = binding.editText.text
        if (text.isNullOrEmpty()) {
            return
        }
        binding.editText.requestFocus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.editText.windowInsetsController?.show(WindowInsets.Type.ime())
        } else {
            // show keyboard the old way
            val ime = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            ime.showSoftInput(binding.editText, 0)
        }
    }

    private fun updateMotionLayoutState() {
        binding.cardViewDialectSwitcher.root.progress = viewModel.motionLayoutProgress.value ?: 0.0f
    }

    // By Commenting out this block and Not catching the reentry is also a good option that disables
    // the reentry transition animation
    // Also makes the UI faster as transition disables if yo remove this block
    override fun getReenterTransition(): Any? {
        if (viewModel.motionLayoutProgress.value == 0.0f) {
            // animation is on start mode - English is at the left
            binding.editTextLayout.transitionName =  getString(R.string.container_transition_1)
        } else {
            binding.editTextLayout.transitionName = getString(R.string.container_transition_2)
        }
        if (viewModel.fastReEntry.value == true) {
            requestEditTextFocus()
        }
        return super.getReenterTransition()
    }
}
