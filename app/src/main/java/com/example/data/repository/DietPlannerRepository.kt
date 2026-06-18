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
            // Construct detailed prompt
            val prompt = """
                Generate a personalized healthy diet plan in JSON format for a person with the following details:
                - Age: ${profile.age} years old
                - Gender: ${profile.gender}
                - Weight: ${profile.weight} kg
                - Height: ${profile.height} cm
                - Goal: ${profile.goal}
                - Dietary Preference: ${profile.dietaryPreference}
                - Allergies: ${profile.allergies.ifBlank { "None" }}
                - Medical Conditions: ${profile.medicalConditions.ifBlank { "None" }}
                - Regional Cuisine: ${profile.cuisinePreferences.ifBlank { "Bengali / Bangladesh" }}

                Make sure all meals contain healthy choice items (Traditional foods matching the regional cuisine or general global fit).
                The fields in JSON MUST match exactly:
                {
                   "totalCalories": <Int value representing daily target>,
                   "breakfast": "<food description with details, e.g. Oatmeal with Almonds>",
                   "breakfastCal": <Int calorie>,
                   "snack1": "<detailed snack description>",
                   "snack1Cal": <Int calorie>,
                   "lunch": "<detailed lunch description>",
                   "lunchCal": <Int calorie>,
                   "snack2": "<detailed snack description>",
                   "snack2Cal": <Int calorie>,
                   "dinner": "<detailed dinner description>",
                   "dinnerCal": <Int calorie>,
                   "dailyTip": "<personalized actionable clinical/wellness suggestion>"
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
                rawResponse = text
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
        // Basic metabolic math (Harris-Benedict formula approximation)
        val bmr = if (profile.gender == "Male") {
            (10 * profile.weight) + (6.25 * profile.height) - (5 * profile.age) + 5
        } else {
            (10 * profile.weight) + (6.25 * profile.height) - (5 * profile.age) - 161
        }
        val tdee = (bmr * 1.3).toInt() // Active level approximation
        val targetCal = when (profile.goal) {
            "Weight Loss" -> (tdee - 500).coerceAtLeast(1200)
            "Weight Gain" -> tdee + 400
            else -> tdee
        }

        return MealPlanEntity(
            date = date,
            calorieTarget = targetCal,
            breakfast = "Lal Atar Ruti (2 pcs) (লাল আটার রুটি), Egg white omelette (2 eggs) (ডিমের সাদা অংশ), mixed traditional vegetables (সবজি)",
            breakfastCal = (targetCal * 0.25).toInt(),
            snack1 = "Handful of almonds (কাঠবাদাম) and 1 local banana (পাকা কলা)",
            snack1Cal = (targetCal * 0.1).toInt(),
            lunch = "Brown rice (1.5 cups) (লাল চালের ভাত), Lentil soup (Dal) (ডাল), Bhorta/Vaji (ভর্তা ও ভাজি), and Baked Local Fish (মাছ)",
            lunchCal = (targetCal * 0.35).toInt(),
            snack2 = "Green tea (গ্রিন টি) and roasted chickpeas (Chhola bhaja) (ছোলা ভাজা)",
            snack2Cal = (targetCal * 0.1).toInt(),
            dinner = "Chicken vegetable soup (চিকেন স্যুপ) with local coriander, or flatbread with dahl / চিকেন ও সবজি স্যুপ",
            dinnerCal = (targetCal * 0.2).toInt(),
            dailyTip = "Maintain stable insulin levels by eating complex high-fiber foods.",
            rawResponse = "Offline Fallback Generated"
        )
    }
}
