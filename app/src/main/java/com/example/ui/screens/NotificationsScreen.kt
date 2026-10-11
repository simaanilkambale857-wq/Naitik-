package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NotificationEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onMarkAllRead: () -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit
) {
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Notification settings local state
    var pushEnabled by remember { mutableStateOf(true) }
    var likesEnabled by remember { mutableStateOf(true) }
    var commentsEnabled by remember { mutableStateOf(true) }
    var followsEnabled by remember { mutableStateOf(true) }
    var dmsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("notifications_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row {
                IconButton(onClick = { showSettingsSheet = true }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Notification Settings")
                }
                IconButton(onClick = onMarkAllRead) {
                    Icon(Icons.Default.DoneAll, contentDescription = "Mark all read", tint = NkCyan)
                }
                IconButton(onClick = onClearAll) {
                    Icon(Icons.Outlined.DeleteSweep, contentDescription = "Clear all", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "You're all caught up!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "New likes, comments, and followers will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (!notif.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!notif.isRead) NkPurpleLight.copy(alpha = 0.3f) else Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Type
                            val (icon, iconColor) = when (notif.type) {
                                "LIKE" -> Pair(Icons.Filled.Favorite, NkPink)
                                "COMMENT" -> Pair(Icons.Filled.ChatBubble, NkCyan)
                                "FOLLOW" -> Pair(Icons.Filled.PersonAdd, NkBlue)
                                else -> Pair(Icons.Filled.Mail, NkPurple)
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(iconColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = buildString {
                                        append(notif.fromUsername)
                                        append(" ")
                                        append(notif.message)
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(notif.timestamp))
                                Text(
                                    text = timeStr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Notification Settings Bottom Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Notification Preferences",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                NotificationSettingRow(
                    title = "Push Notifications",
                    subtitle = "Receive notifications on device lockscreen",
                    checked = pushEnabled,
                    onCheckedChange = { pushEnabled = it }
                )

                NotificationSettingRow(
                    title = "Likes Alerts",
                    subtitle = "Notify when someone likes your post or short",
                    checked = likesEnabled,
                    onCheckedChange = { likesEnabled = it }
                )

                NotificationSettingRow(
                    title = "Comments Alerts",
                    subtitle = "Notify when someone comments on your creations",
                    checked = commentsEnabled,
                    onCheckedChange = { commentsEnabled = it }
                )

                NotificationSettingRow(
                    title = "New Followers",
                    subtitle = "Notify when a creator starts following you",
                    checked = followsEnabled,
                    onCheckedChange = { followsEnabled = it }
                )

                NotificationSettingRow(
                    title = "Direct Message Alerts",
                    subtitle = "Notify instantly for incoming private messages",
                    checked = dmsEnabled,
                    onCheckedChange = { dmsEnabled = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { showSettingsSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NkPurple),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun NotificationSettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
