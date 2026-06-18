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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import com.example.viewmodel.DietPlannerViewModel
import com.example.data.model.MoodLogEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodTrendChart(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val moodLogs by viewModel.currentMoodLogs.collectAsState()
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
    val weeklyData = remember(moodLogs, isBengali) {
        dateList.map { date ->
            val dateStr = ymdFormat.format(date)
            val label = if (isBengali) dayFormatBn.format(date) else dayFormatEn.format(date)

            // Find logs for this specific date
            val moodsOnDay = moodLogs.filter { it.date == dateStr }
            val latestMoodLog = moodsOnDay.lastOrNull()
            val moodString = latestMoodLog?.mood ?: ""
            val moodNote = latestMoodLog?.note ?: ""

            // Mood score conversion (1 to 5 scale)
            val moodScore = when (moodString.lowercase(Locale.ROOT)) {
                "happy" -> 5f
                "energized", "calm" -> 4f
                "neutral" -> 3f
                "stressed", "tired" -> 2f
                "sad", "angry" -> 1f
                else -> 3f // Default fallback for unlogged days
            }

            MoodTrendItem(
                dateStr = dateStr,
                label = label,
                moodScore = moodScore,
                moodString = moodString.ifBlank { "Neutral" },
                moodNote = moodNote,
                isLogged = moodString.isNotBlank()
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
            .testTag("mood_trend_chart_card")
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
                        Text("📉", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "মনোভাবের সাপ্তাহিক গতিধারা" else "Weekly Mood Velocity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF004D40)
                        )
                        Text(
                            text = if (isBengali) "রুম ডাটাবেজ থেকে সরাসরি আপনার আবেগীয় গ্রাফ বিশ্লেষণ" else "Live weekly trace of emotional scores synced with Room",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0F2F1), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBengali) "সরাসরি" else "Live Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFF00796B)
                    )
                }
            }

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(color = Color(0xFF00796B), label = if (isBengali) "মনোভাবের রেখাচিত্র" else "Mood Trend Line")
                LegendIndicator(color = Color(0xFF80CBC4), label = if (isBengali) "বাস্তব রেকর্ডকৃত নোডস" else "Logged Trace Points")
            }

            // Canvas Line Chart Drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFF5FBFB), RoundedCornerShape(18.dp))
                    .padding(8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(weeklyData) {
                            detectTapGestures { offset ->
                                val xSegmentWidth = (size.width - 120.dp.toPx()) / 6f
                                val leftPadding = 60.dp.toPx()
                                val clickedIndex = ((offset.x - leftPadding + xSegmentWidth / 2f) / xSegmentWidth)
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

                    // Draw Horizontal dashed reference grid lines
                    for (i in 0..4) {
                        val y = topPadding + (i * (graphHeight / 4f))
                        drawLine(
                            color = Color(0xFFB2DFDB).copy(alpha = 0.4f),
                            start = Offset(leftPadding, y),
                            end = Offset(leftPadding + graphWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    // Draw Y-Axis Emoji Markers
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

                    // Map all daily data points to graph coordinates
                    val xSegmentWidth = graphWidth / 6f
                    val points = weeklyData.mapIndexed { idx, item ->
                        val x = leftPadding + (idx * xSegmentWidth)
                        val moodY = topPadding + (5f - item.moodScore) / 4f * graphHeight
                        Offset(x, moodY)
                    }

                    // Vertical alignment cursor guide line for the selected index
                    val selectedX = leftPadding + selectedDayIndex * xSegmentWidth
                    drawLine(
                        color = Color(0xFF00796B).copy(alpha = 0.5f),
                        start = Offset(selectedX, topPadding),
                        end = Offset(selectedX, topPadding + graphHeight),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )

                    // Draw the core trend fill area gradient brush
                    val areaPath = Path().apply {
                        moveTo(leftPadding, topPadding + graphHeight)
                        points.forEach { pt ->
                            val securedY = if (pt.y.isNaN() || pt.y.isInfinite()) topPadding + graphHeight else pt.y
                            lineTo(pt.x, securedY)
                        }
                        lineTo(leftPadding + graphWidth, topPadding + graphHeight)
                        close()
                    }
                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF80CBC4).copy(alpha = 0.35f), Color(0xFFE0F2F1).copy(alpha = 0.01f)),
                            startY = topPadding,
                            endY = topPadding + graphHeight
                        )
                    )

                    // Draw the primary trend line tracing spline
                    for (i in 0 until points.size - 1) {
                        val startPt = points[i]
                        val endPt = points[i + 1]
                        drawLine(
                            color = Color(0xFF00796B),
                            start = startPt,
                            end = endPt,
                            strokeWidth = 3.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Overlay and highlight point nodes
                    points.forEachIndexed { index, pt ->
                        val item = weeklyData[index]

                        // Glow indicator for selection
                        drawCircle(
                            color = if (index == selectedDayIndex) Color(0xFF00796B).copy(alpha = 0.25f) else Color.Transparent,
                            radius = 12.dp.toPx(),
                            center = pt
                        )

                        // Outline of trace node
                        drawCircle(
                            color = if (item.isLogged) Color(0xFF004D40) else Color(0xFFB2DFDB),
                            radius = 6.dp.toPx(),
                            center = pt
                        )

                        // Center white core
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = pt
                        )

                        // Plot X-Axis labels (Day of the week)
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

            // Expanded interactive logger details
            selectedItem?.let { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF8F6)),
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
                                text = "${if (isBengali) "বিশ্লেষণ দিন:" else "Trend Day:"} ${item.label} (${item.dateStr})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40)
                            )

                            val moodLabel = when (item.moodString.lowercase(Locale.ROOT)) {
                                "happy" -> if (isBengali) "😊 অত্যন্ত আনন্দিত" else "😊 Happy"
                                "energized" -> if (isBengali) "⚡ অফুরন্ত শক্তি" else "⚡ Energized"
                                "calm" -> if (isBengali) "🧘 শান্ত ও সমাহিত" else "🧘 Calm"
                                "stressed" -> if (isBengali) "😰 মানসিক চাপ" else "😰 Stressed"
                                "sad" -> if (isBengali) "😢 বিষণ্ণ বা ক্লান্ত" else "😢 Sad"
                                else -> if (isBengali) "😐 সাধারণ দিন" else "😐 Neutral / Unlogged"
                            }
                            Text(
                                text = moodLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00796B)
                            )
                        }

                        // Clinical wellness analysis description
                        val trendInsight = remember(item, isBengali) {
                            if (item.isLogged) {
                                when (item.moodScore) {
                                    5f -> if (isBengali) "🌟 মনের আনন্দ আপনার জীবনীশক্তির ভারসাম্য সর্বোচ্চ স্তরে রেখেছে। চমৎকার কাটুক আজকের দিন!" else "🌟 Peak spirits of ultimate joy and gratitude! Harness this momentum today."
                                    4f -> if (isBengali) "🌱 আপনার মানসিক পরিবেশ অত্যন্ত শান্ত অথবা সক্রিয়। স্বাস্থ্যকর খাদ্য বজায় রাখুন।" else "🌱 Peaceful or energized state of trace. A perfect time for minor routines."
                                    3f -> if (isBengali) "⚖️ একটি সমাহিত ও স্বাভাবিক বিষয়ের উপস্থিতি। সামান্য হাঁটাহাঁটি বা জলযোগ দিনটি সুন্দর করবে।" else "⚖️ Balanced and baseline state. Minor outdoor walk or hydration will elevate this."
                                    else -> if (isBengali) "⚠️ এই দিনটিতে কিছুটা অবসাদ দেখা যাচ্ছে। মানসিক সমর্থন এবং পুষ্টিকর খাবার আপনার বিষণ্নতা কাটবে।" else "⚠️ Trace of lower mood detected. Gentle breathing exercise and dynamic nutrition can support recovery."
                                }
                            } else {
                                if (isBengali) "💡 এই দিনে কোনও মনোভাব লগ করা হয়নি। নিচে কুইক প্যানেল ব্যবহার করে আজকের ডাটা আপডেট দিন।" else "💡 No specific feedback log for today yet. Use the quick logger below to trace."
                            }
                        }

                        Text(
                            text = trendInsight,
                            fontSize = 11.sp,
                            color = Color(0xFF004D40),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp
                        )

                        if (item.moodNote.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "✏️ Memo: \"${item.moodNote}\"",
                                    fontSize = 11.sp,
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

data class MoodTrendItem(
    val dateStr: String,
    val label: String,
    val moodScore: Float,
    val moodString: String,
    val moodNote: String,
    val isLogged: Boolean
)
