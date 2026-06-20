package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MindfulnessSpace(
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Guided Breathing cycles state
    var isBreathingActive by remember { mutableStateOf(false) }
    var breathingPhase by remember { mutableStateOf("Inhale") } // Inhale, Hold, Exhale
    var phaseTimer by remember { mutableIntStateOf(4) }

    // Sound simulation states
    var playForest by remember { mutableStateOf(false) }
    var playRain by remember { mutableStateOf(false) }
    var playOcean by remember { mutableStateOf(false) }
    var volumeForest by remember { mutableFloatStateOf(0.6f) }
    var volumeRain by remember { mutableFloatStateOf(0.4f) }
    var volumeOcean by remember { mutableFloatStateOf(0.5f) }

    // Interactive target tracker sessions list
    val sharedPrefs = remember {
        context.getSharedPreferences("niljori_mindfulness_logs", Context.MODE_PRIVATE)
    }
    var mindfulnessLogs by remember {
        mutableStateOf(loadMindfulnessLogs(context))
    }

    // Celebration/Achievement animation trigger state
    var showSuccessCelebration by remember { mutableStateOf(false) }
    var celebrationScale by remember { mutableStateOf(0f) }

    // Breathing infinite transition scale modifier
    val transition = rememberInfiniteTransition(label = "breathe")
    val circleScale by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isBreathingActive) 1.7f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle_scale"
    )

    // Side effect to update the text labels dynamically on the breathing loop
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            while (isBreathingActive) {
                // Inhale
                breathingPhase = "Inhale"
                for (i in 4 downTo 1) {
                    phaseTimer = i
                    delay(1000)
                }
                // Hold
                breathingPhase = "Hold"
                for (i in 4 downTo 1) {
                    phaseTimer = i
                    delay(1000)
                }
                // Exhale
                breathingPhase = "Exhale"
                for (i in 4 downTo 1) {
                    phaseTimer = i
                    delay(1000)
                }
            }
        } else {
            breathingPhase = "Idle"
            phaseTimer = 0
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFB39DDB).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("mindfulness_space_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Space
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFEDE7F6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧘", fontSize = 22.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "মাইন্ডফুলনেস ও প্রশান্তি থেরাপি" else "Mindfulness & Zen Space",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF673AB7)
                        )
                        Text(
                            text = if (isBengali) "শ্বাসক্রিয়া ব্যায়াম ও স্ট্রেস রিডাকশন" else "Rhythmic deep diaphragmatic breathing & relaxation",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val shareText = if (isBengali) {
                            "আমি সুভেছা (Suvecha) অ্যাপের মাইন্ডফুলনেস স্টুডিওতে শ্বাস ব্যায়াম সম্পন্ন করেছি! আমার মানসিক শান্তি সূচক ১০০% চার্জড।"
                        } else {
                            "Just finished a calming breathing mindfulness flow in Suvecha Wellness Zen Space! 🧘 Breathe deep and remain positive."
                        }
                        SharingUtils.shareText(context, shareText, "Share Mindfulness Flow")
                    },
                    modifier = Modifier.size(36.dp).testTag("share_mindfulness_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF673AB7),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Divider(color = Color(0xFFEDE7F6))

            // Guided Breathing Sphere Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9).copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(1.dp, Color(0xFFD1C4E9), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pulsing animated breathing orb
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(if (isBreathingActive) circleScale else 1.0f)
                            .background(Color(0xFF9575CD).copy(alpha = 0.25f), CircleShape)
                            .border(2.dp, Color(0xFF673AB7).copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFF673AB7).copy(alpha = 0.2f), CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Phase label
                    Text(
                        text = if (!isBreathingActive) {
                            if (isBengali) "গভীর শ্বাসক্রিয়া শুরু করতে চাপ দিন" else "Tap below to begin Guided Breathing"
                        } else {
                            when (breathingPhase) {
                                "Inhale" -> if (isBengali) "শ্বাস গ্রহণ করুন (Inhale) - $phaseTimer" else "Inhale - $phaseTimer Sec"
                                "Hold" -> if (isBengali) "শ্বাস ধরে রাখুন (Hold) - $phaseTimer" else "Hold - $phaseTimer Sec"
                                "Exhale" -> if (isBengali) "শ্বাস ছেড়ে দিন (Exhale) - $phaseTimer" else "Exhale - $phaseTimer Sec"
                                else -> ""
                            }
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF311B92),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Play/Pause Action Controls
            Button(
                onClick = { isBreathingActive = !isBreathingActive },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBreathingActive) Color(0xFFD32F2F) else Color(0xFF673AB7)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("breathe_toggle_action_btn")
            ) {
                Icon(
                    imageVector = if (isBreathingActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBreathingActive) {
                        if (isBengali) "ব্যায়াম বন্ধ করুন" else "Stop Calm Session"
                    } else {
                        if (isBengali) "শান্ত শ্বাস-প্রশ্বাস শুরু করুন" else "Start Mindful Breathing"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            // Ambient Sound Simulator Module
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isBengali) "প্রাকৃতিক পরিবেষ্টন সাউন্ড হিলিং (সিমুলেটর)" else "Ambient Nature Healing Sounds (Simulator)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF455A64)
                    )

                    // Forest sound
                    SoundControlRow(
                        title = if (isBengali) "🌲 গভীর জঙ্গল" else "🌲 Mystic Forest",
                        isActive = playForest,
                        volume = volumeForest,
                        onActiveChange = { playForest = it },
                        onVolumeChange = { volumeForest = it }
                    )

                    // Rain sound
                    SoundControlRow(
                        title = if (isBengali) "🌧️ ঝুম বৃষ্টি" else "🌧️ Gentle Rainfall",
                        isActive = playRain,
                        volume = volumeRain,
                        onActiveChange = { playRain = it },
                        onVolumeChange = { volumeRain = it }
                    )

                    // Ocean sound
                    SoundControlRow(
                        title = if (isBengali) "🌊 সমুদ্রের ঢেউ" else "🌊 Ocean Waves",
                        isActive = playOcean,
                        volume = volumeOcean,
                        onActiveChange = { playOcean = it },
                        onVolumeChange = { volumeOcean = it }
                    )
                }
            }

            // Quick Log mindfulness minutes
            Text(
                text = if (isBengali) "আজকের সেশন সংরক্ষণ করুন:" else "Register Calm Minutes:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF455A64)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2, 5, 10, 20).forEach { mins ->
                    OutlinedButton(
                        onClick = {
                            val newLog = JSONObject().apply {
                                put("id", "mindful_${System.currentTimeMillis()}")
                                put("duration", mins)
                                put("timestamp", System.currentTimeMillis())
                            }
                            val updatedList = mindfulnessLogs.toMutableList()
                            updatedList.add(mins)
                            mindfulnessLogs = updatedList
                            saveMindfulnessLogs(context, updatedList)

                            // Trigger Achievement celebration sequence
                            showSuccessCelebration = true
                            coroutineScope.launch {
                                // Dynamic trophy scaling
                                celebrationScale = 1.2f
                                delay(300)
                                celebrationScale = 1.0f
                                delay(2200)
                                showSuccessCelebration = false
                            }

                            Toast.makeText(
                                context,
                                if (isBengali) "$mins মিনিটের সেশন সংরক্ষিত! 🎉" else "Calmed for $mins minutes recorded! 🎉",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        border = BorderStroke(1.dp, Color(0xFFB39DDB)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("log_mindfulness_${mins}_btn")
                    ) {
                        Text(
                            text = "$mins " + (if (isBengali) "মি" else "min"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF512DA8)
                        )
                    }
                }
            }

            // Beautiful achievement card or state
            AchievementBanner(
                isBengali = isBengali,
                totalMinutes = mindfulnessLogs.sum()
            )
        }
    }

    // Success achievement animations overlay
    if (showSuccessCelebration) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { /* Block taps */ }
                .semantics { contentDescription = "Calm Session Achievement Celebration Overlay Animated" },
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFFFFD54F)),
                modifier = Modifier
                    .width(310.dp)
                    .scale(celebrationScale)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("🏆", fontSize = 54.sp)
                    Text(
                        text = if (isBengali) "অপূর্ব প্রশান্তি অর্জন!" else "High Calmness Achieved!",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFFD84315),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isBengali) {
                            "আপনার মনের ভারসাম্য এখন অসাধারণ স্তরে উন্নীত হয়েছে। নিয়মিত সচেতন শ্বাসক্রিয়া আপনার আয়ু ও মস্তিষ্কের গতিশক্তি বৃদ্ধি করে।"
                        } else {
                            "Deeper oxygen concentrations allow peaceful restoration. Mindfulness milestones unlocked successfully!"
                        },
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    LinearProgressIndicator(
                        color = Color(0xFFFFD54F),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                    Button(
                        onClick = { showSuccessCelebration = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isBengali) "ধন্যবাদ" else "Continue Calmly", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SoundControlRow(
    title: String,
    isActive: Boolean,
    volume: Float,
    onActiveChange: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1.3f)
        ) {
            Checkbox(
                checked = isActive,
                onCheckedChange = { onActiveChange(it) },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF673AB7))
            )
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
        }

        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            enabled = isActive,
            modifier = Modifier
                .weight(1.2f)
                .height(24.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF673AB7),
                activeTrackColor = Color(0xFFD1C4E9)
            )
        )
    }
}

