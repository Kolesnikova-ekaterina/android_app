package com.example.my_api.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.my_api.viewmodel.CharacterViewModel
import com.example.my_api.NetworkUtils
import com.example.my_api.R
import com.example.my_api.data.local.AppDatabase
import com.example.my_api.data.repository.GenshinRepositoryImpl
import com.example.my_api.databinding.FragmentCharacterListBinding

class CharacterListFragment : Fragment(R.layout.fragment_character_list) {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CharacterViewModel by viewModels{
        CharacterViewModelFactory(
            AppDatabase.getInstance(requireContext())
        )
    }

    private lateinit var adapter: CharacterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCharacterListBinding.bind(view)

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(context, "Нет соединения с интернетом", Toast.LENGTH_LONG).show()
            return
        }

        adapter = CharacterAdapter { name ->
            val action =
                CharacterListFragmentDirections.actionCharacterListFragmentToCharacterDetailFragment(name)
            findNavController().navigate(action)
        }

        binding.recyclerView.adapter = adapter

        viewModel.characters.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchCharacters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CharacterViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterViewModel(GenshinRepositoryImpl(db)) as T
    }
}