package com.example.tugasakhirprogmob

import android.content.Context
import android.content.SharedPreferences

class SearchHistoryRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)

    private val historyKey = "search_history"
    private val maxHistorySize = 10 // Batasi jumlah riwayat yang disimpan

    fun getSearchHistory(): List<String> {
        // Mengambil data sebagai Set, lalu mengubahnya menjadi List
        return prefs.getStringSet(historyKey, emptySet())?.toList()?.reversed() ?: emptyList()
    }

    fun addSearchTerm(term: String) {
        if (term.isBlank()) return // Jangan simpan query kosong

        val currentHistory = getSearchHistory().toMutableList()
        // Hapus jika sudah ada agar bisa dipindahkan ke paling atas (terbaru)
        currentHistory.remove(term)
        // Tambahkan term baru ke posisi paling atas (indeks 0)
        currentHistory.add(0, term)

        // Batasi ukuran riwayat
        val trimmedHistory = if (currentHistory.size > maxHistorySize) {
            currentHistory.subList(0, maxHistorySize)
        } else {
            currentHistory
        }

        // Simpan kembali ke SharedPreferences sebagai Set
        prefs.edit().putStringSet(historyKey, trimmedHistory.toSet()).apply()
    }
}