package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodLogEntity
import com.example.data.model.UserProfileEntity
import com.example.viewmodel.DietPlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NutritionalDailySummary(
    viewModel: DietPlannerViewModel,
    userProfile: UserProfileEntity,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val allFoodLogs by viewModel.allFoodLogs.collectAsState()

    // 1. Create a 7-day trailing date list
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
    val dayNumFormat = remember { SimpleDateFormat("d", Locale.ROOT) }

    // 2. Identify food logs for the currently selected date
    val currentDayLogs = remember(selectedDate, allFoodLogs) {
        allFoodLogs.filter { it.date == selectedDate }
    }

    // 3. Macronutrient Targets based on user profile goals
    val calorieTarget = userProfile.dailyCalorieTarget
    val proteinPct: Double
    val carbsPct: Double
    val fatPct: Double

    when (userProfile.goal.lowercase()) {
        "weight loss", "ওজন কমানো" -> {
            proteinPct = 0.35 // 35% Protein
            carbsPct = 0.35   // 35% Carbohydrates
            fatPct = 0.30     // 30% Fat
        }
        "weight gain", "ওজন বাড়ানো" -> {
            proteinPct = 0.25 // 25% Protein
            carbsPct = 0.50   // 50% Carbohydrates
            fatPct = 0.25     // 25% Fat
        }
        else -> { // Maintaining weight / Default
            proteinPct = 0.25 // 25% Protein
            carbsPct = 0.45   // 45% Carbohydrates
            fatPct = 0.30     // 30% Fat
        }
    }

    // Protein & Carbs feature 4 kcal/g, Fat features 9 kcal/g
    val targetProteinGrams = remember(calorieTarget, proteinPct) {
        ((calorieTarget * proteinPct) / 4.0).toInt().coerceAtLeast(1)
    }
    val targetCarbsGrams = remember(calorieTarget, carbsPct) {
        ((calorieTarget * carbsPct) / 4.0).toInt().coerceAtLeast(1)
    }
    val targetFatGrams = remember(calorieTarget, fatPct) {
        ((calorieTarget * fatPct) / 9.0).toInt().coerceAtLeast(1)
    }

    // Actual Consumed metrics
    val consumedCalories = remember(currentDayLogs) {
        currentDayLogs.sumOf { it.calories }
    }
    val consumedProtein = remember(currentDayLogs) {
        currentDayLogs.sumOf { it.protein }
    }
    val consumedCarbs = remember(currentDayLogs) {
        currentDayLogs.sumOf { it.carbs }
    }
    val consumedFat = remember(currentDayLogs) {
        currentDayLogs.sumOf { it.fat }
    }

    val totalMacrosGrams = (consumedProtein + consumedCarbs + consumedFat).coerceAtLeast(0.1)
    val proteinPercentOfWeight = (consumedProtein / totalMacrosGrams * 100).toInt()
    val carbsPercentOfWeight = (consumedCarbs / totalMacrosGrams * 100).toInt()
    val fatPercentOfWeight = (consumedFat / totalMacrosGrams * 100).toInt()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("nutritional_daily_summary_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with custom icon and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Nutrition Icon",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBengali) "পুষ্টির দৈনিক সারাংশ" else "Daily Nutritional Summary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = if (isBengali) "দৈনিক খাবার নির্বাচন ও ম্যাক্রো বিভাজন বিশ্লেষণ" else "Analyze calorie distribution & macro balancing",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBengali) "লাইভ ম্যাক্রো চার্ট" else "Live Chart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFECEFF1), thickness = 1.dp)

            // Dynamic Week Calendar Day Picker Slider
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isBengali) "তারিখ নির্বাচন করুন:" else "Select Tracking Day:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(dateList) { _, date ->
                        val dateYmd = ymdFormat.format(date)
                        val isSelected = dateYmd == selectedDate
                        val dayLabel = if (isBengali) dayFormatBn.format(date) else dayFormatEn.format(date)
                        val dayNum = dayNumFormat.format(date)

                        // Check if food logs exist for this trailing date
                        val logsExist = remember(allFoodLogs, dateYmd) {
                            allFoodLogs.any { it.date == dateYmd }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)))
                                    } else {
                                        Brush.verticalGradient(listOf(Color(0xFFF9FBFB), Color(0xFFF1F5F5)))
                                    }
                                )
                                .border(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    color = if (logsExist) Color(0xFF81C784) else Color(0xFFECEFF1),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    viewModel.selectDate(dateYmd)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("nutritional_day_chip_$dateYmd"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.Gray
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dayNum,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.White else Color(0xFF263238)
                                )
                                if (logsExist) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(if (isSelected) Color.White else Color(0xFF2E7D32), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Dashboard stats with Circular Ring (Energy representation) & Macro breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calorie Target circular visualizer
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress = if (calorieTarget > 0) (consumedCalories.toFloat() / calorieTarget).coerceIn(0f, 1f) else 0f
                    val animatedProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
                        label = "calorie_ring"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Track ring
                        drawCircle(
                            color = Color(0xFFE8F5E9),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Active ring
                        drawArc(
                            color = Color(0xFF2E7D32),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
                            size = size
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$consumedCalories",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E4620)
                        )
                        Text(
                            text = "kcal",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = if (isBengali) "লক্ষ্য: $calorieTarget" else "Goal: $calorieTarget",
                            fontSize = 9.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                // Micro breakdown list with progress bars
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val progressProtein = if (targetProteinGrams > 0) (consumedProtein.toFloat() / targetProteinGrams).coerceIn(0f, 1f) else 0f
                    val progressCarbs = if (targetCarbsGrams > 0) (consumedCarbs.toFloat() / targetCarbsGrams).coerceIn(0f, 1f) else 0f
                    val progressFat = if (targetFatGrams > 0) (consumedFat.toFloat() / targetFatGrams).coerceIn(0f, 1f) else 0f

                    // Carbs (Amber)
                    MacroRowProgressItem(
                        label = if (isBengali) "কার্বোহাইড্রেট" else "Carbohydrates",
                        consumed = consumedCarbs,
                        target = targetCarbsGrams,
                        progress = progressCarbs,
                        color = Color(0xFFFFA000), // Amber
                        testTagPrefix = "carbs"
                    )

                    // Protein (Pink Red)
                    MacroRowProgressItem(
                        label = if (isBengali) "প্রোটিন" else "Protein",
                        consumed = consumedProtein,
                        target = targetProteinGrams,
                        progress = progressProtein,
                        color = Color(0xFFEC407A), // Pink Red
                        testTagPrefix = "protein"
                    )

                    // Fat (Indigo/Teal)
                    MacroRowProgressItem(
                        label = if (isBengali) "ফ্যাট / চর্বি" else "Fats",
                        consumed = consumedFat,
                        target = targetFatGrams,
                        progress = progressFat,
                        color = Color(0xFF00ACC1), // Cyan/Teal
                        testTagPrefix = "fat"
                    )
                }
            }

            // Stacked multi-color segmented progress bar chart representing actual macro distribution percent
            if (consumedCalories > 0) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isBengali) "প্রকৃত ম্যাক্রো পুষ্টির অনুপাত (ক্যালোরি ভাগ):" else "Actual Macronutrient Share (Gram balance):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color(0xFFEEEEEE))
                    ) {
                        // Carbs Segment
                        if (carbsPercentOfWeight > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(carbsPercentOfWeight.toFloat().coerceAtLeast(1f))
                                    .background(Color(0xFFFFA000))
                                    .testTag("macro_distribution_carbs")
                            )
                        }
                        // Protein Segment
                        if (proteinPercentOfWeight > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(proteinPercentOfWeight.toFloat().coerceAtLeast(1f))
                                    .background(Color(0xFFEC407A))
                                    .testTag("macro_distribution_protein")
                            )
                        }
                        // Fat Segment
                        if (fatPercentOfWeight > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(fatPercentOfWeight.toFloat().coerceAtLeast(1f))
                                    .background(Color(0xFF00ACC1))
                                    .testTag("macro_distribution_fat")
                            )
                        }
                    }

                    // Legends Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendMetricItem(color = Color(0xFFFFA000), label = if (isBengali) "কার্বস ($carbsPercentOfWeight%)" else "Carbs ($carbsPercentOfWeight%)")
                        LegendMetricItem(color = Color(0xFFEC407A), label = if (isBengali) "প্রোটিন ($proteinPercentOfWeight%)" else "Protein ($proteinPercentOfWeight%)")
                        LegendMetricItem(color = Color(0xFF00ACC1), label = if (isBengali) "ফ্যাট ($fatPercentOfWeight%)" else "Fats ($fatPercentOfWeight%)")
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F7F7), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (isBengali) "কোন খাবার লগ পাওয়া যায়নি!" else "No food logs recorded today!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = if (isBengali) "অনুগ্রহ করে টুলস ট্যাবে উপরের প্যানেল থেকে খাবার লগ করুন।" else "Configure your diet logs above to calculate daily breakdown.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // AI Coach Diagnostic Insights note
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 22.sp)
                    Column {
                        val coachTitle = if (isBengali) "স্মার্ট ডায়েট গাইড ডায়াগনস্টিক:" else "Diet Coach Direct Analysis:"
                        val coachAdvice = remember(consumedCalories, consumedProtein, consumedCarbs, targetProteinGrams, isBengali) {
                            generateInsightAdvice(
                                consumedCalories = consumedCalories,
                                consumedProtein = consumedProtein,
                                targetProteinGrams = targetProteinGrams,
                                calorieTarget = calorieTarget,
                                isBengali = isBengali
                            )
                        }

                        Text(
                            text = coachTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = coachAdvice,
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroRowProgressItem(
    label: String,
    consumed: Double,
    target: Int,
    progress: Float,
    color: Color,
    testTagPrefix: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progressBar"
    )

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Text(
                text = "${consumed.toInt()}g / ${target}g",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238),
                modifier = Modifier.testTag("${testTagPrefix}_progress_text")
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(color, CircleShape)
                    .testTag("${testTagPrefix}_progress_bar")
            )
        }
    }
}

