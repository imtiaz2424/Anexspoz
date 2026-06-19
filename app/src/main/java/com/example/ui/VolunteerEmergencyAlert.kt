package com.example.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.DietPlannerViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
fun VolunteerEmergencyAlert(
    viewModel: DietPlannerViewModel,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Broadcast states
    var isBroadcasting by remember { mutableStateOf(false) }
    var countdownValue by remember { mutableStateOf(5) }
    var customNotes by remember { mutableStateOf("") }
    var emergencyCategory by remember { mutableStateOf("Medical / Allergy") }
    var customAddressInput by remember { mutableStateOf("") }

    // Detected location fallback
    var isLoadingLocation by remember { mutableStateOf(false) }

    // Live active volunteer interactions (simulated list updates)
    var activeVolunteersList by remember { mutableStateOf<List<SimulatedVolunteer>>(emptyList()) }
    var notificationMessage by remember { mutableStateOf("") }

    // Emergency Contact Numbers
    val nationalEmergencyNumber = "999"

    // Play services location
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isLoadingLocation = true
            scope.launch {
                detectCurrentLocationReverseGeocode(
                    context = context,
                    fusedLocationClient = fusedLocationClient,
                    isBengali = isBengali,
                    onLocationDetected = { address ->
                        customAddressInput = address
                        isLoadingLocation = false
                        Toast.makeText(context, if (isBengali) "অবস্থান লোড হয়েছে!" else "Location loaded!", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { error ->
                        isLoadingLocation = false
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    }
                )
            }
        } else {
            Toast.makeText(context, if (isBengali) "অবস্থান পারমিশন দেওয়া আবশ্যক!" else "GPS Location permission required!", Toast.LENGTH_SHORT).show()
        }
    }

    // Default static list of registered neighborhood guardians
    val staticLocalGuardians = remember(isBengali) {
        listOf(
            SimulatedVolunteer(
                id = "vol_1",
                name = if (isBengali) "আরিফ রহমান" else "Arif Rahman",
                phone = "+8801711122233",
                distance = "0.3 km",
                skills = listOf(if (isBengali) "প্রাথমিক চিকিৎসা" else "First Aid CPR", if (isBengali) "অ্যালার্জি সাহায্য" else "Allergy Help"),
                rating = 4.9f
            ),
            SimulatedVolunteer(
                id = "vol_2",
                name = if (isBengali) "ডাঃ তানিয়া আহমেদ" else "Dr. Tania Ahmed",
                phone = "+8801822233344",
                distance = "0.7 km",
                skills = listOf(if (isBengali) "চিকিৎসক" else "Physician", if (isBengali) "মানসিক শান্তি" else "Mental Calmness"),
                rating = 5.0f
            ),
            SimulatedVolunteer(
                id = "vol_3",
                name = if (isBengali) "হাসান আলী" else "Hasan Ali",
                phone = "+8801933344455",
                distance = "1.5 km",
                skills = listOf(if (isBengali) "জরুরি ড্রাইভার" else "Emergency Driver", if (isBengali) "খাদ্য সরবরাহকারী" else "Dietary Guide"),
                rating = 4.8f
            )
        )
    }

    // Handled timing loops for simulated response flow when broadcasting is activated
    LaunchedEffect(isBroadcasting) {
        if (isBroadcasting) {
            // Step 1: Countdown cancel stage
            countdownValue = 5
            while (countdownValue > 0 && isBroadcasting) {
                delay(1000)
                countdownValue--
            }

            if (isBroadcasting) {
                notificationMessage = if (isBengali) "ডিফেন্ডারদের সতর্ক সংকেত প্রেরণ করা হয়েছে..." else "Dispatched distress flare to Symmetry Guardians..."
                activeVolunteersList = emptyList()

                // Simulation: volunteer responds
                delay(2500)
                if (isBroadcasting) {
                    val responder1 = staticLocalGuardians[0].copy(status = "Heading to your location", accepted = true)
                    activeVolunteersList = listOf(responder1)
                    notificationMessage = if (isBengali) "🚨 ${responder1.name} আপনার সাহায্য সংকেত গ্রহণ করেছেন এবং আসছেন!" else "🚨 ${responder1.name} accepted your crisis alert and is route-bound!"
                }

                delay(4000)
                if (isBroadcasting) {
                    val responder1 = staticLocalGuardians[0].copy(status = "Heading to your location", accepted = true)
                    val responder2 = staticLocalGuardians[1].copy(status = "Active Consultation via Call", accepted = true)
                    activeVolunteersList = listOf(responder1, responder2)
                    notificationMessage = if (isBengali) "💖 ডঃ তানিয়া আহমেদ টেলি-মেডিসিন সহায়তার জন্য এক্টিভ হয়েছেন!" else "💖 Dr. Tania Ahmed initiated direct emergency consultation!"
                }
            }
        } else {
            activeVolunteersList = emptyList()
            notificationMessage = ""
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBroadcasting) Color(0xFFFFEBEE) else Color.White
        ),
        border = BorderStroke(1.dp, if (isBroadcasting) Color(0xFFEF5350) else Color(0xFFFFCDD2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("volunteer_emergency_alert_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Row Header
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
                            .background(if (isBroadcasting) Color(0xFFD32F2F) else Color(0xFFFFEBEE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🚨", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = if (isBengali) "স্বেচ্ছাসেবক জরুরি সাহায্য সংকেত" else "Guardian distress signal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = if (isBengali) "সহযোগিতার জন্য নিকটস্থ স্বেচ্ছাসেবকদের সাথে সংযোগ করুন" else "Broadcast status flare to local registered volunteers",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Divider(color = Color(0xFFFFCDD2).copy(alpha = 0.5f), thickness = 1.dp)

            if (!isBroadcasting) {
                // Category Config
                Text(
                    text = if (isBengali) "সাহায্যের ধরন নির্বাচন করুন:" else "Select emergency category:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFFD32F2F)
                )

                val categories = listOf("Medical / Allergy", "Panic Attack", "Fitness Accident", "General Support")
                val categoryLabelsBn = mapOf(
                    "Medical / Allergy" to "অ্যালার্জি/চিকিৎসা",
                    "Panic Attack" to "মানসিক প্যানিক",
                    "Fitness Accident" to "শরীরচর্চা আঘাত",
                    "General Support" to "অন্যান্য সাহায্য"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        item {
                            val isSelected = emergencyCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(if (isSelected) Color(0xFFC62828) else Color(0xFFF9FBFB))
                                    .clickable { emergencyCategory = cat }
                                    .border(1.dp, if (isSelected) Color(0xFFC62828) else Color(0xFFFFCDD2), RoundedCornerShape(30.dp))
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                    .testTag("emergency_cat_$cat")
                            ) {
                                Text(
                                    text = if (isBengali) (categoryLabelsBn[cat] ?: cat) else cat,
                                    color = if (isSelected) Color.White else Color(0xFFC62828),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Custom context text fields
                OutlinedTextField(
                    value = customNotes,
                    onValueChange = { customNotes = it },
                    label = { Text(if (isBengali) "জরুরি মনের অবস্থা বা লক্ষণসমূহ লিখুন (ঐচ্ছিক)" else "Briefly describe your symptoms / situation (Optional)") },
                    placeholder = { Text(if (isBengali) "যেমন: খাবারে হঠাৎ তীব্র ডায়রিয়া / হঠাৎ বুক ধড়ফড় করা ও মাথা ঘোরা।" else "e.g. Sharp allergic hives from nut ingestion / extreme panic episode") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFC62828),
                        unfocusedBorderColor = Color(0xFFFFCDD2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_notes_field")
                )

                // Input Address
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customAddressInput,
                        onValueChange = { customAddressInput = it },
                        label = { Text(if (isBengali) "জরুরি ঠিকানা বা বিবরণ" else "Rescue Address or Details") },
                        placeholder = { Text(if (isBengali) "যেমন: ৩য় তলা, বাড়ি ৩৫, রোড ১২" else "e.g. Flat B3, Lane 4, sector 7") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFC62828),
                            unfocusedBorderColor = Color(0xFFFFCDD2)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("emergency_address_field")
                    )

                    // Location finder GPS button
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            val fineLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            val coarseLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            if (fineLoc == PackageManager.PERMISSION_GRANTED || coarseLoc == PackageManager.PERMISSION_GRANTED) {
                                isLoadingLocation = true
                                scope.launch {
                                    detectCurrentLocationReverseGeocode(
                                        context = context,
                                        fusedLocationClient = fusedLocationClient,
                                        isBengali = isBengali,
                                        onLocationDetected = { address ->
                                            customAddressInput = address
                                            isLoadingLocation = false
                                        },
                                        onFailure = { err ->
                                            isLoadingLocation = false
                                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            } else {
                                requestPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFEBEE))
                            .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                            .testTag("emergency_location_btn")
                    ) {
                        if (isLoadingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFD32F2F),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Locate Me",
                                tint = Color(0xFFD32F2F)
                            )
                        }
                    }
                }

                // Interactive Broadcast SOS Launch Point
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        isBroadcasting = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("trigger_sos_broadcast_btn")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🚨", fontSize = 18.sp)
                        Text(
                            text = if (isBengali) "জরুরি সাহায্য সংকেত পাঠান (SOS)" else "Broadcast Distress SOS Signal",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

            } else {
                // BROADCAST ACTIVE STATE DISPLAY
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (countdownValue > 0) {
                        // Safe Stage: Can Cancel SOS
                        Text(
                            text = if (isBengali) "জরুরি সংকেত প্রেরণ করা হচ্ছে..." else "Distress SOS signal is pending...",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )

                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFFD32F2F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$countdownValue",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp
                            )
                        }

                        Text(
                            text = if (isBengali) "ভুলবশত ক্লিক করে থাকলে অবিলম্বে বাতিল করুন।" else "Press cancel immediately to abort unintended distress broadcast.",
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            color = Color.DarkGray
                        )

                        Button(
                            onClick = { isBroadcasting = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(180.dp)
                                .height(44.dp)
                                .testTag("cancel_sos_btn")
                        ) {
                            Text(
                                text = if (isBengali) "সংকেত বাতিল করুন" else "Cancel Broadcast",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        // LIVE SOS STAGE
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFD32F2F),
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = if (isBengali) "জরুরি সাহায্য লাইভ ব্রডকাস্ট সক্রিয়!" else "LIVE DISTRESS Broadcaster Active!",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD32F2F),
                                fontSize = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFEF5350).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isBengali) "সংকেত বিবরণ:" else "Distress Data Packet:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = if (isBengali) "ধরণ: ${emergencyCategory}" else "Category: ${emergencyCategory}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFC62828)
                                )
                                if (customAddressInput.isNotBlank()) {
                                    Text(
                                        text = if (isBengali) "ঠিকানা: ${customAddressInput}" else "Rescue Addr: ${customAddressInput}",
                                        fontSize = 11.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                if (customNotes.isNotBlank()) {
                                    Text(
                                        text = "\"${customNotes}\"",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Simulation notifications ticker banner
                        AnimatedVisibility(visible = notificationMessage.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFEE58).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFFBC02D), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = notificationMessage,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Active Volunteers who accepted signal
                        if (activeVolunteersList.isNotEmpty()) {
                            Text(
                                text = if (isBengali) "সহযোগিতার জন্য অগ্রসরমান স্বেচ্ছাসেবক:" else "Guardians responding to your flare:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFC62828),
                                modifier = Modifier.align(Alignment.Start)
                            )

                            activeVolunteersList.forEach { responder ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color(0xFFEF5350)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("active_responder_${responder.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFFFFEBEE), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🛡️", fontSize = 20.sp)
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = responder.name,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFFC62828)
                                                )
                                                Text(
                                                    text = "📍 ${responder.distance}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFE65100)
                                                )
                                            }

                                            Text(
                                                text = "${responder.status} • Rating: ⭐ ${responder.rating}",
                                                fontSize = 11.sp,
                                                color = Color.DarkGray
                                            )

                                            // Displaying skills badges
                                            Row(
                                                modifier = Modifier.padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                responder.skills.forEach { sk ->
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color(0xFFE0F2F1), RoundedCornerShape(6.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(sk, fontSize = 8.sp, color = Color(0xFF004D40), fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        // Call action direct Intent trigger dialer
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${responder.phone}"))
                                                context.startActivity(intent)
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE8F5E9))
                                                .testTag("call_responder_${responder.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = "Call volunteer",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Stop/Release distress broadcast button
                        OutlinedButton(
                            onClick = {
                                isBroadcasting = false
                                customNotes = ""
                            },
                            border = BorderStroke(1.dp, Color(0xFFC62828)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("resolve_sos_btn")
                        ) {
                            Text(
                                text = if (isBengali) "জরুরি সংকেত সমাধান / বন্ধ করুন" else "I'm Safe Now (Deactivate Alert)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Quick dial standard national medical help support services
            Divider(color = Color(0xFFFFCDD2).copy(alpha = 0.3f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth().testTag("national_dial_row"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBengali) "জাতীয় জরুরি হেল্পলাইন কল দিন" else "Call National Medical Hotlines",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Dial to $nationalEmergencyNumber (Bangladesh Emergency)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$nationalEmergencyNumber"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                        Text(
                            text = if (isBengali) "$nationalEmergencyNumber কল করুন" else "Dial $nationalEmergencyNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Data holder
private data class SimulatedVolunteer(
    val id: String,
    val name: String,
    val phone: String,
    val distance: String,
    val skills: List<String>,
    val rating: Float,
    val status: String = "Standby Defender",
    val accepted: Boolean = false
)

private suspend fun detectCurrentLocationReverseGeocode(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    isBengali: Boolean,
    onLocationDetected: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        val finePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (finePermission != PackageManager.PERMISSION_GRANTED && coarsePermission != PackageManager.PERMISSION_GRANTED) {
            withContext(Dispatchers.Main) {
                onFailure(if (isBengali) "অবস্থান পারমিশন দেওয়া হয়নি!" else "Location permissions are not granted yet!")
            }
            return@withContext
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(context, if (isBengali) Locale("bn", "BD") else Locale.ROOT)
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val addressObj = addresses[0]
                                val subLocality = addressObj.subLocality ?: addressObj.locality ?: addressObj.subAdminArea ?: "Selected Area"
                                val finalName = if (addressObj.locality != null && subLocality != addressObj.locality) {
                                    "$subLocality, ${addressObj.locality}"
                                } else {
                                    subLocality
                                }
                                onLocationDetected(finalName)
                            } else {
                                onLocationDetected("${location.latitude.toString().take(6)}, ${location.longitude.toString().take(6)}")
                            }
                        } catch (e: Exception) {
                            onLocationDetected("${location.latitude.toString().take(6)}, ${location.longitude.toString().take(6)}")
                        }
                    } else {
                        fusedLocationClient.getCurrentLocation(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        ).addOnSuccessListener { freshLocation ->
                            if (freshLocation != null) {
                                try {
                                    val geocoder = Geocoder(context, if (isBengali) Locale("bn", "BD") else Locale.ROOT)
                                    val addresses = geocoder.getFromLocation(freshLocation.latitude, freshLocation.longitude, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val addressObj = addresses[0]
                                        val subLocality = addressObj.subLocality ?: addressObj.locality ?: "Selected Area"
                                        onLocationDetected(subLocality)
                                    } else {
                                        onLocationDetected("${freshLocation.latitude.toString().take(6)}, ${freshLocation.longitude.toString().take(6)}")
                                    }
                                } catch (e: Exception) {
                                    onLocationDetected("${freshLocation.latitude.toString().take(6)}, ${freshLocation.longitude.toString().take(6)}")
                                }
                            } else {
                                onFailure(if (isBengali) "বর্তমান জিপিএস সিগন্যাল পাওয়া যাচ্ছে না!" else "Unable to fetch GPS signals. Please satisfy typing manually.")
                            }
                        }.addOnFailureListener { err ->
                            onFailure(err.localizedMessage ?: "GPS sensor tracking exceeded limit.")
                        }
                    }
                }
                .addOnFailureListener { err ->
                    onFailure(err.localizedMessage ?: "Location sensor tracking failure.")
                }
        } catch (e: SecurityException) {
            withContext(Dispatchers.Main) {
                onFailure(e.localizedMessage ?: "GPS sensor security validation failed.")
            }
        }
    }
}
