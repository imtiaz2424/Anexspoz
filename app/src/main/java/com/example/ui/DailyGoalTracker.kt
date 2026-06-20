package com.example.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CustomGoal(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val icon: String,
    val categoryEn: String,
    val categoryBn: String,
    val color: Color
)

@Composable
fun DailyGoalTracker(
    selectedDate: String,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPrefs = remember(selectedDate) {
        context.getSharedPreferences("niljori_goals_$selectedDate", Context.MODE_PRIVATE)
    }

    val defaultGoals = remember {
        listOf(
            CustomGoal("water", "Drink 8 glasses of water (2L)", "৮ গ্লাস পানি পান করুন (২ লিটার)", "💧", "Hydration", "হাইড্রেশন", Color(0xFF03A9F4)),
            CustomGoal("workout", "30-min Active Workout", "৩০ মিনিট কায়িক পরিশ্রম বা ব্যায়াম", "🏋️", "Fitness", "ফিটনেস", Color(0xFFFF9800)),
            CustomGoal("vegetables", "Eat green vegetables/fruits", "সবুজ শাকসবজি বা ফলমূল আহার", "🥗", "Diet", "খাদ্য তালিকা", Color(0xFF4CAF50)),
            CustomGoal("sugar", "Limit soft drinks & extra sugar", "মিষ্টি পানীয় ও অতিরিক্ত চিনি বর্জন", "🍎", "Habit", "অভ্যাসের পরিবর্তন", Color(0xFFE91E63)),
            CustomGoal("mood", "Log Daily Reflection & Mood", "দৈনিক মেজাজ ও মনের যত্ন ডায়েরি লিখুন", "🧘", "Mental Wellness", "মানসিক স্বাস্থ্য", Color(0xFF9C27B0)),
            CustomGoal("recipe", "Explore high-protein healthy recipe", "উচ্চ-প্রোটিন পুষ্টিকর খাদ্য রেসিপি পড়া", "📖", "Learning", "শিক্ষা", Color(0xFF009688)),
            CustomGoal("sleep", "Achieve 7-8 Hours Restful Sleep", "৭-৮ ঘন্টা প্রশান্তিদায়ক ঘুম নিশ্চিত করুন", "💤", "Rest", "বিশ্রাম", Color(0xFF3F51B5))
        )
    }

    // Dynamic state containing maps of toggled status for the specific date
    var checkedStateMap by remember(selectedDate) {
        mutableStateOf(
            defaultGoals.associate { goal ->
                goal.id to sharedPrefs.getBoolean(goal.id, false)
            }
        )
    }

    val completedCount = checkedStateMap.values.count { it }
    val totalCount = defaultGoals.size
    val progressRate = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_goal_tracker_panel")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            .size(38.dp)
                            .background(Color(0xFFFFECB3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "দৈনিক লক্ষ্য ট্র্যাকার" else "Daily Wellness Goal Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isBengali) "তারিখ: $selectedDate" else "Date: $selectedDate",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Completion status pills
                Box(
                    modifier = Modifier
                        .background(
                            color = if (progressRate >= 1.0f) Color(0xFFE8F5E9) else Color(0xFFECEFF1),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBengali) "$completedCount / $totalCount সম্পন্ন" else "$completedCount / $totalCount Done",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (progressRate >= 1.0f) Color(0xFF2E7D32) else Color(0xFF455A64)
                    )
                }
            }

            // Real-time dynamic gauge progress indicator
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = progressRate,
                    trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                    color = if (progressRate >= 0.7f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Text(
                    text = if (isBengali) {
                        "মোট অগ্রগতি: ${(progressRate * 100).toInt()}%"
                    } else {
                        "Completion Rate: ${(progressRate * 100).toInt()}%"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            // Motivational Banner
            AnimatedContent(
                targetState = progressRate,
                label = "MotivationText"
            ) { rate ->
                val bannerColor: Color
                val motivationText: String

                when {
                    rate >= 1.0f -> {
                        bannerColor = Color(0xFFE8F5E9)
                        motivationText = if (isBengali) {
                            "🎉 অসাধারণ! আপনি আজকের সমস্ত স্বাস্থ্যের লক্ষ্য অর্জন করেছেন! আপনি একজন আদর্শ স্বাস্থ্য চ্যাম্পিয়ন।"
                        } else {
                            "🎉 Spectacular! You have completed all health targets for today! You are building the perfect lifestyle."
                        }
                    }
                    rate >= 0.5f -> {
                        bannerColor = Color(0xFFE0F7FA)
                        motivationText = if (isBengali) {
                            "🌟 অর্ধেক পথ সমাপ্ত! বাকি সহজ লক্ষ্যগুলো চমৎকারভাবে পূরণ করে দিনের সম্পূর্ণ অর্জন নিশ্চিত করুন।"
                        } else {
                            "🌟 Over halfway there! Keep checking off targets to ensure your wellness routine is spotless today."
                        }
                    }
                    rate > 0f -> {
                        bannerColor = Color(0xFFFFF3E0)
                        motivationText = if (isBengali) {
                            "💪 সূচনা চমৎকার! ছোট ছোট পরিবর্তন স্বাস্থ্য জীবনে অনন্য ভূমিকা রাখে। এগিয়ে যান!"
                        } else {
                            "💪 Solid start! Checking off small targets accumulates into major transformations. Keep up the rhythm!"
                        }
                    }
                    else -> {
                        bannerColor = Color(0xFFF5F5F5)
                        motivationText = if (isBengali) {
                            "💤 নতুন দিনে স্বাগতম। আসুন আজকের সুস্বাস্থ্য ও সুন্দর মানসিকতার লক্ষ্যগুলো পূরণ করতে শুরু করি!"
                        } else {
                            "💤 Welcome to a fresh day. Let's start taking action and check off healthy targets together!"
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bannerColor, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = motivationText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                        lineHeight = 15.sp
                    )
                }
            }

            // Interactive lists
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                defaultGoals.forEach { goal ->
                    val isChecked = checkedStateMap[goal.id] ?: false

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isChecked) goal.color.copy(alpha = 0.05f) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { value ->
                                val updatedMap = checkedStateMap.toMutableMap()
                                updatedMap[goal.id] = value
                                checkedStateMap = updatedMap

                                // Persist state immediately
                                sharedPrefs.edit().putBoolean(goal.id, value).apply()
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = goal.color,
                                uncheckedColor = Color.Gray
                            ),
                            modifier = Modifier
                                .scale(0.85f)
                                .testTag("goal_checkbox_${goal.id}")
                        )

                        // Icon Label
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(goal.color.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(goal.icon, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isBengali) goal.titleBn else goal.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isChecked) Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isBengali) goal.categoryBn else goal.categoryEn,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = goal.color
                            )
                        }
                    }
                }
            }
        }
    }
}
