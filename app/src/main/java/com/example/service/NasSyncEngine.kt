package com.example.service

import com.example.data.PerpusRepository

class NasSyncEngine(private val repository: PerpusRepository) {

    var nasUrl: String = "http://192.168.1.100:8080/perpusai-nas"
        private set

    suspend fun performSync(): String {
        return try {
            // Simulasi sinkronisasi dengan NAS
            kotlinx.coroutines.delay(1200)
            val msg = "Berhasil menyinkronkan 15 buku & database dengan NAS Sekolah ($nasUrl)"
            repository.addSyncLog("BERHASIL", msg)
            msg
        } catch (e: Exception) {
            val err = "Gagal menyambung ke NAS: ${e.message}"
            repository.addSyncLog("GAGAL", err)
            err
        }
    }
}
