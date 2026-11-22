package com.example.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.JodivaContainer
import com.example.data.model.ThemeMode
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreateScreen
import com.example.ui.screens.ExploreHubScreen
import com.example.ui.screens.HelpAndSettingsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizLeaderboardScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.JodivaTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.JodivaViewModel

object JodivaRoutes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val CREATE = "create?mode={mode}"
    const val REELS = "reels"
    const val PROFILE = "profile"
    const val USER_PROFILE = "user_profile/{userId}"
    const val MESSAGES = "messages?threadId={threadId}"
    const val QUIZ = "quiz"
    const val EXPLORE = "explore/{section}"
    const val HELP_SETTINGS = "help_settings/{tab}"

    fun create(mode: String = "Post") = "create?mode=$mode"
    fun userProfile(userId: String) = "user_profile/$userId"
    fun messages(threadId: String? = null) =
        if (threadId != null) "messages?threadId=$threadId" else "messages"
    fun explore(section: String) = "explore/$section"
    fun helpSettings(tab: String) = "help_settings/$tab"
}

private data class BottomNavTab(
    val route: String,
    val matchPrefix: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

private val mainTabs = listOf(
    BottomNavTab(JodivaRoutes.HOME, "home", "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    BottomNavTab(JodivaRoutes.SEARCH, "search", "Search", Icons.Filled.Search, Icons.Filled.Search, "nav_search"),
    BottomNavTab(JodivaRoutes.create("Post"), "create", "Create", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline, "nav_create"),
    BottomNavTab(JodivaRoutes.REELS, "reels", "Reels", Icons.Filled.Movie, Icons.Outlined.Movie, "nav_reels"),
    BottomNavTab(JodivaRoutes.PROFILE, "profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
)

@Composable
fun JodivaApp(container: JodivaContainer) {
    val mainViewModel: JodivaViewModel = viewModel(factory = JodivaViewModel.factory(container.repository))
    val prefs by mainViewModel.preferences.collectAsStateWithLifecycle()
    val currentUser by mainViewModel.currentUser.collectAsStateWithLifecycle()
    val statusBanner by mainViewModel.statusBanner.collectAsStateWithLifecycle()

    val isDark = when (prefs.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    JodivaTheme(darkTheme = isDark) {
        val uid = prefs.currentUserId
        if (uid.isNullOrBlank()) {
            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(container.repository))
            AuthScreen(viewModel = authViewModel)
        } else {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: JodivaRoutes.HOME

            val showMainNav = currentRoute == JodivaRoutes.HOME ||
                currentRoute == JodivaRoutes.SEARCH ||
                currentRoute.startsWith("create") ||
                currentRoute == JodivaRoutes.REELS ||
                currentRoute == JodivaRoutes.PROFILE

            LaunchedEffect(statusBanner) {
                val msg = statusBanner
                if (msg != null) {
                    snackbarHostState.showSnackbar(msg)
                    mainViewModel.clearStatusBanner()
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useRail = maxWidth >= 600.dp

                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (showMainNav && !useRail) {
                            NavigationBar {
                                mainTabs.forEach { tab ->
                                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                                        it.route?.startsWith(tab.matchPrefix) == true
                                    } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(tab.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(tab.label) },
                                        modifier = Modifier.testTag(tab.tag)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (showMainNav && useRail) {
                            NavigationRail {
                                mainTabs.forEach { tab ->
                                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                                        it.route?.startsWith(tab.matchPrefix) == true
                                    } == true
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(tab.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(tab.label) }
                                    )
                                }
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = JodivaRoutes.HOME,
                            modifier = Modifier.weight(1f)
                        ) {
                            composable(JodivaRoutes.HOME) {
                                HomeScreen(
                                    viewModel = mainViewModel,
                                    onOpenMessages = { navController.navigate(JodivaRoutes.messages()) },
                                    onOpenQuiz = { navController.navigate(JodivaRoutes.QUIZ) },
                                    onOpenExploreHub = { sec -> navController.navigate(JodivaRoutes.explore(sec)) },
                                    onOpenHelp = { navController.navigate(JodivaRoutes.helpSettings("AI Help")) },
                                    onOpenCreateStory = { navController.navigate(JodivaRoutes.create("Story")) },
                                    onOpenUserProfile = { targetId ->
                                        if (targetId == currentUser?.userId) {
                                            navController.navigate(JodivaRoutes.PROFILE)
                                        } else {
                                            navController.navigate(JodivaRoutes.userProfile(targetId))
                                        }
                                    }
                                )
                            }

                            composable(JodivaRoutes.SEARCH) {
                                SearchScreen(
                                    viewModel = mainViewModel,
                                    onOpenThread = { threadId -> navController.navigate(JodivaRoutes.messages(threadId)) },
                                    onOpenUserProfile = { targetId -> navController.navigate(JodivaRoutes.userProfile(targetId)) }
                                )
                            }

                            composable(
                                route = JodivaRoutes.CREATE,
                                arguments = listOf(navArgument("mode") {
                                    type = NavType.StringType
                                    defaultValue = "Post"
                                })
                            ) { backStackEntry ->
                                val mode = backStackEntry.arguments?.getString("mode") ?: "Post"
                                CreateScreen(
                                    viewModel = mainViewModel,
                                    initialMode = mode,
                                    onPublished = {
                                        navController.navigate(JodivaRoutes.HOME) {
                                            popUpTo(JodivaRoutes.HOME) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(JodivaRoutes.REELS) {
                                ReelsScreen(
                                    viewModel = mainViewModel,
                                    onCreateReel = { navController.navigate(JodivaRoutes.create("Reel")) },
                                    onOpenUserProfile = { targetId -> navController.navigate(JodivaRoutes.userProfile(targetId)) }
                                )
                            }

                            composable(JodivaRoutes.PROFILE) {
                                ProfileScreen(
                                    viewModel = mainViewModel,
                                    onOpenSettings = { navController.navigate(JodivaRoutes.helpSettings("Settings")) },
                                    onOpenThread = { threadId -> navController.navigate(JodivaRoutes.messages(threadId)) }
                                )
                            }

                            composable(
                                route = JodivaRoutes.USER_PROFILE,
                                arguments = listOf(navArgument("userId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val targetId = backStackEntry.arguments?.getString("userId")
                                ProfileScreen(
                                    viewModel = mainViewModel,
                                    viewedUserId = targetId,
                                    onBack = { navController.popBackStack() },
                                    onOpenSettings = { navController.navigate(JodivaRoutes.helpSettings("Settings")) },
                                    onOpenThread = { threadId -> navController.navigate(JodivaRoutes.messages(threadId)) }
                                )
                            }

                            composable(
                                route = JodivaRoutes.MESSAGES,
                                arguments = listOf(navArgument("threadId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                })
                            ) { backStackEntry ->
                                val threadId = backStackEntry.arguments?.getString("threadId")
                                MessagesScreen(
                                    viewModel = mainViewModel,
                                    initialThreadId = threadId,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(JodivaRoutes.QUIZ) {
                                QuizLeaderboardScreen(
                                    viewModel = mainViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = JodivaRoutes.EXPLORE,
                                arguments = listOf(navArgument("section") {
                                    type = NavType.StringType
                                    defaultValue = "News"
                                })
                            ) { backStackEntry ->
                                val sec = backStackEntry.arguments?.getString("section") ?: "News"
                                ExploreHubScreen(
                                    viewModel = mainViewModel,
                                    initialSection = sec,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = JodivaRoutes.HELP_SETTINGS,
                                arguments = listOf(navArgument("tab") {
                                    type = NavType.StringType
                                    defaultValue = "Settings"
                                })
                            ) { backStackEntry ->
                                val tab = backStackEntry.arguments?.getString("tab") ?: "Settings"
                                HelpAndSettingsScreen(
                                    viewModel = mainViewModel,
                                    initialTab = tab,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
