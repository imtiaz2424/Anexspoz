package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

data class EmergencyContact(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val number: String,
    val descEn: String,
    val descBn: String,
    val icon: String,
    val color: Color,
    val isCustom: Boolean = false
)

@Composable
fun LocalSOSContacts(
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPrefs = remember {
        context.getSharedPreferences("niljori_sos_contacts", Context.MODE_PRIVATE)
    }

    // Baseline national emergencies
    val baseEmergencyContacts = remember {
        listOf(
            EmergencyContact("999", "National Emergency Hot", "জাতীয় জরুরি সেবা", "999", "Police, Fire and Ambulance services", "ফায়ার সার্ভিস, পুলিশ ও অ্যাম্বুলেন্স", "🚨", Color(0xFFD32F2F)),
            EmergencyContact("109", "National Helpline for Women/Children", "নারী ও শিশু নির্যাতন প্রতিরোধ", "109", "Prevent violence against girls", "নির্যাতন ও অন্যায় প্রতিরোধ হেল্পলাইন", "👩", Color(0xFFC2185B)),
            EmergencyContact("333", "Government Information Service", "জাতীয় ও তথ্য সেবা", "333", "Public wellness directory list", "সরকারি সকল তথ্য ও নাগরিক সেবা", "📞", Color(0xFF00897B)),
            EmergencyContact("106", "Anti-Corruption Commission Helpline", "দুর্নীতি দমন কমিশন", "106", "Report illegal medical corruption", "দুর্নীতি ও অব্যবস্থাপনা অভিযোগ", "🏛️", Color(0xFFE65100)),
            EmergencyContact("16263", "Sashtho Batayon (Health Line)", "স্বাস্থ্য বাতায়ন হেল্পলাইন", "16263", "24/7 medical prescription & support", "২৪ ঘন্টা সরকারি চিকিৎসকের স্বাস্থ্যসেবা", "🩺", Color(0xFF2E7D32))
        )
    }

    // Dynamic state of custom local contacts
    var customContactsList by remember {
        mutableStateOf(loadCustomContacts(context))
    }

    // Custom creation state fields
    var isCreationOpen by remember { mutableStateOf(false) }
    var cNameEn by remember { mutableStateOf("") }
    var cNameBn by remember { mutableStateOf("") }
    var cNumber by remember { mutableStateOf("") }
    var cDescEn by remember { mutableStateOf("") }
    var cDescBn by remember { mutableStateOf("") }

    val allContacts = baseEmergencyContacts + customContactsList

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFFFCDD2).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("sos_emergency_contacts_dashboard")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
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
                            .size(38.dp)
                            .background(Color(0xFFFFEBEE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚨", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "জরুরি সাহায্য ও স্থানীয় ডিরেক্টরি" else "Local SOS Safety Directory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = if (isBengali) "ওয়ান-ক্লিক সরাসরি ডায়াল ও যোগাযোগ" else "Instant tap-to-call active response",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { isCreationOpen = !isCreationOpen },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("add_custom_sos_contact_btn")
                ) {
                    Icon(
                        imageVector = if (isCreationOpen) Icons.Default.Close else Icons.Default.AddCircle,
                        contentDescription = "Add Contact",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Divider(color = Color(0xFFFFEBEE))

            // Expandable Create Panel
            AnimatedVisibility(
                visible = isCreationOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE).copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBengali) "নতুন স্থানীয় নম্বর যুক্ত করুন" else "Register Private Local SOS Contact",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFC62828)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = cNameEn,
                                onValueChange = { cNameEn = it },
                                label = { Text("Name (English)", fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_sos_name_en"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = cNameBn,
                                onValueChange = { cNameBn = it },
                                label = { Text("নাম (বাংলা)", fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_sos_name_bn"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        OutlinedTextField(
                            value = cNumber,
                            onValueChange = { cNumber = it },
                            label = { Text(if (isBengali) "ফোন নাম্বার (Phone)" else "Phone Number", fontSize = 11.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_sos_phone_val"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = cDescEn,
                            onValueChange = { cDescEn = it },
                            label = { Text("Short Description (English)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = cDescBn,
                            onValueChange = { cDescBn = it },
                            label = { Text("সংক্ষিপ্ত বিবরণ (বাং)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                if (cNumber.isBlank() || (cNameEn.isBlank() && cNameBn.isBlank())) {
                                    Toast.makeText(context, if (isBengali) "দয়া করে নাম এবং মোবাইল নম্বরটি পূরণ করুন" else "Please satisfy all core fields first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val newId = "custom_${System.currentTimeMillis()}"
                                val created = EmergencyContact(
                                    id = newId,
                                    nameEn = if (cNameEn.isNotBlank()) cNameEn else cNameBn,
                                    nameBn = if (cNameBn.isNotBlank()) cNameBn else cNameEn,
                                    number = cNumber.trim(),
                                    descEn = if (cDescEn.isNotBlank()) cDescEn else "Local First responder Contact",
                                    descBn = if (cDescBn.isNotBlank()) cDescBn else "স্থানীয় জরুরি সাহায্য লাইন",
                                    icon = "📞",
                                    color = Color(0xFFC62828),
                                    isCustom = true
                                )

                                val updatedList = customContactsList.toMutableList()
                                updatedList.add(created)
                                customContactsList = updatedList
                                saveCustomContacts(context, updatedList)

                                // Clear fields
                                cNameEn = ""
                                cNameBn = ""
                                cNumber = ""
                                cDescEn = ""
                                cDescBn = ""
                                isCreationOpen = false

                                val successMsg = if (isBengali) "সংখ্যাটি ডিরেক্টরিতে সংরক্ষণ করা হয়েছে!" else "Emergency SOS contact saved to local list!"
                                Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("save_custom_sos_contact_action")
                        ) {
                            Text(if (isBengali) "নম্বর সংরক্ষণ করুন" else "Save Contact Digitally", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // List of SOS Numbers
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                allContacts.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAFAFA), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFECEFF1), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(item.color.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.icon, fontSize = 15.sp)
                            }

                            Column {
                                Text(
                                    text = if (isBengali) item.nameBn else item.nameEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF37474F)
                                )
                                Text(
                                    text = if (isBengali) item.descBn else item.descEn,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    lineHeight = 12.sp
                                )
                                Text(
                                    text = "Phone: ${item.number}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = item.color
                                )
                            }
                        }

                        // Call Action & Custom Deletion Action
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (item.isCustom) {
                                IconButton(
                                    onClick = {
                                        val updatedList = customContactsList.filter { it.id != item.id }
                                        customContactsList = updatedList
                                        saveCustomContacts(context, updatedList)
                                        Toast.makeText(context, if (isBengali) "যোগাযোগটি মুছে ফেলা হয়েছে।" else "Contact deleted.", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.number}"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = item.color),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("dial_emergency_${item.number}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Call",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = if (isBengali) "কল" else "Dial",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
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
}

private fun loadCustomContacts(context: Context): List<EmergencyContact> {
    val list = mutableListOf<EmergencyContact>()
    try {
        val sharedPrefs = context.getSharedPreferences("niljori_sos_contacts", Context.MODE_PRIVATE)
        val jsonStr = sharedPrefs.getString("custom_contacts_list", null) ?: return list
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                EmergencyContact(
                    id = obj.getString("id"),
                    nameEn = obj.getString("nameEn"),
                    nameBn = obj.getString("nameBn"),
                    number = obj.getString("number"),
                    descEn = obj.getString("descEn"),
                    descBn = obj.getString("descBn"),
                    icon = obj.getString("icon"),
                    color = Color(0xFFC62828),
                    isCustom = true
                )
            )
        }
    } catch (_: Exception) {}
    return list
}

private fun saveCustomContacts(context: Context, list: List<EmergencyContact>) {
    try {
        val array = JSONArray()
        for (contact in list) {
            val obj = JSONObject().apply {
                put("id", contact.id)
                put("nameEn", contact.nameEn)
                put("nameBn", contact.nameBn)
                put("number", contact.number)
                put("descEn", contact.descEn)
                put("descBn", contact.descBn)
                put("icon", contact.icon)
            }
            array.put(obj)
        }
        val sharedPrefs = context.getSharedPreferences("niljori_sos_contacts", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("custom_contacts_list", array.toString()).apply()
    } catch (_: Exception) {}
}
