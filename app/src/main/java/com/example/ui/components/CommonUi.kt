package com.example.ui.components

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduSyncTopAppBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    showThemeToggle: Boolean = false,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A),
                            Color(0xFF2563EB),
                            Color(0xFF4F46E5)
                        )
                    )
                )
        ) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.3.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFDBEAFE),
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("nav_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    if (showThemeToggle) {
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier.testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = Color(0xFFFDE68A)
                            )
                        }
                    }
                    CompositionLocalProvider(LocalContentColor provides Color.White) {
                        actions()
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.85f), iconColor.copy(alpha = 0.25f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("stat_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            iconBgColor.copy(alpha = 0.18f)
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(iconBgColor, iconColor.copy(alpha = 0.22f))
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun Ultra3DGlassCard(
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF3B82F6),
    glowColor: Color = accentColor,
    containerColor: Color? = null,
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardBg = containerColor ?: MaterialTheme.colorScheme.surface
    val baseModifier = modifier
        .shadow(10.dp, RoundedCornerShape(cornerRadius))
        .border(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    glowColor.copy(alpha = 0.35f),
                    Color(0xFF8B5CF6).copy(alpha = 0.25f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = baseModifier,
            shape = RoundedCornerShape(cornerRadius),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                cardBg,
                                glowColor.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .padding(14.dp),
                content = content
            )
        }
    } else {
        Card(
            modifier = baseModifier,
            shape = RoundedCornerShape(cornerRadius),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                cardBg,
                                glowColor.copy(alpha = 0.06f)
                            )
                        )
                    ),
                content = content
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status.uppercase()) {
        "ACTIVE", "VALID", "PASSED", "EVALUATED", "VERIFIED" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        "BLOCKED", "SUSPENDED", "REVOKED", "CANCELLED", "EXPIRED" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
        "PRESENT" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        "ABSENT" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
        "GRADED", "SCHEDULED", "UPCOMING" -> Color(0xFFEDE9FE) to Color(0xFF6D28D9)
        else -> Color(0xFFF1F5F9) to Color(0xFF475569)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, fg.copy(alpha = 0.25f))
    ) {
        Text(
            text = status,
            color = fg,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Renders either the student's uploaded/captured photo from [photoUri] or
 * a glowing 3D circular initials emblem matching the reference UI.
 */
@Composable
fun StudentPhotoAvatar(
    photoUri: String?,
    fullName: String,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = CircleShape
) {
    val context = LocalContext.current
    val bitmap = remember(photoUri) {
        loadStudentBitmap(context, photoUri)
    }
    val initials = remember(fullName) {
        val parts = fullName.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "ST"
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1D4ED8), Color(0xFF4F46E5), Color(0xFF0EA5E9))
                )
            )
            .border(
                width = 2.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFDE047), Color(0xFF38BDF8), Color.White)
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Photo of $fullName",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
            )
        } else {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = (size.value * 0.34f).sp
            )
        }
    }
}

fun loadStudentBitmap(context: Context, photoUri: String?): Bitmap? {
    if (photoUri.isNullOrBlank() || photoUri.startsWith("avatar_")) return null
    return try {
        val file = File(photoUri)
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            val uri = Uri.parse(photoUri)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }
    } catch (_: Exception) {
        null
    }
}

fun savePickedImageToInternalStorage(context: Context, uri: Uri, prefix: String = "student_photo"): String? {
    return try {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(input)
        input.close()
        if (bitmap == null) return null
        saveBitmapToInternalStorage(context, bitmap, prefix)
    } catch (_: Exception) {
        null
    }
}

fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, prefix: String = "student_photo"): String? {
    return try {
        val dir = File(context.filesDir, "student_photos").apply { mkdirs() }
        val outFile = File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 88, fos)
        }
        outFile.absolutePath
    } catch (_: Exception) {
        null
    }
}

/**
 * Interactive Touch-Screen Student E-Signature Pad for Admission Forms.
 */
