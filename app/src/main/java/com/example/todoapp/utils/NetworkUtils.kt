package com.example.todoapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {
    // Função para verificar se o dispositivo está online
    fun isOnline(context: Context): Boolean {
        // Obtém o serviço de conectividade do sistema
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Obtém a rede ativa
        val network = connectivityManager.activeNetwork ?: return false
        // Obtém as capacidades da rede ativa
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        // Verifica os tipos de rede (Wi-Fi, celular, Ethernet)
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
