# 📋 TodoApp

Este é um aplicativo de lista de tarefas simples, permitindo que os usuários **cadastrem, editem e excluam tarefas**. Ele suporta **autenticação de usuários** e armazena dados localmente no **Room** e remotamente no **Firestore**, garantindo que cada usuário tenha acesso às suas próprias tarefas. 

---

## 🚀 Funcionalidades

- **Autenticação de usuário**:
  - Cadastro de novos usuários
  - Login e logout de usuários existentes

- **Gerenciamento de Tarefas**:
  - Criar, editar e excluir tarefas
  - Sincronização entre armazenamento local (Room) e remoto (Firestore)
  - As tarefas são **associadas a cada usuário individualmente**

---

## 🛠️ Tecnologias utilizadas

- **Kotlin** & **Jetpack Compose**: Para interface nativa e funcional.
- **Room**: Banco de dados local.
- **Firebase Firestore**: Armazenamento remoto em tempo real.
- **Firebase Authentication**: Gerenciamento de contas de usuário.
- **Navigation Component**: Navegação entre páginas do app.

---
## 📱 Uso do App

### Tela de Login / Cadastro

- Usuários podem criar uma conta ou acessar uma existente.

### Tela Principal

- Exibe as tarefas do usuário logado.

### Adicionar ou Editar Tarefas

- Preencha título e descrição da tarefa.
- Salve para que a tarefa seja armazenada e exibida na tela principal.

---

## 🔐 Gestão de Autenticação

A autenticação é gerenciada usando o **Firebase Authentication**. O fluxo de autenticação inclui:

- **Cadastro**: Usuários podem se registrar fornecendo um e-mail e senha.
- **Login**: Usuários podem acessar suas contas existentes.
- **Logout**: Usuários podem sair de suas contas a qualquer momento.

A autenticação é monitorada por meio do **AuthViewModel**, que mantém o estado de autenticação e informa a interface sobre mudanças.

---

## 🐞 Problemas Conhecidos

- O aplicativo pode apresentar lentidão em dispositivos mais antigos durante a sincronização com o Firestore.
- Algumas animações podem não funcionar corretamente em certas versões do Android.
- Problemas ocasionais com a conexão à internet podem afetar a experiência do usuário.


## 🗂️ Estrutura do Projeto

```bash
├── AuthViewModel.kt        # Gerencia autenticação e estado do usuário
├── TaskViewModel.kt        # Gerencia as tarefas do usuário
├── MainActivity.kt         # Atividade principal do aplicativo
├── data/                   # Modelos de dados e configuração de banco
│   ├── model/              # Modelos de dados
│   │   ├── Task.kt         # Modelo da tarefa
│   ├── repository/         # Repositórios para manipulação de dados
│   │   ├── FirestoreRepository.kt # Interação com Firestore
│   │   └── TaskRepository.kt       # Interação com Room
│   └── database/          # Configuração do banco de dados
│       └── TodoAppDatabase.kt     # Configuração do Room
├── dao/                    # Interfaces DAO
│   └── TaskDao.kt          # Acesso ao banco de dados de tarefas
├── utils/                  # Utilitários
│   ├── NetworkUtils.kt      # Funções utilitárias de rede
│   └── FirebaseException.kt  # Tratamento de exceções do Firebase
├── worker/                 # WorkManager
│   └── TaskSyncWorker.kt    # Sincronização de tarefas
└── pages/                  # Páginas do app
    ├── LoginPage.kt        # Tela de login
    ├── SignupPage.kt       # Tela de cadastro
    ├── HomePage.kt         # Tela inicial
    ├── AddTaskPage.kt      # Tela para adicionar nova tarefa
    └── EditTaskPage.kt     # Tela para editar tarefa existente
└── navigation/             # Navegação
    └── AppNavigation.kt     # Configuração da navegação