@Composable
fun TouchSignaturePad(
    signatureName: String,
    onSignatureChanged: (Boolean, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var typedSignature by remember { mutableStateOf(signatureName) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Draw,
                    contentDescription = null,
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Student Digital E-Signature Pad",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF1E293B)
                )
            }
            TextButton(
                onClick = {
                    paths.clear()
                    currentPoints = emptyList()
                    onSignatureChanged(typedSignature.isNotBlank(), typedSignature)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("Clear Pad", fontSize = 11.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFF94A3B8), RoundedCornerShape(10.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints = currentPoints + change.position
                        },
                        onDragEnd = {
                            if (currentPoints.size > 1) {
                                paths.add(currentPoints)
                                onSignatureChanged(true, typedSignature.ifBlank { "Signed-Digitally" })
                            }
                            currentPoints = emptyList()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (paths.isEmpty() && currentPoints.isEmpty()) {
                Text(
                    text = if (typedSignature.isNotBlank()) typedSignature else "Draw signature here with finger",
                    color = if (typedSignature.isNotBlank()) Color(0xFF1E3A8A).copy(alpha = 0.7f) else Color(0xFF94A3B8),
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    fontSize = if (typedSignature.isNotBlank()) 20.sp else 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val allStrokes = paths + listOf(currentPoints)
                allStrokes.forEach { stroke ->
                    if (stroke.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke.first().x, stroke.first().y)
                            for (i in 1 until stroke.size) {
                                lineTo(stroke[i].x, stroke[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF1E3A8A),
                            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = typedSignature,
            onValueChange = {
                typedSignature = it
                onSignatureChanged(it.isNotBlank() || paths.isNotEmpty(), it)
            },
            label = { Text("Applicant Full Signature Name") },
            placeholder = { Text("Type full name to confirm digital signature") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Official Institute Digital Stamp & Controller Signature block for Certificates, Marksheets, Receipts & ID Cards.
 */
@Composable
fun DigitalStampAndSignature(
    signatoryTitle: String = "Director & Controller of Examinations",
    signatoryName: String = "Dr. R. K. Vishwakarma",
    studentSignature: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Official Circular Institute Stamp
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF))
                    .border(2.dp, Color(0xFF1D4ED8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Official Seal",
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "EDUSYNC",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        "SEAL",
                        fontSize = 6.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
            }
            if (!studentSignature.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = studentSignature,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        "Holder E-Signature",
                        fontSize = 9.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        // Authorized Signatory
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = signatoryName,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color(0xFF1E3A8A)
            )
            HorizontalDivider(
                modifier = Modifier.width(130.dp),
                thickness = 1.dp,
                color = Color(0xFF94A3B8)
            )
            Text(
                text = signatoryTitle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
            Text(
                text = "Digitally Signed & Stamp Verified",
                fontSize = 8.sp,
                color = Color(0xFF15803D),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Shared Settings Dialog with Dark Mode Toggle & Log Out action (Requirement 14).
 */
@Composable
fun PortalSettingsDialog(
    portalName: String = "Portal",
    userName: String = "",
    userRoleLabel: String = "",
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    logoutTestTag: String = "settings_logout_button",
    extraContent: @Composable ColumnScope.() -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("$portalName Settings", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                    Text("$userName • $userRoleLabel", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Dark Mode Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    if (isDarkTheme) "Enabled across app" else "Light theme active",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme() },
                            modifier = Modifier.testTag("settings_dark_mode_switch")
                        )
                    }
                }

                extraContent()

                HorizontalDivider()

                Button(
                    onClick = {
                        onDismiss()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(logoutTestTag)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out of $portalName", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Generates a real multi-page PDF file in internal cache for previewing with Android's PdfRenderer
 * or exporting to user-selected storage (Requirement 6 & 9).
 */
fun buildStudyDocumentPdfFile(
    context: Context,
    title: String,
    subtitle: String,
    courseName: String,
    pagesContent: List<List<String>>
): File {
    val pdfDoc = PdfDocument()
    val titlePaint = Paint().apply {
        color = android.graphics.Color.rgb(30, 58, 138)
        textSize = 16f
        isFakeBoldText = true
    }
    val subPaint = Paint().apply {
        color = android.graphics.Color.rgb(71, 85, 105)
        textSize = 11f
    }
    val bodyPaint = Paint().apply {
        color = android.graphics.Color.rgb(15, 23, 42)
        textSize = 11.5f
    }
    val sealPaint = Paint().apply {
        color = android.graphics.Color.rgb(21, 128, 61)
        textSize = 10f
        isFakeBoldText = true
    }

    val safePages = if (pagesContent.isEmpty()) {
        listOf(listOf("Official EduSync Academy Study Material", title, subtitle))
    } else {
        pagesContent
    }

    safePages.forEachIndexed { index, lines ->
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, index + 1).create()
        val page = pdfDoc.startPage(pageInfo)
        val canvas = page.canvas

        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawText("EDUSYNC ACADEMY • OFFICIAL DIGITAL MATERIAL", 40f, 45f, subPaint)
        canvas.drawText(title.take(55), 40f, 70f, titlePaint)
        canvas.drawText("$courseName • $subtitle (Page ${index + 1} of ${safePages.size})", 40f, 90f, subPaint)

        var y = 125f
        lines.forEach { paragraph ->
            val words = paragraph.split(" ")
            var currentLine = StringBuilder()
            for (word in words) {
                if (currentLine.length + word.length + 1 > 78) {
                    canvas.drawText(currentLine.toString(), 40f, y, bodyPaint)
                    y += 18f
                    currentLine = StringBuilder(word)
                } else {
                    if (currentLine.isNotEmpty()) currentLine.append(" ")
                    currentLine.append(word)
                }
            }
            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine.toString(), 40f, y, bodyPaint)
                y += 22f
            }
        }

        canvas.drawText(
            "Digitally Signed & Stamp Verified • Controller of Examinations • EduSync Academy",
            40f,
            800f,
            sealPaint
        )
        pdfDoc.finishPage(page)
    }

    val dir = File(context.cacheDir, "in_app_pdfs").apply { mkdirs() }
    val safeName = title.replace("[^a-zA-Z0-9_-]".toRegex(), "_").take(35)
    val outFile = File(dir, "${safeName}_preview.pdf")
    FileOutputStream(outFile).use { fos ->
        pdfDoc.writeTo(fos)
    }
    pdfDoc.close()
    return outFile
}

fun savePdfFileToMediaStoreDownloads(
    context: Context,
    sourceFile: File,
    displayFileName: String
): Pair<Boolean, String> {
    return try {
        val cleanName = if (displayFileName.endsWith(".pdf", ignoreCase = true)) {
            displayFileName
        } else {
            "$displayFileName.pdf"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, cleanName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/EduSyncAcademy")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { out ->
                    sourceFile.inputStream().use { input -> input.copyTo(out) }
                }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                return true to "Saved to Downloads/EduSyncAcademy/$cleanName"
            }
        }
        val fallbackDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val destFile = File(fallbackDir, cleanName)
        sourceFile.copyTo(destFile, overwrite = true)
        true to "Saved to ${destFile.absolutePath}"
    } catch (e: Exception) {
        false to "Could not save file: ${e.localizedMessage ?: "Storage error"}"
    }
}

/**
 * In-App PDF & E-Book Document Viewer Dialog (Requirement 6).
 * Renders real PDF pages via Android's PdfRenderer without auto-starting downloads on preview,
 * and offers both "Save to Device Picker" and "Save to Downloads" options.
 */
@Composable
fun InAppPdfDocumentViewerDialog(
    title: String,
    subtitle: String,
    courseName: String = "EduSync Academy",
    fileSize: String = "2.5 MB",
    category: String = "PDF",
    contentBody: String = "",
    fileNamePrefix: String = "",
    pagesContent: List<List<String>> = listOf(
        listOf(
            "Document Category: $category",
            title,
            subtitle,
            contentBody.ifBlank { "Official EduSync Academy Digital Study Resource." }
        )
    ),
    onDismiss: () -> Unit,
    onExportConfirmed: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var currentPageIndex by remember { mutableIntStateOf(0) }
    var statusBanner by remember { mutableStateOf<String?>(null) }

    val pdfFile = remember(title, pagesContent) {
        buildStudyDocumentPdfFile(context, title, subtitle, courseName, pagesContent)
    }

    val renderedBitmap by produceState<Bitmap?>(initialValue = null, pdfFile, currentPageIndex) {
        value = try {
            val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val safeIndex = currentPageIndex.coerceIn(0, (renderer.pageCount - 1).coerceAtLeast(0))
            val page = renderer.openPage(safeIndex)
            val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
            bmp.eraseColor(android.graphics.Color.WHITE)
            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            renderer.close()
            pfd.close()
            bmp
        } catch (_: Exception) {
            null
        }
    }

    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { targetUri: Uri? ->
        if (targetUri != null) {
            try {
                context.contentResolver.openOutputStream(targetUri)?.use { out ->
                    pdfFile.inputStream().use { input -> input.copyTo(out) }
                }
                val msg = "Exported '$title.pdf' to your selected folder!"
                statusBanner = msg
                onExportConfirmed(msg)
            } catch (e: Exception) {
                statusBanner = "Export failed: ${e.localizedMessage}"
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF4F46E5))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "IN-APP PDF & E-BOOK READER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF93C5FD)
                            )
                            Text(
                                title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "$subtitle • $fileSize • Preview Mode (No Auto-Download)",
                                fontSize = 11.sp,
                                color = Color(0xFFDBEAFE)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close Reader", tint = Color.White)
                        }
                    }
                }

                if (statusBanner != null) {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                statusBanner!!,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Page Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentPageIndex > 0) currentPageIndex-- },
                        enabled = currentPageIndex > 0,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("← Prev Page", fontSize = 11.sp)
                    }
                    Text(
                        "Page ${currentPageIndex + 1} of ${pagesContent.size.coerceAtLeast(1)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    OutlinedButton(
                        onClick = { if (currentPageIndex < pagesContent.size - 1) currentPageIndex++ },
                        enabled = currentPageIndex < pagesContent.size - 1,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Next Page →", fontSize = 11.sp)
                    }
                }

                // Document Content Viewport
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (renderedBitmap != null) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                bitmap = renderedBitmap!!.asImageBitmap(),
                                contentDescription = "PDF Page ${currentPageIndex + 1}",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Selectable/searchable text companion card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Transcript — Page ${currentPageIndex + 1}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            val activeLines = pagesContent.getOrElse(currentPageIndex) { emptyList() }
                            activeLines.forEach { line ->
                                Text(line, fontSize = 13.sp, lineHeight = 19.sp)
                            }
                        }
                    }
                }

                // Bottom Export / Save Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val cleanFileName = title.replace("[^a-zA-Z0-9_-]".toRegex(), "_") + ".pdf"
                            createDocLauncher.launch(cleanFileName)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pdf_viewer_choose_location_btn")
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save As...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val (ok, msg) = savePdfFileToMediaStoreDownloads(context, pdfFile, title)
                            statusBanner = msg
                            if (ok) onExportConfirmed(msg)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pdf_viewer_save_downloads_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save to Downloads", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * High-definition QR Code matrix simulation drawn via Canvas.
 * Generates official finder patterns and data dots.
 */
@Composable
fun QrCodeView(
    dataString: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    foregroundColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = this.size.width
            val cells = 21
            val cellSize = canvasWidth / cells

            fun drawFinder(topRow: Int, leftCol: Int) {
                drawRect(
                    color = foregroundColor,
                    topLeft = Offset(leftCol * cellSize, topRow * cellSize),
                    size = Size(7 * cellSize, 7 * cellSize)
                )
                drawRect(
                    color = backgroundColor,
                    topLeft = Offset((leftCol + 1) * cellSize, (topRow + 1) * cellSize),
                    size = Size(5 * cellSize, 5 * cellSize)
                )
                drawRect(
                    color = foregroundColor,
                    topLeft = Offset((leftCol + 2) * cellSize, (topRow + 2) * cellSize),
                    size = Size(3 * cellSize, 3 * cellSize)
                )
            }

            drawFinder(0, 0)
            drawFinder(0, cells - 7)
            drawFinder(cells - 7, 0)

            val hash = dataString.hashCode()
            val random = java.util.Random(hash.toLong())

            for (r in 0 until cells) {
                for (c in 0 until cells) {
                    val inFinderTopLeft = r < 7 && c < 7
                    val inFinderTopRight = r < 7 && c >= cells - 7
                    val inFinderBottomLeft = r >= cells - 7 && c < 7
                    if (!inFinderTopLeft && !inFinderTopRight && !inFinderBottomLeft) {
                        if (random.nextBoolean()) {
                            drawRect(
                                color = foregroundColor,
                                topLeft = Offset(c * cellSize, r * cellSize),
                                size = Size(cellSize * 0.9f, cellSize * 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}
