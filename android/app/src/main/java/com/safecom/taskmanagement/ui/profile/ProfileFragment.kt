package com.safecom.taskmanagement.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.safecom.taskmanagement.R
import com.safecom.taskmanagement.databinding.FragmentProfileBinding
import com.safecom.taskmanagement.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val profileViewModel: ProfileViewModel by viewModels()
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileViewModel.updateProfileImage(it) }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupObservers()
        
        // Load profile data
        profileViewModel.loadProfile()
    }
    
    private fun setupUI() {
        binding.ivProfileImage.setOnClickListener {
            showImagePickerDialog()
        }
        
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileToEditProfile()
            )
        }
        
        binding.llNotifications.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileToNotificationSettings()
            )
        }
        
        binding.llSecurity.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileToSecuritySettings()
            )
        }
        
        binding.llAbout.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileToAbout()
            )
        }
        
        binding.llHelp.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileToHelp()
            )
        }
        
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
        
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            profileViewModel.updateThemeMode(isChecked)
        }
        
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            profileViewModel.updateNotificationEnabled(isChecked)
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            profileViewModel.profileState.collect { state ->
                when (state) {
                    is ProfileState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.GONE
                    }
                    is ProfileState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE
                        updateProfileUI(state.profile)
                    }
                    is ProfileState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE
                        // Show error message
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            profileViewModel.logoutState.collect { loggedOut ->
                if (loggedOut) {
                    navigateToAuth()
                }
            }
        }
    }
    
    private fun updateProfileUI(profile: UserProfile) {
        binding.apply {
            tvUserName.text = profile.name
            tvUserEmail.text = profile.email
            tvUserRole.text = profile.role
            tvMemberSince.text = "Member since ${profile.memberSince}"
            
            // Load profile image
            Glide.with(requireContext())
                .load(profile.profileImageUrl)
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .circleCrop()
                .into(ivProfileImage)
            
            // Update task statistics
            tvCompletedTasksCount.text = profile.completedTasks.toString()
            tvPendingTasksCount.text = profile.pendingTasks.toString()
            
            // Update settings
            switchDarkMode.isChecked = profile.isDarkModeEnabled
            switchNotifications.isChecked = profile.isNotificationEnabled
        }
    }
    
    private fun showImagePickerDialog() {
        val options = arrayOf("Choose from Gallery", "Remove Photo")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Profile Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> imagePickerLauncher.launch("image/*")
                    1 -> profileViewModel.removeProfileImage()
                }
            }
            .show()
    }
    
    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                profileViewModel.logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun navigateToAuth() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
