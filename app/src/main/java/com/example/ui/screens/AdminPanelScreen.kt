package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.local.ReportEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class AdminTab {
    REPORTS,
    USERS,
    OVERVIEW
}

@Composable
fun AdminPanelScreen(
    reports: List<ReportEntity>,
    users: List<UserEntity>,
    onResolveReport: (reportId: String, status: String) -> Unit,
    onDeleteReportedPost: (postId: String) -> Unit,
    onToggleBanUser: (UserEntity) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AdminTab.REPORTS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("admin_panel_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NK Admin Console",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = NkCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "MODERATOR",
                            color = NkCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Safety, content review, and community enforcement",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Admin Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = NkPurple
        ) {
            Tab(
                selected = selectedTab == AdminTab.REPORTS,
                onClick = { selectedTab = AdminTab.REPORTS },
                text = { Text("Reports Queue (${reports.filter { it.status == "PENDING" }.size})") }
            )
            Tab(
                selected = selectedTab == AdminTab.USERS,
                onClick = { selectedTab = AdminTab.USERS },
                text = { Text("User Accounts (${users.size})") }
            )
            Tab(
                selected = selectedTab == AdminTab.OVERVIEW,
                onClick = { selectedTab = AdminTab.OVERVIEW },
                text = { Text("Health & Logs") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            AdminTab.REPORTS -> {
                val pendingReports = reports.filter { it.status == "PENDING" }
                if (pendingReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(56.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No pending reports!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("The community is healthy and abuse-free.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingReports, key = { it.id }) { rep ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = NkPink.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = rep.reason,
                                                color = NkPink,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }

                                        val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(rep.timestamp))
                                        Text(text = timeStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Target: ${rep.targetTitle}",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (rep.details.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Reporter context: ${rep.details}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                if (rep.targetType == "POST") {
                                                    onDeleteReportedPost(rep.targetId)
                                                }
                                                onResolveReport(rep.id, "RESOLVED")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Remove Post", fontSize = 12.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { onResolveReport(rep.id, "DISMISSED") },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Dismiss Report", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AdminTab.USERS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(users, key = { it.id }) { user ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (user.isBanned) Color.Red else NkPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.username.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            if (user.role == "ADMIN") {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("(ADMIN)", color = NkCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "@${user.username} • ${if (user.isBanned) "SUSPENDED" else "ACTIVE"}",
                                            fontSize = 12.sp,
                                            color = if (user.isBanned) Color.Red else Color(0xFF22C55E)
                                        )
                                    }
                                }

                                if (user.role != "ADMIN") {
                                    Button(
                                        onClick = { onToggleBanUser(user) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (user.isBanned) Color(0xFF22C55E) else Color.Red
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (user.isBanned) "Unsuspend" else "Suspend",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AdminTab.OVERVIEW -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Database & Security Controls", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Local persistent Room Database encrypted & isolated", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("• Zero third-party telemetry or unsecured endpoints", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("• Auto 24-hour expiration cron for ephemeral stories", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("• GDPR compliant instant data wiping on account deletion", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
