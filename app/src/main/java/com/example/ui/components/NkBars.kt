package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.SubScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NkTopBar(
    isDarkMode: Boolean,
    unreadNotifications: Int,
    unreadMessages: Int,
    onToggleDarkMode: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMessages: () -> Unit,
    onAdminClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("nk_top_bar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* Brand tap */ }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NkBrandGradient)
                        .padding(2.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_nk_logo),
                        contentDescription = "NK Social 2.0 Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NK Social",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NkCyan.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NkCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "2.0",
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NkCyan,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Admin shortcut if available
                if (onAdminClick != null) {
                    IconButton(
                        onClick = onAdminClick,
                        modifier = Modifier.testTag("top_bar_admin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Moderation Panel",
                            tint = NkPurpleLight
                        )
                    }
                }

                // Dark/Light Mode Switcher
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = if (isDarkMode) Color(0xFFFFD54F) else NkPurple
                    )
                }

                // Notifications
                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier.testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifications > 0) {
                                Badge(
                                    containerColor = NkPink,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadNotifications")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Direct Messages
                IconButton(
                    onClick = onOpenMessages,
                    modifier = Modifier.testTag("direct_messages_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadMessages > 0) {
                                Badge(
                                    containerColor = NkBlue,
                                    contentColor = Color.White
                                ) {
                                    Text("$unreadMessages")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = "Direct Messages",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NkBottomBar(
    currentTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("nk_bottom_navigation"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp)
        ) {
            // FEED TAB
            NavigationBarItem(
                selected = currentTab == MainTab.FEED,
                onClick = { onTabSelected(MainTab.FEED) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == MainTab.FEED) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = "Home Feed",
                        tint = if (currentTab == MainTab.FEED) NkPurple else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text("Feed", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.FEED) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NkPurple.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("tab_feed")
            )

            // SHORTS TAB
            NavigationBarItem(
                selected = currentTab == MainTab.SHORTS,
                onClick = { onTabSelected(MainTab.SHORTS) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == MainTab.SHORTS) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                        contentDescription = "NK Shorts",
                        tint = if (currentTab == MainTab.SHORTS) NkBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text("Shorts", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.SHORTS) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NkBlue.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("tab_shorts")
            )

            // CREATE TAB
            NavigationBarItem(
                selected = currentTab == MainTab.CREATE,
                onClick = { onTabSelected(MainTab.CREATE) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NkBrandGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Post / Short / Story",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = { Text("Create", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.CREATE) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),
                modifier = Modifier.testTag("tab_create")
            )

            // EXPLORE TAB
            NavigationBarItem(
                selected = currentTab == MainTab.EXPLORE,
                onClick = { onTabSelected(MainTab.EXPLORE) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == MainTab.EXPLORE) Icons.Filled.Search else Icons.Outlined.Search,
                        contentDescription = "Explore & Search",
                        tint = if (currentTab == MainTab.EXPLORE) NkCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text("Explore", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.EXPLORE) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NkCyan.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("tab_explore")
            )

            // PROFILE TAB
            NavigationBarItem(
                selected = currentTab == MainTab.PROFILE,
                onClick = { onTabSelected(MainTab.PROFILE) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "User Profile",
                        tint = if (currentTab == MainTab.PROFILE) NkPink else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text("Profile", fontSize = 11.sp, fontWeight = if (currentTab == MainTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NkPink.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("tab_profile")
            )
        }
    }
}
