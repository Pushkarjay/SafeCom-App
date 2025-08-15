package com.safecom.taskmanagement.ui.stub

// Stub implementations for missing UI elements to enable APK generation
// These can be properly implemented later

import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

// Stub binding classes for fragments that have missing UI elements
class StubFragmentTasksBinding {
    val fabCreateTask = FloatingActionButton(null)
    val btnSearch = MaterialButton(null)
    val rvTasks = RecyclerView(null)
    val progressBar = ProgressBar(null)
    val tvEmptyState = TextView(null)
    val chipGroupStatus = View(null)
    val chipGroupFilters = View(null)
    val etSearch = TextInputEditText(null)
}

class StubFragmentMessagesBinding {
    val btnSearch = MaterialButton(null)
    val swipeRefreshLayout = SwipeRefreshLayout(null)
    val rvConversations = RecyclerView(null)
    val progressBar = ProgressBar(null)
    val tvEmptyState = TextView(null)
}

class StubFragmentProfileBinding {
    val ivProfileImage = ImageView(null)
    val llNotifications = LinearLayout(null)
    val llSecurity = LinearLayout(null)
    val llAbout = LinearLayout(null)
    val llHelp = LinearLayout(null)
    val btnLogout = MaterialButton(null)
    val switchDarkMode = SwitchMaterial(null)
    val switchNotifications = SwitchMaterial(null)
    val progressBar = ProgressBar(null)
    val scrollView = NestedScrollView(null)
    val tvUserEmail = TextView(null)
    val tvUserRole = TextView(null)
    val tvMemberSince = TextView(null)
    val tvCompletedTasksCount = TextView(null)
    val tvPendingTasksCount = TextView(null)
}

// Stub binding classes for adapters
class StubItemTaskBinding {
    val tvPriority = TextView(null)
    val tvStatus = TextView(null)
    val tvDueDate = TextView(null)
    val ivDueDateIcon = ImageView(null)
    val tvAssignedTo = TextView(null)
    val tvTags = TextView(null)
    val btnMarkCompleted = MaterialButton(null)
    val btnMarkInProgress = MaterialButton(null)
    val viewPriorityIndicator = View(null)
    val root = View(null)
}

class StubItemConversationBinding {
    val tvParticipantName = TextView(null)
    val tvTimestamp = TextView(null)
    val ivParticipantAvatar = ImageView(null)
    val ivOnlineStatus = ImageView(null)
    val ivMutedIndicator = ImageView(null)
}

class StubItemRecentActivityBinding {
    val tvUserName = TextView(null)
    val tvTimestamp = TextView(null)
    val cardActivityIcon = CardView(null)
    val ivUserAvatar = ImageView(null)
    val root = View(null)
}
