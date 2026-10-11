package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PostEntity
import com.example.data.local.ShortVideoEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*

enum class ProfileTab {
    POSTS,
    SHORTS,
    SAVED,
    LIKED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserEntity,
    posts: List<PostEntity>,
    shorts: List<ShortVideoEntity>,
    savedPosts: List<PostEntity>,
    likedPosts: List<PostEntity>,
    onPostClick: (PostEntity) -> Unit,
    onUpdateProfile: (displayName: String, bio: String, website: String, isPrivate: Boolean) -> Unit,
    onOpenAdminPanel: () -> Unit,
    onOpenSettings: () -> Unit,
    onSwitchAccountClick: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(ProfileTab.POSTS) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showDeleteAccountConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Top Profile Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top Action Bar with Settings & Admin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (user.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = NkCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Row {
                        if (user.role == "ADMIN") {
                            IconButton(onClick = onOpenAdminPanel) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Panel",
                                    tint = NkPurpleLight
                                )
                            }
                        }

                        Box {
                            IconButton(
                                onClick = { showSettingsMenu = true },
                                modifier = Modifier.testTag("profile_settings_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Profile Settings",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            DropdownMenu(
                                expanded = showSettingsMenu,
                                onDismissRequest = { showSettingsMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Profile") },
                                    leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                                    onClick = {
                                        showSettingsMenu = false
                                        showEditProfileDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Switch Demo Account") },
                                    leadingIcon = { Icon(Icons.Outlined.SwitchAccount, contentDescription = null) },
                                    onClick = {
                                        showSettingsMenu = false
                                        onSwitchAccountClick()
                                    }
                                )
                                if (user.role == "ADMIN") {
                                    DropdownMenuItem(
                                        text = { Text("Admin Moderation Console") },
                                        leadingIcon = { Icon(Icons.Default.Shield, tint = NkCyan, contentDescription = null) },
                                        onClick = {
                                            showSettingsMenu = false
                                            onOpenAdminPanel()
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Account Settings") },
                                    leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                                    onClick = {
                                        showSettingsMenu = false
                                        onOpenSettings()
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Log Out") },
                                    leadingIcon = { Icon(Icons.Outlined.Logout, contentDescription = null) },
                                    onClick = {
                                        showSettingsMenu = false
                                        onLogout()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Account & Data", color = Color.Red) },
                                    leadingIcon = { Icon(Icons.Outlined.DeleteForever, tint = Color.Red, contentDescription = null) },
                                    onClick = {
                                        showSettingsMenu = false
                                        showDeleteAccountConfirm = true
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar and Counters Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Profile Avatar with Gradient Ring
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(NkBrandGradient)
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.username.take(1).uppercase(),
                                fontWeight = FontWeight.ExtraBold,
                                color = NkPurple,
                                fontSize = 32.sp
                            )
                        }
                    }

                    // Stat Counters
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 24.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatItem(count = "${posts.size}", label = "Posts")
                        ProfileStatItem(count = "${user.followersCount}", label = "Followers")
                        ProfileStatItem(count = "${user.followingCount}", label = "Following")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name and Bio
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (user.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                if (user.website.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.website,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NkBlueLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Profile & Share Profile Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("edit_profile_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "Edit Profile",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "Privacy & Settings",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Tab Row: Posts, Shorts, Saved, Liked
        item {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = NkPurple,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == ProfileTab.POSTS,
                    onClick = { selectedTab = ProfileTab.POSTS },
                    icon = { Icon(Icons.Default.GridOn, contentDescription = "Posts") }
                )
                Tab(
                    selected = selectedTab == ProfileTab.SHORTS,
                    onClick = { selectedTab = ProfileTab.SHORTS },
                    icon = { Icon(Icons.Default.PlayCircle, contentDescription = "Shorts") }
                )
                Tab(
                    selected = selectedTab == ProfileTab.SAVED,
                    onClick = { selectedTab = ProfileTab.SAVED },
                    icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "Saved") }
                )
                Tab(
                    selected = selectedTab == ProfileTab.LIKED,
                    onClick = { selectedTab = ProfileTab.LIKED },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Liked") }
                )
            }
        }

        // Tab Content Grid
        val activeList = when (selectedTab) {
            ProfileTab.POSTS -> posts
            ProfileTab.SAVED -> savedPosts
            ProfileTab.LIKED -> likedPosts
            ProfileTab.SHORTS -> emptyList() // handled below
        }

        if (selectedTab == ProfileTab.SHORTS) {
            if (shorts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No shorts uploaded yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                val chunkedShorts = shorts.chunked(3)
                items(chunkedShorts) { rowShorts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowShorts.forEach { short ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(9f / 16f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_short_dance),
                                    contentDescription = short.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Text("${short.likesCount}", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        if (rowShorts.size < 3) {
                            repeat(3 - rowShorts.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            }
        } else {
            if (activeList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (selectedTab) {
                                ProfileTab.SAVED -> "No saved posts yet"
                                ProfileTab.LIKED -> "No liked posts yet"
                                else -> "No posts published yet"
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val chunkedPosts = activeList.chunked(3)
                items(chunkedPosts) { rowPosts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowPosts.forEach { post ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onPostClick(post) }
                            ) {
                                val drawableId = when (post.mediaUrl) {
                                    "img_post_travel" -> R.drawable.img_post_travel
                                    "img_short_dance" -> R.drawable.img_short_dance
                                    "img_nk_logo" -> R.drawable.img_nk_logo
                                    else -> R.drawable.img_post_travel
                                }
                                Image(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = post.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        if (rowPosts.size < 3) {
                            repeat(3 - rowPosts.size) { Spacer(modifier = Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editDisplayName by remember { mutableStateOf(user.displayName) }
        var editBio by remember { mutableStateOf(user.bio) }
        var editWebsite by remember { mutableStateOf(user.website) }
        var editIsPrivate by remember { mutableStateOf(user.isPrivate) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editDisplayName,
                        onValueChange = { editDisplayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editWebsite,
                        onValueChange = { editWebsite = it },
                        label = { Text("Website Link") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Private Account", fontWeight = FontWeight.Bold)
                            Text("Only approved followers see posts", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = editIsPrivate,
                            onCheckedChange = { editIsPrivate = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editDisplayName, editBio, editWebsite, editIsPrivate)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NkPurple)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteAccountConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirm = false },
            title = { Text("Delete Account & Data?") },
            text = {
                Text(
                    "This action is permanent. All your profile data, posts, comments, stories, and messages will be permanently wiped from the database.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountConfirm = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Permanently Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
