package com.safecom.taskmanagement.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.safecom.taskmanagement.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private val messagesViewModel: MessagesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup messages view
        setupMessagesView()
    }
    
    private fun setupMessagesView() {
        // Messages functionality will be implemented here
        // For now, the fragment will just show the layout
    }
}
