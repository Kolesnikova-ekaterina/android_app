package com.example.my_api.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.my_api.R
import com.example.my_api.databinding.FragmentCharacterDetailBinding
import com.example.my_api.NetworkUtils
import com.example.my_api.viewmodel.CharacterViewModel
import com.bumptech.glide.Glide
import com.example.my_api.data.local.AppDatabase
import com.example.my_api.data.repository.GenshinRepository
import com.example.my_api.data.repository.GenshinRepositoryImpl

class CharacterDetailFragment : Fragment(R.layout.fragment_character_detail) {

    private var _binding: FragmentCharacterDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CharacterViewModel
    //private val database = AppDatabase.getInstance(requireContext())
    //private val viewModel: CharacterViewModel by viewModels { CharacterViewModelFactory(database) }
    private val args: CharacterDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCharacterDetailBinding.bind(view)


        val database = AppDatabase.getInstance(requireContext())
        viewModel = ViewModelProvider(this, CharacterViewModelFactory(database))
            .get(CharacterViewModel::class.java)
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(context, "Нет соединения с интернетом", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.characterDetails.observe(viewLifecycleOwner) { character ->
            binding.apply {
                tvName.text = character.name.replaceFirstChar { it.uppercaseChar() }
                tvVision.text = "Vision: ${character.vision}"
                tvWeapon.text = "Weapon: ${character.weapon}"
                tvNation.text = "Nation: ${character.nation}"
                tvAffiliation.text = "Affiliation: ${character.affiliation}"
                tvRarity.text = "Rarity: ${character.rarity}★"
                tvConstellation.text = "Constellation: ${character.constellation}"
                tvBirthday.text = "Birthday: ${character.birthday}"
                tvDescription.text = character.description
                //Toast.makeText(context, character.card, Toast.LENGTH_LONG).show()
                    Glide.with(this@CharacterDetailFragment)
                   // .load("https://genshin.jmp.blue/characters/"+character.id+"/card")
                        .load(character.card)
                        .placeholder(R.drawable.placeholder) // опционально: картинка-заглушка
                    .error(R.drawable.error_image)       // опционально: картинка при ошибке загрузки
                    .into(ivCharacterImage)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchCharacterDetails(args.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