@Composable
private fun LegendMetricItem(color: Color, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}

private fun generateInsightAdvice(
    consumedCalories: Int,
    consumedProtein: Double,
    targetProteinGrams: Int,
    calorieTarget: Int,
    isBengali: Boolean
): String {
    if (consumedCalories == 0) {
        return if (isBengali) {
            "আজকের ডায়েটে পুষ্টির তথ্য গণনা শুরু করতে আপনার প্রথম খাবার এন্ট্রিটি যুক্ত করুন!"
        } else {
            "Please record your first meal log of the day to generate automated dietary metrics analysis!"
        }
    }

    val calDelta = calorieTarget - consumedCalories
    val isCalorieSurplus = calDelta < 0

    return if (isBengali) {
        val build = StringBuilder()
        if (isCalorieSurplus) {
            build.append("ক্যালরি উদ্বৃত্তিলীলা! আপনি নির্ধারিত সীমার বাইরে ${-calDelta} kcal অতিরিক্ত গ্রহণ করেছেন। ")
        } else {
            build.append("আপনি সফলভাবে বাজেটের মধ্যে আছেন, এখনও ${calDelta} kcal বাকি আছে। ")
        }

        if (consumedProtein < targetProteinGrams * 0.8) {
            val remain = (targetProteinGrams - consumedProtein).toInt().coerceAtLeast(1)
            build.append("পেশী পুনর্গঠন ত্বরান্বিত করতে আরও $remain গ্রাম প্রোটিন সম্পন্ন খাবার যুক্ত করুন।")
        } else {
            build.append("চমৎকার! আপনার আজকের প্রোটন বিভাজন লক্ষ্যমাত্রা সুস্থ মাত্রায় রয়েছে।")
        }
        build.toString()
    } else {
        val build = StringBuilder()
        if (isCalorieSurplus) {
            build.append("Caloric surplus detected! You entered ${-calDelta} kcal over the daily planned budget. ")
        } else {
            build.append("Great energy budget pacing! You still have ${calDelta} kcal remaining. ")
        }

        if (consumedProtein < targetProteinGrams * 0.8) {
            val remain = (targetProteinGrams - consumedProtein).toInt().coerceAtLeast(1)
            build.append("Protein deficiency: Add another $remain g of rich proteins (like egg whites/lentils) to prevent muscle fatigue.")
        } else {
            build.append("Perfect protein ratio! Excellent choice of balanced foods for cell recovery.")
        }
        build.toString()
    }
}
