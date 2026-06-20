package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.data.model.*
import com.example.viewmodel.DietPlannerViewModel
import java.io.File
import androidx.core.content.FileProvider
import java.util.Locale
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity

data class PresetFoodItem(
    val nameEn: String,
    val nameBn: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

@Composable
fun MacroBar(
    label: String,
    consumed: Double,
    target: Int,
    progress: Float,
    color: Color,
    isBengali: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Text(
                text = if (isBengali)
                    "${String.format("%.1f", consumed)}g / ${target}g"
                else
                    "${String.format("%.1f", consumed)}g of ${target}g",
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }

        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            color = color,
            trackColor = color.copy(alpha = 0.12f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

// ==========================================
// SUB-COMPONENTS FOR MEAL PLAN TAB
// ==========================================

@Composable
fun DateSelectorHeader(
    selectedDate: String,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onTodayDate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousDate,
            modifier = Modifier.testTag("prev_date_button")
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous Day",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { onTodayDate() }
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Date icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = selectedDate,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "TODAY",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        IconButton(
            onClick = onNextDate,
            modifier = Modifier.testTag("next_date_button")
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next Day",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun NiljoriAppDescriptionCard(isBengali: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isBengali) "নীলজরি এর প্রধান লক্ষ্য" else "About Niljori Health Tracker",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = Color(0xFF01579B),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = if (isBengali)
                    "আপনার দৈনন্দিন ওজন ট্র্যাকার, ক্যালোরি গণনা এবং পানি পানের মাত্রা রিয়েল-টাইমে নজরদারিতে রাখতে সাহায্যকারী সুপ্রতিষ্ঠিত মেটাবলিক সঙ্গী। এটি বৈজ্ঞানিক সূত্রের আলোকে আপনার লক্ষ্য বজায় রাখে।"
                else
                    "Your ultimate biological partner in planning custom-crafted food menus, logging weights, maintaining water goals, and enjoying expert-guided recipes seamlessly.",
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun ProfileMiniCard(userProfile: UserProfileEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 20.sp)
            }
            Column {
                Text(
                    text = "User Profile: ${userProfile.gender} (${userProfile.age} yo)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = Color(0xFF01579B)
                )
                Text(
                    text = "Weight Goal: ${userProfile.goal} | Target Cal: ${userProfile.dailyCalorieTarget} kcal",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun AIGeneratingStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = "AI is generating your meal plan...",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "Tailoring nutrition guidelines dynamically based on your clinical conditions and location preferences.",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoMealPlanPlaceholderCard(
    isBengali: Boolean,
    onGenerate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
        border = BorderStroke(1.5.dp, Color(0xFFC8E6C9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🍽️", fontSize = 36.sp)
            Text(
                text = if (isBengali) "কোনো ডায়েট প্ল্যান পাওয়া যায়নি!" else "No Customized Plan Found!",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF01579B)
            )
            Text(
                text = if (isBengali)
                    "আপনার জন্য সুনির্দিষ্ট পুষ্টিকর খাবারের তালিকা রিয়েল-টাইমে তৈরি করতে নিচের বাটনে চাপ দিন।"
                else
                    "Generate a custom-crafted meal plan matching your local Bangladesh cuisine preferences and clinical records.",
                fontSize = 11.5.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )

            Button(
                onClick = onGenerate,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_initial_meal_plan")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Generate")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "নতুন ডুয়েট প্ল্যান জেনারেট করুন" else "Generate Smart Diet Plan",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MealPlanHeaderSummary(
    mealPlan: MealPlanEntity,
    consumedCal: Int,
    progressPercent: Float
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Diet Overview & Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Net Calories Logged",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$consumedCal / ${mealPlan.calorieTarget} kcal",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(progressPercent * 100).toInt()}% Done",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = progressPercent,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun MealCardItem(
    mealTitle: String,
    mealDetails: String,
    calories: Int,
    categoryName: String,
    isDone: Boolean,
    onDoneChange: (Boolean) -> Unit,
    isNextUp: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) Color(0xFFF1F8E9) else Color.White
        ),
        border = BorderStroke(
            width = if (isNextUp) 1.5.dp else 1.dp,
            color = if (isDone) Color(0xFFC8E6C9)
                    else if (isNextUp) MaterialTheme.colorScheme.primary
                    else Color(0xFFECEFF1)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meal_card_${categoryName.lowercase(Locale.ROOT)}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = onDoneChange,
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0288D1)),
                        modifier = Modifier.testTag("meal_checkbox_${categoryName.lowercase(Locale.ROOT)}")
                    )
                    Column {
                        Text(
                            text = mealTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = if (isDone) Color.Gray else Color.Black
                        )
                        if (isNextUp) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE0F7FA))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "NEXT UP",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0F7FA))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$calories kcal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF0288D1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mealDetails,
                fontSize = 12.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }
}

