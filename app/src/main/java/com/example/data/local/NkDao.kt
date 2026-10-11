package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NkDao {

    // USERS
    @Query("SELECT * FROM users WHERE isBanned = 0")
    fun getAllActiveUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :emailOrPhone OR phone = :emailOrPhone OR username = :emailOrPhone LIMIT 1")
    suspend fun findUserByLogin(emailOrPhone: String): UserEntity?

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun setUserBannedStatus(userId: String, isBanned: Boolean)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    // POSTS
    @Query("SELECT * FROM posts WHERE isHidden = 0 ORDER BY createdAt DESC")
    fun getFeedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE userId = :userId AND isHidden = 0 ORDER BY createdAt DESC")
    fun getPostsByUserId(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSavedByMe = 1 AND isHidden = 0 ORDER BY createdAt DESC")
    fun getSavedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isLikedByMe = 1 AND isHidden = 0 ORDER BY createdAt DESC")
    fun getLikedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE caption LIKE '%' || :query || '%' OR hashtags LIKE '%' || :query || '%' ORDER BY likesCount DESC")
    fun searchPosts(query: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("UPDATE posts SET isHidden = 1 WHERE id = :postId")
    suspend fun hidePost(postId: String)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    @Query("DELETE FROM posts WHERE userId = :userId")
    suspend fun deletePostsByUserId(userId: String)

    // SHORTS / REELS
    @Query("SELECT * FROM short_videos WHERE isHidden = 0 ORDER BY createdAt DESC")
    fun getAllShorts(): Flow<List<ShortVideoEntity>>

    @Query("SELECT * FROM short_videos WHERE userId = :userId AND isHidden = 0 ORDER BY createdAt DESC")
    fun getShortsByUserId(userId: String): Flow<List<ShortVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShort(short: ShortVideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShorts(shorts: List<ShortVideoEntity>)

    @Update
    suspend fun updateShort(short: ShortVideoEntity)

    @Query("UPDATE short_videos SET isHidden = 1 WHERE id = :shortId")
    suspend fun hideShort(shortId: String)

    @Query("DELETE FROM short_videos WHERE id = :shortId")
    suspend fun deleteShort(shortId: String)

    // STORIES
    @Query("SELECT * FROM stories WHERE expiresAt > :now ORDER BY createdAt DESC")
    fun getActiveStories(now: Long = System.currentTimeMillis()): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET isSeen = 1 WHERE id = :storyId")
    suspend fun markStorySeen(storyId: String)

    // COMMENTS
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    // MESSAGES
    @Query("SELECT * FROM messages WHERE senderId = :userId OR recipientId = :userId ORDER BY timestamp ASC")
    fun getMessagesForUser(userId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getConversationMessages(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun clearNotifications(userId: String)

    // REPORTS (ADMIN & MODERATION)
    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET status = :status WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: String, status: String)

    // BLOCKING
    @Query("SELECT * FROM blocked_users WHERE userId = :userId")
    fun getBlockedUsers(userId: String): Flow<List<BlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity)

    @Query("DELETE FROM blocked_users WHERE userId = :userId AND blockedUserId = :blockedUserId")
    suspend fun unblockUser(userId: String, blockedUserId: String)
}
