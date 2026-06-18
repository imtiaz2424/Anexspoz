package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseLogEntity
import com.example.data.model.FoodLogEntity
import com.example.data.model.MoodLogEntity
import com.example.viewmodel.DietPlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodHealthReportExporter(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val moodLogs by viewModel.currentMoodLogs.collectAsState()
    val foodLogs by viewModel.allFoodLogs.collectAsState()
    val exerciseLogs by viewModel.allExerciseLogs.collectAsState()

    var selectedRangeDays by remember { mutableStateOf(7) } // 7 or 30 days
    var isReportVisible by remember { mutableStateOf(false) }
    var generatedReportText by remember { mutableStateOf("") }

    // Re-generate report when state data changes
    LaunchedEffect(moodLogs, foodLogs, exerciseLogs, selectedRangeDays, isBengali) {
        generatedReportText = generateTextReport(
            days = selectedRangeDays,
            moodLogs = moodLogs,
            foodLogs = foodLogs,
            exerciseLogs = exerciseLogs,
            isBengali = isBengali
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0F2F1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("mood_health_report_exporter_card")
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
                        Text("📄", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "অগ্রগতি প্রতিবেদন এক্সপোর্ট" else "Mood & Health Report Exporter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF004D40)
                        )
                        Text(
                            text = if (isBengali) "আপনার খাবার, ব্যায়াম এবং মনের অবস্থার সামারি এক্সপোর্ট করুন" else "Compile your comprehensive mood & habit diagnostics to paper/diary",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Time Selector Buttons (7 days vs 30 days)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(7, 30).forEach { range ->
                    val isSelected = selectedRangeDays == range
                    Button(
                        onClick = { selectedRangeDays = range },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF00796B) else Color(0xFFE0F2F1),
                            contentColor = if (isSelected) Color.White else Color(0xFF004D40)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("report_range_${range}_btn")
                    ) {
                        Text(
                            text = if (isBengali) "বিগত $range দিন" else "Last $range Days",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Quick Generate Trigger button
            Button(
                onClick = { isReportVisible = !isReportVisible },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D40)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generate_report_toggle_btn")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚙️", fontSize = 16.sp)
                    Text(
                        text = if (isReportVisible) {
                            if (isBengali) "প্রতিবেদন লুকান" else "Hide Live Report"
                        } else {
                            if (isBengali) "অগ্রগতি প্রতিবেদন তৈরি করুন" else "Compile Progress Report"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            // Collapsible Monospace Live Report Canvas Preview
            AnimatedVisibility(
                visible = isReportVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isBengali) "রিপোর্ট প্রিভিউ:" else "Compiled Report Preview:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    // Text Box Display Screen
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .background(Color(0xFFF5FBFB), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFB2DFDB).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = generatedReportText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                lineHeight = 15.sp,
                                modifier = Modifier.testTag("report_preview_text")
                            )
                        }
                    }

                    // Export / Share Actions Panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Copy to Clipboard
                        OutlinedButton(
                            onClick = {
                                copyToClipboard(context, generatedReportText)
                                val msg = if (isBengali) "রিপোর্ট ক্লিপবোর্ডে কপি করা হয়েছে!" else "Report copied to clipboard!"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            border = BorderStroke(1.dp, Color(0xFF00796B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("copy_report_btn")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📋", fontSize = 14.sp)
                                Text(
                                    text = if (isBengali) "কপি করুন" else "Copy Text",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00796B)
                                )
                            }
                        }

                        // 2. Android Native Share Sheet Intent
                        Button(
                            onClick = {
                                shareTextReport(context, generatedReportText, selectedRangeDays, isBengali)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("share_report_btn")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📤", fontSize = 14.sp)
                                Text(
                                    text = if (isBengali) "শেয়ার করুন" else "Share Summary",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Symmetry Personal Health Report", text)
    clipboard.setPrimaryClip(clip)
}

private fun shareTextReport(context: Context, text: String, days: Int, isBengali: Boolean) {
    val title = if (isBengali) "বিগত $days দিনের স্বাস্থ্য ও মনোভাব প্রতিবেদন" else "Personal Progress Report (Last $days Days)"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, if (isBengali) "প্রতিবেদনটি শেয়ার করুন" else "Export Report"))
}

private fun generateTextReport(
    days: Int,
    moodLogs: List<MoodLogEntity>,
    foodLogs: List<FoodLogEntity>,
    exerciseLogs: List<ExerciseLogEntity>,
    isBengali: Boolean
): String {
    val sdfYmd = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
    val sdfHuman = SimpleDateFormat("EEEE, MMMM dd, yyyy", if (isBengali) Locale("bn", "BD") else Locale.ENGLISH)
    val dateList = mutableListOf<Date>()

    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -(days - 1))
    for (i in 0 until days) {
        dateList.add(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }

    var totalMoodScore = 0f
    var moodLoggedDaysCount = 0
    var totalCaloriesIn = 0
    var totalCaloriesOut = 0
    var processedDaysLoggedCount = 0

    val dailyBreakdowns = StringBuilder()

    dateList.forEachIndexed { idx, date ->
        val dateStr = sdfYmd.format(date)
        val dayLabel = SimpleDateFormat("EEE", if (isBengali) Locale("bn", "BD") else Locale.ENGLISH).format(date)

        val moodsOnDay = moodLogs.filter { it.date == dateStr }
        val foodsOnDay = foodLogs.filter { it.date == dateStr }
        val exercisesOnDay = exerciseLogs.filter { it.date == dateStr }

        val hasLogs = moodsOnDay.isNotEmpty() || foodsOnDay.isNotEmpty() || exercisesOnDay.isNotEmpty()
        if (hasLogs) {
            processedDaysLoggedCount++
        }

        val latestMoodLog = moodsOnDay.lastOrNull()
        val moodStr = latestMoodLog?.mood ?: ""
        val moodNote = latestMoodLog?.note ?: ""

        val moodScore = when (moodStr.lowercase(Locale.ROOT)) {
            "happy" -> 5f
            "energized", "calm" -> 4f
            "neutral" -> 3f
            "stressed", "tired" -> 2f
            "sad", "angry" -> 1f
            else -> null
        }

        if (moodScore != null) {
            totalMoodScore += moodScore
            moodLoggedDaysCount++
        }

        val calIn = foodsOnDay.sumOf { it.calories }
        val calOut = exercisesOnDay.sumOf { it.caloriesBurned }

        totalCaloriesIn += calIn
        totalCaloriesOut += calOut

        if (hasLogs) {
            val formattedDate = SimpleDateFormat("MMM dd", if (isBengali) Locale("bn", "BD") else Locale.ENGLISH).format(date)
            dailyBreakdowns.append(
                if (isBengali) {
                    "📌 $dayLabel ($formattedDate):\n" +
                            "  • মনোভাব: ${if (moodStr.isNotBlank()) moodStr else "অনুল্লিখিত"}\n" +
                            "  • খাবার গ্রহণ: $calIn কি.ক্যালরি\n" +
                            "  • ব্যায়ামের মাধ্যমে হ্রাস: $calOut কি.ক্যালরি\n" +
                            (if (moodNote.isNotBlank()) "  • মেমো: \"$moodNote\"\n" else "")
                } else {
                    "📌 $dayLabel ($formattedDate):\n" +
                            "  • Mood: ${if (moodStr.isNotBlank()) moodStr else "Skipped"}\n" +
                            "  • Calories In: $calIn kcal\n" +
                            "  • Workout Out: $calOut kcal\n" +
                            (if (moodNote.isNotBlank()) "  • Memo: \"$moodNote\"\n" else "")
                }
            )
            dailyBreakdowns.append("\n")
        }
    }

    val avgMood = if (moodLoggedDaysCount > 0) String.format(Locale.ROOT, "%.1f", totalMoodScore / moodLoggedDaysCount) else "N/A"
    val netEnergy = totalCaloriesIn - totalCaloriesOut

    val titleText = if (isBengali) {
        "======================================\n" +
                "🧘 সুষম অগ্রগতি ও মানসিক ট্র্যাকার রিপোর্ট 🧘\n" +
                "উৎপাদন সময়: ${sdfHuman.format(Date())}\n" +
                "======================================\n\n"
    } else {
        "======================================\n" +
                "🧘 INTEGRATED PROGRESS & WELLNESS DIARY 🧘\n" +
                "Generated On: ${sdfHuman.format(Date())}\n" +
                "======================================\n\n"
    }

    val statsSection = if (isBengali) {
        "📊 বিগত $days দিনের সামগ্রিক পরিসংখ্যান:\n" +
                "--------------------------------------\n" +
                "• গড় মেজাজ রেটিং: $avgMood / ৫.০\n" +
                "• মোট খাবার ক্যালরি গ্রহণ: $totalCaloriesIn কি.ক্যালরি\n" +
                "• মোট ব্যায়াম ক্যালরি বার্ন: $totalCaloriesOut কি.ক্যালরি\n" +
                "• নেট শক্তি অবশিষ্টাংশ: $netEnergy কি.ক্যালরি\n" +
                "• সচল লগিং দিন সংখ্যা: $processedDaysLoggedCount দিন\n\n"
    } else {
        "📊 SUMMARY METRICS (Last $days Days):\n" +
                "--------------------------------------\n" +
                "• Average Emotional rating: $avgMood / 5.0\n" +
                "• Total Food Calories consumed: $totalCaloriesIn kcal\n" +
                "• Total Fitness workout burn: $totalCaloriesOut kcal\n" +
                "• Net Energy balance: $netEnergy kcal\n" +
                "• Multi-Metric logs inputted: $processedDaysLoggedCount days\n\n"
    }

    val breakdownHeader = if (isBengali) {
        "📅 প্রতিদিনের ট্র্যাকিং বিশ্লেষণ:\n" +
                "--------------------------------------\n"
    } else {
        "📅 DAY-BY-DAY CHRONOLOGY:\n" +
                "--------------------------------------\n"
    }

    val insightsSection = if (isBengali) {
        val insight = when {
            totalCaloriesIn > 0 && totalCaloriesOut > 0 && totalMoodScore / (moodLoggedDaysCount.coerceAtLeast(1)) >= 4f -> {
                "🌟 অসামান্য অগ্রগতি! ডায়েটিং এর সাথে নিয়মিত শরীরচর্চা আপনার মেজাজ উন্নত রাখতে অত্যন্ত কার্যকর হিসেবে প্রমাণিত।"
            }
            totalCaloriesIn == 0 && totalCaloriesOut == 0 -> {
                "📝 কায়িক পরিশ্রম ও খাদ্যতালিকা নিয়মিত আপডেট রাখুন যাতে পরবর্তী লুপের সঠিক ডায়াগনস্টিক রিপোর্ট প্রস্তুত করা সম্ভব হয়।"
            }
            else -> {
                "⚖️ সুষম ডায়েট এবং সক্রিয় জীবন দিনলিপির ভারসাম্য বজায় রাখতে পরম কার্যকরী। পুষ্টিকর স্বাস্থ্য পান।"
            }
        }
        "💡 সাপ্তাহিক অগ্রগতি পরামর্শ:\n" +
                "--------------------------------------\n" +
                "$insight\n"
    } else {
        val insight = when {
            totalCaloriesIn > 0 && totalCaloriesOut > 0 && totalMoodScore / (moodLoggedDaysCount.coerceAtLeast(1)) >= 4f -> {
                "🌟 Peak wellness correlation! Coupling nutritious habits with structural fitness yields ideal psychological and emotional outcomes."
            }
            totalCaloriesIn == 0 && totalCaloriesOut == 0 -> {
                "📝 Keep logging items! Consistent records construct robust, highly tailored diagnostics of your routine over time."
            }
            else -> {
                "⚖️ Maintain a healthy diet and dynamic routines to foster ideal metabolic and mental stability."
            }
        }
        "💡 CUSTOM WELLNESS FORECAST:\n" +
                "--------------------------------------\n" +
                "$insight\n"
    }

    val footer = if (isBengali) {
        "\n--------------------------------------\n" +
                "🌱 ANEXSOPZ ডিজিট্যাল ডায়েরি দ্বারা তৈরি।"
    } else {
        "\n--------------------------------------\n" +
                "🌱 Generated with ANEXSOPZ Live Companion."
    }

    return titleText + statsSection + breakdownHeader + (if (dailyBreakdowns.isNotEmpty()) dailyBreakdowns.toString() else (if (isBengali) "কোনও রেকর্ডকৃত ডায়েরি পাওয়া যায়নি।\n\n" else "No valid entries tracked in this timeframe.\n\n")) + insightsSection + footer
}
