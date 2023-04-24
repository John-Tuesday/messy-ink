package org.calamarfederal.messyink.feature_notes.presentation

import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.calamarfederal.messyink.feature_notes.navigation.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeatureNoteEntry(
    parentNavController: NavHostController,
    backStackEntry: NavBackStackEntry,
) {

    var drawerEnabled by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var drawerContent by remember { mutableStateOf<@Composable ColumnScope.() -> Unit>({}) }
    val drawerScope = rememberCoroutineScope()
    LaunchedEffect(drawerEnabled) {
        if (!drawerEnabled)
            drawerScope.launch { drawerState.snapTo(DrawerValue.Closed) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerEnabled,
        drawerContent = { ModalDrawerSheet { drawerContent() } },
    ) {
        val viewModel: NotesViewEditModel = hiltViewModel(backStackEntry)

        val noteNavController = rememberNavController()

        val drawerOnBack = remember(drawerState) {
            OnBackInvokedCallback {
                drawerScope.launch {
                    drawerState.snapTo(DrawerValue.Closed)
                }
            }
        }
        val dispatcher = LocalView.current.findOnBackInvokedDispatcher()

        DisposableEffect(drawerState.targetValue, dispatcher, drawerOnBack) {
            if (drawerState.targetValue == DrawerValue.Open) {
                dispatcher?.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_OVERLAY,
                    drawerOnBack,
                )
            }
            onDispose {
                dispatcher?.unregisterOnBackInvokedCallback(drawerOnBack)
            }
        }

        val drawerSetter by remember {
            derivedStateOf {
                NoteDrawer {
                    if (it == null) {
                        drawerEnabled = false; drawerContent = {}
                    } else {
                        drawerEnabled = true; drawerContent = it
                    }
                }
            }
        }


        NoteNavHost.SubNavHost(
            navController = noteNavController,
        ) {
            viewAllNotes(
                noteNavController,
                drawerContent = drawerSetter,
                navigateToNoteView = { noteNavController.toViewNote(it) },
                navigateToNoteEdit = { noteNavController.toEditNote(it) },
                navigateToNoteCreate = { noteNavController.toEditNewNote() },
            )
            viewNote(
                noteNavController,
                drawerContent = drawerSetter,
                navigateToNoteEdit = { noteNavController.toEditNote(it) },
                navigateToNoteView = { noteNavController.toViewNote(it) },
                navigateToViewAll = { noteNavController.toViewAll() },
                onDoesNotExist = { noteNavController.popBackStack() },
                onOpenDrawer = { drawerScope.launch { drawerState.open() }},
            )
            editNote(
                noteNavController,
                drawerContent = drawerSetter,
                navigateToViewNote = { noteNavController.toViewNote(it) },
                navigateToViewAll = { noteNavController.toViewAll() },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteTopBar(
    title: String,
    onOpenDrawer: () -> Unit,
    drawerEnabled: Boolean,
    actions: @Composable RowScope.() -> Unit,
    navigationIcon: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
//        colors = TopAppBarDefaults.smallTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
//            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        ),
        scrollBehavior = scrollBehavior,
        title = { Text(title) },
        navigationIcon = {
            if (drawerEnabled)
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Filled.Menu, "open drawer")
                }
            else navigationIcon()
        },
        actions = actions,
    )
}
