package com.example.todoapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {
    // Função para verificar se o dispositivo está conectado à internet
    fun isOnline(context: Context): Boolean {
        // Obtém o serviço de conectividade do sistema (ConnectivityManager)
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Obtém a rede atualmente ativa ou retorna 'false' se não houver rede ativa
        val network = connectivityManager.activeNetwork ?: return false
        // Obtém as capacidades da rede ativa (ex: se suporta Wi-Fi, celular, etc.)
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        // Verifica os tipos de transporte suportados pela rede ativa e retorna 'true' se for Wi-Fi, celular ou Ethernet
        return when {
            // Verifica se a rede é Wi-Fi
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            // Verifica se a rede é de dados móveis
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            // Verifica se a rede é via Ethernet
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            // Caso contrário, retorna 'false', indicando que o dispositivo está offline
            else -> false
        }
    }
}