@Composable
fun AchievementBanner(
    isBengali: Boolean,
    totalMinutes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEDE7F6), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFD1C4E9), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("🎯", fontSize = 24.sp)
        Column {
            Text(
                text = if (isBengali) "মোট প্রশান্তি কাল" else "Total Calm Mindfulness",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF512DA8)
            )
            Text(
                text = if (isBengali) {
                    "$totalMinutes মিনিট শান্ত তরঙ্গে নিমজ্জিত হয়েছেন"
                } else {
                    "Logged $totalMinutes calming oxygenation minutes"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF311B92)
            )
        }
    }
}

private fun loadMindfulnessLogs(context: Context): List<Int> {
    val list = mutableListOf<Int>()
    try {
        val sharedPrefs = context.getSharedPreferences("niljori_mindfulness_logs", Context.MODE_PRIVATE)
        val jsonStr = sharedPrefs.getString("calm_mins_arr", null) ?: return list
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            list.add(array.getInt(i))
        }
    } catch (_: Exception) {}
    return list
}

private fun saveMindfulnessLogs(context: Context, list: List<Int>) {
    try {
        val array = JSONArray()
        list.forEach { array.put(it) }
        val sharedPrefs = context.getSharedPreferences("niljori_mindfulness_logs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("calm_mins_arr", array.toString()).apply()
    } catch (_: Exception) {}
}
