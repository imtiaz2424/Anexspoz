package com.example.ui

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import com.example.viewmodel.DietPlannerViewModel
import com.example.data.model.MoodLogEntity
import com.example.data.model.FoodLogEntity
import com.example.data.model.ExerciseLogEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodAnalysisDashboard(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val moodLogs by viewModel.currentMoodLogs.collectAsState()
    val foodLogs by viewModel.allFoodLogs.collectAsState()
    val exerciseLogs by viewModel.allExerciseLogs.collectAsState()

    var selectedDayIndex by remember { mutableStateOf(6) } // Defaults to today (6)

    // Layout dates for past 7 days (index 0 is 6 days ago, index 6 is today)
    val dateList = remember {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        for (i in 0..6) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    val ymdFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT) }
    val dayFormatEn = remember { SimpleDateFormat("EEE", Locale.ENGLISH) }
    val dayFormatBn = remember { SimpleDateFormat("EEE", Locale("bn", "BD")) }

    // Map logs to exact past 7 days
    val weeklyData = remember(moodLogs, foodLogs, exerciseLogs, isBengali) {
        dateList.map { date ->
            val dateStr = ymdFormat.format(date)
            val label = if (isBengali) dayFormatBn.format(date) else dayFormatEn.format(date)

            // Find logs for this specific date
            val moodsOnDay = moodLogs.filter { it.date == dateStr }
            val foodsOnDay = foodLogs.filter { it.date == dateStr }
            val exercisesOnDay = exerciseLogs.filter { it.date == dateStr }

            // Calculate aggregate values or use latest
            val latestMoodLog = moodsOnDay.lastOrNull()
            val moodString = latestMoodLog?.mood ?: "Neutral"
            val moodNote = latestMoodLog?.note ?: ""

            // Mood score conversion (1 to 5 scale)
            val moodScore = when (moodString.lowercase(Locale.ROOT)) {
                "happy" -> 5f
                "energized", "calm" -> 4f
                "neutral" -> 3f
                "stressed", "tired" -> 2f
                "sad", "angry" -> 1f
                else -> 3f // Default
            }

            val hasMeal = foodsOnDay.isNotEmpty()
            val totalCalIn = foodsOnDay.sumOf { it.calories }.toFloat()

            val hasExercise = exercisesOnDay.isNotEmpty()
            val totalCalOut = exercisesOnDay.sumOf { it.caloriesBurned }.toFloat()

            MoodAnalysisItem(
                dateStr = dateStr,
                label = label,
                moodScore = moodScore,
                moodString = moodString,
                moodNote = moodNote,
                hasMeal = hasMeal,
                caloriesIn = totalCalIn,
                hasExercise = hasExercise,
                caloriesOut = totalCalOut
            )
        }
    }

    val selectedItem = weeklyData.getOrNull(selectedDayIndex)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("mood_analysis_dashboard_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header panel with applet/branding design style
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
                        Text("🧘", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "মনোভাব ও অভ্যাসের সমীকরণ" else "Mental & Habits Equation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4A148C)
                        )
                        Text(
                            text = if (isBengali) "খাবার ও ব্যায়ামের সাথে মানসিক সংযোগ বিশ্লেষণ" else "Analyze emotional states overlaid with meals & activities",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFF0F5), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBengali) "সক্রিয়" else "Active Analyzer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFFC2185B)
                    )
                }
            }

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(color = Color(0xFF9C27B0), label = if (isBengali) "মনোভাব সূচক (১-৫)" else "Mood Curve (1-5)")
                LegendIndicator(color = Color(0xFF4CAF50), label = if (isBengali) "খাবার সংকেত (🍲)" else "Meal Marker (🍲)")
                LegendIndicator(color = Color(0xFFFF9800), label = if (isBengali) "ব্যায়াম সংকেত (🏃)" else "Exercise Marker (🏃)")
            }

            // Modern Canvas-based Line Chart with Overlaid Meal/Activity markers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(18.dp))
                    .padding(8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(weeklyData) {
                            detectTapGestures { offset ->
                                val xSegmentWidth = (size.width - 120.dp.toPx()) / 6f
                                val leftPadding = 60.dp.toPx()
                                val clickedIndex = ((offset.x - leftPadding + xSegmentWidth/2f) / xSegmentWidth)
                                    .toInt()
                                    .coerceIn(0, 6)
                                selectedDayIndex = clickedIndex
                            }
                        }
                ) {
                    val leftPadding = 60.dp.toPx()
                    val rightPadding = 60.dp.toPx()
                    val topPadding = 25.dp.toPx()
                    val bottomPadding = 35.dp.toPx()
                    val graphWidth = size.width - leftPadding - rightPadding
                    val graphHeight = size.height - topPadding - bottomPadding

                    // Draw Horizontal Reference lines
                    for (i in 0..4) {
                        val y = topPadding + (i * (graphHeight / 4f))
                        drawLine(
                            color = Color(0xFFE0E0E0),
                            start = Offset(leftPadding, y),
                            end = Offset(leftPadding + graphWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                        )
                    }

                    // Y-Axis Mood emojis labels
                    val moodEmojis = listOf("😭", "😰", "😐", "🧘", "😊")
                    for (i in 0..4) {
                        val labelY = topPadding + graphHeight - (i * (graphHeight / 4f))
                        drawContext.canvas.nativeCanvas.drawText(
                            moodEmojis[i],
                            leftPadding - 32.dp.toPx(),
                            labelY + 5.dp.toPx(),
                            Paint().apply {
                                textSize = 15.sp.toPx()
                                textAlign = Paint.Align.LEFT
                            }
                        )
                    }

                    // Map all data points to vector coordinates
                    val xSegmentWidth = graphWidth / 6f
                    val points = weeklyData.mapIndexed { idx, item ->
                        val x = leftPadding + (idx * xSegmentWidth)
                        // Mood score Y coordinate (1f to 5f map)
                        val moodY = topPadding + (5f - item.moodScore) / 4f * graphHeight
                        Offset(x, moodY)
                    }

                    // Draw Selection Highlight Line
                    val selectedX = leftPadding + selectedDayIndex * xSegmentWidth
                    drawLine(
                        color = Color(0xFF8E24AA).copy(alpha = 0.4f),
                        start = Offset(selectedX, topPadding),
                        end = Offset(selectedX, topPadding + graphHeight),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Draw the primary Mood spline glow shadow gradient area
                    val glowAreaPath = Path().apply {
                        moveTo(leftPadding, topPadding + graphHeight)
                        points.forEach { pt ->
                            lineTo(pt.x, pt.ptY(topPadding + graphHeight))
                        }
                        lineTo(leftPadding + graphWidth, topPadding + graphHeight)
                        close()
                    }
                    drawPath(
                        path = glowAreaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFE040FB).copy(alpha = 0.25f), Color(0xFFFAF0FF).copy(alpha = 0.01f)),
                            startY = topPadding,
                            endY = topPadding + graphHeight
                        )
                    )

                    // Helper extension to keep code clean
                    // Draw Smooth Mood Spline Curves
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = Color(0xFF8E24AA),
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw Data Points, Meal/Activity overlay markers directly on nodes!
                    points.forEachIndexed { index, pt ->
                        val item = weeklyData[index]

                        // 1. Core node indicators
                        drawCircle(
                            color = if (index == selectedDayIndex) Color(0xFF8E24AA).copy(alpha = 0.35f) else Color.Transparent,
                            radius = 10.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = Color(0xFF8E24AA),
                            radius = 3.dp.toPx(),
                            center = pt
                        )

                        // 2. Meal overlaid marker (🍲 badge hovering above the node)
                        if (item.hasMeal) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "🍲",
                                pt.x - 6.dp.toPx(),
                                pt.y - 12.dp.toPx(),
                                Paint().apply {
                                    textSize = 10.sp.toPx()
                                }
                            )
                        }

                        // 3. Exercise overlaid marker (🏃 badge hovering below the node)
                        if (item.hasExercise) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "🏃",
                                pt.x - 6.dp.toPx(),
                                pt.y + 20.dp.toPx(),
                                Paint().apply {
                                    textSize = 10.sp.toPx()
                                }
                            )
                        }

                        // X-axis day name labels
                        drawContext.canvas.nativeCanvas.drawText(
                            item.label,
                            pt.x,
                            topPadding + graphHeight + 22.dp.toPx(),
                            Paint().apply {
                                textSize = 10.sp.toPx()
                                color = if (index == selectedDayIndex) android.graphics.Color.BLACK else android.graphics.Color.GRAY
                                isAntiAlias = true
                                textAlign = Paint.Align.CENTER
                                typeface = if (index == selectedDayIndex) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
                            }
                        )
                    }
                }
            }

            // Interactive Correlation Summary Info Box
            selectedItem?.let { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5).copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (isBengali) "তারিখ ও দিন:" else "Date & Day:"} ${item.label} (${item.dateStr})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A148C)
                            )

                            val moodLiteral = when (item.moodString.lowercase(Locale.ROOT)) {
                                "happy" -> if (isBengali) "😊 অত্যন্ত আনন্দিত" else "😊 Happy"
                                "energized" -> if (isBengali) "⚡ অফুরন্ত শক্তি" else "⚡ Energized"
                                "calm" -> if (isBengali) "🧘 শান্ত ও সমাহিত" else "🧘 Calm"
                                "stressed" -> if (isBengali) "😰 মানসিক চাপ" else "😰 Stressed"
                                "sad" -> if (isBengali) "😢 বিষণ্ণ বা ক্লান্ত" else "😢 Sad"
                                else -> if (isBengali) "😐 স্বাভাবিক" else "😐 Neutral"
                            }
                            Text(
                                text = moodLiteral,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF7B1FA2)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Meal statistics for date
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFE8F5E9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🍲", fontSize = 12.sp)
                                }
                                Column {
                                    Text(
                                        text = if (isBengali) "খাবার গ্রহণ" else "Meals Taken",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (item.hasMeal) "${item.caloriesIn.toInt()} kcal" else (if (isBengali) "নেই" else "None"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.hasMeal) Color(0xFF2E7D32) else Color.Gray
                                    )
                                }
                            }

                            // Exercise statistics for date
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFFFFF3E0), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏃", fontSize = 12.sp)
                                }
                                Column {
                                    Text(
                                        text = if (isBengali) "ব্যায়ামের মাত্রা" else "Workout Burn",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (item.hasExercise) "${item.caloriesOut.toInt()} kcal" else (if (isBengali) "নেই" else "None"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.hasExercise) Color(0xFFE65100) else Color.Gray
                                    )
                                }
                            }
                        }

                        // Habits / Mood Correlation Wisdom insight!
                        val insightText = remember(item, isBengali) {
                            when {
                                item.hasMeal && item.hasExercise && item.moodScore >= 4f -> {
                                    if (isBengali)
                                        "🌟 অসামান্য মিল! পুষ্টিকর খাবার গ্রহণ এবং শারীরিক ব্যায়াম আপনার মেজাজকে সর্বোচ্চ শিহরে উন্নীত করেছে।"
                                    else
                                        "🌟 Stellar correlation! Nutritious eating paired with physical exercise kept your mind at peak levels."
                                }
                                item.hasMeal && item.moodScore >= 4f -> {
                                    if (isBengali)
                                        "🍏 ভালো খাদ্য হজমে ইতিবাচক উদ্দীপনা জুগিয়েছে। খাবারের গুণাগুণ মেজাজ সুস্থ রাখে।"
                                    else
                                        "🍏 Healthy food choices kept your energy steady and built positive emotional loops today."
                                }
                                item.hasExercise && item.moodScore >= 4f -> {
                                    if (isBengali)
                                        "⚡ এন্ডোরফিন হরমোন নিঃসরণ! নিয়মতান্ত্রিক ব্যায়াম এবং শরীরচর্চা আপনার মানসিক ক্লান্তি মুক্ত করেছে।"
                                    else
                                        "⚡ Endorphin rush! Active moving built a healthy outlet and lifted your mood."
                                }
                                !item.hasMeal && !item.hasExercise && item.moodScore <= 2f -> {
                                    if (isBengali)
                                        "⚠️ সতর্ক বার্তা: আজকের খাদ্য লগ বা সচল ট্র্যাকিং শূন্য। একটু হাঁটাহাঁটি বা পুষ্টিকর খাবার আপনার বিষণ্ণতা দ্রুত কাটাতে পারে।"
                                    else
                                        "⚠️ Insight: Sedentary pattern detected alongside low spirit. Simple walking or a light meal can uplift you."
                                }
                                else -> {
                                    if (isBengali)
                                        "⚖️ মনোভাব বিশ্লেষণ স্থিতিশীল রয়েছে। পুষ্টিকর ডায়েট এবং সক্রিয় জীবন মানসিক ভারসাম্য বজায় রাখার সহজ চাবি।"
                                    else
                                        "⚖️ Balanced day. Keep logging meals and activities regularly to build predictive wellness insights."
                                }
                            }
                        }

                        Text(
                            text = insightText,
                            fontSize = 11.sp,
                            color = Color(0xFF4A148C),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (item.moodNote.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "✍️ Memo: \"${item.moodNote}\"",
                                    fontSize = 10.5.sp,
                                    color = Color.DarkGray,
                                    style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendIndicator(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 9.5.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper to secure graph heights
private fun Offset.ptY(fallback: Float): Float {
    return if (this.y.isNaN() || this.y.isInfinite()) fallback else this.y
}

data class MoodAnalysisItem(
    val dateStr: String,
    val label: String,
    val moodScore: Float,
    val moodString: String,
    val moodNote: String,
    val hasMeal: Boolean,
    val caloriesIn: Float,
    val hasExercise: Boolean,
    val caloriesOut: Float
)
