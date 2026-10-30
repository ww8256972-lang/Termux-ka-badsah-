package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.AdminAccountEntity
import com.example.data.local.entity.ParentGuardianEntity
import com.example.data.local.entity.StudentEntity
import com.example.data.local.entity.TeacherEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class RequirePasswordChange(val username: String) : AuthResult<Nothing>()
    data class AccountLocked(val remainingSeconds: Long) : AuthResult<Nothing>()
    data class AccountBlocked(val reason: String) : AuthResult<Nothing>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository(private val db: AppDatabase) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private suspend fun createSession(uid: String) {
        val deviceId = UUID.randomUUID().toString()
        val sessionData = hashMapOf(
            "deviceId" to deviceId,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("users").document(uid).collection("sessions").document(deviceId).set(sessionData).await()
    }

    suspend fun loginAdmin(username: String, rawPassword: String): AuthResult<AdminAccountEntity> = withContext(Dispatchers.IO) {
        try {
            val email = "${username.trim().lowercase()}@edusync.admin.test"
            val result = auth.signInWithEmailAndPassword(email, rawPassword).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")

            // Note: in a real prod system we would create a user in Firebase Auth if it didn't exist for demo purposes.
            // For now, since we MUST NOT use fallback logic to Room, we just query Firestore.

            createSession(uid)
            val doc = firestore.collection("admin_accounts").document(username).get().await()
            if (!doc.exists()) {
                return@withContext AuthResult.Success(AdminAccountEntity(username = username, passwordHash = ""))
            }
            AuthResult.Success(AdminAccountEntity(
                username = doc.id,
                passwordHash = doc.getString("passwordHash") ?: ""
            ))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Invalid credentials")
        }
    }

    suspend fun changeAdminPassword(username: String = "admin", newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser
            user?.updatePassword(newPassword)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginTeacher(username: String, rawPassword: String): AuthResult<TeacherEntity> = withContext(Dispatchers.IO) {
        try {
            val email = "${username.trim().lowercase()}@edusync.teacher.test"
            val result = auth.signInWithEmailAndPassword(email, rawPassword).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")

            createSession(uid)
            AuthResult.Success(TeacherEntity(
                teacherId = username,
                username = username,
                passwordHash = "",
                fullName = username,
                email = email,
                phone = "",
                specialization = ""
            ))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Invalid teacher credentials")
        }
    }

    suspend fun loginStudent(usernameOrId: String, rawPassword: String): AuthResult<StudentEntity> = withContext(Dispatchers.IO) {
        try {
            val email = "${usernameOrId.trim().lowercase()}@edusync.student.test"
            val result = auth.signInWithEmailAndPassword(email, rawPassword).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")

            createSession(uid)
            AuthResult.Success(StudentEntity(
                studentId = usernameOrId,
                username = usernameOrId,
                passwordHash = "",
                fullName = usernameOrId,
                email = email,
                phone = "",
                courseId = "",
                courseName = ""
            ))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Invalid student credentials")
        }
    }

    suspend fun loginGuardian(username: String, rawPassword: String): AuthResult<ParentGuardianEntity> = withContext(Dispatchers.IO) {
        try {
            val email = "${username.trim().lowercase()}@edusync.guardian.test"
            val result = auth.signInWithEmailAndPassword(email, rawPassword).await()
            val uid = result.user?.uid ?: throw Exception("Auth failed")

            createSession(uid)
            AuthResult.Success(ParentGuardianEntity(
                guardianId = username,
                username = username,
                passwordHash = "",
                fullName = username,
                email = email,
                phone = "",
                linkedStudentId = ""
            ))
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Invalid guardian credentials")
        }
    }
}
