package com.example.ui

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.testTag
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.api.*
import com.example.viewmodel.DietPlannerViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietPlannerDashboard(viewModel: DietPlannerViewModel) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val showProfileSetupOnboarding by viewModel.showProfileSetupOnboarding.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val currentMealPlan by viewModel.currentMealPlan.collectAsState()
    val waterLog by viewModel.waterLog.collectAsState()
    val weightLogs by viewModel.allWeightLogs.collectAsState()
    val reminders by viewModel.allReminders.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val eventMessage by viewModel.eventMessage.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    // Screen navigation state inside our dashboard (0: Home/Tools, 1: Meals, 2: Explore, 3: Tracker, 4: Account)
    var currentTab by remember { mutableStateOf(2) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            currentTab = 2 // Redirect straight to Explore tab upon login!
        }
    }

    // Floating success snackbar notification helper
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(eventMessage) {
        eventMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearEventMessage()
        }
    }

    val currentHeaderDay = remember(isBengali) {
        val sdf = if (isBengali) {
            SimpleDateFormat("EEEE, dd MMMM", Locale("bn", "BD"))
        } else {
            SimpleDateFormat("EEEE, MMM dd", Locale.ENGLISH)
        }
        sdf.format(Date())
    }

    // Modal Drawer dialogue tracking states
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showRatingsDialog by remember { mutableStateOf(false) }
    var showAppInfoDialog by remember { mutableStateOf(false) }
    var showAICoachDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var showHealthPrefsScreen by remember { mutableStateOf(false) }
    var showShoppingListScreen by remember { mutableStateOf(false) }
    var showQuickLogDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (!isLoggedIn) {
        ANEXSOPZLoginScreen(
            isBengali = isBengali,
            onLogin = { email, password, onResult ->
                viewModel.login(email, password) { success, error ->
                    if (success && email == "guest@subecha.com") {
                        viewModel.preloadAllDemoDataForUser("guest")
                    }
                    onResult(success, error)
                }
            },
            onSignUp = { email, password, onResult ->
                viewModel.signup(email, password) { success, error ->
                    if (success && email == "guest@subecha.com") {
                        viewModel.preloadAllDemoDataForUser("guest")
                    }
                    onResult(success, error)
                }
            }
        )
    } else if (showProfileSetupOnboarding) {
        UserProfileSetupScreen(
            viewModel = viewModel,
            isBengali = isBengali,
            onComplete = {
                viewModel.setProfileSetupOnboardingShown(false)
                currentTab = 2 // Redirect straight to Explore tab
            }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(300.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Header of Drawer with ANEXSOPZ Branding Logo
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(bottom = 24.dp, top = 12.dp)
                        ) {
                            ANEXSOPZModernLogo(
                                modifier = Modifier.size(54.dp),
                                showText = false,
                                isBengali = isBengali
                            )
                            Column {
                                Text(
                                    text = "ANEXSOPZ Health",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (isBengali) "আপনার বিশ্বস্ত জীবনযাত্রা সহযোগী" else "Your trusted Smart Health Companion",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(bottom = 16.dp))

                        // Custom Suvecha upper drawer items (Shopping List, AI Coach, Ratings, Info Manual)
                        val upperDrawerItems = listOf(
                            Triple(if (isBengali) "সাপ্তাহিক বাজারের ফর্দ" else "Weekly Shopping List", Icons.Default.ShoppingCart) {
                                showShoppingListScreen = true
                            },
                            Triple(if (isBengali) "লাইফস্টাইল কোচ" else "Lifestyle Coach", Icons.Default.Face) {
                                showAICoachDialog = true
                            },
                            Triple(if (isBengali) "রেটিং এবং রিভিউ" else "Ratings & Reviews", Icons.Default.Star) {
                                showRatingsDialog = true
                            },
                            Triple(if (isBengali) "অ্যাপ গাইড ও সহায়িকা" else "Information Manual", Icons.Default.MenuBook) {
                                showAppInfoDialog = true
                            }
                        )

                        // Custom Suvecha lower drawer items (Terms, Privacy)
                        val lowerDrawerItems = listOf(
                            Triple(if (isBengali) "ব্যবহারের শর্তাবলী" else "Terms & Conditions", Icons.Default.Description) {
                                showTermsDialog = true
                            },
                            Triple(if (isBengali) "প্রাইভেসি পলিসি" else "Privacy Policy", Icons.Default.Security) {
                                showPrivacyDialog = true
                            }
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            upperDrawerItems.forEach { (title, icon, action) ->
                                NavigationDrawerItem(
                                    label = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        action()
                                    },
                                    icon = { Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedContainerColor = Color.Transparent,
                                        unselectedIconColor = MaterialTheme.colorScheme.primary,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            // Dynamic Dark Theme Switch Item in the Drawer
                            NavigationDrawerItem(
                                label = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isBengali) "ডার্ক মোড (Dark Theme)" else "Dark Theme Mode",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        )
                                        Switch(
                                            checked = isDarkTheme,
                                            onCheckedChange = { viewModel.toggleTheme(context) },
                                            modifier = Modifier.scale(0.75f),
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    }
                                },
                                selected = false,
                                onClick = { viewModel.toggleTheme(context) },
                                icon = {
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = "Theme Icon",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 4.dp))

                            lowerDrawerItems.forEach { (title, icon, action) ->
                                NavigationDrawerItem(
                                    label = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        action()
                                    },
                                    icon = { Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedContainerColor = Color.Transparent,
                                        unselectedIconColor = MaterialTheme.colorScheme.primary,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        // Footer section with Suvecha brand, version and sign out
                        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(bottom = 12.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    viewModel.logout()
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (isBengali) "লগআউট করুন" else "Sign Out",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFC62828)
                            )
                        }

                        // Support query section beneath Sign Out
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isBengali) "যেকোনো প্রশ্নের জন্য যোগাযোগ করুন:" else "For any query, contact us at:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "support@anexsopz.com",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        Text(
                            text = "ANEXSOPZ Health Plus v2.1",
                            fontSize = 9.sp,
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                        // Left Side Hamburger Toggle Button
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "ANEXSOPZ Navigation Drawer",
                                tint = Color(0xFF1E5E2F),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Centered Logo & Brand Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            ANEXSOPZModernLogo(
                                modifier = Modifier.size(34.dp),
                                showText = false,
                                isBengali = isBengali
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "ANEXSOPZ",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E5E2F),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = if (isBengali) "সুস্থ জীবনের পথ" else "Path to Healthy Living",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF43A047),
                                    letterSpacing = 0.2.sp
                                )
                            }
                        }

                        // Right Side Language Switch Row with custom alerts option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            // Language Toggle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable { viewModel.toggleLanguage() }
                                    .padding(horizontal = 7.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "EN" else "বাংলা",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Theme Toggle Button
                            IconButton(
                                onClick = { viewModel.toggleTheme(context) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Notification Option with indicator badge
                            var showNotifications by remember { mutableStateOf(false) }
                            IconButton(
                                onClick = { showNotifications = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "ANEXSOPZ Alerts",
                                        tint = Color(0xFF1E5E2F),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .align(Alignment.TopEnd)
                                    )
                                }
                            }

                            if (showNotifications) {
                                ANEXSOPZNotificationDialog(
                                    isBengali = isBengali,
                                    onDismiss = { showNotifications = false }
                                )
                            }

                            if (showSearchDialog) {
                                ANEXSOPZSearchDialog(
                                    isBengali = isBengali,
                                    onDismiss = { showSearchDialog = false },
                                    onNavigateToTab = { tabIndex ->
                                        currentTab = tabIndex
                                        showSearchDialog = false
                                    }
                                )
                            }

                            if (showTermsDialog) {
                                ANEXSOPZTermsDialog(
                                    isBengali = isBengali,
                                    onDismiss = { showTermsDialog = false }
                                )
                            }

                            if (showPrivacyDialog) {
                                ANEXSOPZPrivacyPolicyDialog(
                                    isBengali = isBengali,
                                    onDismiss = { showPrivacyDialog = false }
                                )
                            }

                            if (showAICoachDialog) {
                                ANEXSOPZAICoachDialog(
                                    isBengali = isBengali,
                                    onDismiss = { showAICoachDialog = false }
                                )
                            }

                            if (showRatingsDialog) {
                                ANEXSOPZRatingsDialog(
                                    isBengali = isBengali,
                                    onDismiss = { showRatingsDialog = false }
                                )
                            }

                            if (showAppInfoDialog) {
                                ANEXSOPZAppInfoDialog(
                                    isBengali = isBengali,
                                    onDismiss = { showAppInfoDialog = false }
                                )
                            }

                            if (showQuickLogDialog) {
                                ANEXSOPZQuickLogDialog(
                                    viewModel = viewModel,
                                    isBengali = isBengali,
                                    onDismiss = { showQuickLogDialog = false }
                                )
                            }
                        }
                        }

                        // Sliding Search Box Bar below the navigation row representing standard exploration search
                        var searchPlaceholderIndex by remember { mutableStateOf(0) }
                        val searchPlaceholders = listOf(
                            "egg protein ...",
                            "brown rice carbs ...",
                            "oatmeal recipe ...",
                            "water tracker log ...",
                            "cardio exercises ...",
                            "healthy salads ..."
                        )
                        LaunchedEffect(Unit) {
                            while (true) {
                                kotlinx.coroutines.delay(2500)
                                searchPlaceholderIndex = (searchPlaceholderIndex + 1) % searchPlaceholders.size
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { showSearchDialog = true }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Search for " + searchPlaceholders[searchPlaceholderIndex],
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color(0xFFF3F4F9)
                    ) {
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { currentTab = 0 },
                            icon = { Icon(Icons.Default.Home, contentDescription = "হোম (Home)") },
                            label = { Text(if (isBengali) "হোম" else "Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { currentTab = 1 },
                            icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "নাস্তা ও খাবার (Diet Plan)") },
                            label = { Text(if (isBengali) "খাবার" else "Meals", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        NavigationBarItem(
                            selected = currentTab == 2,
                            onClick = { currentTab = 2 },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "এক্সপলোর (Explore)") },
                            label = { Text(if (isBengali) "এক্সপ্লোর" else "Explore", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        NavigationBarItem(
                            selected = currentTab == 3,
                            onClick = { currentTab = 3 },
                            icon = { Icon(Icons.Default.TrendingUp, contentDescription = "ওজন ও ট্র্যাক (Progress)") },
                            label = { Text(if (isBengali) "ট্র্যাকার" else "Tracker", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                        NavigationBarItem(
                            selected = currentTab == 4,
                            onClick = { currentTab = 4 },
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "প্রোফাইল (Profile)") },
                            label = { Text(if (isBengali) "অ্যাকাউন্ট" else "Account", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                },
                floatingActionButton = {
                    if (currentTab == 1 && userProfile != null && !isGenerating) {
                        FloatingActionButton(
                            onClick = { viewModel.generateMealPlan(context) },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Generate"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "সুষম খাবার" else "Custom Plan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else if ((currentTab == 0 || currentTab == 3) && userProfile != null) {
                        FloatingActionButton(
                            onClick = { showQuickLogDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(bottom = 8.dp).testTag("quick_log_fab")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Quick Log"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "কুইক লগ" else "Quick Log",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
        ) {
            // Check if profile exists, if empty, redirect to profile screen first
            if (userProfile == null) {
                ProfileSetupView(
                    isBengali = isBengali,
                    onSave = { age, gender, weight, height, goal, preference, allergies, medical, cuisine ->
                        viewModel.saveProfile(age, gender, weight, height, goal, preference, allergies, medical, cuisine)
                        currentTab = 0 // Navigate to index after creation
                    }
                )
            } else if (showHealthPrefsScreen) {
                HealthPreferencesScreen(
                    viewModel = viewModel,
                    isBengali = isBengali,
                    onBack = { showHealthPrefsScreen = false }
                )
            } else if (showShoppingListScreen) {
                ShoppingListScreen(
                    viewModel = viewModel,
                    onBack = { showShoppingListScreen = false }
                )
            } else {
                when (currentTab) {
                    0 -> ToolsTab(
                        viewModel = viewModel,
                        reminders = reminders,
                        userProfile = userProfile!!,
                        selectedDate = selectedDate
                    )
                    1 -> MealPlanTab(
                        viewModel = viewModel,
                        selectedDate = selectedDate,
                        mealPlan = currentMealPlan,
                        isGenerating = isGenerating,
                        userProfile = userProfile!!,
                        context = context,
                        onNavigateToShoppingList = { showShoppingListScreen = true }
                    )
                    2 -> ExploreTab(
                        viewModel = viewModel,
                        isBengali = isBengali,
                        onNavigateToTab = { tabIndex -> currentTab = tabIndex }
                    )
                    3 -> ProgressTrackerTab(
                        viewModel = viewModel,
                        weightLogs = weightLogs,
                        waterLog = waterLog,
                        userProfile = userProfile!!,
                        selectedDate = selectedDate
                    )
                    4 -> ProfileEditTab(
                        userProfile = userProfile!!,
                        isBengali = isBengali,
                        onNavigateToHealthPrefs = { showHealthPrefsScreen = true },
                        onSave = { age, gender, weight, height, goal, preference, allergies, medical, cuisine ->
                            viewModel.saveProfile(age, gender, weight, height, goal, preference, allergies, medical, cuisine)
                        }
                    )
                }
            }
        }
    }
    }
    }
}

// ==========================================
// TAB 0: MEAL PLAN & DIET SCREEN
// ==========================================
@Composable
fun MealPlanTab(
    viewModel: DietPlannerViewModel,
    selectedDate: String,
    mealPlan: MealPlanEntity?,
    isGenerating: Boolean,
    userProfile: UserProfileEntity,
    context: Context,
    onNavigateToShoppingList: () -> Unit
) {
    // Collect extra food and exercise logs to automatically calculate net calorie balance
    val currentFoodLogs by viewModel.currentFoodLogs.collectAsState()
    val currentExerciseLogs by viewModel.currentExerciseLogs.collectAsState()
    val isBengali by viewModel.isBengali.collectAsState()
    val waterLog by viewModel.waterLog.collectAsState()

    var showDailyInsightDialog by remember { mutableStateOf(false) }

    val extraSnacksCal = currentFoodLogs.sumOf { it.calories }
    val workoutBurntCal = currentExerciseLogs.sumOf { it.caloriesBurned }

    // In-memory persistent map for checked meals per category on specific dates
    val completedMeals = remember { mutableStateMapOf<String, Boolean>() }

    val breakfastKey = "${selectedDate}_breakfast"
    val snack1Key = "${selectedDate}_snack1"
    val lunchKey = "${selectedDate}_lunch"
    val snack2Key = "${selectedDate}_snack2"
    val dinnerKey = "${selectedDate}_dinner"

    // Default mock setup for a cohesive first-run matching HTML design mockup (Oatmeal & Apple Done, Lunch Next Up)
    LaunchedEffect(selectedDate) {
        if (!completedMeals.containsKey(breakfastKey)) {
            completedMeals[breakfastKey] = true
            completedMeals[snack1Key] = true
        }
    }

    val isBreakfastDone = completedMeals[breakfastKey] == true
    val isSnack1Done = completedMeals[snack1Key] == true
    val isLunchDone = completedMeals[lunchKey] == true
    val isSnack2Done = completedMeals[snack2Key] == true
    val isDinnerDone = completedMeals[dinnerKey] == true

    val totalCalorieTarget = mealPlan?.calorieTarget ?: userProfile.dailyCalorieTarget
    var consumedCal = 0
    if (mealPlan != null) {
        if (isBreakfastDone) consumedCal += mealPlan.breakfastCal
        if (isSnack1Done) consumedCal += mealPlan.snack1Cal
        if (isLunchDone) consumedCal += mealPlan.lunchCal
        if (isSnack2Done) consumedCal += mealPlan.snack2Cal
        if (isDinnerDone) consumedCal += mealPlan.dinnerCal
    } else {
        consumedCal = (totalCalorieTarget * 0.65).toInt()
    }

    // Remaining calculations
    val totalConsumed = consumedCal + extraSnacksCal
    val netCalories = (totalConsumed - workoutBurntCal).coerceAtLeast(0)

    val currentHeaderDay = remember(isBengali) {
        val sdf = if (isBengali) {
            SimpleDateFormat("EEEE, dd MMMM", Locale("bn", "BD"))
        } else {
            SimpleDateFormat("EEEE, MMM dd", Locale.ENGLISH)
        }
        sdf.format(Date())
    }

    Scaffold(
        snackbarHost = {}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date Selector Bar
            DateSelectorHeader(
                selectedDate = selectedDate,
                onPreviousDate = {
                    val newDate = adjustDateString(selectedDate, -1)
                    viewModel.selectDate(newDate)
                },
                onNextDate = {
                    val newDate = adjustDateString(selectedDate, 1)
                    viewModel.selectDate(newDate)
                },
                onTodayDate = {
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    viewModel.selectDate(format.format(Date()))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Beautiful application description in the main body
            ANEXSOPZAppDescriptionCard(isBengali = isBengali)

            Spacer(modifier = Modifier.height(16.dp))

            // Basic info summary card
            ProfileMiniCard(userProfile = userProfile)

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Insight Dashboard Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDailyInsightDialog = true }
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌟", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = if (isBengali) "আজকের স্বাস্থ্য অন্তর্দৃষ্টি (Insights)" else "View Daily Health Insights",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isBengali) "ক্যালরি, পানি ও ব্যায়ামের নিখুঁত সমন্বিত রিপোর্ট"
                                       else "Consolidated report of calories, water & active workouts",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Show Insight Dialog",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (showDailyInsightDialog) {
                ANEXSOPZDailyInsightDialog(
                    isBengali = isBengali,
                    totalCalorieTarget = totalCalorieTarget,
                    consumedMealsCal = consumedCal,
                    extraSnacksCal = extraSnacksCal,
                    workoutBurntCal = workoutBurntCal,
                    waterLog = waterLog,
                    dailyWaterTarget = userProfile.dailyWaterTargetMl,
                    currentExerciseLogs = currentExerciseLogs,
                    onDismiss = { showDailyInsightDialog = false }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isGenerating) {
                AIGeneratingStateCard()
            } else if (mealPlan == null) {
                NoMealPlanPlaceholderCard(
                    isBengali = isBengali,
                    onGenerate = {
                        viewModel.generateMealPlan(context)
                    }
                )
            } else {
                // Meal Plan Display - Custom Calorie Progress card
                val progressPercent = if (totalCalorieTarget > 0) {
                    (netCalories.toFloat() / totalCalorieTarget.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }
                MealPlanHeaderSummary(
                    mealPlan = mealPlan,
                    consumedCal = netCalories,
                    progressPercent = progressPercent
                )

                Spacer(modifier = Modifier.height(16.dp))

                MealCardItem(
                    mealTitle = "Breakfast (সকালের নাস্তা)",
                    mealDetails = mealPlan.breakfast,
                    calories = mealPlan.breakfastCal,
                    categoryName = "Breakfast",
                    isDone = isBreakfastDone,
                    onDoneChange = { completedMeals[breakfastKey] = it },
                    isNextUp = !isBreakfastDone
                )
                Spacer(modifier = Modifier.height(12.dp))

                MealCardItem(
                    mealTitle = "Snack 1 (সকালের হালকা খাবার)",
                    mealDetails = mealPlan.snack1,
                    calories = mealPlan.snack1Cal,
                    categoryName = "Snack 1",
                    isDone = isSnack1Done,
                    onDoneChange = { completedMeals[snack1Key] = it },
                    isNextUp = isBreakfastDone && !isSnack1Done
                )
                Spacer(modifier = Modifier.height(12.dp))

                MealCardItem(
                    mealTitle = "Lunch (দুপুরের খাবার)",
                    mealDetails = mealPlan.lunch,
                    calories = mealPlan.lunchCal,
                    categoryName = "Lunch",
                    isDone = isLunchDone,
                    onDoneChange = { completedMeals[lunchKey] = it },
                    isNextUp = isSnack1Done && !isLunchDone
                )
                Spacer(modifier = Modifier.height(12.dp))

                MealCardItem(
                    mealTitle = "Snack 2 (বিকালের হালকা খাবার)",
                    mealDetails = mealPlan.snack2,
                    calories = mealPlan.snack2Cal,
                    categoryName = "Snack 2",
                    isDone = isSnack2Done,
                    onDoneChange = { completedMeals[snack2Key] = it },
                    isNextUp = isLunchDone && !isSnack2Done
                )
                Spacer(modifier = Modifier.height(12.dp))

                MealCardItem(
                    mealTitle = "Dinner (রাতের খাবার)",
                    mealDetails = mealPlan.dinner,
                    calories = mealPlan.dinnerCal,
                    categoryName = "Dinner",
                    isDone = isDinnerDone,
                    onDoneChange = { completedMeals[dinnerKey] = it },
                    isNextUp = isSnack2Done && !isDinnerDone
                )

                Spacer(modifier = Modifier.height(20.dp))

                // motivational health tip banner card
                HealthTipCard(tip = mealPlan.dailyTip)

                Spacer(modifier = Modifier.height(24.dp))

                // Auto-generated interactive ingredients checklist shopping list
                LocalShoppingListCard(viewModel = viewModel, isBengali = isBengali)

                Spacer(modifier = Modifier.height(12.dp))

                // Premium Week-long categorized checklist screen trigger
                Button(
                    onClick = { onNavigateToShoppingList() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weekly_shopping_list_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Weekly Shopping List",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBengali) "সাপ্তাহিক বাজারের ফর্দ" else "View Weekly Shopping List",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val pdfFile = viewModel.exportPdfReport(context, mealPlan, userProfile)
                            if (pdfFile != null) {
                                sharePdf(context, pdfFile)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Report Export")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBengali) "PDF রিপোর্ট" else "PDF Report")
                    }

                    OutlinedButton(
                        onClick = {
                            shareMealPlanToSocial(context, mealPlan)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Social Sharing")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBengali) "শেয়ার করুন" else "Share Plan")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { viewModel.generateMealPlan(context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBengali) "নতুন ডুয়েট প্ল্যান জেনারেট করুন" else "Regenerate Diet Plan")
                }
            }
        }
    }
}
