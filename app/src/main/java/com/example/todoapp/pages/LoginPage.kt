package com.example.todoapp.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todoapp.AuthViewModel
import com.example.todoapp.State

@Composable
fun LoginPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    // Variáveis de estado para armazenar o e-mail e a senha do usuário
    // Estado do campo de e-mail
    var email by remember { mutableStateOf("") }
    // Estado do campo de senha
    var password by remember { mutableStateOf("") }
    // Estado de autenticação observado do ViewModel
    // Observa o estado de autenticação
    val authState by authViewModel.authState.collectAsState()
    // Contexto local para exibir mensagens Toast
    val context = LocalContext.current
    // Efeito lançado quando o estado de autenticação muda
    LaunchedEffect(authState) {
        when (authState) {
            is State.Authenticated ->
                // Se autenticado, navega para a página inicial
                navController.navigate("home/${authViewModel.currentUser?.uid}")
            is State.Error ->
                // Se houver um erro, exibe uma mensagem Toast
                Toast.makeText(context, (authState as State.Error).message, Toast.LENGTH_SHORT).show()
            // Caso não haja mudança relevante, faz nada
            else -> Unit
        }
    }
    // Layout da página de login
    Column(
        // Preenche toda a tela e aplica padding
        modifier = modifier.fillMaxSize().padding(16.dp),
        // Centraliza verticalmente
        verticalArrangement = Arrangement.Center,
        // Centraliza horizontalmente
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título da página
        Text(text = "Login", style = MaterialTheme.typography.headlineLarge)
        // Espaçamento entre os elementos
        Spacer(modifier = Modifier.height(16.dp))
        // Campo de texto para o e-mail
        OutlinedTextField(
            // Valor atual do e-mail
            value = email,
            // Atualiza o valor do e-mail
            onValueChange = { email = it },
            // Rótulo do campo
            label = { Text(text = "E-mail") },
            // Preenche a largura disponível
            modifier = Modifier.fillMaxWidth()
        )
        // Espaçamento entre os campos
        Spacer(modifier = Modifier.height(8.dp))
        // Campo de texto para a senha
        OutlinedTextField(
            // Valor atual da senha
            value = password,
            // Atualiza o valor da senha
            onValueChange = { password = it },
            // Rótulo do campo
            label = { Text(text = "Senha") },
            // Preenche a largura disponível
            modifier = Modifier.fillMaxWidth(),
            // Oculta a senha digitada
            visualTransformation = PasswordVisualTransformation()
        )
        // Espaçamento entre os campos
        Spacer(modifier = Modifier.height(16.dp))
        // Botão de login
        Button(
            // Chama a função de login do ViewModel
            onClick = { authViewModel.login(email, password) },
            // Desabilita o botão se o estado for Loading
            enabled = authState != State.Loading,
            // Preenche a largura disponível
            modifier = Modifier.fillMaxWidth()
        ) {
            // Texto do botão
            Text(text = "Login")
        }
        // Espaçamento entre o botão e o próximo elemento
        Spacer(modifier = Modifier.height(8.dp))
        // Botão para navegação para a página de cadastro
        TextButton(onClick = {
            // Reseta o estado de autenticação
            authViewModel.resetState()
            // Navega para a página de cadastro
            navController.navigate("signup")
        }) {
            // Texto do botão de cadastro
            Text(text = "Cadastre-se agora")
        }
    }
}
