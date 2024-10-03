package com.example.todoapp.exceptions

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

// Objeto que encapsula o tratamento de exceções específicas do Firebase, fornecendo mensagens de erro amigáveis ao usuário
object FireBaseException {
    // Função que recebe uma exceção do Firebase e retorna uma mensagem de erro apropriada com base no tipo da exceção
    fun fireBaseError(exception : Exception?) : String {
        return when(exception) {
            // Erro lançado quando o e-mail já está vinculado a outra conta
            is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso em outra conta."
            // Erro lançado quando a senha fornecida é muito fraca
            is FirebaseAuthWeakPasswordException -> "A senha deve ter pelo menos 6 caracteres."
            // Erro lançado quando as credenciais de login são inválidas (e-mail ou senha incorretos)
            is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
            // Erro lançado quando há muitas tentativas de login em um curto período de tempo
            is FirebaseTooManyRequestsException -> "Muitas tentativas de login. Por favor, tente novamente mais tarde."
            // Erro lançado quando não há conexão com a internet
            is FirebaseNetworkException -> "Sem conexão com a internet. Verifique sua conexão e tente novamente."
            // Tratamento padrão para outros erros não específicos
            else -> "Algo deu errado. Por favor, tente novamente."
        }
    }
}
