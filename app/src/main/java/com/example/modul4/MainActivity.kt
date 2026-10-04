package com.example.modul4 // Sesuaikan dengan nama package proyek Anda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modul4.ui.theme.Modul4Theme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Modul4Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TicketScreenParent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// 1. Parent Composable (Mengelola State Utama)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreenParent(modifier: Modifier = Modifier) {
    // State yang dikelola oleh Parent (State Hoisting)
    var namaPembeli by rememberSaveable { mutableStateOf("") }
    var jumlahTiket by rememberSaveable { mutableStateOf(1) }

    // Status kondisi UI: "initial", "processing", "success", atau "error"
    var statusState by rememberSaveable { mutableStateOf("initial") }
    var isProcessing by rememberSaveable { mutableStateOf(false) }

    // LaunchedEffect untuk menangani proses async (simulasi loading 2 detik & validasi)
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            if (namaPembeli.isBlank()) {
                statusState = "error"
                isProcessing = false
            } else {
                statusState = "processing"
                delay(2000) // Proses selama 2 detik sesuai gambar referensi
                statusState = "success"
                isProcessing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pemesanan Tiket") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        // Memanggil Child Composable yang bersifat Stateless
        TicketScreenContent(
            modifier = Modifier.padding(paddingValues),
            nama = namaPembeli,
            onNamaChanged = {
                namaPembeli = it
                if (statusState == "error") statusState = "initial" // Reset error saat mengetik
            },
            jumlahTiket = jumlahTiket,
            onAddTiket = { jumlahTiket++ },
            onMinTiket = { if (jumlahTiket > 1) jumlahTiket-- },
            statusState = statusState,
            isProcessing = isProcessing,
            onPesanClicked = { isProcessing = true }
        )
    }
}

// 2. Child Composable (Stateless UI untuk Tampilan)
@Composable
fun TicketScreenContent(
    modifier: Modifier = Modifier,
    nama: String,
    onNamaChanged: (String) -> Unit,
    jumlahTiket: Int,
    onAddTiket: () -> Unit,
    onMinTiket: () -> Unit,
    statusState: String,
    isProcessing: Boolean,
    onPesanClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Nama
        OutlinedTextField(
            value = nama,
            onValueChange = onNamaChanged,
            label = { Text("Nama") },
            placeholder = { Text("Masukkan nama Anda") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing
        )

        // Pengaturan Jumlah Tiket
        Text(text = "Jumlah Tiket", fontSize = 16.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onMinTiket,
                enabled = !isProcessing
            ) {
                Text("-")
            }
            Text(text = "$jumlahTiket", fontSize = 20.sp)
            Button(
                onClick = onAddTiket,
                enabled = !isProcessing
            ) {
                Text("+")
            }
        }

        // Tombol Pesan Tiket
        Button(
            onClick = onPesanClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isProcessing
        ) {
            Text("Pesan Tiket")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kotak Status Berdasarkan 4 Kondisi di Mockup
        when (statusState) {
            "initial" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Status: Silakan pesan tiket", fontSize = 16.sp)
                    }
                }
            }
            "processing" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text(text = "Status: Memproses pemesanan...", fontSize = 16.sp)
                    }
                }
            }
            "success" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA)) // Nuansa hijau sukses
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "✔", color = Color(0xFF155724), fontSize = 18.sp)
                        Text(text = "Status: Tiket berhasil dipesan!", fontSize = 16.sp, color = Color(0xFF155724))
                    }
                }
            }
            "error" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8D7DA)) // Nuansa merah error
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "!", color = Color(0xFF721C24), fontSize = 18.sp)
                        Text(text = "Status: Nama harus diisi", fontSize = 16.sp, color = Color(0xFF721C24))
                    }
                }
            }
        }
    }
}