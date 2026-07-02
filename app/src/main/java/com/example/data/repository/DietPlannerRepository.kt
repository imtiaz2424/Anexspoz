package com.example.data.repository

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.BuildConfig
import com.example.receiver.AlarmReceiver
import com.example.data.api.*
import com.example.data.local.DietPlannerDao
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

class DietPlannerRepository(private val dao: DietPlannerDao) {

    fun getUserProfile(userId: String): Flow<UserProfileEntity?> = dao.getUserProfileFlow(userId)
    suspend fun getUserProfileDirect(userId: String): UserProfileEntity? = withContext(Dispatchers.IO) {
        dao.getUserProfile(userId)
    }
    val allWeightLogs: Flow<List<WeightLogEntity>> = dao.getAllWeightLogsFlow()
    val allReminders: Flow<List<MealReminderEntity>> = dao.getAllRemindersFlow()

    fun getMoodLogs(userId: String): Flow<List<MoodLogEntity>> = dao.getMoodLogsFlow(userId)

    suspend fun saveMoodLog(moodLog: MoodLogEntity) = withContext(Dispatchers.IO) {
        dao.insertMoodLog(moodLog)
    }

    suspend fun saveMoodLogs(moodLogs: List<MoodLogEntity>) = withContext(Dispatchers.IO) {
        dao.insertMoodLogs(moodLogs)
    }

    suspend fun saveWaterLogs(waterLogs: List<WaterLogEntity>) = withContext(Dispatchers.IO) {
        dao.insertWaterLogs(waterLogs)
    }

    suspend fun saveWeightLogs(weightLogs: List<WeightLogEntity>) = withContext(Dispatchers.IO) {
        dao.insertWeightLogs(weightLogs)
    }

    suspend fun saveFoodLogs(foodLogs: List<FoodLogEntity>) = withContext(Dispatchers.IO) {
        dao.insertFoodLogs(foodLogs)
    }

    suspend fun saveExerciseLogs(exerciseLogs: List<ExerciseLogEntity>) = withContext(Dispatchers.IO) {
        dao.insertExerciseLogs(exerciseLogs)
    }

    suspend fun deleteMoodLog(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteMoodLog(id)
    }

    fun getMealPlanFlow(date: String): Flow<MealPlanEntity?> = dao.getMealPlanFlow(date)
    fun getAllMealPlansFlow(): Flow<List<MealPlanEntity>> = dao.getAllMealPlansFlow()
    fun getWaterLogFlow(date: String): Flow<WaterLogEntity?> = dao.getWaterLogFlow(date)
    fun getShoppingItemsFlow(date: String): Flow<List<ShoppingItemEntity>> = dao.getShoppingItemsFlow(date)

    suspend fun updateShoppingItemChecked(id: Int, isChecked: Boolean) = withContext(Dispatchers.IO) {
        dao.updateShoppingItemChecked(id, isChecked)
    }

