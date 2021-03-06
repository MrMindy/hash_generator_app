package br.com.hashgenerator

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.hashgenerator.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        binding.generateHashButton.setOnClickListener {onGenerateClick()}

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val hasAlgorithms = resources.getStringArray(R.array.hash_algorithms)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, hasAlgorithms)

        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    private suspend fun applyAnimation() {

        binding.generateHashButton.isClickable = false

        binding.titleTextView.animate()
            .alpha(0f)
            .translationXBy(1200f)
        binding.generateHashButton.animate()
            .alpha(0f)
            .translationXBy(1200f)
        binding.textInputLayout.animate()
            .alpha(0f)
            .translationXBy(-1200f)
            .duration = 400L
        binding.plainText.animate()
            .alpha(0f)
            .translationXBy(1200f)
            .duration = 400L

        delay(300)

        binding.successBackground.animate().alpha(1f).duration = 600L
        binding.successBackground.animate().rotationBy(720f).duration = 600L
        binding.successBackground.animate().scaleXBy(900f).duration = 800L
        binding.successBackground.animate().scaleYBy(900f).duration = 800L

        delay(300)

        binding.successImageView.animate().alpha(1f).duration = 1000L

        delay(1500L)

    }

    private fun navigateToSuccess(hash: String) {
        val directions = HomeFragmentDirections.actionHomeFragmentToSuccessFragment(hash)
        findNavController().navigate(directions)
    }

    private fun onGenerateClick() {

        if (binding.plainText.text.isEmpty()) {
            showSnackBar("Field Empty")
            return
        }

        lifecycleScope.launch {
            applyAnimation()
            navigateToSuccess(getHashData())
        }
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            binding.rootLayout,
            message,
            Snackbar.LENGTH_SHORT
        )

        snackBar.setAction("Okay") {}
        snackBar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        snackBar.show()

    }

    private fun getHashData() : String {
        val algorithm = binding.autoCompleteTextView.text.toString()
        val plainText = binding.plainText.text.toString()

        return homeViewModel.getHash(plainText, algorithm)
    }

    private fun clearFields(){
        binding.plainText.text.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clear) {
            clearFields()
            showSnackBar("Clear")
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}