package com.example.track_my_pregnancy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.track_my_pregnancy.ViewModel.PregnancyViewModel
import com.example.track_my_pregnancy.ViewModel.Repository
import com.example.track_my_pregnancy.roomdb.Pregnancy
import com.example.track_my_pregnancy.roomdb.PregnancyDatabase
import com.example.track_my_pregnancy.ui.theme.Track_My_PregnancyTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            PregnancyDatabase::class.java,
            "Pregnancy.db"
        ).build()
    }

    val viewModel by viewModels<PregnancyViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PregnancyViewModel(Repository(db)) as T
                }
            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Track_My_PregnancyTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PregnancyViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val pregnancyData by viewModel.getAllPregnancy().observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track My Pregnancy", color = Color(0xFF5F1C9C)) }, // Updated text color
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFC8ADFC), // Updated background color
                    titleContentColor = Color(0xFF5F1C9C) // Ensuring title color is also applied
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(16.dp),
                shape = CircleShape,
                containerColor = Color(0xFF9C4DB9),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(pregnancyData) { pregnancy ->
                    PregnancyCard(pregnancy)
                }
            }
        }
    }

    if (showDialog) {
        AddVitalsDialog(
            onDismiss = { showDialog = false },
            onSave = { sysBP, diaBP, weight, babyKicks ->
                val pregnancy = Pregnancy(sysBP, diaBP, weight, babyKicks)
                viewModel.upsertPregnancy(pregnancy)
                showDialog = false
            }
        )
    }
}

@Composable
fun PregnancyCard(pregnancy: Pregnancy) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1A3FF)), // Light purple background
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp) // Wider card
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp) // Inner padding
            ) {
                // First Row: Sys BP & Dia BP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Heart Rate", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${pregnancy.sysBP} bpm", color = Color(0xFF5F1C9C))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "BP", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${pregnancy.diaBP} mmHg", color = Color(0xFF5F1C9C))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second Row: Weight & Baby Kicks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bloodtype, contentDescription = "Weight", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${pregnancy.weight} kg", color = Color(0xFF5F1C9C))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = "Kicks", tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${pregnancy.babyKicks} kicks", color = Color(0xFF5F1C9C))
                    }
                }
            }

            // Colored Bar for Date & Time
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF9F58B9)) // Dark purple bar
                    .padding(vertical = 6.dp) // Padding inside the bar
                    .height(32.dp)
            ) {
                Text(
                    text = formatDate(pregnancy.timestamp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp), // Increase text size to 18sp
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End)
                )

            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun AddVitalsDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var sysBP by remember { mutableStateOf("") }
    var diaBP by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var babyKicks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Vitals") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    VitalsTextField(
                        label = "Sys BP", value = sysBP,
                        onValueChange = { sysBP = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    VitalsTextField(
                        label = "Dia BP", value = diaBP,
                        onValueChange = { diaBP = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                VitalsTextField(
                    label = "Weight (kg)", value = weight,
                    onValueChange = { weight = it } // Ensure this is provided
                )
                VitalsTextField(
                    label = "Baby Kicks", value = babyKicks,
                    onValueChange = { babyKicks = it } // Ensure this is provided
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(sysBP, diaBP, weight, babyKicks)
                    onDismiss()
                }
            ) {
                Text("Submit")
            }
        }
    )
}

@Composable
fun VitalsTextField(
    label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
    )
}