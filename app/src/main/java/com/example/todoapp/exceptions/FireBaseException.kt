package com.example.todoapp.exceptions

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

object FireBaseException {
    // Função para tratar erros do Firebase e retornar mensagens apropriadas
    fun fireBaseError(exception : Exception?) : String {
        return when(exception) {
            // Caso o e-mail já esteja em uso
            is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso em outra conta."
            // Caso a senha seja muito fraca
            is FirebaseAuthWeakPasswordException -> "A senha deve ter pelo menos 6 caracteres."
            // Caso as credenciais sejam inválidas
            is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
            // Caso haja muitas tentativas de login
            is FirebaseTooManyRequestsException -> "Muitas tentativas de login. Por favor, tente novamente mais tarde."
            // Caso não haja conexão com a internet
            is FirebaseNetworkException -> "Sem conexão com a internet. Verifique sua conexão e tente novamente."
            // Caso ocorra qualquer outro erro
            else -> "Algo deu errado. Por favor, tente novamente."
        }
    }
}
