package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodLogEntity
import com.example.viewmodel.DietPlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyMoodJournal(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val moodLogs by viewModel.currentMoodLogs.collectAsState()
    val focusManager = LocalFocusManager.current

    // Emotion list definitions
    val emotions = remember {
        listOf(
            EmotionItem("Happy", "😊", if (isBengali) "আনন্দিত" else "Happy", Color(0xFFFFF9C4), Color(0xFFFBC02D)),
            EmotionItem("Energized", "⚡", if (isBengali) "প্রাণবন্ত" else "Energized", Color(0xFFE8F5E9), Color(0xFF4CAF50)),
            EmotionItem("Calm", "🧘", if (isBengali) "শান্ত" else "Calm", Color(0xFFE0F2F1), Color(0xFF009688)),
            EmotionItem("Neutral", "😐", if (isBengali) "স্বাভাবিক" else "Neutral", Color(0xFFECEFF1), Color(0xFF78909C)),
            EmotionItem("Stressed", "😰", if (isBengali) "দুশ্চিন্তা" else "Stressed", Color(0xFFFFE0B2), Color(0xFFF57C00)),
            EmotionItem("Sad", "😢", if (isBengali) "বিষণ্ণ" else "Sad", Color(0xFFFFCDD2), Color(0xFFE53935))
        )
    }

    var selectedMood by remember { mutableStateOf("Happy") }
    var moodNoteInput by remember { mutableStateOf("") }
    var foodImpactInput by remember { mutableStateOf("") }
    var activityInput by remember { mutableStateOf("") }

    // Today's Date format
    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date()) }
    val todayLog = moodLogs.find { it.date == todayDateStr }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_mood_journal_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
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
                            .background(Color(0xFFE0F2F1), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📓", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "দৈনিক আবেগ ডায়েরি" else "Daily Mood Journal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF004D40)
                        )
                        Text(
                            text = if (isBengali) "আপনার মন এবং খাদ্যতালিকাগত সংযোগ খুঁজুন" else "Trace mind state and physical lifestyle correlations",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                if (todayLog != null) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE0F2F1), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Logged",
                                tint = Color(0xFF00796B),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isBengali) "আজকের সম্পন্ন" else "Today Logged",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color(0xFF00796B)
                            )
                        }
                    }
                }
            }

            Divider(color = Color(0xFFECEFF1), thickness = 1.dp)

            // Step 1: Mood Choice Segment
            Text(
                text = if (isBengali) "১. আজকে আপনার মনের অবস্থা কেমন?" else "1. How are you feeling today?",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF004D40)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                emotions.forEach { emotion ->
                    val isSelected = selectedMood.lowercase() == emotion.key.lowercase()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) emotion.accentColor.copy(alpha = 0.25f) else Color(0xFFF5FBFB))
                            .clickable {
                                selectedMood = emotion.key
                            }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) emotion.accentColor else Color(0xFFE0F2F1),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = emotion.emoji, fontSize = 24.sp)
                            Text(
                                text = emotion.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF004D40) else Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Step 2 & 3: Journal Text Info Input Fields
            Text(
                text = if (isBengali) "২. আপনার ডায়েরি নোট এবং সংযোগ লিখুন (ঐচ্ছিক)" else "2. Add diary notes & habits context (Optional)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF004D40)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Journal Note
                OutlinedTextField(
                    value = moodNoteInput,
                    onValueChange = { moodNoteInput = it },
                    label = { Text(if (isBengali) "আজকে আপনি কেমন অনুভব করছেন?" else "Describe today's thoughts...") },
                    placeholder = { Text(if (isBengali) "যেমন: সকালের সেশনটি দারুণ ছিল এবং কাজগুলো সম্পন্ন করতে পেরেছি।" else "e.g. Felt very positive after the morning workout session.") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00796B),
                        unfocusedBorderColor = Color(0xFFB2DFDB)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mood_note_text_field")
                )

                // Associated Diet and Exercise Logs Correlation Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = foodImpactInput,
                        onValueChange = { foodImpactInput = it },
                        label = { Text(if (isBengali) "খাদ্য সংযোগ" else "Diet Link") },
                        placeholder = { Text(if (isBengali) "যেমন: সালাদ" else "e.g. Fresh Salad") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00796B),
                            unfocusedBorderColor = Color(0xFFB2DFDB)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mood_food_field")
                    )

                    OutlinedTextField(
                        value = activityInput,
                        onValueChange = { activityInput = it },
                        label = { Text(if (isBengali) "সক্রিয় কাজ" else "Activity Link") },
                        placeholder = { Text(if (isBengali) "যেমন: যোগব্যায়াম" else "e.g. Yoga Class") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00796B),
                            unfocusedBorderColor = Color(0xFFB2DFDB)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mood_activity_field")
                    )
                }
            }

            // Save Action Indicator
            Button(
                onClick = {
                    viewModel.saveMoodLog(
                        mood = selectedMood,
                        note = moodNoteInput,
                        food = foodImpactInput,
                        activity = activityInput
                    )
                    moodNoteInput = ""
                    foodImpactInput = ""
                    activityInput = ""
                    focusManager.clearFocus()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_mood_diary_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💾", fontSize = 16.sp)
                    Text(
                        text = if (isBengali) "স্মৃতি ডায়েরি সংরক্ষণ করুন" else "Save Daily Mood Journal Log",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            // Collapse historical log listings
            if (moodLogs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📚", fontSize = 14.sp)
                    Text(
                        text = if (isBengali) "বিগত আবেগ রেকর্ড হিস্টোরি" else "Past Mood Logs History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF004D40)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    moodLogs.take(5).forEach { log ->
                        val matchingEmotion = emotions.find { it.key.lowercase() == log.mood.lowercase() }
                        val rowEmoji = matchingEmotion?.emoji ?: "😐"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9FBFB), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0xFFE0F2F1), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(rowEmoji, fontSize = 20.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isBengali) (matchingEmotion?.label ?: log.mood) else log.mood,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF004D40)
                                    )
                                    Text(
                                        text = log.date,
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }

                                if (log.note.isNotBlank()) {
                                    Text(
                                        text = log.note,
                                        fontSize = 11.sp,
                                        color = Color.DarkGray,
                                        style = androidx.compose.ui.text.TextStyle(fontStyle = FontStyle.Italic),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                if (log.food.isNotBlank() || log.activity.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (log.food.isNotBlank()) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                 Text("🍲 ${log.food}", fontSize = 9.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        if (log.activity.isNotBlank()) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFE3F2FD), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("🏃 ${log.activity}", fontSize = 9.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteMoodLog(log.id) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_mood_${log.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete log",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class EmotionItem(
    val key: String,
    val emoji: String,
    val label: String,
    val containerColor: Color,
    val accentColor: Color
)
