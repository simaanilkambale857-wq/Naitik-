package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val website: String = "",
    val isVerified: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val isPrivate: Boolean = false,
    val role: String = "USER", // "USER" or "ADMIN"
    val isBanned: Boolean = false,
    val isFollowedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val authorUsername: String,
    val authorDisplayName: String,
    val authorAvatar: String,
    val authorVerified: Boolean = false,
    val mediaUrl: String, // Can be res drawable identifier like "img_post_travel" or custom URI / asset name
    val mediaType: String = "IMAGE", // "IMAGE" or "VIDEO"
    val caption: String,
    val location: String = "",
    val hashtags: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false,
    val isHidden: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "short_videos")
data class ShortVideoEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val authorUsername: String,
    val authorDisplayName: String,
    val authorAvatar: String,
    val authorVerified: Boolean = false,
    val mediaUrl: String,
    val caption: String,
    val soundTitle: String = "Original Audio - NK Vibes",
    val hashtags: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val isHidden: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val authorUsername: String,
    val authorAvatar: String,
    val mediaUrl: String,
    val caption: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86400000L, // 24 hours
    val isSeen: Boolean = false
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val authorUsername: String,
    val authorAvatar: String,
    val authorVerified: Boolean = false,
    val content: String,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val mediaUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val fromUserId: String,
    val fromUsername: String,
    val fromUserAvatar: String,
    val type: String, // "LIKE", "COMMENT", "FOLLOW", "MESSAGE"
    val message: String,
    val targetId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterId: String,
    val targetType: String, // "POST", "SHORT", "USER"
    val targetId: String,
    val targetTitle: String,
    val reason: String,
    val details: String = "",
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocked_users")
data class BlockEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val blockedUserId: String,
    val blockedUsername: String,
    val timestamp: Long = System.currentTimeMillis()
)
