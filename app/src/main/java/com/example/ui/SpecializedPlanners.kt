package com.example.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.data.model.UserProfileEntity
import com.example.viewmodel.DietPlannerViewModel
import java.util.Locale

// Simple custom helper for nice colors
private val AppGreen = Color(0xFF2E7D32)
private val AppLightGreen = Color(0xFFE8F5E9)
private val AppSkyBlue = Color(0xFF0288D1)
private val AppLightBlue = Color(0xFFE1F5FE)
private val AppOrange = Color(0xFFE65100)
private val AppLightOrange = Color(0xFFFFF3E0)

// Data class to represent a simple meal item for the specialized planners
data class SpecializedMeal(
    val name: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val portion: String = "",
    val recipe: String = "",
    val ingredients: List<String> = emptyList(),
    val vegAlternative: String = "",
    val nonVegAlternative: String = ""
)

// Data class to represent a day in the weekly planner
data class WeeklyDayPlan(
    val dayNumber: Int,
    val dayName: String,
    val breakfast: SpecializedMeal,
    val snack1: SpecializedMeal,
    val lunch: SpecializedMeal,
    val snack2: SpecializedMeal,
    val dinner: SpecializedMeal,
    val dailyTip: String
)

@Composable
fun WeeklyMealPlannerView(userProfile: UserProfileEntity, isBengali: Boolean) {
    var selectedDayIndex by remember { mutableStateOf(0) }
    
    // Auto calculate calories based on user goals
    val dailyCalorieBase = userProfile.dailyCalorieTarget
    
    // Simple mock list for 7 days tailored with authentic Bangladeshi healthy choices
    val weeklyPlan = remember(dailyCalorieBase) {
        List(7) { index ->
            val dayNumber = index + 1
            val dayName = when (dayNumber) {
                1 -> if (isBengali) "শনিবার" else "Saturday"
                2 -> if (isBengali) "রবিবার" else "Sunday"
                3 -> if (isBengali) "সোমবার" else "Monday"
                4 -> if (isBengali) "মঙ্গলবার" else "Tuesday"
                5 -> if (isBengali) "বুধবার" else "Wednesday"
                6 -> if (isBengali) "বৃহস্পতিবার" else "Thursday"
                else -> if (isBengali) "শুক্রবার" else "Friday"
            }
            
            WeeklyDayPlan(
                dayNumber = dayNumber,
                dayName = dayName,
                breakfast = SpecializedMeal(
                    name = if (isBengali) "লাল আটার রুটি ও ডিমের সাদা অংশের ওমলেট" else "Lal Atar Ruti & Egg White Omelet",
                    calories = (dailyCalorieBase * 0.25).toInt(),
                    carbs = 40, protein = 30, fat = 30,
                    portion = "2 pcs ruti (60g) + 2 egg whites"
                ),
                snack1 = SpecializedMeal(
                    name = if (isBengali) "পাকা পেঁপে ও কাঠবাদাম" else "Fresh Papaya & Almonds",
                    calories = (dailyCalorieBase * 0.1).toInt(),
                    carbs = 40, protein = 30, fat = 30,
                    portion = "1 cup papaya + 8 almonds"
                ),
                lunch = SpecializedMeal(
                    name = if (isBengali) "লাল চালের ভাত, রুই মাছের কারি এবং পাতলা মসুর ডাল" else "Brown Rice, Rui Fish Curry & Lentil Dal",
                    calories = (dailyCalorieBase * 0.35).toInt(),
                    carbs = 40, protein = 30, fat = 30,
                    portion = "1.5 cups rice + 1 pc Rui fish + 1 cup Dal"
                ),
                snack2 = SpecializedMeal(
                    name = if (isBengali) "মুড়ি মাখা (শসা ও টমেটো সহ)" else "Healthy Muri Makha with cucumber & tomatoes",
                    calories = (dailyCalorieBase * 0.1).toInt(),
                    carbs = 40, protein = 30, fat = 30,
                    portion = "1.5 cups muri"
                ),
                dinner = SpecializedMeal(
                    name = if (isBengali) "চিকেন স্যুপ অথবা লাল আটার রুটি ও পাতলা সবজি" else "Chicken Veg Soup or Lal Atar Ruti with veggies",
                    calories = (dailyCalorieBase * 0.2).toInt(),
                    carbs = 40, protein = 30, fat = 30,
                    portion = "1 large bowl soup"
                ),
                dailyTip = when (dayNumber) {
                    1 -> if (isBengali) "সকালে পর্যাপ্ত পানি পান করে দিন শুরু করুন।" else "Start your morning with a glass of warm water to boost metabolism."
                    2 -> if (isBengali) "খাবারে লবণের ব্যবহার কমান, এটি রক্তচাপ নিয়ন্ত্রণে সাহায্য করে।" else "Reduce salt intake to maintain optimal vascular pressure."
                    3 -> if (isBengali) "সবুজ শাকসবজিতে প্রচুর ফাইবার থাকে যা হজমশক্তি বাড়ায়।" else "Leafy vegetables are loaded with dietary fibers that assist digestion."
                    4 -> if (isBengali) "মিষ্টি বা প্রক্রিয়াজাত কোমল পানীয় এড়িয়ে চলুন।" else "Avoid carbonated beverages and refined sugary foods."
                    5 -> if (isBengali) "রাতে ঘুমানোর কমপক্ষে ২ ঘণ্টা আগে রাতের খাবার শেষ করুন।" else "Try to eat dinner at least 2 hours before going to sleep."
                    6 -> if (isBengali) "প্রতিদিন ৩০ মিনিট হাঁটার অভ্যাস বজায় রাখুন।" else "Aim to complete 30 minutes of walking or physical movement today."
                    else -> if (isBengali) "সপ্তাহের শেষ দিনটিতে হালকা স্ট্রেচিং বা ইয়োগা করুন।" else "Spend some time practicing deep breathing and yoga on this weekend."
                }
            )
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Horizontal Day Selector
        Text(
            text = if (isBengali) "সাপ্তাহিক ৭ দিনের ডায়েট প্ল্যানার" else "7-Day Weekly Meal Planner",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = AppGreen
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(weeklyPlan.size) { index ->
                val day = weeklyPlan[index]
                val isSelected = selectedDayIndex == index
                
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedDayIndex = index },
                    label = { 
                        Text(
                            text = day.dayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppGreen,
                        selectedLabelColor = Color.White,
                        containerColor = AppLightGreen,
                        labelColor = AppGreen
                    )
                )
            }
        }

        val activeDay = weeklyPlan[selectedDayIndex]

        // Day detail Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${activeDay.dayName} - " + (if (isBengali) "খাদ্য তালিকা" else "Meal List"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF37474F)
                    )
                    Text(
                        text = "Total: $dailyCalorieBase kcal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AppGreen,
                        modifier = Modifier
                            .background(AppLightGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Divider(color = Color(0xFFECEFF1))

                // Breakfast
                WeeklyMealRowItem(
                    label = if (isBengali) "সকালের নাস্তা (Breakfast)" else "Breakfast",
                    meal = activeDay.breakfast,
                    isBengali = isBengali
                )
                // Snack 1
                WeeklyMealRowItem(
                    label = if (isBengali) "স্ন্যাকস ১ (Snack 1)" else "Snack 1",
                    meal = activeDay.snack1,
                    isBengali = isBengali
                )
                // Lunch
                WeeklyMealRowItem(
                    label = if (isBengali) "দুপুরের খাবার (Lunch)" else "Lunch",
                    meal = activeDay.lunch,
                    isBengali = isBengali
                )
                // Snack 2
                WeeklyMealRowItem(
                    label = if (isBengali) "স্ন্যাকস ২ (Snack 2)" else "Snack 2",
                    meal = activeDay.snack2,
                    isBengali = isBengali
                )
                // Dinner
                WeeklyMealRowItem(
                    label = if (isBengali) "রাতের খাবার (Dinner)" else "Dinner",
                    meal = activeDay.dinner,
                    isBengali = isBengali
                )

                Divider(color = Color(0xFFECEFF1))

                // Motivational Tip Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", fontSize = 16.sp)
                    Column {
                        Text(
                            text = if (isBengali) "দিনের অনুপ্রেরণা:" else "Daily Tip:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037)
                        )
                        Text(
                            text = activeDay.dailyTip,
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }
        }

        // Weekly Grocery List Card at the end
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppLightBlue),
            border = BorderStroke(1.dp, Color(0xFFB3E5FC)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛒", fontSize = 18.sp)
                    Text(
                        text = if (isBengali) "৭ দিনের সাপ্তাহিক বাজার তালিকা" else "Weekly Grocery Shopping List",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AppSkyBlue
                    )
                }

                Text(
                    text = if (isBengali) "আপনার লক্ষ্য অনুযায়ী সাপ্তাহিক প্রয়োজনীয় সুস্থ উপাদানসমূহ:" else "Recommended grocery basket items based on your target plan:",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )

                val groceries = listOf(
                    if (isBengali) "✓ লাল চালের চাল (Brown Rice) - ২ কেজি" else "✓ Brown Rice - 2 kg",
                    if (isBengali) "✓ লাল আটা (Whole Wheat Flour) - ১.৫ কেজি" else "✓ Lal Atta (Whole Wheat) - 1.5 kg",
                    if (isBengali) "✓ তাজা রুই মাছ বা দেশী মাছ - ১ কেজি" else "✓ Local Fish (Rui/Tilapia) - 1 kg",
                    if (isBengali) "✓ মুরগির বুকের মাংস (Chicken Breast) - ১.৫ কেজি" else "✓ Chicken Breast - 1.5 kg",
                    if (isBengali) "✓ মসুর ও মুগ ডাল - ১ কেজি" else "✓ Lentils (Dal) - 1 kg",
                    if (isBengali) "✓ ডিম (দেশী হাঁস/মুরগি) - ২ ডজন" else "✓ Fresh Eggs - 2 Dozen",
                    if (isBengali) "✓ মৌসুমি সবজি (পেঁপে, করলা, পটল, লাল শাক) - ৩ কেজি" else "✓ Fresh Seasonal Veggies (Papaya, Potol, Spinach) - 3 kg",
                    if (isBengali) "✓ পাকা কলা ও পাকা পেঁপে - পর্যাপ্ত" else "✓ Fresh Local Fruits (Banana, Papaya)",
                    if (isBengali) "✓ মুড়ি ও চিড়া - ১ কেজি" else "✓ Puffed Rice (Muri) - 1 kg",
                    if (isBengali) "✓ কাঠবাদাম বা চিনা বাদাম - ২৫০ গ্রাম" else "✓ Almonds or Peanuts - 250g"
                )

                groceries.forEach { item ->
                    Text(
                        text = item,
                        fontSize = 11.sp,
                        color = Color(0xFF263238),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyMealRowItem(label: String, meal: SpecializedMeal, isBengali: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = meal.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF263238)
            )
            Text(
                text = "Portion: ${meal.portion}",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "${meal.calories} kcal",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = AppOrange
        )
    }
}

@Composable
fun RecipeBasedPlannerView(userProfile: UserProfileEntity, isBengali: Boolean) {
    var expandedMealIndex by remember { mutableStateOf(-1) }

    val baseCal = userProfile.dailyCalorieTarget

    val recipes = remember(baseCal) {
        listOf(
            SpecializedMeal(
                name = if (isBengali) "লাল আটার রুটি ও ভাজি" else "Lal Atar Ruti with Healthy Veg",
                calories = (baseCal * 0.25).toInt(),
                carbs = 40, protein = 30, fat = 30,
                portion = "2 pieces (60g) with 1 cup veggies",
                ingredients = listOf(
                    if (isBengali) "লাল আটা - ৬০ গ্রাম" else "Lal Atta - 60g",
                    if (isBengali) "পানি ও সামান্য লবণ" else "Water & pinch of salt",
                    if (isBengali) "পেঁপে, গাজর ও বরবটি - ১০০ গ্রাম" else "Seasonal Papaya, Carrot, Beans - 100g",
                    if (isBengali) "সরিষার তেল - ১ চা চামচ" else "Mustard oil - 1 tsp"
                ),
                recipe = if (isBengali) "১. কুসুম গরম পানিতে আটা মাখিয়ে ডো বানান।\n২. পাতলা করে রুটি বেলে তাওয়াতে তেল ছাড়া সেঁকে নিন।\n৩. সামান্য সরিষার তেল ও পেঁয়াজ দিয়ে সবজি হালকা সেদ্ধ বা ভাজি করুন।"
                         else "1. Knead whole wheat flour with lukewarm water.\n2. Roll thin and roast on dry skillet.\n3. Lightly steam veggies with 1 tsp mustard oil and spices.",
                vegAlternative = if (isBengali) "সবজি ভাজি ও টফু তরকারি" else "Tofu or Mushroom curry alternative",
                nonVegAlternative = if (isBengali) "ডিম পোচ (অল্প তেলে সেঁকা)" else "Add 1 poached or hardboiled egg"
            ),
            SpecializedMeal(
                name = if (isBengali) "লাল চালের ভাত ও রুই মাছের ঝোল" else "Brown Rice & Rui Fish Curry",
                calories = (baseCal * 0.35).toInt(),
                carbs = 40, protein = 30, fat = 30,
                portion = "1.5 cups brown rice with 1 medium fish piece (100g)",
                ingredients = listOf(
                    if (isBengali) "লাল চাল - ৮০ গ্রাম" else "Brown Rice - 80g",
                    if (isBengali) "রুই মাছ - ১ টুকরো (১০০ গ্রাম)" else "Rui Fish - 1 piece (100g)",
                    if (isBengali) "হলুদ, মরিচ, জিরার গুড়ো" else "Turmeric, chili & cumin spices",
                    if (isBengali) "সরিষার তেল - ১.৫ চা চামচ" else "Mustard oil - 1.5 tsp",
                    if (isBengali) "মসুর ডাল - ১/২ কাপ" else "Red lentils - 0.5 cup"
                ),
                recipe = if (isBengali) "১. লাল চালের ভাত ভালোভাবে ফুটিয়ে নিন।\n২. মাছে হলুদ ও লবণ মাখিয়ে কড়াইতে সরিষার তেলে হালকা সাঁতলে নিন।\n৩. পেঁয়াজ, মরিচ এবং জিরার গুড়ো দিয়ে মাছের পাতলা ঝোল রান্না করুন।"
                         else "1. Wash and boil brown rice thoroughly.\n2. Marinate fish with turmeric and salt; lightly pan-fry in minimal mustard oil.\n3. Make gravy with chopped onions, standard spices, and water; simmer till cooked.",
                vegAlternative = if (isBengali) "মসুর ডালের বদলে ঘন ছোলার ডাল ও পনির তরকারি" else "Replace fish with 100g Paneer or Chana Masala",
                nonVegAlternative = if (isBengali) "রুই মাছের বদলে কাতলা মাছ বা পাবদা মাছ" else "Chicken breast or catfish curry"
            ),
            SpecializedMeal(
                name = if (isBengali) "মসলাদার টকদই ও বাদামের স্ন্যাকস" else "Tangy Yogurt & Almonds Snack",
                calories = (baseCal * 0.2).toInt(),
                carbs = 40, protein = 30, fat = 30,
                portion = "1 cup tokdoi with 8-10 almonds",
                ingredients = listOf(
                    if (isBengali) "টকদই (চিনি ছাড়া) - ১৫০ গ্রাম" else "Plain Sour Yogurt - 150g",
                    if (isBengali) "কাঠবাদাম বা চিনা বাদাম - ১০টি" else "Almonds or peanuts - 10 pcs",
                    if (isBengali) "সামান্য বিট লবণ ও পুদিনা পাতা" else "Pinch of pink salt & mint leaves"
                ),
                recipe = if (isBengali) "১. টকদই একটি বাটিতে নিয়ে সামান্য বিট লবণ ও পুদিনা কুচি দিয়ে ফেটিয়ে নিন।\n২. ওপরে বাদাম কুচি ছড়িয়ে পরিবেশন করুন।"
                         else "1. Whisk cold unsweetened sour yogurt with fresh mint and a pinch of pink salt.\n2. Garnish with crushed almonds and serve fresh.",
                vegAlternative = if (isBengali) "টকদইয়ের বদলে কচি ডাবের পানি ও পাকা পেয়ারা" else "Fresh Pear / Guava with Black Pepper",
                nonVegAlternative = if (isBengali) "অপরিবর্তিত রাখুন" else "Kept same (highly recommended probiotic)"
            ),
            SpecializedMeal(
                name = if (isBengali) "চিকেন ও ভেজিটেবল ক্লিয়ার স্যুপ" else "Chicken & Vegetable Clear Soup",
                calories = (baseCal * 0.2).toInt(),
                carbs = 40, protein = 30, fat = 30,
                portion = "1 large bowl (approx 350ml)",
                ingredients = listOf(
                    if (isBengali) "মুরগির বুকের মাংস - ১০০ গ্রাম" else "Chicken Breast - 100g",
                    if (isBengali) "পেঁপে, গাজর, ফুলকপি - ১ কাপ" else "Papaya, carrot, cauliflower - 1 cup",
                    if (isBengali) "আদা ও রসুন বাটা - ১ চা চামচ" else "Ginger-garlic paste - 1 tsp",
                    if (isBengali) "গোলমরিচের গুড়ো ও ধনেপাতা" else "Black pepper & fresh coriander"
                ),
                recipe = if (isBengali) "১. পানিতে আদা-রসুন বাটা ও লবণ দিয়ে মুরগির মাংস সেদ্ধ করুন।\n২. সেদ্ধ হয়ে এলে সবজি কুচি দিন এবং মৃদু আঁচে ঢেকে রাখুন।\n৩. গোলমরিচের গুড়ো এবং ধনেপাতা কুচি দিয়ে গরম গরম নামিয়ে নিন।"
                         else "1. Boil chicken pieces with ginger-garlic paste and salt in water.\n2. Add chopped seasonal veggies and simmer on medium flame until tender.\n3. Season with black pepper and fresh coriander.",
                vegAlternative = if (isBengali) "সবজি ডাল বা মাশরুম স্যুপ" else "Mushroom & Veg Tofu soup",
                nonVegAlternative = if (isBengali) "মুরগির বদলে দেশী রুই মাছের স্যুপ" else "Beef clear soup or boiled eggs salad"
            )
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isBengali) "রেসিপি-ভিত্তিক ডায়েট প্ল্যানার" else "Recipe-Based Diet & Portion Planner",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = AppSkyBlue
        )

        recipes.forEachIndexed { index, meal ->
            val isExpanded = expandedMealIndex == index
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, if (isExpanded) AppSkyBlue else Color(0xFFECEFF1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedMealIndex = if (isExpanded) -1 else index }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(AppLightBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (index) {
                                        0 -> "🍳"
                                        1 -> "🍛"
                                        2 -> "🥛"
                                        else -> "🍲"
                                    },
                                    fontSize = 18.sp
                                )
                            }
                            Column {
                                Text(
                                    text = meal.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF37474F)
                                )
                                Text(
                                    text = "Portion: ${meal.portion}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${meal.calories} kcal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AppSkyBlue
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFFECEFF1))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Ingredients
                        Text(
                            text = if (isBengali) "প্রয়োজনীয় উপাদান (Ingredients):" else "Required Ingredients:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                        meal.ingredients.forEach { ing ->
                            Text(text = "• $ing", fontSize = 11.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Method
                        Text(
                            text = if (isBengali) "প্রস্তুত প্রণালী (Cooking Method):" else "Cooking Method:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = meal.recipe,
                            fontSize = 11.sp,
                            color = Color(0xFF37474F),
                            lineHeight = 15.sp,
                            modifier = Modifier
                                .background(Color(0xFFF5F7F6), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Alternatives
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = if (isBengali) "নিরামিষ বিকল্প (Veg Alternative)" else "Veg Option:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Text(text = meal.vegAlternative, fontSize = 10.sp, color = Color(0xFF1B5E20))
                                }
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = if (isBengali) "আমিষ বিকল্প (Non-Veg Alternative)" else "Non-Veg Option:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC62828)
                                    )
                                    Text(text = meal.nonVegAlternative, fontSize = 10.sp, color = Color(0xFFB71C1C))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Hydration Reminder Box at the end
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
            border = BorderStroke(1.dp, Color(0xFFB3E5FC)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💧", fontSize = 22.sp)
                Column {
                    Text(
                        text = if (isBengali) "পানি পানের অনুস্মারক (Hydration Reminder)" else "Hydration Reminder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AppSkyBlue
                    )
                    Text(
                        text = if (isBengali) "আজ অন্তত ২.৮ লিটার বিশুদ্ধ পানি পান করে শরীর সতেজ রাখুন।" else "Aim to drink at least 2.8 to 3.2 liters of water daily to maintain metabolic balance.",
                        fontSize = 11.sp,
                        color = Color(0xFF01579B)
                    )
                }
            }
        }
    }
}

