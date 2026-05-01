package com.rein.tradingcoach.ui.screens.violations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rein.tradingcoach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViolationHistoryScreen(
    onViolationClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: ViolationHistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Violations", color = TcTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TcTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TcPageBg),
            )
        },
        containerColor = TcPageBg,
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = viewModel::loadFirstPage,
            modifier = Modifier.padding(padding),
        ) {
            state.error?.let {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(it, color = TcRed) }
                return@PullToRefreshBox
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.violations, key = { it.id }) { violation ->
                    ViolationRowCard(violation = violation, onClick = { onViolationClick(violation.id) })
                    LaunchedEffect(violation.id) {
                        viewModel.loadNextPageIfNeeded(violation)
                    }
                }
                if (state.isLoadingNextPage) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TcBlue)
                        }
                    }
                }
            }
        }
    }
}
