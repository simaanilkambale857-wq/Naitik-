package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: (emailOrPhone: String, pass: String) -> Unit,
    onRegisterSuccess: (username: String, displayName: String, emailOrPhone: String, bio: String) -> Unit,
    onQuickSwitchUser: (UserEntity) -> Unit,
    availableUsers: List<UserEntity>
) {
    var isSignUp by remember { mutableStateOf(false) }
    var emailOrPhone by remember { mutableStateOf("simaanilkambale857@gmail.com") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Signup specific
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // App Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(NkBrandGradient)
                .padding(3.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_nk_logo),
                contentDescription = "NK Social Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(17.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "NK Social 2.0",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Connect. Create. Inspire.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Tabs: Log In vs Sign Up
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = !isSignUp,
                onClick = {
                    isSignUp = false
                    errorMessage = null
                },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("Log In", fontWeight = if (!isSignUp) FontWeight.Bold else FontWeight.Normal)
            }
            SegmentedButton(
                selected = isSignUp,
                onClick = {
                    isSignUp = true
                    errorMessage = null
                },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("Create Account", fontWeight = if (isSignUp) FontWeight.Bold else FontWeight.Normal)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (errorMessage != null) {
            Surface(
                color = Color.Red.copy(alpha = 0.1f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Form Fields
        if (isSignUp) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                placeholder = { Text("e.g. alex_vance") },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = NkPurple) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Full Name") },
                placeholder = { Text("e.g. Alex Vance") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NkBlue) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Email or Phone
        OutlinedTextField(
            value = emailOrPhone,
            onValueChange = { emailOrPhone = it },
            label = { Text("Email or Phone Number") },
            placeholder = { Text("name@example.com or +1234567890") },
            leadingIcon = { Icon(Icons.Default.ContactMail, contentDescription = null, tint = NkCyan) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_email_or_phone_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NkPink) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_password_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        if (isSignUp) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio (optional)") },
                placeholder = { Text("A few words about you...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                maxLines = 3
            )
        } else {
            // Forgot Password Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Forgot password?",
                    color = NkBlueLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { showForgotPasswordDialog = true }
                        .padding(4.dp)
                        .testTag("forgot_password_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                if (isSignUp) {
                    if (username.isBlank() || emailOrPhone.isBlank()) {
                        errorMessage = "Please enter both username and email or phone."
                        return@Button
                    }
                    onRegisterSuccess(username, displayName, emailOrPhone, bio)
                } else {
                    if (emailOrPhone.isBlank()) {
                        errorMessage = "Please enter your email or phone."
                        return@Button
                    }
                    onLoginSuccess(emailOrPhone, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("auth_submit_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NkPurple),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (isSignUp) "Create My Account" else "Log In to NK Social",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Demo Accounts Switcher
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "One-Tap Demo Accounts:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableUsers.take(3).forEach { user ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onQuickSwitchUser(user) }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "@${user.username}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (user.role == "ADMIN") "Admin" else "Creator",
                            fontSize = 10.sp,
                            color = if (user.role == "ADMIN") NkCyan else NkPurpleLight
                        )
                    }
                }
            }
        }
    }

    // Password Recovery Dialog
    if (showForgotPasswordDialog) {
        var recoveryEmail by remember { mutableStateOf(emailOrPhone) }
        var resetSent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Password Recovery") },
            text = {
                Column {
                    if (!resetSent) {
                        Text(
                            "Enter your email or phone number. We'll send an authentication recovery link to reset your password.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = recoveryEmail,
                            onValueChange = { recoveryEmail = it },
                            label = { Text("Email or Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Reset link sent!", fontWeight = FontWeight.Bold)
                            Text("Check your inbox or SMS for instructions to recover your NK Social account.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            confirmButton = {
                if (!resetSent) {
                    Button(
                        onClick = { resetSent = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NkBlue)
                    ) {
                        Text("Send Recovery Link")
                    }
                } else {
                    Button(onClick = { showForgotPasswordDialog = false }) {
                        Text("Done")
                    }
                }
            },
            dismissButton = {
                if (!resetSent) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