@Composable
fun EmotionBasedPlannerView(userProfile: UserProfileEntity, isBengali: Boolean) {
    var selectedEmotion by remember { mutableStateOf("happy") }

    val baseCal = userProfile.dailyCalorieTarget

    // Emotion-based customizable plans
    val emotionalPlan = remember(selectedEmotion, baseCal) {
        when (selectedEmotion) {
            "stressed" -> {
                // Calming Foods
                listOf(
                    SpecializedMeal("Kamomile or Green Tea & Walnuts (মানসিক চাপ হ্রাসকারী গ্রিন টি ও আখরোট)", (baseCal * 0.15).toInt(), 30, 20, 50, "1 cup tea + 8 nuts"),
                    SpecializedMeal("Steamed Rice with Rui Fish & Tomato Slices (রুই মাছের ঝোল ও তাজা শসা-টমেটো)", (baseCal * 0.45).toInt(), 50, 30, 20, "1.5 cups rice + 100g fish"),
                    SpecializedMeal("Dark Chocolate or Warm Almond Milk (ডার্ক চকলেট ও গরম দুধ)", (baseCal * 0.40).toInt(), 40, 25, 35, "1 square chocolate + 1 glass milk")
                )
            }
            "tired" -> {
                // Energy Boosters
                listOf(
                    SpecializedMeal("Oatmeal with Honey, Banana & Dates (কলা, মধু ও ওটস)", (baseCal * 0.30).toInt(), 60, 20, 20, "1 cup cooked oats + 1 banana"),
                    SpecializedMeal("Chicken Breast Curry with Lentils (মসুর ডাল ও চিকেন কারি)", (baseCal * 0.40).toInt(), 40, 40, 20, "150g chicken + 1 cup Dal"),
                    SpecializedMeal("Sweet Potato and Roasted Peanuts (মিষ্টি আলু ও ভাজা বাদাম)", (baseCal * 0.30).toInt(), 50, 15, 35, "1 medium potato")
                )
            }
            "motivated" -> {
                // High Protein / Active
                listOf(
                    SpecializedMeal("Egg White Scramble with Spinach (পালং শাক ও ৩টি ডিমের ওমলেট)", (baseCal * 0.25).toInt(), 20, 50, 30, "3 egg whites + spinach"),
                    SpecializedMeal("Brown Rice, Chicken Breast & Mixed Broccoli (লাল চালের ভাত ও সেদ্ধ মুরগি)", (baseCal * 0.45).toInt(), 40, 40, 20, "1.5 cups rice + 150g chicken"),
                    SpecializedMeal("High-Protein Chola or Yogurt (উচ্চ প্রোটিন ছোলা সেদ্ধ)", (baseCal * 0.30).toInt(), 50, 30, 20, "1 cup cooked chola")
                )
            }
            else -> { // happy - balanced with variety
                listOf(
                    SpecializedMeal("Lal Atar Ruti with Mixed Bengali Vegetables (লাল আটার রুটি ও সবজি ভাজি)", (baseCal * 0.25).toInt(), 45, 25, 30, "2 ruti + 1 cup veg"),
                    SpecializedMeal("Brown Rice with Rui Fish Curry & Cucumber Salad (রুই মাছ ও সালাদ)", (baseCal * 0.45).toInt(), 40, 30, 30, "1.5 cups rice + 1 pc fish"),
                    SpecializedMeal("Seasonal Mango, Papaya & Tokdoi (টকদই ও মিষ্টি মৌসুমি ফল)", (baseCal * 0.30).toInt(), 55, 25, 20, "1 cup yogurt + fruits")
                )
            }
        }
    }

    val motivationalQuote = when (selectedEmotion) {
        "stressed" -> if (isBengali) "“ধৈর্য ধরুন, চাপ সাময়িক কিন্তু সুস্থতা চিরস্থায়ী।”" else "“Breathe deeply. Stress is temporary, but the healthy choices you make today will support you forever.”"
        "tired" -> if (isBengali) "“ক্লান্তি সাময়িক বিরতির ইঙ্গিত দেয়, হাল ছাড়ার নয়। সতেজ হোন!”" else "“Tiredness indicates the need to nourish and rest your body, not to quit. Recharge and shine!”"
        "motivated" -> if (isBengali) "“আপনার আজকের পরিশ্রমই কালকের সুস্থ ও সবল শরীরের ভিত্তি।”" else "“Your efforts today are building the strength and wellness you will experience tomorrow.”"
        else -> if (isBengali) "“সুখী মন ও সুস্থ শরীর একে অপরের পরিপূরক। ইতিবাচক থাকুন!”" else "“A happy mind and a healthy body elevate each other. Keep moving forward with joy!”"
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isBengali) "আবেগ ও মেজাজ-সচেতন ডায়েট প্ল্যানার" else "Emotion & Mood-Aware AI Diet Planner",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = AppOrange
        )

        Text(
            text = if (isBengali) "আপনার বর্তমান মেজাজ নির্বাচন করুন:" else "How are you feeling right now?",
            fontSize = 11.sp,
            color = Color.Gray
        )

        // Emotion selector buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val emotions = listOf(
                "happy" to ("😊 " + (if (isBengali) "সুখী" else "Happy")),
                "stressed" to ("😰 " + (if (isBengali) "চিন্তিত" else "Stressed")),
                "tired" to ("😴 " + (if (isBengali) "ক্লান্ত" else "Tired")),
                "motivated" to ("💪 " + (if (isBengali) "উদ্যমী" else "Motivated"))
            )

            emotions.forEach { pair ->
                val key = pair.first
                val label = pair.second
                val isSel = selectedEmotion == key
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) AppLightOrange else Color(0xFFF5F7F6))
                        .border(
                            1.dp,
                            if (isSel) AppOrange else Color(0xFFECEFF1),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedEmotion = key }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) AppOrange else Color(0xFF37474F)
                    )
                }
            }
        }

        // Emotional foods summary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBengali) "মেজাজ-উপযোগী খাদ্য পরামর্শ" else "Custom Mood-Soothing Meal Menu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF37474F)
                )

                emotionalPlan.forEachIndexed { idx, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (idx) {
                                    0 -> if (isBengali) "সকাল বা হালকা খাবার" else "Morning / Light Meal"
                                    1 -> if (isBengali) "প্রধান খাবার" else "Core Sustaining Meal"
                                    else -> if (isBengali) "বিকেলের নাস্তা / রাতের খাবার" else "Afternoon / Evening Refreshment"
                                },
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF263238)
                            )
                            Text(
                                text = "Portion: ${item.portion}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "${item.calories} kcal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppOrange
                        )
                    }
                    if (idx < emotionalPlan.size - 1) {
                        Divider(color = Color(0xFFECEFF1))
                    }
                }
            }
        }

        // Quote card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)),
            border = BorderStroke(1.dp, Color(0xFFFFF59D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = motivationalQuote,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color(0xFF8D6E63),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun FitnessIntegratedPlannerView(
    viewModel: DietPlannerViewModel,
    userProfile: UserProfileEntity,
    isBengali: Boolean
) {
    // Collect tracked calories dynamically
    val currentExerciseLogs by viewModel.currentExerciseLogs.collectAsState()
    val totalBurned = currentExerciseLogs.sumOf { it.caloriesBurned }

    val baseCal = userProfile.dailyCalorieTarget
    val finalTarget = baseCal - totalBurned

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (isBengali) "ব্যায়াম ও ফিটনেস-সংযুক্ত প্ল্যানার" else "Fitness & Activity Integrated Planner",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = AppGreen
        )

        // Calorie Sync Overview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppLightGreen),
            border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔄", fontSize = 18.sp)
                    Text(
                        text = if (isBengali) "ক্যালরি সিঙ্ক মেকানিজম" else "Active Calories Sync Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AppGreen
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(if (isBengali) "ডায়েট ক্যালরি লক্ষ্য" else "Target Intake", fontSize = 11.sp, color = Color.Gray)
                        Text("$baseCal kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                    }
                    Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Column {
                        Text(if (isBengali) "ব্যায়ামে পোড়ানো ক্যালরি" else "Burned Calories", fontSize = 11.sp, color = Color.Gray)
                        Text("$totalBurned kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }
                    Text("=", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Column {
                        Text(if (isBengali) "নিট লক্ষ্য ক্যালরি" else "Net Remaining", fontSize = 11.sp, color = Color.Gray)
                        Text("$finalTarget kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppGreen)
                    }
                }

                LinearProgressIndicator(
                    progress = if (baseCal > 0) ((baseCal - totalBurned).toFloat() / baseCal.toFloat()).coerceIn(0f, 1f) else 1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AppGreen,
                    trackColor = Color(0xFFC8E6C9)
                )
            }
        }

        // Suggested Workouts based on user goal
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECEFF1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBengali) "প্রস্তাবিত ব্যায়াম ও কার্যক্রম" else "Recommended Exercises & Workouts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF37474F)
                )

                val suggestions = listOf(
                    Triple(if (isBengali) "দ্রুত হাঁটা (Brisk Walking)" else "Brisk Walking", 30, 150),
                    Triple(if (isBengali) "বাসায় ফ্রি-হ্যান্ড ব্যায়াম বা ইয়োগা" else "Home Yoga & Stretching", 25, 110),
                    Triple(if (isBengali) "শরীরের ওজনের স্কোয়াট ও পুশ-আপ" else "Bodyweight Calisthenics (Squats, Pushups)", 20, 180)
                )

                suggestions.forEachIndexed { index, workout ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = workout.first,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF263238)
                            )
                            Text(
                                text = "Duration: ${workout.second} mins",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        Button(
                            onClick = {
                                // Track exercise logs inside viewModel
                                viewModel.addExerciseLog(workout.first, workout.second, workout.third)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(text = "Track +${workout.third} kcal", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (index < suggestions.size - 1) {
                        Divider(color = Color(0xFFECEFF1))
                    }
                }
            }
        }

        // Sleep & Hydration tips
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            border = BorderStroke(1.dp, Color(0xFFFFE082)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛌", fontSize = 18.sp)
                    Text(
                        text = if (isBengali) "ঘুম এবং পুনরুদ্ধারের পরামর্শ (Sleep & Recovery)" else "Sleep & Biological Recovery Tip",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFD84315)
                    )
                }
                Text(
                    text = if (isBengali) "মাংসপেশির ক্ষয় রোধ ও পুনরুদ্ধারের জন্য প্রতিদিন কমপক্ষে ৭-৮ ঘণ্টা গভীর ঘুম নিশ্চিত করুন।"
                             else "Ensure at least 7-8 hours of continuous night sleep. Deep sleep is vital for cellular repair and muscle fiber recovery.",
                    fontSize = 11.sp,
                    color = Color(0xFFBF360C),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
