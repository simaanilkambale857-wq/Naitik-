package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PostEntity
import com.example.data.local.StoryEntity
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesRow
import com.example.ui.theme.NkPurple

@Composable
fun FeedScreen(
    currentUserId: String,
    stories: List<StoryEntity>,
    posts: List<PostEntity>,
    onAddStoryClick: () -> Unit,
    onStoryClick: (StoryEntity) -> Unit,
    onLikePost: (PostEntity) -> Unit,
    onCommentClick: (PostEntity) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onSaveClick: (PostEntity) -> Unit,
    onReportClick: (PostEntity) -> Unit,
    onBlockUser: (String, String) -> Unit,
    onDeletePost: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("feed_screen_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Top Stories row
        item {
            StoriesRow(
                stories = stories,
                onAddStoryClick = onAddStoryClick,
                onStoryClick = onStoryClick
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 0.5.dp
            )
        }

        // Empty Feed State
        if (posts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No posts in your feed yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button below to create your first post!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            // Posts Feed
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    currentUserId = currentUserId,
                    onLike = onLikePost,
                    onCommentClick = onCommentClick,
                    onShareClick = onShareClick,
                    onSaveClick = onSaveClick,
                    onReportClick = onReportClick,
                    onBlockAuthorClick = onBlockUser,
                    onDeletePostClick = onDeletePost
                )
            }
        }
    }
}