@Composable
fun HealthTipCard(tip: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        border = BorderStroke(1.dp, Color(0xFFFFECB3)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 24.sp)
            Column {
                Text(
                    text = "Healthy Suggestion & Tip",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF5D4037)
                )
                Text(
                    text = tip,
                    fontSize = 11.5.sp,
                    color = Color(0xFF795548),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun LocalShoppingListCard(viewModel: DietPlannerViewModel, isBengali: Boolean) {
    val shoppingItems by viewModel.shoppingItems.collectAsState()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECEFF1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = if (isBengali) "আজকের রান্নার উপকরণসমূহ" else "Auto-generated Cooking Ingredients Checklist",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = Color(0xFF01579B),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            if (shoppingItems.isEmpty()) {
                Text(
                    text = if (isBengali) "আজকের কোনো সামগ্রী পাওয়া যায়নি।" else "No ingredients calculated for today. Fill or generate your meal plan first.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            } else {
                val previewLimit = minOf(shoppingItems.size, 5)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 0 until previewLimit) {
                        val item = shoppingItems[i]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (item.isChecked) Color(0xFF0288D1) else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = item.name,
                                    fontSize = 12.sp,
                                    color = if (item.isChecked) Color.Gray else Color.Black
                                )
                            }
                            Text(
                                text = item.quantity,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (shoppingItems.size > 5) {
                        Text(
                            text = if (isBengali) "...ও আরও ${shoppingItems.size - 5} টি সামগ্রী বাজার ফর্দে রয়েছে" else "... and ${shoppingItems.size - 5} more items",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0288D1),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// DYNAMIC MODAL DIALOGS
// ==========================================

@Composable
fun NiljoriNotificationDialog(isBengali: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBengali) "রিয়েল-টাইম নোটিফিকেশন" else "Niljori Live Alerts", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isBengali)
                        "💧 পানি পান রিমাইন্ডার: অভিনন্দন! আপনি আজকের পানি পানের লক্ষ্যের ৬০% পূরণ করেছেন।"
                    else
                        "💧 Drink Water: Great jo! You completed 60% of hydration volume today.",
                    fontSize = 12.sp
                )
                Text(
                    text = if (isBengali)
                        "🍉 পুষ্টি পরামর্শ: বিকেলের নাস্তায় ফলের সাথে সামান্য কাঠবাদাম খেলে মেটাবলিজম উন্নত হয়।"
                    else
                        "🍉 Nutrition Alert: Pairing afternoon fruits with almonds boosts glycemic control.",
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiljoriSearchDialog(
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onNavigateToTab: (Int) -> Unit
) {
    val context = LocalContext.current
    var searchInput by remember { mutableStateOf("") }
    val mockPages = listOf(
        "Meal Plan (খাবারের রেসিপি ও ডায়েট)" to 1,
        "Water Tracker (দৈনিক পানি পানের তথ্য)" to 3,
        "Weight Logger (ওজন বৃদ্ধির চার্ট)" to 3,
        "Explore Tab (সুষম খাদ্য জ্ঞান)" to 2,
        "User Profile (প্রোফাইল মেজারমেন্ট)" to 4
    )

    // Voice Input Speech Recognizer Launcher logic
    val voiceInputLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: ""
            if (spokenText.isNotEmpty()) {
                searchInput = spokenText
                Toast.makeText(context, "Voice input: $spokenText", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val filtered = mockPages.filter {
        it.first.lowercase().contains(searchInput.lowercase())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBengali) "সম্পূর্ণ অ্যাপ অনুসন্ধান করুন" else "Global App Search Explorer",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    placeholder = { Text(if (isBengali) "কণ্ঠস্বর বা নাম দিয়ে খুঁজুন..." else "Search pages (Voice enabled)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, if (isBengali) "অনুগ্রহ করে কথা বলুন..." else "Speak now search query...")
                                    }
                                    voiceInputLauncher.launch(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Voice speech recognition not supported on device.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.testTag("global_search_voice_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Input Mic",
                                tint = Color(0xFF00796B)
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("app_global_search_input")
                )

                Text(
                    text = if (isBengali) "অনুসন্ধানের ফলাফলসমূহ:" else "Matched Pages / Utilities:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    filtered.forEach { (label, index) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToTab(index) }
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (filtered.isEmpty()) {
                        Text(
                            text = "No pages found. Try simple terms like 'water' or 'weight'.",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বন্ধ করুন" else "Close")
            }
        }
    )
}

@Composable
fun NiljoriTermsDialog(isBengali: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBengali) "ব্যবহারের শর্তাবলী" else "Terms & Conditions", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Niljori Health Tracker provides customized nutritional suggestions under automated algorithm baselines. These outputs should not be replaced by standard clinical medical diagnostics.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "By creating profiles, you agree to local data storage using encrypted Android Room layers safely.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun NiljoriPrivacyPolicyDialog(isBengali: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBengali) "প্রাইভেসি পলিসি" else "Privacy Policy", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Niljori values your personal and biological weights records. We guarantee 100% security with no servers-side leaks.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "OAuth and Google credentials are dynamically bounded on your active Android OS sandboxes securely.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun NiljoriAICoachDialog(isBengali: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBengali) "স্মার্ট এআই কোচ" else "Niljori Lifestyle Coach", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Our Lifestyle Coach analyzes your daily water patterns and glycemic calorie intake averages to balance insulin sensitivity.",
                    fontSize = 12.sp
                )
                Text(
                    text = "💡 Advice for today: Complete at least 2.5 Liters of hydration to detoxify cellular systems properly.",
                    fontSize = 12.sp,
                    color = Color(0xFF0288D1),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun NiljoriRatingsDialog(isBengali: Boolean, onDismiss: () -> Unit) {
    var rating by remember { mutableStateOf(5) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBengali) "আপনার মূল্যবান রেটিং দিন" else "Rate Our Application", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBengali) "আমরা আপনার অভিজ্ঞতায় সন্তুষ্ট। আরও পুষ্টিকর আপডেট দিতে আমাদের ভালো রেটিং দিন!" else "Love tracking with us? Lend a friendly hand by providing feedback!",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFBC02D),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    Toast.makeText(context, if (isBengali) "আপনার মতামতের জন্য অসংখ্য ধন্যবাদ!" else "Thank you for supporting us!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text(if (isBengali) "দাখিল করুন" else "Submit Rating")
            }
        }
    )
}

@Composable
fun NiljoriAppInfoDialog(isBengali: Boolean, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBengali) "অ্যাপ নির্দেশিকা ও ম্যানুয়াল" else "Application Information Manual", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Niljori Diet Planner helps you maintain stable metabolic states. Key functions include:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1. AI Diet Generation: Formulates personalized meal recommendations directly in English/Bengali.\n\n2. Real-time hydration logs: Record fluid volumes in milliliters to meet target numbers.\n\n3. Regional Recipes: High fidelity traditional healthy recipes.",
                    fontSize = 11.5.sp,
                    color = Color.DarkGray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun NiljoriDailyInsightDialog(
    isBengali: Boolean,
    totalCalorieTarget: Int,
    consumedMealsCal: Int,
    extraSnacksCal: Int,
    workoutBurntCal: Int,
    waterLog: WaterLogEntity?,
    dailyWaterTarget: Int,
    currentExerciseLogs: List<ExerciseLogEntity>,
    onDismiss: () -> Unit
) {
    val totalConsumed = consumedMealsCal + extraSnacksCal
    val netCalories = (totalConsumed - workoutBurntCal).coerceAtLeast(0)
    val waterAmount = waterLog?.amountMl ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📊", fontSize = 24.sp)
                Text(
                    text = if (isBengali) "আজকের পুষ্টি ও স্বাস্থ্য অন্তর্দৃষ্টি" else "Consolidated Daily Insights",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Calorie summary box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE0F7FA))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isBengali) "ক্যালোরি অ্যাকাউন্টাবিমিটি (Calorie Log):" else "Calorie Logging Stats:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF01579B)
                        )
                        Text(
                            text = "• Target Baseline: $totalCalorieTarget kcal\n• Meals Logged Check: $consumedMealsCal kcal\n• Extra Manual Snacks: $extraSnacksCal kcal\n• Workout Burnt: -$workoutBurntCal kcal\n\n👉 Net Remaining: ${(totalCalorieTarget - netCalories).coerceAtLeast(0)} kcal",
                            fontSize = 11.5.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Water stats box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE3F2FD))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isBengali) "হাইড্রেশন লেভেল (Hydration Level):" else "Water Hydration Summary:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E88E5)
                        )
                        Text(
                            text = "• Target Volume: $dailyWaterTarget mL\n• Logged Hydrated: $waterAmount mL\n• Completion: ${if (dailyWaterTarget > 0) (waterAmount * 100) / dailyWaterTarget else 0}%",
                            fontSize = 11.5.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                // Workouts details box
                if (currentExerciseLogs.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isBengali) "আজকের ব্যায়ামসমূহ (Workouts):" else "Daily Workouts Details:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFE65100)
                            )
                            currentExerciseLogs.forEach { log ->
                                Text(
                                    text = "• ${log.activity}: ${log.durationMin} mins (-${log.caloriesBurned} kcal)",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

// ==========================================
// SHARING UTILITY ACTIONS
// ==========================================

fun sharePdf(context: Context, file: File) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share AI Diet Report"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error sharing PDF report.", Toast.LENGTH_SHORT).show()
    }
}

