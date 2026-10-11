package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.NkSocialRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab {
    FEED,
    SHORTS,
    CREATE,
    EXPLORE,
    PROFILE
}

enum class SubScreen {
    NONE,
    MESSAGES,
    CHAT_DETAIL,
    NOTIFICATIONS,
    ADMIN_PANEL,
    SETTINGS,
    SAVED_POSTS,
    BLOCKED_USERS
}

data class ReportTarget(
    val type: String, // "POST", "SHORT", "USER"
    val id: String,
    val title: String
)

class NkSocialViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = NkSocialRepository(database.nkDao())

    // UI Navigation & Shell State
    private val _currentTab = MutableStateFlow(MainTab.FEED)
    val currentTab = _currentTab.asStateFlow()

    private val _subScreen = MutableStateFlow(SubScreen.NONE)
    val subScreen = _subScreen.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true) // Default modern dark theme for NK Social 2.0
    val isDarkMode = _isDarkMode.asStateFlow()

    // Active Overlays
    private val _activeStory = MutableStateFlow<StoryEntity?>(null)
    val activeStory = _activeStory.asStateFlow()

    private val _activeCommentsPost = MutableStateFlow<PostEntity?>(null)
    val activeCommentsPost = _activeCommentsPost.asStateFlow()

    private val _activeSharePost = MutableStateFlow<PostEntity?>(null)
    val activeSharePost = _activeSharePost.asStateFlow()

    private val _activeReportTarget = MutableStateFlow<ReportTarget?>(null)
    val activeReportTarget = _activeReportTarget.asStateFlow()

    private val _chatRecipient = MutableStateFlow<UserEntity?>(null)
    val chatRecipient = _chatRecipient.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    // User session
    val currentUser = repository.currentUser

    // Data streams
    val feedPosts = repository.getFeedPosts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savedPosts = repository.getSavedPosts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val likedPosts = repository.getLikedPosts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val shorts = repository.getAllShorts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val stories = repository.getActiveStories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allUsers = repository.getAllUsers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notifications = repository.getNotifications().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminReports = repository.getAllReports().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val blockedUsers = repository.getBlockedUsers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchUsersResults = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.getAllUsers()
        else repository.searchUsers(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchPostsResults = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.getFeedPosts()
        else repository.searchPosts(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Comments Stream
    val currentComments = _activeCommentsPost.flatMapLatest { post ->
        if (post == null) flowOf(emptyList())
        else repository.getComments(post.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Conversation Messages Stream
    val activeConversationMessages = combine(currentUser, _chatRecipient) { user, recipient ->
        if (user != null && recipient != null) {
            val convId = listOf(user.id, recipient.id).sorted().joinToString("_", prefix = "conv_")
            convId
        } else null
    }.flatMapLatest { convId ->
        if (convId != null) repository.getConversationMessages(convId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeSession()
        }
    }

    // Navigation triggers
    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
        _subScreen.value = SubScreen.NONE
    }

    fun openSubScreen(screen: SubScreen) {
        _subScreen.value = screen
    }

    fun navigateBack() {
        if (_subScreen.value != SubScreen.NONE) {
            _subScreen.value = SubScreen.NONE
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Overlay Setters
    fun openStory(story: StoryEntity) {
        _activeStory.value = story
        viewModelScope.launch {
            repository.markStorySeen(story.id)
        }
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun openComments(post: PostEntity) {
        _activeCommentsPost.value = post
    }

    fun closeComments() {
        _activeCommentsPost.value = null
    }

    fun openShare(post: PostEntity) {
        _activeSharePost.value = post
    }

    fun closeShare() {
        _activeSharePost.value = null
    }

    fun openReport(target: ReportTarget) {
        _activeReportTarget.value = target
    }

    fun closeReport() {
        _activeReportTarget.value = null
    }

    fun openChat(user: UserEntity) {
        _chatRecipient.value = user
        _subScreen.value = SubScreen.CHAT_DETAIL
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // AUTH ACTIONS
    fun login(emailOrPhone: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.login(emailOrPhone, pass)
            res.fold(
                onSuccess = {
                    showSnackbar("Welcome back, ${it.displayName}!")
                    onSuccess()
                },
                onFailure = {
                    onError(it.message ?: "Login failed")
                }
            )
        }
    }

    fun register(
        username: String,
        displayName: String,
        emailOrPhone: String,
        bio: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.register(username, displayName, emailOrPhone, bio)
            res.fold(
                onSuccess = {
                    showSnackbar("Welcome to NK Social 2.0, ${it.displayName}!")
                    onSuccess()
                },
                onFailure = {
                    onError(it.message ?: "Registration failed")
                }
            )
        }
    }

    fun switchAccount(user: UserEntity) {
        viewModelScope.launch {
            repository.switchAccount(user)
            showSnackbar("Switched to @${user.username}")
        }
    }

    fun logout() {
        repository.logout()
        showSnackbar("Logged out")
    }

    fun updateProfile(displayName: String, bio: String, website: String, isPrivate: Boolean) {
        viewModelScope.launch {
            repository.updateProfile(displayName, bio, website, isPrivate)
            showSnackbar("Profile updated successfully")
        }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCurrentAccount()
            showSnackbar("Account and personal data removed")
            onComplete()
        }
    }

    // SOCIAL ACTIONS
    fun toggleLike(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleLikePost(post)
        }
    }

    fun toggleSave(post: PostEntity) {
        viewModelScope.launch {
            repository.toggleSavePost(post)
            showSnackbar(if (!post.isSavedByMe) "Saved to your collection" else "Removed from saved")
        }
    }

    fun toggleFollow(user: UserEntity) {
        viewModelScope.launch {
            repository.toggleFollow(user)
            showSnackbar(if (!user.isFollowedByMe) "Now following @${user.username}" else "Unfollowed @${user.username}")
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(postId, text.trim())
            showSnackbar("Comment posted")
        }
    }

    fun toggleLikeShort(short: ShortVideoEntity) {
        viewModelScope.launch {
            repository.toggleLikeShort(short)
        }
    }

    fun createPost(caption: String, mediaUrl: String, location: String, hashtags: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            repository.createPost(caption, mediaUrl, location, hashtags)
            showSnackbar("Post published to NK Feed! ✨")
            _currentTab.value = MainTab.FEED
            onFinished()
        }
    }

    fun uploadShort(caption: String, mediaUrl: String, soundTitle: String, hashtags: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            repository.uploadShort(caption, mediaUrl, soundTitle, hashtags)
            showSnackbar("Short video published! 🎬")
            _currentTab.value = MainTab.SHORTS
            onFinished()
        }
    }

    fun uploadStory(caption: String, mediaUrl: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            repository.createStory(caption, mediaUrl)
            showSnackbar("Story added (available for 24h) ⏱️")
            _currentTab.value = MainTab.FEED
            onFinished()
        }
    }

    // MESSAGING
    fun sendMessage(recipientId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(recipientId, content.trim())
        }
    }

    // NOTIFICATIONS
    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            showSnackbar("Notifications cleared")
        }
    }

    // SAFETY & REPORTING
    fun submitReport(reason: String, details: String) {
        val target = _activeReportTarget.value ?: return
        viewModelScope.launch {
            repository.reportContent(target.type, target.id, target.title, reason, details)
            closeReport()
            showSnackbar("Thank you. Report received for admin review.")
        }
    }

    fun blockUser(user: UserEntity) {
        viewModelScope.launch {
            repository.blockUser(user)
            showSnackbar("Blocked @${user.username}. You will no longer see their content.")
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            repository.unblockUser(userId)
            showSnackbar("User unblocked")
        }
    }

    // ADMIN ACTIONS
    fun toggleBanUser(user: UserEntity) {
        viewModelScope.launch {
            val newStatus = !user.isBanned
            repository.setBannedStatus(user.id, newStatus)
            showSnackbar(if (newStatus) "User @${user.username} suspended" else "User @${user.username} unsuspended")
        }
    }

    fun resolveReport(reportId: String, status: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, status)
            showSnackbar("Report marked as $status")
        }
    }

    fun adminRemovePost(postId: String) {
        viewModelScope.launch {
            repository.adminDeletePost(postId)
            showSnackbar("Post removed by administrator")
        }
    }
}
