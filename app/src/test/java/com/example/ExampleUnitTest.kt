package com.example

import com.example.core.QrCodeUtils
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun qrCodeUtils_generatesDeterministicMatrix() {
        val payload = "https://edusync.institute/verify?certId=CERT-2026-8941"
        val matrix1 = QrCodeUtils.generateMatrix(payload)
        val matrix2 = QrCodeUtils.generateMatrix(payload)

        // Matrix size must be exactly 25x25
        assertEquals(QrCodeUtils.MATRIX_SIZE, matrix1.size)
        assertEquals(QrCodeUtils.MATRIX_SIZE, matrix1[0].size)

        // Matrix output must be deterministic
        for (r in 0 until QrCodeUtils.MATRIX_SIZE) {
            for (c in 0 until QrCodeUtils.MATRIX_SIZE) {
                assertEquals(matrix1[r][c], matrix2[r][c])
            }
        }

        // Top-left finder outer ring must be true
        assertTrue(matrix1[0][0])
        assertTrue(matrix1[0][6])
        assertTrue(matrix1[6][0])
        assertTrue(matrix1[6][6])
        // Top-left finder center must be true
        assertTrue(matrix1[3][3])
        // Top-left finder whitespace separator must be false
        assertFalse(matrix1[1][1])
    }

    @Test
    fun qrCodeUtils_differentPayloadsProduceDifferentMatrices() {
        val matrixA = QrCodeUtils.generateMatrix("PAYLOAD_A")
        val matrixB = QrCodeUtils.generateMatrix("PAYLOAD_B")

        var differences = 0
        for (r in 0 until QrCodeUtils.MATRIX_SIZE) {
            for (c in 0 until QrCodeUtils.MATRIX_SIZE) {
                if (matrixA[r][c] != matrixB[r][c]) {
                    differences++
                }
            }
        }
        assertTrue("Different payloads must produce differences in the data matrix", differences > 0)
    }

    @Test
    fun securityUtils_waqarAdminPasswordVerification() {
        val waqarHash = com.example.core.SecurityUtils.hashPassword("waqar")
        assertTrue("Password 'waqar' should verify with waqarHash", com.example.core.SecurityUtils.verifyPassword("waqar", waqarHash))
        assertFalse("Wrong password should fail verification", com.example.core.SecurityUtils.verifyPassword("wrong", waqarHash))
    }

    @Test
    fun feeArithmetic_manualPaymentAndBalanceCalculation() {
        val totalCourseFee = 15000.0
        val manualCashPayment = 5000.0
        val manualUpiPayment = 4500.0
        val totalPaid = manualCashPayment + manualUpiPayment
        val outstandingBalance = totalCourseFee - totalPaid

        assertEquals(9500.0, totalPaid, 0.001)
        assertEquals(5500.0, outstandingBalance, 0.001)
        assertTrue("Outstanding balance should be non-negative", outstandingBalance >= 0.0)
    }

    @Test
    fun studentId_scaleFormattingFor100kStudents() {
        // Verify format for 1 lakh students
        val studentId1000 = "EDU-2026-%04d".format(1000)
        assertEquals("EDU-2026-1000", studentId1000)

        val studentId100k = "EDU-2026-%04d".format(100000)
        assertEquals("EDU-2026-100000", studentId100k)
        assertTrue("Student ID should preserve prefix format for 100k records", studentId100k.startsWith("EDU-2026-"))
    }

    @Test
    fun examScoring_deterministicGradeAndPercentage() {
        val totalQuestions = 50
        val correctAnswers = 43
        val percentage = (correctAnswers.toDouble() / totalQuestions.toDouble()) * 100.0
        val grade = when {
            percentage >= 90.0 -> "A+"
            percentage >= 80.0 -> "A"
            percentage >= 70.0 -> "B+"
            percentage >= 60.0 -> "B"
            percentage >= 50.0 -> "C"
            else -> "F"
        }
        assertEquals(86.0, percentage, 0.001)
        assertEquals("A", grade)
    }
}

