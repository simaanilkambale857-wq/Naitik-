package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PostEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostCard(
    post: PostEntity,
    currentUserId: String,
    onLike: (PostEntity) -> Unit,
    onCommentClick: (PostEntity) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onSaveClick: (PostEntity) -> Unit,
    onReportClick: (PostEntity) -> Unit,
    onBlockAuthorClick: (String, String) -> Unit, // userId, username
    onDeletePostClick: ((String) -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDoubleTapHeart by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Author details & Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NkBrandGradient)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = post.authorUsername.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = NkPurple,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorDisplayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.authorVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = NkCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${post.authorUsername}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (post.location.isNotBlank()) {
                                Text(
                                    text = " • ${post.location}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NkBlueLight,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Options Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("post_options_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Share Post") },
                            leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onShareClick(post)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Report Inappropriate") },
                            leadingIcon = { Icon(Icons.Outlined.Flag, tint = NkPink, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onReportClick(post)
                            }
                        )

                        if (post.userId != currentUserId) {
                            DropdownMenuItem(
                                text = { Text("Block @${post.authorUsername}") },
                                leadingIcon = { Icon(Icons.Outlined.Block, tint = Color.Red, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onBlockAuthorClick(post.userId, post.authorUsername)
                                }
                            )
                        }

                        if (post.userId == currentUserId && onDeletePostClick != null) {
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete Post", color = Color.Red) },
                                leadingIcon = { Icon(Icons.Outlined.Delete, tint = Color.Red, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    onDeletePostClick(post.id)
                                }
                            )
                        }
                    }
                }
            }

            // Media Container with Double-tap to Like
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!post.isLikedByMe) {
                                    onLike(post)
                                }
                                showDoubleTapHeart = true
                                coroutineScope.launch {
                                    delay(800)
                                    showDoubleTapHeart = false
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Determine media drawable
                val drawableId = when (post.mediaUrl) {
                    "img_post_travel" -> R.drawable.img_post_travel
                    "img_short_dance" -> R.drawable.img_short_dance
                    "img_nk_logo" -> R.drawable.img_nk_logo
                    else -> R.drawable.img_post_travel
                }

                Image(
                    painter = painterResource(id = drawableId),
                    contentDescription = "Post Media",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Double-tap heart animation
                androidx.compose.animation.AnimatedVisibility(
                    visible = showDoubleTapHeart,
                    enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)),
                    exit = scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Liked",
                        tint = NkPink,
                        modifier = Modifier.size(96.dp)
                    )
                }
            }

            // Interactive Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like button
                    IconButton(
                        onClick = { onLike(post) },
                        modifier = Modifier.testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLikedByMe) NkPink else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Comment button
                    IconButton(
                        onClick = { onCommentClick(post) },
                        modifier = Modifier.testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = { onShareClick(post) },
                        modifier = Modifier.testTag("share_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Bookmark / Save button
                IconButton(
                    onClick = { onSaveClick(post) },
                    modifier = Modifier.testTag("save_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isSavedByMe) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSavedByMe) NkPurple else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Likes and Engagement stats
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                Text(
                    text = "${post.likesCount} likes",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Caption
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = buildString {
                            append(post.authorUsername)
                            append(" ")
                            append(post.caption)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // View comments trigger
                if (post.commentsCount > 0) {
                    Text(
                        text = "View all ${post.commentsCount} comments",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clickable { onCommentClick(post) }
                            .padding(vertical = 2.dp)
                    )
                }

                // Timestamp
                val timeStr = remember(post.createdAt) {
                    val diff = System.currentTimeMillis() - post.createdAt
                    when {
                        diff < 60000L -> "Just now"
                        diff < 3600000L -> "${diff / 60000L}m ago"
                        diff < 86400000L -> "${diff / 3600000L}h ago"
                        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(post.createdAt))
                    }
                }

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
