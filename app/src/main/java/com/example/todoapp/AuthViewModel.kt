package com.example.todoapp

import androidx.lifecycle.ViewModel
import com.example.todoapp.utils.FireBaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    // Instância do FirebaseAuth para autenticação com o Firebase
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    // Propriedade para acessar o usuário atual autenticado (pode ser nulo se não houver usuário autenticado)
    val currentUser : FirebaseUser? get() = auth.currentUser
    // Estado de autenticação mantido internamente usando MutableStateFlow (padrão inicial: NotAuthenticated)
    private val _authState = MutableStateFlow<State>(State.NotAuthenticated)
    // Exposição pública do estado de autenticação, como StateFlow imutável
    val authState: StateFlow<State> = _authState
    // Bloco de inicialização que verifica o status de autenticação assim que o ViewModel é criado
    init {
        authStatus()
    }
    // Verifica se há um usuário autenticado e atualiza o estado de acordo
    private fun authStatus() {
        if(auth.currentUser == null) {
            _authState.value = State.NotAuthenticated
        } else {
            _authState.value = State.Authenticated
        }
    }
    // Função de login com email e senha, usando Firebase Authentication
    fun login(email : String, password : String) {
        // Verifica se os campos de email e senha não estão vazios
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = State.Error("E-mail e senha não podem estar vazios")
            return
        }
        // Atualiza o estado para "Loading" enquanto a operação de login está em andamento
        _authState.value = State.Loading
        // Executa a autenticação com Firebase e atualiza o estado com base no resultado
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // Se o login for bem-sucedido, o estado é atualizado para "Authenticated"
                    _authState.value = State.Authenticated
                } else {
                    // Em caso de falha, o estado é atualizado com a mensagem de erro apropriada
                    val exception = task.exception
                    val errorMessage = FireBaseException.fireBaseError(exception)
                    _authState.value = State.Error(errorMessage)
                }
            }
    }
    // Função de cadastro com email e senha, semelhante ao login, mas cria uma nova conta no Firebase
    fun signup(email : String, password : String) {
        // Verifica se os campos de email e senha não estão vazios
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = State.Error("E-mail e senha não podem estar vazios")
            return
        }
        // Atualiza o estado para "Loading" enquanto a operação de cadastro está em andamento
        _authState.value = State.Loading
        // Executa a criação de conta no Firebase e atualiza o estado com base no resultado
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    // Se o cadastro for bem-sucedido, o estado é atualizado para "Authenticated"
                    _authState.value = State.Authenticated
                } else {
                    // Em caso de falha, o estado é atualizado com a mensagem de erro apropriada
                    val exception = task.exception
                    val errorMessage = FireBaseException.fireBaseError(exception)
                    _authState.value = State.Error(errorMessage)
                }
            }
    }
    // Função para realizar o logout do usuário, atualizando o estado para "NotAuthenticated"
    fun logout() {
        // Faz o sign out do FirebaseAuth
        auth.signOut()
        // Atualiza o estado para "Não Autenticado"
        _authState.value = State.NotAuthenticated
    }
    // Função para resetar o estado de autenticação para "NotAuthenticated"
    fun resetState() {
        _authState.value = State.NotAuthenticated
    }
}
    // Classe selada que define os diferentes estados possíveis de autenticação
sealed class State {
        // Estado quando o usuário está autenticado
    object Authenticated : State()
        // Estado quando o usuário não está autenticado
    object NotAuthenticated : State()
        // Estado durante operações de login/cadastro
    object Loading : State()
        // Estado para erros com uma mensagem específica
    data class Error(val message : String) : State()
}
