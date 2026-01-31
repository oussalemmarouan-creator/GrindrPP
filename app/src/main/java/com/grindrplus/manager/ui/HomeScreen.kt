package com.grindrplus.manager.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(innerPadding: PaddingValues, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val lastUpdated = viewModel.lastUpdated.value
    val lastUpdatedText = lastUpdated?.let {
        DateTimeFormatter.ofPattern("MMM d, yyyy • h:mm a")
            .withZone(ZoneId.systemDefault())
            .format(it)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "GrindrPlus",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enhanced Features for Grindr",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.fetchData(forceRefresh = true) },
                    enabled = !viewModel.isLoading.value
                ) {
                    Text(text = if (viewModel.isLoading.value) "Refreshing..." else "Refresh")
                }

                if (lastUpdatedText != null) {
                    Text(
                        text = "Last updated $lastUpdatedText",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        }

        viewModel.errorMessage.value?.let { message ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = message,
                    color = Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Button(onClick = { viewModel.fetchData(forceRefresh = true) }) {
                    Text("Try again")
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Contributors",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (viewModel.isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.contributors.size) { index ->
                        val (login, avatarUrl) = viewModel.contributors.entries.elementAt(viewModel.contributors.size - index - 1)
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar of $login",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable {
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        "https://github.com/$login".toUri()
                                    ).also { intent ->
                                        context.startActivity(intent)
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            val sortedReleases = viewModel.releases.entries.sortedByDescending { (_, release) -> release.publishedAt }
            items(sortedReleases.size) { index ->
                val (_, release) = sortedReleases[index]
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = release.avatarUrl,
                                contentDescription = "Avatar of ${release.author}",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text =
                                    "${release.author} • ${release.name}", fontSize = 14.sp, fontWeight = FontWeight.Bold
                            )
                        }

                        MarkdownText(
                            markdown = release.description,
                            syntaxHighlightColor = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
