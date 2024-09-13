package com.example.todoapp.exceptions

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

object FireBaseException {

    fun fireBaseError(exception : Exception?) : String {
        return when(exception) {
            is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso em outra conta."
            is FirebaseAuthWeakPasswordException -> "A senha deve ter pelo menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
            is FirebaseTooManyRequestsException -> "Muitas tentativas de login. Por favor, tente novamente mais tarde."
            is FirebaseNetworkException -> "Sem conexão com a internet. Verifique sua conexão e tente novamente."
            else -> "Algo deu errado. Por favor, tente novamente."
        }
    }
}
