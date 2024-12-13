package com.capstone.skinaliyze.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.capstone.skinaliyze.databinding.FragmentProfileBinding
import com.capstone.skinaliyze.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private val PICK_IMAGE_REQUEST = 71
    private var profileImageUri: Uri? = null
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        val genderOptions = arrayOf("Male", "Female", "Other")
        val genderSpinner = binding.userGenderSpinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        profileViewModel.userProfile.observe(viewLifecycleOwner, Observer { userProfile ->
            binding.userNameEditText.setText(userProfile.username)
            binding.userAgeEditText.setText(userProfile.age.toString())
            binding.userGenderSpinner.setSelection(genderOptions.indexOf(userProfile.gender))
            if (userProfile.profilePicture.isNotEmpty()) {
                Glide.with(this)
                    .load(userProfile.profilePicture)
                    .into(binding.profilePicture)
            }
        })

        profileViewModel.getUserProfile()

        binding.editButton.setOnClickListener {
            isEditing = !isEditing
            binding.userNameEditText.isEnabled = isEditing
            binding.userAgeEditText.isEnabled = isEditing
            binding.userGenderSpinner.isEnabled = isEditing

            if (isEditing) {
                binding.editButton.text = "Save"
            } else {
                binding.editButton.text = "Edit"
            }

            if (!isEditing) {
                val username = binding.userNameEditText.text.toString()
                val ageText = binding.userAgeEditText.text.toString()
                val gender = binding.userGenderSpinner.selectedItem.toString()

                if (username.isEmpty() || ageText.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val age = try {
                    ageText.toInt()
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Invalid age format", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val profilePictureBase64 = profileImageUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    profileViewModel.encodeImageToBase64(bitmap)
                } ?: ""

                val updatedProfile = UserProfile(username, age, gender, profilePictureBase64)

                profileViewModel.updateUserProfile(updatedProfile)
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            profileViewModel.saveLoginStatus(false)
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()


            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finishAffinity()
        }


        binding.profilePicture.setOnClickListener {
            openGallery()
        }

        return root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            profileImageUri = data.data
            Glide.with(this)
                .load(profileImageUri)
                .into(binding.profilePicture)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
