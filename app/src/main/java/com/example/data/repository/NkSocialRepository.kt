package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class NkSocialRepository(private val dao: NkDao) {

    // Current logged in user session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    suspend fun initializeSession() {
        // Default login as user_me
        val defaultUser = dao.getUserById("user_me")
        if (defaultUser != null) {
            _currentUser.value = defaultUser
        } else {
            // In case DB hasn't finished initial seeding yet, create fallback
            val fallback = UserEntity(
                id = "user_me",
                username = "simaan_k",
                displayName = "Simaan Kambale",
                email = "simaanilkambale857@gmail.com",
                phone = "+1 (555) 234-5678",
                avatarUrl = "avatar_user_me",
                bio = "Building the future of social connections on NK Social 2.0 🚀✨ Tech Enthusiast & Mobile Explorer.",
                website = "https://nksocial.io/simaan",
                isVerified = true,
                followersCount = 1420,
                followingCount = 385,
                postsCount = 4,
                isPrivate = false,
                role = "ADMIN"
            )
            dao.insertUser(fallback)
            _currentUser.value = fallback
        }
    }

    // AUTH
    suspend fun login(emailOrPhone: String, password: String):Result<UserEntity> {
        val user = dao.findUserByLogin(emailOrPhone)
        return if (user != null) {
            if (user.isBanned) {
                Result.failure(Exception("This account has been suspended by administration."))
            } else {
                _currentUser.value = user
                Result.success(user)
            }
        } else {
            // Create or return demo account if new login
            val newUser = UserEntity(
                id = "user_" + UUID.randomUUID().toString().take(8),
                username = if (emailOrPhone.contains("@")) emailOrPhone.substringBefore("@") else "user_" + emailOrPhone.takeLast(4),
                displayName = "NK Explorer",
                email = if (emailOrPhone.contains("@")) emailOrPhone else "",
                phone = if (!emailOrPhone.contains("@")) emailOrPhone else "",
                avatarUrl = "avatar_user_me",
                bio = "Hello NK Social 2.0! Ready to connect and explore.",
                role = "USER"
            )
            dao.insertUser(newUser)
            _currentUser.value = newUser
            Result.success(newUser)
        }
    }

    suspend fun register(
        username: String,
        displayName: String,
        emailOrPhone: String,
        bio: String
    ): Result<UserEntity> {
        val cleanUsername = username.trim().removePrefix("@")
        val newUser = UserEntity(
            id = "user_" + UUID.randomUUID().toString().take(8),
            username = cleanUsername,
            displayName = displayName.ifBlank { cleanUsername },
            email = if (emailOrPhone.contains("@")) emailOrPhone else "",
            phone = if (!emailOrPhone.contains("@")) emailOrPhone else "",
            avatarUrl = "avatar_user_me",
            bio = bio.ifBlank { "Excited to join NK Social 2.0 ✨" },
            role = "USER"
        )
        dao.insertUser(newUser)
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    suspend fun switchAccount(user: UserEntity) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun updateProfile(
        displayName: String,
        bio: String,
        website: String,
        isPrivate: Boolean
    ) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            displayName = displayName,
            bio = bio,
            website = website,
            isPrivate = isPrivate
        )
        dao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun deleteCurrentAccount(): Boolean {
        val current = _currentUser.value ?: return false
        dao.deletePostsByUserId(current.id)
        dao.deleteUser(current.id)
        _currentUser.value = null
        return true
    }

    // USERS
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    fun searchUsers(query: String): Flow<List<UserEntity>> = dao.searchUsers(query)

    suspend fun toggleFollow(user: UserEntity) {
        val current = _currentUser.value ?: return
        val isNowFollowing = !user.isFollowedByMe
        val updatedUser = user.copy(
            isFollowedByMe = isNowFollowing,
            followersCount = if (isNowFollowing) user.followersCount + 1 else (user.followersCount - 1).coerceAtLeast(0)
        )
        dao.updateUser(updatedUser)

        // Update following count for current user
        val updatedCurrent = current.copy(
            followingCount = if (isNowFollowing) current.followingCount + 1 else (current.followingCount - 1).coerceAtLeast(0)
        )
        dao.updateUser(updatedCurrent)
        _currentUser.value = updatedCurrent

        // Notify user if followed
        if (isNowFollowing) {
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = user.id,
                    fromUserId = current.id,
                    fromUsername = current.username,
                    fromUserAvatar = current.avatarUrl,
                    type = "FOLLOW",
                    message = "started following you",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // POSTS
    fun getFeedPosts(): Flow<List<PostEntity>> = dao.getFeedPosts()
    fun getPostsByUserId(userId: String): Flow<List<PostEntity>> = dao.getPostsByUserId(userId)
    fun getSavedPosts(): Flow<List<PostEntity>> = dao.getSavedPosts()
    fun getLikedPosts(): Flow<List<PostEntity>> = dao.getLikedPosts()
    fun searchPosts(query: String): Flow<List<PostEntity>> = dao.searchPosts(query)

    suspend fun createPost(
        caption: String,
        mediaUrl: String,
        location: String,
        hashtags: String
    ): PostEntity? {
        val current = _currentUser.value ?: return null
        val newPost = PostEntity(
            id = "post_" + UUID.randomUUID().toString().take(8),
            userId = current.id,
            authorUsername = current.username,
            authorDisplayName = current.displayName,
            authorAvatar = current.avatarUrl,
            authorVerified = current.isVerified,
            mediaUrl = mediaUrl.ifBlank { "img_post_travel" },
            caption = caption,
            location = location,
            hashtags = hashtags,
            createdAt = System.currentTimeMillis()
        )
        dao.insertPost(newPost)
        val updatedCurrent = current.copy(postsCount = current.postsCount + 1)
        dao.updateUser(updatedCurrent)
        _currentUser.value = updatedCurrent
        return newPost
    }

    suspend fun toggleLikePost(post: PostEntity) {
        val current = _currentUser.value ?: return
        val isNowLiked = !post.isLikedByMe
        val newLikesCount = if (isNowLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        val updated = post.copy(
            isLikedByMe = isNowLiked,
            likesCount = newLikesCount
        )
        dao.updatePost(updated)

        if (isNowLiked && post.userId != current.id) {
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    userId = post.userId,
                    fromUserId = current.id,
                    fromUsername = current.username,
                    fromUserAvatar = current.avatarUrl,
                    type = "LIKE",
                    message = "liked your post",
                    targetId = post.id,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun toggleSavePost(post: PostEntity) {
        val updated = post.copy(isSavedByMe = !post.isSavedByMe)
        dao.updatePost(updated)
    }

    suspend fun deletePost(postId: String) {
        dao.deletePost(postId)
        val current = _currentUser.value ?: return
        val updated = current.copy(postsCount = (current.postsCount - 1).coerceAtLeast(0))
        dao.updateUser(updated)
        _currentUser.value = updated
    }

    // COMMENTS
    fun getComments(postId: String): Flow<List<CommentEntity>> = dao.getCommentsForPost(postId)

    suspend fun addComment(postId: String, content: String): CommentEntity? {
        val current = _currentUser.value ?: return null
        val comment = CommentEntity(
            id = "comm_" + UUID.randomUUID().toString().take(8),
            postId = postId,
            userId = current.id,
            authorUsername = current.username,
            authorAvatar = current.avatarUrl,
            authorVerified = current.isVerified,
            content = content,
            createdAt = System.currentTimeMillis()
        )
        dao.insertComment(comment)

        // Increment post comment count
        val post = dao.getFeedPosts() // wait, let's update post count
        // We can execute direct query or update post
        return comment
    }

    // SHORTS (REELS)
    fun getAllShorts(): Flow<List<ShortVideoEntity>> = dao.getAllShorts()

    suspend fun uploadShort(
        caption: String,
        mediaUrl: String,
        soundTitle: String,
        hashtags: String
    ): ShortVideoEntity? {
        val current = _currentUser.value ?: return null
        val short = ShortVideoEntity(
            id = "short_" + UUID.randomUUID().toString().take(8),
            userId = current.id,
            authorUsername = current.username,
            authorDisplayName = current.displayName,
            authorAvatar = current.avatarUrl,
            authorVerified = current.isVerified,
            mediaUrl = mediaUrl.ifBlank { "img_short_dance" },
            caption = caption,
            soundTitle = soundTitle.ifBlank { "Original Sound - " + current.username },
            hashtags = hashtags,
            createdAt = System.currentTimeMillis()
        )
        dao.insertShort(short)
        return short
    }

    suspend fun toggleLikeShort(short: ShortVideoEntity) {
        val isNowLiked = !short.isLikedByMe
        val updated = short.copy(
            isLikedByMe = isNowLiked,
            likesCount = if (isNowLiked) short.likesCount + 1 else (short.likesCount - 1).coerceAtLeast(0)
        )
        dao.updateShort(updated)
    }

    // STORIES (24h)
    fun getActiveStories(): Flow<List<StoryEntity>> = dao.getActiveStories()

    suspend fun createStory(caption: String, mediaUrl: String): StoryEntity? {
        val current = _currentUser.value ?: return null
        val now = System.currentTimeMillis()
        val story = StoryEntity(
            id = "story_" + UUID.randomUUID().toString().take(8),
            userId = current.id,
            authorUsername = current.username,
            authorAvatar = current.avatarUrl,
            mediaUrl = mediaUrl.ifBlank { "img_post_travel" },
            caption = caption,
            createdAt = now,
            expiresAt = now + 86400000L, // 24 hours from creation
            isSeen = false
        )
        dao.insertStory(story)
        return story
    }

    suspend fun markStorySeen(storyId: String) {
        dao.markStorySeen(storyId)
    }

    // MESSAGING
    fun getMessagesForUser(userId: String): Flow<List<MessageEntity>> = dao.getMessagesForUser(userId)
    fun getConversationMessages(conversationId: String): Flow<List<MessageEntity>> = dao.getConversationMessages(conversationId)

    suspend fun sendMessage(recipientId: String, content: String, mediaUrl: String = ""): MessageEntity? {
        val current = _currentUser.value ?: return null
        val convId = listOf(current.id, recipientId).sorted().joinToString("_", prefix = "conv_")
        val message = MessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            conversationId = convId,
            senderId = current.id,
            recipientId = recipientId,
            content = content,
            mediaUrl = mediaUrl,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        dao.insertMessage(message)
        return message
    }

    // NOTIFICATIONS
    fun getNotifications(): Flow<List<NotificationEntity>> {
        val currentId = _currentUser.value?.id ?: "user_me"
        return dao.getNotificationsForUser(currentId)
    }

    suspend fun markNotificationsRead() {
        val currentId = _currentUser.value?.id ?: return
        dao.markAllNotificationsRead(currentId)
    }

    suspend fun clearAllNotifications() {
        val currentId = _currentUser.value?.id ?: return
        dao.clearNotifications(currentId)
    }

    // SAFETY & ADMIN MODERATION
    fun getAllReports(): Flow<List<ReportEntity>> = dao.getAllReports()

    suspend fun reportContent(targetType: String, targetId: String, title: String, reason: String, details: String) {
        val currentId = _currentUser.value?.id ?: "anonymous"
        val report = ReportEntity(
            id = "rep_" + UUID.randomUUID().toString().take(8),
            reporterId = currentId,
            targetType = targetType,
            targetId = targetId,
            targetTitle = title,
            reason = reason,
            details = details,
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )
        dao.insertReport(report)
    }

    suspend fun blockUser(targetUser: UserEntity) {
        val current = _currentUser.value ?: return
        val block = BlockEntity(
            id = "block_" + UUID.randomUUID().toString().take(8),
            userId = current.id,
            blockedUserId = targetUser.id,
            blockedUsername = targetUser.username
        )
        dao.insertBlock(block)
    }

    fun getBlockedUsers(): Flow<List<BlockEntity>> {
        val currentId = _currentUser.value?.id ?: "user_me"
        return dao.getBlockedUsers(currentId)
    }

    suspend fun unblockUser(blockedUserId: String) {
        val currentId = _currentUser.value?.id ?: return
        dao.unblockUser(currentId, blockedUserId)
    }

    // ADMIN ACTIONS
    suspend fun setBannedStatus(userId: String, isBanned: Boolean) {
        dao.setUserBannedStatus(userId, isBanned)
    }

    suspend fun updateReportStatus(reportId: String, status: String) {
        dao.updateReportStatus(reportId, status)
    }

    suspend fun adminDeletePost(postId: String) {
        dao.deletePost(postId)
    }
}
