package kg.jarnama.ozgon.database

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class Functions {
    val random = SecureRandom()
    val salt: ByteArray = "worldisenemy".toByteArray()

    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    fun isExpectedPassword(password: String, expectedHash: ByteArray): Boolean {
        val pwdHash = hash(password)
        if (pwdHash.size != expectedHash.size) return false
        return pwdHash.indices.all { pwdHash[it] == expectedHash[it] }
    }

    fun hash(password: String): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, 1000, 256)
        try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            return skf.generateSecret(spec).encoded
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError("Error while hashing a password: " + e.message, e)
        } catch (e: InvalidKeySpecException) {
            throw AssertionError("Error while hashing a password: " + e.message, e)
        } finally {
            spec.clearPassword()
        }
    }
    companion object{
        fun generateToken(length: Long): String {
            val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz"
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }
        fun it(): Functions {
            return Functions()
        }
    }
}