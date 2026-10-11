package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: NkSocialViewModel = viewModel()
            val isDarkMode by vm.isDarkMode.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkMode) {
                NkSocialApp(vm = vm)
            }
        }
    }
}

@Composable
fun NkSocialApp(vm: NkSocialViewModel) {
    val currentUser by vm.currentUser.collectAsStateWithLifecycle()
    val currentTab by vm.currentTab.collectAsStateWithLifecycle()
    val subScreen by vm.subScreen.collectAsStateWithLifecycle()
    val isDarkMode by vm.isDarkMode.collectAsStateWithLifecycle()

    val feedPosts by vm.feedPosts.collectAsStateWithLifecycle()
    val savedPosts by vm.savedPosts.collectAsStateWithLifecycle()
    val likedPosts by vm.likedPosts.collectAsStateWithLifecycle()
    val shorts by vm.shorts.collectAsStateWithLifecycle()
    val stories by vm.stories.collectAsStateWithLifecycle()
    val allUsers by vm.allUsers.collectAsStateWithLifecycle()
    val notifications by vm.notifications.collectAsStateWithLifecycle()
    val adminReports by vm.adminReports.collectAsStateWithLifecycle()
    val blockedUsers by vm.blockedUsers.collectAsStateWithLifecycle()

    val searchQuery by vm.searchQuery.collectAsStateWithLifecycle()
    val searchUsersResults by vm.searchUsersResults.collectAsStateWithLifecycle()
    val searchPostsResults by vm.searchPostsResults.collectAsStateWithLifecycle()

    val activeStory by vm.activeStory.collectAsStateWithLifecycle()
    val activeCommentsPost by vm.activeCommentsPost.collectAsStateWithLifecycle()
    val currentComments by vm.currentComments.collectAsStateWithLifecycle()
    val activeSharePost by vm.activeSharePost.collectAsStateWithLifecycle()
    val activeReportTarget by vm.activeReportTarget.collectAsStateWithLifecycle()
    val chatRecipient by vm.chatRecipient.collectAsStateWithLifecycle()
    val activeConversationMessages by vm.activeConversationMessages.collectAsStateWithLifecycle()

    val snackbarMessage by vm.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearSnackbar()
        }
    }

    // Android Hardware / Predictive Back Button handling
    BackHandler(enabled = subScreen != SubScreen.NONE || currentTab != MainTab.FEED) {
        if (subScreen != SubScreen.NONE) {
            vm.navigateBack()
        } else if (currentTab != MainTab.FEED) {
            vm.selectTab(MainTab.FEED)
        }
    }

    // 1. Unauthenticated State
    if (currentUser == null) {
        AuthScreen(
            onLoginSuccess = { email, pass ->
                vm.login(email, pass, onSuccess = {}, onError = { vm.showSnackbar(it) })
            },
            onRegisterSuccess = { username, name, email, bio ->
                vm.register(username, name, email, bio, onSuccess = {}, onError = { vm.showSnackbar(it) })
            },
            onQuickSwitchUser = { user ->
                vm.switchAccount(user)
            },
            availableUsers = allUsers
        )
        return
    }

    val user = currentUser!!

    // 2. Sub-screens (Messages, Chat, Notifications, Admin Panel, Settings)
    when (subScreen) {
        SubScreen.MESSAGES -> {
            MessagesScreen(
                users = allUsers.filter { it.id != user.id },
                onSelectUser = { vm.openChat(it) },
                onBack = { vm.navigateBack() }
            )
            return
        }

        SubScreen.CHAT_DETAIL -> {
            chatRecipient?.let { recipient ->
                ChatDetailScreen(
                    currentUserId = user.id,
                    recipient = recipient,
                    messages = activeConversationMessages,
                    onSendMessage = { text -> vm.sendMessage(recipient.id, text) },
                    onBack = { vm.navigateBack() },
                    onBlockUser = { targetUser ->
                        vm.blockUser(targetUser)
                        vm.navigateBack()
                    },
                    onReportUser = { targetUser ->
                        vm.openReport(ReportTarget("USER", targetUser.id, "@${targetUser.username}"))
                    }
                )
            }
            return
        }

        SubScreen.NOTIFICATIONS -> {
            NotificationsScreen(
                notifications = notifications,
                onMarkAllRead = { vm.markNotificationsRead() },
                onClearAll = { vm.clearNotifications() },
                onBack = { vm.navigateBack() }
            )
            return
        }

        SubScreen.ADMIN_PANEL -> {
            AdminPanelScreen(
                reports = adminReports,
                users = allUsers,
                onResolveReport = { id, status -> vm.resolveReport(id, status) },
                onDeleteReportedPost = { postId -> vm.adminRemovePost(postId) },
                onToggleBanUser = { targetUser -> vm.toggleBanUser(targetUser) },
                onBack = { vm.navigateBack() }
            )
            return
        }

        SubScreen.SETTINGS -> {
            SettingsScreen(
                user = user,
                isDarkMode = isDarkMode,
                blockedUsers = blockedUsers,
                onToggleDarkMode = { vm.toggleDarkMode() },
                onTogglePrivacy = { isPrivate -> vm.updateProfile(user.displayName, user.bio, user.website, isPrivate) },
                onUnblockUser = { blockedId -> vm.unblockUser(blockedId) },
                onLogout = { vm.logout() },
                onDeleteAccount = { vm.deleteAccount {} },
                onBack = { vm.navigateBack() }
            )
            return
        }

        else -> {
            // Main Bottom Tab Shell
        }
    }

    // 3. Main Shell with Top Bar, Bottom Bar, and Tab Content
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentTab != MainTab.SHORTS && currentTab != MainTab.CREATE) {
                NkTopBar(
                    isDarkMode = isDarkMode,
                    unreadNotifications = notifications.count { !it.isRead },
                    unreadMessages = 1,
                    onToggleDarkMode = { vm.toggleDarkMode() },
                    onOpenNotifications = { vm.openSubScreen(SubScreen.NOTIFICATIONS) },
                    onOpenMessages = { vm.openSubScreen(SubScreen.MESSAGES) },
                    onAdminClick = if (user.role == "ADMIN") {
                        { vm.openSubScreen(SubScreen.ADMIN_PANEL) }
                    } else null
                )
            }
        },
        bottomBar = {
            NkBottomBar(
                currentTab = currentTab,
                onTabSelected = { vm.selectTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.FEED -> {
                    FeedScreen(
                        currentUserId = user.id,
                        stories = stories,
                        posts = feedPosts,
                        onAddStoryClick = { vm.selectTab(MainTab.CREATE) },
                        onStoryClick = { vm.openStory(it) },
                        onLikePost = { vm.toggleLike(it) },
                        onCommentClick = { vm.openComments(it) },
                        onShareClick = { vm.openShare(it) },
                        onSaveClick = { vm.toggleSave(it) },
                        onReportClick = { post ->
                            vm.openReport(ReportTarget("POST", post.id, post.caption))
                        },
                        onBlockUser = { authorId, authorUsername ->
                            allUsers.find { it.id == authorId }?.let { vm.blockUser(it) }
                        },
                        onDeletePost = { postId ->
                            vm.adminRemovePost(postId)
                        }
                    )
                }

                MainTab.SHORTS -> {
                    ShortsScreen(
                        shorts = shorts,
                        onLikeShort = { vm.toggleLikeShort(it) },
                        onCommentClick = { /* Can show comment sheet */ },
                        onShareClick = { /* Share short */ },
                        onFollowCreator = { creatorId ->
                            allUsers.find { it.id == creatorId }?.let { vm.toggleFollow(it) }
                        }
                    )
                }

                MainTab.CREATE -> {
                    CreatePostScreen(
                        onPublishPost = { caption, media, loc, tags ->
                            vm.createPost(caption, media, loc, tags) {}
                        },
                        onPublishShort = { caption, media, sound, tags ->
                            vm.uploadShort(caption, media, sound, tags) {}
                        },
                        onPublishStory = { caption, media ->
                            vm.uploadStory(caption, media) {}
                        },
                        onCancel = { vm.selectTab(MainTab.FEED) }
                    )
                }

                MainTab.EXPLORE -> {
                    ExploreScreen(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { vm.setSearchQuery(it) },
                        users = searchUsersResults.filter { it.id != user.id },
                        posts = searchPostsResults,
                        onUserClick = { clickedUser ->
                            vm.openChat(clickedUser)
                        },
                        onFollowToggle = { targetUser ->
                            vm.toggleFollow(targetUser)
                        },
                        onPostClick = { clickedPost ->
                            vm.openComments(clickedPost)
                        }
                    )
                }

                MainTab.PROFILE -> {
                    ProfileScreen(
                        user = user,
                        posts = feedPosts.filter { it.userId == user.id },
                        shorts = shorts.filter { it.userId == user.id },
                        savedPosts = savedPosts,
                        likedPosts = likedPosts,
                        onPostClick = { clickedPost ->
                            vm.openComments(clickedPost)
                        },
                        onUpdateProfile = { name, bio, site, priv ->
                            vm.updateProfile(name, bio, site, priv)
                        },
                        onOpenAdminPanel = { vm.openSubScreen(SubScreen.ADMIN_PANEL) },
                        onOpenSettings = { vm.openSubScreen(SubScreen.SETTINGS) },
                        onSwitchAccountClick = {
                            val nextUser = allUsers.firstOrNull { it.id != user.id }
                            if (nextUser != null) vm.switchAccount(nextUser)
                        },
                        onLogout = { vm.logout() },
                        onDeleteAccount = { vm.deleteAccount {} }
                    )
                }
            }

            // Interactive Dialogs & Modal Sheets
            activeStory?.let { story ->
                FullscreenStoryViewer(
                    story = story,
                    onClose = { vm.closeStory() },
                    onReply = { replyMsg ->
                        vm.sendMessage(story.userId, "Replied to story: $replyMsg")
                        vm.showSnackbar("Reply sent to @${story.authorUsername}")
                    }
                )
            }

            activeCommentsPost?.let { post ->
                CommentsBottomSheet(
                    post = post,
                    comments = currentComments,
                    onDismiss = { vm.closeComments() },
                    onAddComment = { text ->
                        vm.addComment(post.id, text)
                    }
                )
            }

            activeSharePost?.let { post ->
                ShareBottomSheet(
                    post = post,
                    users = allUsers.filter { it.id != user.id },
                    onDismiss = { vm.closeShare() },
                    onSendDirect = { recipient ->
                        vm.sendMessage(recipient.id, "Shared post by @${post.authorUsername}: ${post.caption}")
                        vm.showSnackbar("Sent to @${recipient.username}")
                    }
                )
            }

            activeReportTarget?.let { target ->
                ReportDialog(
                    target = target,
                    onDismiss = { vm.closeReport() },
                    onSubmit = { reason, details ->
                        vm.submitReport(reason, details)
                    }
                )
            }
        }
    }
}
