package com.safecom.taskmanagement.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.safecom.taskmanagement.databinding.FragmentMessagesBinding
import com.safecom.taskmanagement.ui.messages.adapter.ConversationsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessagesFragment : Fragment() {
    
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    
    private val messagesViewModel: MessagesViewModel by viewModels()
    private lateinit var conversationsAdapter: ConversationsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupRecyclerView()
        setupObservers()
        
        // Load conversations
        messagesViewModel.loadConversations()
    }
    
    private fun setupUI() {
        binding.fabNewMessage.setOnClickListener {
            findNavController().navigate(
                MessagesFragmentDirections.actionMessagesToNewMessage()
            )
        }
        
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            messagesViewModel.searchConversations(query)
        }
        
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString()
            messagesViewModel.searchConversations(query)
            true
        }
        
        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            messagesViewModel.refreshConversations()
        }
    }
    
    private fun setupRecyclerView() {
        conversationsAdapter = ConversationsAdapter { conversation ->
            findNavController().navigate(
                MessagesFragmentDirections.actionMessagesToChat(conversation.id)
            )
        }
        
        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = conversationsAdapter
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            messagesViewModel.conversationsState.collect { state ->
                binding.swipeRefreshLayout.isRefreshing = false
                
                when (state) {
                    is ConversationsState.Loading -> {
                        if (conversationsAdapter.currentList.isEmpty()) {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvConversations.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.GONE
                        }
                    }
                    is ConversationsState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (state.conversations.isEmpty()) {
                            binding.rvConversations.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.tvEmptyState.text = "No conversations yet"
                        } else {
                            binding.rvConversations.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                            conversationsAdapter.submitList(state.conversations)
                        }
                    }
                    is ConversationsState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        if (conversationsAdapter.currentList.isEmpty()) {
                            binding.rvConversations.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.tvEmptyState.text = state.message
                        }
                    }
                }
            }
        }
        
        lifecycleScope.launch {
            messagesViewModel.unreadCount.collect { count ->
                // Update unread badge if needed
                updateUnreadBadge(count)
            }
        }
    }
    
    private fun updateUnreadBadge(count: Int) {
        // TODO: Update navigation badge or toolbar with unread count
        if (count > 0) {
            // Show badge with count
        } else {
            // Hide badge
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh conversations when fragment becomes visible
        messagesViewModel.refreshConversations()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
