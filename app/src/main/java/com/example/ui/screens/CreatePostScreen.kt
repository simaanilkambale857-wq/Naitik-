package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.*

enum class CreationType {
    POST,
    SHORT,
    STORY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPublishPost: (caption: String, mediaUrl: String, location: String, hashtags: String) -> Unit,
    onPublishShort: (caption: String, mediaUrl: String, soundTitle: String, hashtags: String) -> Unit,
    onPublishStory: (caption: String, mediaUrl: String) -> Unit,
    onCancel: () -> Unit
) {
    var creationType by remember { mutableStateOf(CreationType.POST) }
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var hashtags by remember { mutableStateOf("#NK2026 #Creators") }
    var soundTitle by remember { mutableStateOf("Original Audio - Trending Vibe") }
    var selectedMediaKey by remember { mutableStateOf("img_post_travel") }
    var isPublishing by remember { mutableStateOf(false) }

    val mediaOptions = listOf(
        Pair("img_post_travel", "Coastal Twilight Photography"),
        Pair("img_short_dance", "Studio Dance Reel"),
        Pair("img_nk_logo", "NK Social Official Emblem")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("create_content_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "New Creation",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Close, contentDescription = "Cancel")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Creation Type Selector
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = creationType == CreationType.POST,
                onClick = { creationType = CreationType.POST },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
            ) {
                Text("Feed Post", fontSize = 12.sp)
            }
            SegmentedButton(
                selected = creationType == CreationType.SHORT,
                onClick = { creationType = CreationType.SHORT },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
            ) {
                Text("Short Reel", fontSize = 12.sp)
            }
            SegmentedButton(
                selected = creationType == CreationType.STORY,
                onClick = { creationType = CreationType.STORY },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
            ) {
                Text("24h Story", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Media Preview Box
        val currentDrawable = when (selectedMediaKey) {
            "img_post_travel" -> R.drawable.img_post_travel
            "img_short_dance" -> R.drawable.img_short_dance
            "img_nk_logo" -> R.drawable.img_nk_logo
            else -> R.drawable.img_post_travel
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (creationType == CreationType.SHORT) 280.dp else 220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = currentDrawable),
                contentDescription = "Media Preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Badge overlay for Creation Type
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (creationType) {
                            CreationType.POST -> Icons.Default.Image
                            CreationType.SHORT -> Icons.Default.Videocam
                            CreationType.STORY -> Icons.Default.History
                        },
                        contentDescription = null,
                        tint = NkCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (creationType) {
                            CreationType.POST -> "Feed Photo"
                            CreationType.SHORT -> "Vertical Short"
                            CreationType.STORY -> "Story (Expires in 24h)"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Choose from Media Library
        Text(
            text = "Select Media Asset:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(mediaOptions) { (key, label) ->
                val isSelected = selectedMediaKey == key
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) NkPurple.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) NkPurple else Color.Transparent
                    ),
                    modifier = Modifier.clickable { selectedMediaKey = key }
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NkPurple else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Caption Input
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            label = { Text("Write a caption...") },
            placeholder = { Text("Share what's on your mind...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_caption_input"),
            shape = RoundedCornerShape(14.dp),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Hashtags field
        OutlinedTextField(
            value = hashtags,
            onValueChange = { hashtags = it },
            label = { Text("Hashtags") },
            placeholder = { Text("#NK2026 #Explore") },
            leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = NkCyan) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        if (creationType == CreationType.POST) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Add Location") },
                placeholder = { Text("e.g. San Francisco, California") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = NkPink) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
        }

        if (creationType == CreationType.SHORT) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = soundTitle,
                onValueChange = { soundTitle = it },
                label = { Text("Audio Track / Sound") },
                leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null, tint = NkPurpleLight) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Publish Button
        Button(
            onClick = {
                if (caption.isBlank() && creationType != CreationType.STORY) {
                    caption = "Living in the moment with NK Social 2.0 ✨"
                }
                isPublishing = true
                when (creationType) {
                    CreationType.POST -> onPublishPost(caption, selectedMediaKey, location, hashtags)
                    CreationType.SHORT -> onPublishShort(caption, selectedMediaKey, soundTitle, hashtags)
                    CreationType.STORY -> onPublishStory(caption, selectedMediaKey)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("publish_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NkPurple),
            enabled = !isPublishing
        ) {
            if (isPublishing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Publishing...")
            } else {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (creationType) {
                        CreationType.POST -> "Share to Feed"
                        CreationType.SHORT -> "Post Short Reel"
                        CreationType.STORY -> "Share to 24h Story"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
