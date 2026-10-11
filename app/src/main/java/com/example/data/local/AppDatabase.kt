package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        ShortVideoEntity::class,
        StoryEntity::class,
        CommentEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        ReportEntity::class,
        BlockEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun nkDao(): NkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nk_social_2.db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.nkDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: NkDao) {
            // Seed Users
            val currentLoggedInUser = UserEntity(
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

            val userAlex = UserEntity(
                id = "user_alex",
                username = "alex_cyber",
                displayName = "Alex Vance",
                email = "alex@nksocial.io",
                phone = "+1 (555) 345-6789",
                avatarUrl = "avatar_alex",
                bio = "Cyberpunk photographer & digital visual artist. Chasing neon horizons across Asian megacities 🏙️⚡",
                website = "https://vance.photos",
                isVerified = true,
                followersCount = 48200,
                followingCount = 210,
                postsCount = 48,
                isPrivate = false,
                role = "USER",
                isFollowedByMe = true
            )

            val userElena = UserEntity(
                id = "user_elena",
                username = "elena_motion",
                displayName = "Elena Rostova",
                email = "elena@nksocial.io",
                phone = "+1 (555) 456-7890",
                avatarUrl = "avatar_elena",
                bio = "Choreographer & viral sound creator 🎶🕺 Living between beats and movement. Watch my daily shorts!",
                website = "https://elenadance.com",
                isVerified = true,
                followersCount = 125000,
                followingCount = 180,
                postsCount = 92,
                isPrivate = false,
                role = "USER",
                isFollowedByMe = true
            )

            val userKai = UserEntity(
                id = "user_kai",
                username = "kai_dev",
                displayName = "Kai Nakamura",
                email = "kai@nksocial.io",
                phone = "+1 (555) 567-8901",
                avatarUrl = "avatar_kai",
                bio = "Modern UI/UX Designer & Android Dev. Crafting fluid motion in Jetpack Compose 📱🎨",
                website = "https://kaiui.dev",
                isVerified = false,
                followersCount = 8900,
                followingCount = 450,
                postsCount = 34,
                isPrivate = false,
                role = "USER",
                isFollowedByMe = false
            )

            val userAdmin = UserEntity(
                id = "user_admin",
                username = "admin_nk",
                displayName = "NK Official Moderation",
                email = "admin@nksocial.io",
                phone = "+1 (555) 999-0000",
                avatarUrl = "avatar_admin",
                bio = "Official safety & moderation desk for NK Social 2.0 platform. Keeping communities safe & vibrant.",
                website = "https://nksocial.io/safety",
                isVerified = true,
                followersCount = 500000,
                followingCount = 5,
                postsCount = 12,
                isPrivate = false,
                role = "ADMIN",
                isFollowedByMe = true
            )

            dao.insertUsers(listOf(currentLoggedInUser, userAlex, userElena, userKai, userAdmin))

            // Seed Feed Posts
            val post1 = PostEntity(
                id = "post_1",
                userId = "user_alex",
                authorUsername = "alex_cyber",
                authorDisplayName = "Alex Vance",
                authorAvatar = "avatar_alex",
                authorVerified = true,
                mediaUrl = "img_post_travel",
                mediaType = "IMAGE",
                caption = "Twilight hour in Neo Coastal Bay. The reflections of purple and electric blue lights against the Pacific waters never get old! 🌆✨ #NKTravel #CyberpunkVibes #Photography #CityLights",
                location = "Neo Coastal Bay, Marina District",
                hashtags = "#NKTravel #CyberpunkVibes #Photography #CityLights",
                likesCount = 3421,
                commentsCount = 84,
                sharesCount = 152,
                isLikedByMe = true,
                isSavedByMe = true,
                createdAt = System.currentTimeMillis() - 3600000L * 2
            )

            val post2 = PostEntity(
                id = "post_2",
                userId = "user_admin",
                authorUsername = "admin_nk",
                authorDisplayName = "NK Official Moderation",
                authorAvatar = "avatar_admin",
                authorVerified = true,
                mediaUrl = "img_nk_logo",
                mediaType = "IMAGE",
                caption = "Welcome to the all-new NK Social 2.0! 🎉 Engineered with cutting-edge speed, zero-clutter modern design, short videos, 24h stories, and private end-to-end messaging. Let's build the future together! 🌐💙💜 #NK2026 #NewEra #SocialVibe",
                location = "NK Global Headquarters",
                hashtags = "#NK2026 #NewEra #SocialVibe",
                likesCount = 18900,
                commentsCount = 612,
                sharesCount = 2400,
                isLikedByMe = true,
                isSavedByMe = false,
                createdAt = System.currentTimeMillis() - 3600000L * 8
            )

            val post3 = PostEntity(
                id = "post_3",
                userId = "user_kai",
                authorUsername = "kai_dev",
                authorDisplayName = "Kai Nakamura",
                authorAvatar = "avatar_kai",
                authorVerified = false,
                mediaUrl = "img_short_dance",
                mediaType = "IMAGE",
                caption = "Testing lighting contrast and spatial aesthetics for our upcoming studio live stream! What color grading do you prefer? Drop your thoughts below 👇 #StudioVibes #DesignProcess #CreatorEconomy",
                location = "Tokyo Creative Studio",
                hashtags = "#StudioVibes #DesignProcess #CreatorEconomy",
                likesCount = 982,
                commentsCount = 37,
                sharesCount = 18,
                isLikedByMe = false,
                isSavedByMe = false,
                createdAt = System.currentTimeMillis() - 3600000L * 14
            )

            dao.insertPosts(listOf(post1, post2, post3))

            // Seed Short Videos (Reels)
            val short1 = ShortVideoEntity(
                id = "short_1",
                userId = "user_elena",
                authorUsername = "elena_motion",
                authorDisplayName = "Elena Rostova",
                authorAvatar = "avatar_elena",
                authorVerified = true,
                mediaUrl = "img_short_dance",
                caption = "New choreo drop! 💃⚡ Synced to the electric baseline. Hit follow if you want the breakdown tutorial tomorrow! #DanceReel #NKShorts #Vibes #Trending",
                soundTitle = "Electric Dreams (NK Club Mix) - Synthwave Beats",
                hashtags = "#DanceReel #NKShorts #Vibes #Trending",
                likesCount = 45200,
                commentsCount = 1204,
                sharesCount = 3800,
                isLikedByMe = true,
                createdAt = System.currentTimeMillis() - 3600000L * 3
            )

            val short2 = ShortVideoEntity(
                id = "short_2",
                userId = "user_alex",
                authorUsername = "alex_cyber",
                authorDisplayName = "Alex Vance",
                authorAvatar = "avatar_alex",
                authorVerified = true,
                mediaUrl = "img_post_travel",
                caption = "Hyperlapse over the skyline bridge at 60fps. The colors transition as the sun sets completely. 🎥🌆 #Hyperlapse #Cinematic #TokyoNight",
                soundTitle = "Midnight Echoes - Ambient Chill Beats",
                hashtags = "#Hyperlapse #Cinematic #TokyoNight",
                likesCount = 28100,
                commentsCount = 412,
                sharesCount = 1450,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 3600000L * 10
            )

            dao.insertShorts(listOf(short1, short2))

            // Seed Stories (24h validity)
            val story1 = StoryEntity(
                id = "story_1",
                userId = "user_alex",
                authorUsername = "alex_cyber",
                authorAvatar = "avatar_alex",
                mediaUrl = "img_post_travel",
                caption = "Behind the lens this evening 📸",
                createdAt = System.currentTimeMillis() - 3600000L * 2,
                expiresAt = System.currentTimeMillis() + 86400000L - 3600000L * 2,
                isSeen = false
            )

            val story2 = StoryEntity(
                id = "story_2",
                userId = "user_elena",
                authorUsername = "elena_motion",
                authorAvatar = "avatar_elena",
                mediaUrl = "img_short_dance",
                caption = "Warmup session ready! 🔥",
                createdAt = System.currentTimeMillis() - 3600000L * 4,
                expiresAt = System.currentTimeMillis() + 86400000L - 3600000L * 4,
                isSeen = false
            )

            val story3 = StoryEntity(
                id = "story_3",
                userId = "user_admin",
                authorUsername = "admin_nk",
                authorAvatar = "avatar_admin",
                mediaUrl = "img_nk_logo",
                caption = "NK Social 2.0 is live worldwide! 🚀",
                createdAt = System.currentTimeMillis() - 3600000L * 1,
                expiresAt = System.currentTimeMillis() + 86400000L - 3600000L * 1,
                isSeen = true
            )

            dao.insertStories(listOf(story1, story2, story3))

            // Seed Comments
            val comment1 = CommentEntity(
                id = "comm_1",
                postId = "post_1",
                userId = "user_elena",
                authorUsername = "elena_motion",
                authorAvatar = "avatar_elena",
                authorVerified = true,
                content = "These colors are unreal Alex! The neon purple tone is perfection 😍🔥",
                likesCount = 42,
                isLikedByMe = true,
                createdAt = System.currentTimeMillis() - 3600000L
            )

            val comment2 = CommentEntity(
                id = "comm_2",
                postId = "post_1",
                userId = "user_kai",
                authorUsername = "kai_dev",
                authorAvatar = "avatar_kai",
                authorVerified = false,
                content = "What focal length did you shoot this with? Amazing sharpness on the water reflections!",
                likesCount = 15,
                isLikedByMe = false,
                createdAt = System.currentTimeMillis() - 1800000L
            )

            dao.insertComments(listOf(comment1, comment2))

            // Seed Messages
            val msg1 = MessageEntity(
                id = "msg_1",
                conversationId = "conv_user_alex",
                senderId = "user_alex",
                recipientId = "user_me",
                content = "Hey Simaan! Loved your feedback on the new high-res photo gallery in NK Social 2.0!",
                timestamp = System.currentTimeMillis() - 3600000L * 2,
                isRead = true
            )

            val msg2 = MessageEntity(
                id = "msg_2",
                conversationId = "conv_user_alex",
                senderId = "user_me",
                recipientId = "user_alex",
                content = "Thanks Alex! The latency is virtually zero and the colors pop beautifully on OLED screens.",
                timestamp = System.currentTimeMillis() - 3600000L,
                isRead = true
            )

            val msg3 = MessageEntity(
                id = "msg_3",
                conversationId = "conv_user_elena",
                senderId = "user_elena",
                recipientId = "user_me",
                content = "Hey! Check out my new dance short reel when you get a chance 💃🔥",
                timestamp = System.currentTimeMillis() - 1800000L,
                isRead = false
            )

            dao.insertMessages(listOf(msg1, msg2, msg3))

            // Seed Notifications
            val notif1 = NotificationEntity(
                id = "notif_1",
                userId = "user_me",
                fromUserId = "user_alex",
                fromUsername = "alex_cyber",
                fromUserAvatar = "avatar_alex",
                type = "LIKE",
                message = "liked your post 'Welcome to NK Social 2.0'",
                targetId = "post_1",
                timestamp = System.currentTimeMillis() - 3600000L,
                isRead = false
            )

            val notif2 = NotificationEntity(
                id = "notif_2",
                userId = "user_me",
                fromUserId = "user_elena",
                fromUsername = "elena_motion",
                fromUserAvatar = "avatar_elena",
                type = "FOLLOW",
                message = "started following you",
                targetId = "",
                timestamp = System.currentTimeMillis() - 3600000L * 3,
                isRead = true
            )

            val notif3 = NotificationEntity(
                id = "notif_3",
                userId = "user_me",
                fromUserId = "user_kai",
                fromUsername = "kai_dev",
                fromUserAvatar = "avatar_kai",
                type = "COMMENT",
                message = "commented: 'Awesome UI work on this!'",
                targetId = "post_1",
                timestamp = System.currentTimeMillis() - 3600000L * 5,
                isRead = true
            )

            dao.insertNotifications(listOf(notif1, notif2, notif3))

            // Seed Sample Report for Admin Moderation
            val rep1 = ReportEntity(
                id = "rep_1",
                reporterId = "user_kai",
                targetType = "POST",
                targetId = "post_test_flag",
                targetTitle = "Suspicious cryptocurrency bot solicitation link",
                reason = "Spam / Commercial Bot",
                details = "User repeatedly posted external phishing links in comments section.",
                status = "PENDING",
                timestamp = System.currentTimeMillis() - 3600000L * 12
            )

            dao.insertReport(rep1)
        }
    }
}
