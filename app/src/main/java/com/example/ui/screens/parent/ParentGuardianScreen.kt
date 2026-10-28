package com.example.ui.screens.parent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.*
import com.example.data.repository.InstituteRepository
import com.example.ui.components.AnimatedTabIconHalo
import com.example.ui.components.EduSyncTopAppBar
import com.example.ui.components.FeeReceiptCard
import com.example.ui.components.PortalSettingsDialog
import com.example.ui.components.StatusBadge
import com.example.ui.components.StudentPhotoAvatar
import com.example.ui.components.Ultra3DGlassCard
import com.example.ui.components.pullDownTabInteraction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ParentGuardianScreen(
    guardian: ParentGuardianEntity,
    instituteRepository: InstituteRepository,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var linkedStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val attendanceList by instituteRepository.getAttendanceForStudent(guardian.linkedStudentId).collectAsState(initial = emptyList())
    val feeRecords by instituteRepository.getFeesForStudent(guardian.linkedStudentId).collectAsState(initial = emptyList())
    val examResults by instituteRepository.getResultsForStudent(guardian.linkedStudentId).collectAsState(initial = emptyList())
    val notices by instituteRepository.getNotices().collectAsState(initial = emptyList())

    LaunchedEffect(guardian.linkedStudentId) {
        linkedStudent = instituteRepository.getStudentById(guardian.linkedStudentId)
    }

    val batchTimetables by if (linkedStudent != null) {
        instituteRepository.getTimetablesByBatch(linkedStudent!!.batch).collectAsState(initial = emptyList())
    } else {
        instituteRepository.getAllTimetables().collectAsState(initial = emptyList())
    }

    Scaffold(
        topBar = {
            EduSyncTopAppBar(
                title = "Parent Portal",
                subtitle = "EduSync Academy • Stay Connected • Support Their Future",
                showThemeToggle = true,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                actions = {
                    // Requirement 14: Logout removed from top bar and placed inside Settings
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("parent_settings_btn")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .shadow(12.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp)),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        AnimatedTabIconHalo(selected = selectedTab == 0) {
                            Icon(Icons.Default.PersonSearch, contentDescription = "Overview")
                        }
                    },
                    label = { Text("Ward Overview", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.pullDownTabInteraction(selected = selectedTab == 0).testTag("parent_tab_overview")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        AnimatedTabIconHalo(selected = selectedTab == 1) {
                            Icon(Icons.Default.Payments, contentDescription = "Fees")
                        }
                    },
                    label = { Text("Fee Ledger", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.pullDownTabInteraction(selected = selectedTab == 1).testTag("parent_tab_fees")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        AnimatedTabIconHalo(selected = selectedTab == 2) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Timetable")
                        }
                    },
                    label = { Text("Timetable", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.pullDownTabInteraction(selected = selectedTab == 2).testTag("parent_tab_timetable")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        AnimatedTabIconHalo(selected = selectedTab == 3) {
                            Icon(Icons.Default.Campaign, contentDescription = "Notices")
                        }
                    },
                    label = { Text("Notices", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.pullDownTabInteraction(selected = selectedTab == 3).testTag("parent_tab_notices")
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                0 -> ParentWardOverviewTab(linkedStudent, attendanceList, examResults)
                1 -> ParentFeeLedgerTab(linkedStudent, feeRecords)
                2 -> ParentTimetableTab(linkedStudent, batchTimetables)
                3 -> ParentNoticesTab(notices)
            }
        }
    }

    if (showSettingsDialog) {
        PortalSettingsDialog(
            portalName = "Parent Portal",
            userName = guardian.fullName,
            userRoleLabel = "${guardian.relationship} of ${linkedStudent?.fullName ?: guardian.linkedStudentId}",
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            onDismiss = { showSettingsDialog = false },
            onLogout = onLogout,
            extraContent = {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Guardian Account Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Guardian ID: ${guardian.guardianId}", fontSize = 12.sp)
                        Text("Linked Ward ID: ${guardian.linkedStudentId}", fontSize = 12.sp)
                        Text("Registered Phone: ${guardian.phone}", fontSize = 12.sp)
                    }
                }
            }
        )
    }
}

@Composable
private fun ParentWardOverviewTab(
    student: StudentEntity?,
    attendance: List<AttendanceEntity>,
    results: List<ExamResultEntity>
) {
    if (student == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val totalDays = attendance.size.coerceAtLeast(1)
    val presentDays = attendance.count { it.status == "PRESENT" }
    val absentDays = attendance.count { it.status == "ABSENT" }
    val attendancePct = (presentDays * 100) / totalDays
    val feeBalance = (student.totalCourseFee - student.feePaid).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 3D Glowing Student Profile Header Card (Reference Image 2)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(14.dp, RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF93C5FD), Color(0xFFC084FC), Color(0xFF38BDF8))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E3A8A),
                                Color(0xFF2563EB),
                                Color(0xFF4F46E5)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudentPhotoAvatar(
                        photoUri = student.photoUri,
                        fullName = student.fullName,
                        size = 66.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                student.fullName,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            StatusBadge(student.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Roll No: ${student.rollNo.ifBlank { "101" }} • ID: ${student.studentId}",
                            fontSize = 12.sp,
                            color = Color(0xFFBFDBFE),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Course: ${student.courseName}",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Batch: ${student.batch}",
                            fontSize = 11.sp,
                            color = Color(0xFFDBEAFE)
                        )
                    }
                }
            }
        }

        // Attendance Record 3D Glass Card (Reference Image 2)
        Ultra3DGlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = Color(0xFF16A34A)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Attendance Record",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Surface(
                        color = if (attendancePct >= 75) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (attendancePct >= 75) Color(0xFF22C55E) else Color(0xFFEF4444)
                        )
                    ) {
                        Text(
                            "$attendancePct%",
                            color = if (attendancePct >= 75) Color(0xFF15803D) else Color(0xFFB91C1C),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Working Days: ${attendance.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "Days Present: $presentDays",
                            fontSize = 13.sp,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        "Days Absent: $absentDays",
                        fontSize = 13.sp,
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { attendancePct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = if (attendancePct >= 75) Color(0xFF16A34A) else Color(0xFFDC2626),
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }

        // Fee Account Summary 3D Glass Card (Reference Image 2)
        Ultra3DGlassCard(
            modifier = Modifier.fillMaxWidth(),
            accentColor = Color(0xFF2563EB)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Fee Account Summary", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Paid Amount", fontSize = 12.sp, color = Color(0xFF166534), fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "₹${"%.0f".format(student.feePaid)}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF15803D),
                                fontSize = 22.sp
                            )
                        }
                    }

                    Surface(
                        color = if (feeBalance > 0) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            if (feeBalance > 0) Color(0xFFFECACA) else Color(0xFFBBF7D0)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (feeBalance > 0) Icons.Default.ErrorOutline else Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = if (feeBalance > 0) Color(0xFFDC2626) else Color(0xFF16A34A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Pending Due",
                                    fontSize = 12.sp,
                                    color = if (feeBalance > 0) Color(0xFF991B1B) else Color(0xFF166534),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "₹${"%.0f".format(feeBalance)}",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (feeBalance > 0) Color(0xFFB91C1C) else Color(0xFF15803D),
                                fontSize = 22.sp
                            )
                        }
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Total Fee: ₹${"%.0f".format(student.totalCourseFee)} • Next Installment Due Date: ${student.feeDueDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Academic Results & Grades Card (Reference Image 2)
        Text("Academic Results & Grades", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        if (results.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("No exam results published yet.", modifier = Modifier.padding(16.dp), color = Color.Gray)
            }
        } else {
            results.forEach { r ->
                Ultra3DGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = Color(0xFFD97706)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(r.examName, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text("Exam Date: ${r.examDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "Marks: ${"%.0f".format(r.obtainedMarks)} / ${"%.0f".format(r.totalMarks)} (Rank #${r.rank})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Surface(
                                color = Color(0xFFFEF9C3),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFFACC15))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFFB45309),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${r.grade} (${"%.1f".format(r.percentage)}%)",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF92400E),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Status: ${if (r.percentage >= 40.0) "PASSED" else "NEEDS IMPROVEMENT"}",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentFeeLedgerTab(
    student: StudentEntity?,
    feeRecords: List<FeeRecordEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Official Fee Receipts & Payment History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
            Text(
                "Download, open, or share official QR-verified PDF receipts for all payments.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (feeRecords.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("No fee payment records found.", modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            items(feeRecords) { record ->
                FeeReceiptCard(
                    record = record,
                    studentName = student?.fullName ?: ""
                )
            }
        }
    }
}

/**
 * Requirement 10: Parent Portal strictly Read-Only Timetable Access.
 * Parents can view their child's real-time batch schedule but cannot create, edit, or delete entries.
 */
@Composable
private fun ParentTimetableTab(
    student: StudentEntity?,
    timetables: List<TimetableEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Ward Class & Lab Schedule",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        "Batch: ${student?.batch ?: "Assigned Batch"} • Live Synchronized",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Surface(
                    color = Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "READ-ONLY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (timetables.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("No timetable entries scheduled for this batch.", modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            items(timetables) { item ->
                Ultra3DGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.width(88.dp)
                        ) {
                            Text(
                                text = item.dayOfWeek,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D4ED8),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.subject, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text("Instructor: ${item.teacherName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${item.startTime} - ${item.endTime} • ${item.roomOrLab}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentNoticesTab(notices: List<NoticeEntity>) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Institute Announcements & Exam Circulars",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        items(notices) { notice ->
            Ultra3DGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = Color(0xFFDBEAFE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = notice.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = notice.date,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(notice.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notice.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