fun shareMealPlanToSocial(context: Context, mealPlan: MealPlanEntity) {
    val shareText = """
        My Healthy AI Diet Plan of the Day (${mealPlan.date}) via Niljori:
        - Breakfast: ${mealPlan.breakfast} (${mealPlan.breakfastCal} kcal)
        - Lunch: ${mealPlan.lunch} (${mealPlan.lunchCal} kcal)
        - Dinner: ${mealPlan.dinner} (${mealPlan.dinnerCal} kcal)
        - Baseline Target Calorie: ${mealPlan.calorieTarget} kcal
        
        Stay fit and eat smart! Download Niljori App in Android now.
    """.trimIndent()
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Diet Plan via"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiljoriQuickLogDialog(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    onDismiss: () -> Unit
) {
    var isWaterSelected by remember { mutableStateOf(true) }

    // Water States
    var waterAmountText by remember { mutableStateOf("250") }

    // Food States
    var foodNameText by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("") }
    var carbsText by remember { mutableStateOf("") }
    var fatText by remember { mutableStateOf("") }

    val context = LocalContext.current

    val presetFoods = listOf(
        PresetFoodItem("Boiled Egg", "ডিম সেদ্ধ (১টি)", 78, 6.3, 0.6, 5.3),
        PresetFoodItem("Red Rice", "লাল চালের ভাত (১ কাপ)", 216, 5.0, 45.0, 1.6),
        PresetFoodItem("Ata Ruti", "আটা রুটি", 104, 3.5, 22.0, 0.5),
        PresetFoodItem("Chicken Curry", "মুরগির মাংস", 195, 18.0, 4.0, 11.0),
        PresetFoodItem("Moshur Dal", "মসুর ডাল", 115, 9.0, 20.0, 0.8),
        PresetFoodItem("Banana", "পাকা কলা (১টি)", 105, 1.3, 27.0, 0.3)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBengali) "কুইক লগ ট্র্যাকার" else "Quick Logger",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Segmented Toggle Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isWaterSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { isWaterSelected = true }
                            .padding(vertical = 8.dp)
                            .testTag("water_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = null,
                                tint = if (isWaterSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isBengali) "পানি পান" else "Water",
                                color = if (isWaterSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isWaterSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { isWaterSelected = false }
                            .padding(vertical = 8.dp)
                            .testTag("food_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = if (!isWaterSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isBengali) "খাবার যোগ" else "Food",
                                color = if (!isWaterSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                if (isWaterSelected) {
                    // WATER LOGGING VIEW
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBengali) "পানি পানের পরিমাণ সিলেক্ট করুন:" else "Select or enter water volume:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Glass Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val sizes = listOf(
                                "250" to if (isBengali) "২৫০ মি.লি." else "250ml",
                                "500" to if (isBengali) "৫০০ মি.লি." else "500ml",
                                "750" to if (isBengali) "৭৫০ মি.লি." else "750ml",
                                "1000" to if (isBengali) "১ লিটার" else "1.0L"
                            )
                            sizes.forEach { (volume, label) ->
                                Button(
                                    onClick = { waterAmountText = volume },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (waterAmountText == volume) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (waterAmountText == volume) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(11.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("water_preset_${volume}"),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Custom Numeric Increments/Decrements Selector Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = {
                                    val currentVal = waterAmountText.toIntOrNull() ?: 0
                                    val newVal = (currentVal - 50).coerceAtLeast(0)
                                    waterAmountText = newVal.toString()
                                },
                                modifier = Modifier.size(44.dp).testTag("water_minus_btn")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease 50ml", tint = MaterialTheme.colorScheme.primary)
                            }

                            // Dynamic Text Output
                            OutlinedTextField(
                                value = waterAmountText,
                                onValueChange = { waterAmountText = it.filter { char -> char.isDigit() } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                singleLine = true,
                                trailingIcon = {
                                    Text(
                                        "ml",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier
                                    .width(134.dp)
                                    .background(Color.Transparent)
                                    .testTag("water_input")
                            )

                            IconButton(
                                onClick = {
                                    val currentVal = waterAmountText.toIntOrNull() ?: 0
                                    val newVal = currentVal + 50
                                    waterAmountText = newVal.toString()
                                },
                                modifier = Modifier.size(44.dp).testTag("water_plus_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase 50ml", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                } else {
                    // FOOD/MEAL LOGGING VIEW
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBengali) "খাবারের তথ্য প্রদান করুন বা নিচে থেকে নির্বাচন করুন:" else "Fill food details or tap custom preset below:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Quick Food Presets list from Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetFoods.forEach { item ->
                                FilterChip(
                                    selected = foodNameText == (if (isBengali) item.nameBn else item.nameEn),
                                    onClick = {
                                        foodNameText = if (isBengali) item.nameBn else item.nameEn
                                        caloriesText = item.calories.toString()
                                        proteinText = item.protein.toString()
                                        carbsText = item.carbs.toString()
                                        fatText = item.fat.toString()
                                    },
                                    label = {
                                        Text(
                                            text = if (isBengali) item.nameBn.split(" ").first() else item.nameEn,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    modifier = Modifier.testTag("food_preset_${item.nameEn.replace(" ", "_")}")
                                )
                            }
                        }

                        // Input fields
                        OutlinedTextField(
                            value = foodNameText,
                            onValueChange = { foodNameText = it },
                            label = { Text(if (isBengali) "খাবারের নাম" else "Food Item Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("quick_food_name"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = caloriesText,
                            onValueChange = { caloriesText = it.filter { char -> char.isDigit() } },
                            label = { Text(if (isBengali) "ক্যালরি (kcal)" else "Calories (kcal)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("quick_food_calories"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Macros Grid (3 columns)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = proteinText,
                                onValueChange = { proteinText = it },
                                label = { Text(if (isBengali) "প্রোটিন (g)" else "Protein (g)", fontSize = 8.sp, maxLines = 1) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("quick_food_protein"),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = carbsText,
                                onValueChange = { carbsText = it },
                                label = { Text(if (isBengali) "কার্বস (g)" else "Carbs (g)", fontSize = 8.sp, maxLines = 1) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("quick_food_carbs"),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = fatText,
                                onValueChange = { fatText = it },
                                label = { Text(if (isBengali) "ফ্যাট (g)" else "Fat (g)", fontSize = 8.sp, maxLines = 1) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("quick_food_fat"),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isWaterSelected) {
                        val amountMl = waterAmountText.toIntOrNull() ?: 0
                        if (amountMl > 0) {
                            viewModel.addWater(amountMl)
                            onDismiss()
                        } else {
                            Toast.makeText(context, if (isBengali) "দয়া করে সঠিক পরিমাণ দিন" else "Please specify a valid volume", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val name = foodNameText.trim()
                        val cals = caloriesText.toIntOrNull() ?: 0
                        val prot = proteinText.toDoubleOrNull() ?: 0.0
                        val carb = carbsText.toDoubleOrNull() ?: 0.0
                        val fat = fatText.toDoubleOrNull() ?: 0.0

                        if (name.isNotEmpty() && cals >= 0) {
                            viewModel.addFoodLog(
                                name = name,
                                calories = cals,
                                protein = prot,
                                carbs = carb,
                                fat = fat
                            )
                            onDismiss()
                        } else {
                            Toast.makeText(context, if (isBengali) "খাবারের নাম এবং ক্যালরি আবশ্যক!" else "Food name and calories are required!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.testTag("save_log_confirm_btn")
            ) {
                Text(if (isBengali) "সংরক্ষণ করুন" else "Save Log")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dis_log_btn")
            ) {
                Text(if (isBengali) "বাতিল" else "Cancel")
            }
        }
    )
}
