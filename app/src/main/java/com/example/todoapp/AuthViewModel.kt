package com.example.todoapp

import androidx.lifecycle.ViewModel
import com.example.todoapp.exceptions.FireBaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    // Inicializa uma instância do FirebaseAuth
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    // Retorna o usuário atual autenticado
    val currentUser : FirebaseUser? get() = auth.currentUser
    // Estado de autenticação usando MutableStateFlow
    private val _authState = MutableStateFlow<State>(State.NotAuthenticated)
    // Estado de autenticação exposto como StateFlow
    val authState: StateFlow<State> = _authState
    // Bloco de inicialização que chama o método authStatus
    init {
        authStatus()
    }
    // Método para verificar o status de autenticação
    private fun authStatus() {
        if(auth.currentUser == null) {
            _authState.value = State.NotAuthenticated
        } else {
            _authState.value = State.Authenticated
        }
    }
    // Método para login com email e senha
    fun login(email : String, password : String) {
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = State.Error("E-mail e senha não podem estar vazios")
            return
        }
        _authState.value = State.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = State.Authenticated
                } else {
                    val exception = task.exception
                    val errorMessage = FireBaseException.fireBaseError(exception)
                    _authState.value = State.Error(errorMessage)
                }
            }
    }
    // Método para cadastro com email e senha
    fun signup(email : String, password : String) {
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = State.Error("E-mail e senha não podem estar vazios")
            return
        }
        _authState.value = State.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = State.Authenticated
                } else {
                    val exception = task.exception
                    val errorMessage = FireBaseException.fireBaseError(exception)
                    _authState.value = State.Error(errorMessage)
                }
            }
    }
    // Método para logout
    fun logout() {
        auth.signOut()
        _authState.value = State.NotAuthenticated
    }
    // Método para resetar o estado de autenticação
    fun resetState() {
        _authState.value = State.NotAuthenticated
    }
}
// Classe selada que representa os diferentes estados de autenticação
sealed class State {
    object Authenticated : State()
    object NotAuthenticated : State()
    object Loading : State()
    data class Error(val message : String) : State()
}