    suspend fun addShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        dao.insertShoppingItems(listOf(item))
    }

    suspend fun deleteShoppingItems(date: String) = withContext(Dispatchers.IO) {
        dao.deleteShoppingItemsForDate(date)
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        dao.insertUserProfile(profile)
    }

    suspend fun saveWaterLog(waterLog: WaterLogEntity) = withContext(Dispatchers.IO) {
        dao.insertWaterLog(waterLog)
    }

    suspend fun saveWeightLog(weight: Double, date: String) = withContext(Dispatchers.IO) {
        val log = WeightLogEntity(date = date, weight = weight)
        dao.insertWeightLog(log)
    }

    suspend fun deleteWeightLog(date: String) = withContext(Dispatchers.IO) {
        dao.deleteWeightLog(date)
    }

    suspend fun saveReminder(reminder: MealReminderEntity) = withContext(Dispatchers.IO) {
        dao.insertReminder(reminder)
    }

    suspend fun deleteReminder(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteReminder(id)
    }

    suspend fun preloadDefaultRemindersIfEmpty() = withContext(Dispatchers.IO) {
        val existing = dao.getAllReminders()
        if (existing.isEmpty()) {
            val defaults = listOf(
                MealReminderEntity(1, "Breakfast (সকালের নাস্তা)", 8, 0, true),
                MealReminderEntity(2, "Snack 1 (সকালের হালকা খাবার)", 11, 0, true),
                MealReminderEntity(3, "Lunch (দুপুরের খাবার)", 13, 30, true),
                MealReminderEntity(4, "Snack 2 (বিকালের হালকা খাবার)", 17, 0, true),
                MealReminderEntity(5, "Dinner (রাতের খাবার)", 20, 30, true),
                MealReminderEntity(6, "Water Drink Reminder (পানি পানের রিমাইন্ডার)", 10, 0, true)
            )
            dao.insertReminders(defaults)
        }
    }

    val allRecipesFlow: Flow<List<RecipeEntity>> = dao.getAllRecipesFlow()

    suspend fun toggleRecipeFavorite(id: Int, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        dao.updateRecipeFavorite(id, isFavorite)
    }

    suspend fun preloadDefaultRecipesIfEmpty() = withContext(Dispatchers.IO) {
        val count = dao.getRecipesCount()
        if (count == 0) {
            dao.insertRecipes(com.example.data.model.DietDataStore.getFullRecipesList())
            return@withContext
        }
    }

    fun getLocalFoodLogsFlow(date: String, userId: String): Flow<List<FoodLogEntity>> = dao.getFoodLogsFlow(date, userId)
    fun getAllFoodLogsFlow(userId: String): Flow<List<FoodLogEntity>> = dao.getAllFoodLogsFlow(userId)
    suspend fun saveFoodLog(foodLog: FoodLogEntity) = withContext(Dispatchers.IO) {
        dao.insertFoodLog(foodLog)
    }
    suspend fun deleteFoodLog(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteFoodLog(id)
    }

    fun getLocalExerciseLogsFlow(date: String): Flow<List<ExerciseLogEntity>> = dao.getExerciseLogsFlow(date)
    fun getAllExerciseLogsFlow(): Flow<List<ExerciseLogEntity>> = dao.getAllExerciseLogsFlow()
    suspend fun saveExerciseLog(exerciseLog: ExerciseLogEntity) = withContext(Dispatchers.IO) {
        dao.insertExerciseLog(exerciseLog)
    }
    suspend fun deleteExerciseLog(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteExerciseLog(id)
    }

    fun scheduleReminders(context: Context, reminders: List<MealReminderEntity>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (reminder in reminders) {
            val isWater = reminder.name.lowercase().contains("water") || reminder.name.contains("পানি")
            
            // Craft dynamic consistency titles & messages
            val titleText = if (isWater) {
                "💧 Water Hydration Alert!"
            } else {
                "⏰ Log Meal Tracker: ${reminder.name}"
            }

            val msgText = if (isWater) {
                "Time to drink water! Record it in the app now to keep your hydration streak consistent / পানি পানের ফ্রেশ রেকর্ড রাখুন!"
            } else {
                "It's time to eat! Please log your meal now to maintain your tracking consistency and hit your goals / এখনই খাবারটি রেকর্ড করুন!"
            }

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("title", titleText)
                putExtra("message", msgText)
                putExtra("notification_id", reminder.id)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!reminder.isEnabled) {
                alarmManager.cancel(pendingIntent)
                continue
            }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, reminder.hour)
                set(Calendar.MINUTE, reminder.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    private fun generateLocalShoppingList(mealPlan: MealPlanEntity): List<ShoppingItemEntity> {
        val items = mutableListOf<ShoppingItemEntity>()
        val mealsText = listOf(
            mealPlan.breakfast,
            mealPlan.snack1,
            mealPlan.lunch,
            mealPlan.snack2,
            mealPlan.dinner
        )
        val seen = mutableSetOf<String>()
        for (meal in mealsText) {
            // Split by "+", "or", "and", ",", unique values
            val parts = meal.split(Regex("[+\\n,|]"))
            for (part in parts) {
                val trimmed = part.trim()
                if (trimmed.length > 2 && !trimmed.contains("Water", ignoreCase = true) && !trimmed.contains("পানি", ignoreCase = true)) {
                    val openIndex = trimmed.indexOf('(')
                    val closeIndex = trimmed.indexOf(')')
                    val name: String
                    val quantity: String
                    if (openIndex != -1 && closeIndex != -1 && closeIndex > openIndex) {
                        name = trimmed.substring(0, openIndex).trim()
                        quantity = trimmed.substring(openIndex + 1, closeIndex).trim()
                    } else {
                        name = trimmed
                        quantity = "As needed"
                    }
                    val lower = name.lowercase(Locale.ROOT)
                    if (lower.isNotBlank() && !seen.contains(lower)) {
                        seen.add(lower)
                        items.add(
                            ShoppingItemEntity(
                                date = mealPlan.date,
                                name = name,
                                quantity = quantity,
                                isChecked = false
                            )
                        )
                    }
                }
            }
        }
        return items
    }

    suspend fun generateMealPlanFromAI(
        profile: UserProfileEntity,
        date: String,
        context: Context
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Construct detailed prompt matching exact instructions
            val prompt = """
                You are an AI-powered Diet Planner App.
                Your task is to generate a personalized daily and weekly meal plan for the user based on their profile and preferences.

                User Profile Inputs:
                - Age: ${profile.age}
                - Gender: ${profile.gender}
                - Weight: ${profile.weight} kg
                - Height: ${profile.height} cm
                - Goal: ${profile.goal}
                - Dietary Preference: ${profile.dietaryPreference}
                - Allergies: ${profile.allergies.ifBlank { "None" }}
                - Activity Level: ${profile.activityLevel}

                Instructions:
                1. Calculate daily calorie needs using standard nutrition formulas (Mifflin-St Jeor or Harris-Benedict) based on activity level factor.
                2. Distribute calories into 3 main meals (breakfast, lunch, dinner) and 2 snacks.
                3. Ensure balanced macros: 40% carbs, 30% protein, 30% fat.
                4. Suggest local food options available in Bangladesh (such as Lal Atar Ruti, brown rice, lentils/dal, local fish like Rui/Tilapia, vegetables, local fruits like banana, papaya).
                5. Provide portion sizes in grams or cups with calorie count.
                6. Add recipe suggestions for each meal.
                7. Include water intake recommendation (in liters).
                8. Suggest light exercise or activity tips aligned with the user’s goal.
                9. Provide one motivational health tip at the end of each day.

                The fields in JSON MUST match exactly:
                {
                   "totalCalories": <Int value representing daily target calories calculated using Mifflin-St Jeor>,
                   "breakfast": "<food description, e.g. Lal Atar Ruti with Egg White Omelet>",
                   "breakfastCal": <Int calorie for breakfast>,
                   "breakfastPortion": "<portion size, e.g. 2 pcs ruti (60g), 2 egg whites>",
                   "breakfastRecipe": "<step-by-step cooking recipe suggestion>",
                   "snack1": "<detailed snack description, e.g. Local Banana & Almonds>",
                   "snack1Cal": <Int calorie for snack 1>,
                   "snack1Portion": "<portion size, e.g. 1 medium banana, 10 almonds>",
                   "lunch": "<detailed lunch description, e.g. Brown Rice with Rui Fish & Dal>",
                   "lunchCal": <Int calorie for lunch>,
                   "lunchPortion": "<portion size, e.g. 1.5 cups brown rice, 100g fish>",
                   "lunchRecipe": "<step-by-step cooking recipe suggestion>",
                   "snack2": "<detailed snack description, e.g. Roasted Chickpeas>",
                   "snack2Cal": <Int calorie for snack 2>,
                   "snack2Portion": "<portion size, e.g. 0.5 cup (50g)>",
                   "dinner": "<detailed dinner description, e.g. Chicken Vegetable Soup>",
                   "dinnerCal": <Int calorie for dinner>,
                   "dinnerPortion": "<portion size, e.g. 1 large bowl with 100g chicken>",
                   "dinnerRecipe": "<step-by-step cooking recipe suggestion>",
                   "waterIntakeLiters": <Double recommended water intake in liters, e.g. 2.8>,
                   "exerciseTip": "<short exercise tip matching goal>",
                   "dailyTip": "<motivational health advice at the end of the day>",
                   "macroCarbsPercent": 40,
                   "macroProteinPercent": 30,
                   "macroFatPercent": 30,
                   "weeklyTotalCalories": <Int total calorie target for the week, e.g. totalCalories * 7>,
                   "progressSuggestion": "<BMI progress and weight tracker advice>"
                }
                Do not wrap in anything else. Output only valid JSON.
            """.trimIndent()

            // Call Gemini Service
            val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
            if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY") {
                throw IllegalStateException("API key is missing.")
            }

            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(
                    responseFormat = ResponseFormat(
                        text = ResponseFormatText(
                            mimeType = "application/json"
                        )
                    ),
                    temperature = 0.5f
                )
            )

            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from AI model")

            // Parse JSON dynamic mapping
            val moshi = RetrofitClient.moshiInstance
            val adapter = moshi.adapter(DietPlanJsonResponse::class.java)
            val parsed = adapter.fromJson(text) ?: throw IllegalStateException("Failed to parse JSON diet response")

            val plan = MealPlanEntity(
                date = date,
                calorieTarget = parsed.totalCalories,
                breakfast = parsed.breakfast,
                breakfastCal = parsed.breakfastCal,
                snack1 = parsed.snack1,
                snack1Cal = parsed.snack1Cal,
                lunch = parsed.lunch,
                lunchCal = parsed.lunchCal,
                snack2 = parsed.snack2,
                snack2Cal = parsed.snack2Cal,
                dinner = parsed.dinner,
                dinnerCal = parsed.dinnerCal,
                dailyTip = parsed.dailyTip,
                rawResponse = text,
                breakfastPortion = parsed.breakfastPortion ?: "2 pieces (60g)",
                breakfastRecipe = parsed.breakfastRecipe ?: "Whisk with spices and lightly pan fry.",
                snack1Portion = parsed.snack1Portion ?: "1 fruit, 10 nuts",
                lunchPortion = parsed.lunchPortion ?: "1.5 cups rice, 1 pc fish",
                lunchRecipe = parsed.lunchRecipe ?: "Steam rice, lightly pan-fry fish with mustard oil and turmeric.",
                snack2Portion = parsed.snack2Portion ?: "0.5 cup",
                dinnerPortion = parsed.dinnerPortion ?: "1 bowl soup",
                dinnerRecipe = parsed.dinnerRecipe ?: "Simmer chicken breast with ginger, garlic and seasonal veggies.",
                waterIntakeLiters = parsed.waterIntakeLiters ?: 2.5,
                exerciseTip = parsed.exerciseTip ?: "30 minutes moderate walk.",
                macroCarbsPercent = parsed.macroCarbsPercent ?: 40,
                macroProteinPercent = parsed.macroProteinPercent ?: 30,
                macroFatPercent = parsed.macroFatPercent ?: 30,
                weeklyTotalCalories = parsed.weeklyTotalCalories ?: (parsed.totalCalories * 7),
                progressSuggestion = parsed.progressSuggestion ?: "Check BMI and maintain weights weekly."
            )

            // Save plan and generate checklist
            dao.insertMealPlan(plan)
            val ingredients = generateLocalShoppingList(plan)
            dao.deleteShoppingItemsForDate(date)
            dao.insertShoppingItems(ingredients)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            // Safe offline template fallbacks matching goals
            val offlinePlan = generateOfflinePlanFallback(profile, date)
            dao.insertMealPlan(offlinePlan)
            val ingredients = generateLocalShoppingList(offlinePlan)
            dao.deleteShoppingItemsForDate(date)
            dao.insertShoppingItems(ingredients)
            Result.success(Unit) // Gracefully handled
        }
    }

    private fun generateOfflinePlanFallback(profile: UserProfileEntity, date: String): MealPlanEntity {
        // Mifflin - St Jeor Equation
        val bmr = if (profile.gender.lowercase(Locale.ROOT) == "male") {
            (10.0 * profile.weight) + (6.25 * profile.height) - (5.0 * profile.age) + 5.0
        } else {
            (10.0 * profile.weight) + (6.25 * profile.height) - (5.0 * profile.age) - 161.0
        }
        
        // Active level multiplier
        val multiplier = when (profile.activityLevel.lowercase(Locale.ROOT)) {
            "sedentary" -> 1.2
            "active" -> 1.725
            else -> 1.55 // moderate default
        }
        
        val tdee = (bmr * multiplier).toInt()
        val targetCal = when (profile.goal.lowercase(Locale.ROOT)) {
            "weight loss", "ওজন কমানো" -> (tdee - 500).coerceAtLeast(1200)
            "weight gain", "ওজন বাড়ানো" -> tdee + 400
            else -> tdee
        }

        return MealPlanEntity(
            date = date,
            calorieTarget = targetCal,
            breakfast = "Lal Atar Ruti (লাল আটারের রুটি) & Egg White Omelet",
            breakfastCal = (targetCal * 0.25).toInt(),
            snack1 = "Handful of almonds & Local Banana (কাঠবাদাম ও পাকা কলা)",
            snack1Cal = (targetCal * 0.1).toInt(),
            lunch = "Brown rice (লাল চালের ভাত) with Rui Fish Curry & Lentil Soup (Dal)",
            lunchCal = (targetCal * 0.35).toInt(),
            snack2 = "Green tea & roasted chickpeas (গ্রিন টি ও ভাজা ছোলা)",
            snack2Cal = (targetCal * 0.1).toInt(),
            dinner = "Chicken vegetable soup (চিকেন ও সবজি স্যুপ)",
            dinnerCal = (targetCal * 0.2).toInt(),
            dailyTip = "Keep up the great work! Consistency is the foundation of long-term health and well-being.",
            rawResponse = "Offline Fallback Generated",
            breakfastPortion = "2 pieces (60g), 2 egg whites",
            breakfastRecipe = "Whisk egg whites with green chilies and onions. Cook with 1 tsp mustard oil. Serve with warm ruti.",
            snack1Portion = "1 medium banana, 10 almonds",
            lunchPortion = "1.5 cups brown rice, 1 pc (100g) Rui fish, 1 cup dal",
            lunchRecipe = "Steam brown rice. Cook fish curry with standard Bengali spices (turmeric, onions, coriander) in minimal oil.",
            snack2Portion = "0.5 cup roasted chickpeas, 1 cup green tea",
            dinnerPortion = "1 large bowl soup with 100g chicken breast",
            dinnerRecipe = "Simmer chicken breast with sliced papaya, carrots, ginger-garlic paste and coriander leaves.",
            waterIntakeLiters = (profile.weight * 35 / 1000.0).coerceIn(2.0, 4.0),
            exerciseTip = if (profile.goal.lowercase(Locale.ROOT).contains("loss")) "Complete 30 minutes of brisk walking today." else "Perform 30 minutes of resistance training or bodyweight squats.",
            macroCarbsPercent = 40,
            macroProteinPercent = 30,
            macroFatPercent = 30,
            weeklyTotalCalories = targetCal * 7,
            progressSuggestion = "Check your BMI and log weight weekly. Target BMI range is 18.5 - 24.9."
        )
    }
}
