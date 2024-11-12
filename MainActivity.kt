package com.example.trialapp2

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trialapp2.ui.theme.TrialApp2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaPlayer = MediaPlayer.create(this, R.raw.calmdown)

        setContent {
            var isPlaying by remember { mutableStateOf(false) }
            var currentPosition by remember { mutableStateOf(0) }
            val totalDuration by remember { mutableStateOf(mediaPlayer.duration) }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(isPlaying) {
                if (isPlaying) {
                    while (mediaPlayer.isPlaying) {
                        delay(1000)
                        currentPosition = mediaPlayer.currentPosition
                    }
                }
            }

            TrialApp2Theme {
                MusicPlayerScreen(
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    totalDuration = totalDuration,
                    onPlayPauseClick = {
                        if (isPlaying) {
                            mediaPlayer.pause()
                        } else {
                            mediaPlayer.start()
                            coroutineScope.launch {
                                while (mediaPlayer.isPlaying) {
                                    delay(1000)
                                    currentPosition = mediaPlayer.currentPosition
                                }
                            }
                        }
                        isPlaying = !isPlaying
                    },
                    onSeekBarChange = { progress ->
                        mediaPlayer.seekTo(progress)
                        currentPosition = progress
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}

@Composable
fun MusicPlayerScreen(
    isPlaying: Boolean,
    currentPosition: Int,
    totalDuration: Int,
    onPlayPauseClick: () -> Unit,
    onSeekBarChange: (Int) -> Unit
) {
    val playPauseIcon = if (isPlaying) {
        painterResource(id = R.drawable.baseline_pause_24)
    } else {
        painterResource(id = R.drawable.baseline_play_arrow_24)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "calm_down_selena_rema",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F51B5)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.song),
            contentDescription = "Song Image",
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SeekBarWithProgress(
            currentProgress = currentPosition,
            maxProgress = totalDuration,
            onProgressChange = onSeekBarChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle Skip Previous */ }) {
                Icon(painter = painterResource(id = R.drawable.baseline_skip_previous_24), contentDescription = "Previous")
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = onPlayPauseClick) {
                Icon(painter = playPauseIcon, contentDescription = if (isPlaying) "Pause" else "Play")
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = { /* Handle Skip Next */ }) {
                Icon(painter = painterResource(id = R.drawable.baseline_skip_next_24), contentDescription = "Next")
            }
        }
    }
}

@Composable
fun SeekBarWithProgress(
    currentProgress: Int,
    maxProgress: Int,
    onProgressChange: (Int) -> Unit
) {
    Slider(
        value = currentProgress.toFloat(),
        onValueChange = { onProgressChange(it.toInt()) },
        valueRange = 0f..maxProgress.toFloat(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrialApp2Theme {
        MusicPlayerScreen(
            isPlaying = false,
            currentPosition = 0,
            totalDuration = 100,
            onPlayPauseClick = {},
            onSeekBarChange = {}
        )
    }
}

