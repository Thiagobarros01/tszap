/**
 * ZAPZAP CRM - Frontend Profissional
 * Versão Final: Chat + Gestão de Clientes
 */

const API_URL = 'http://localhost:8080';
const NOTIFICATION_SOUND = new Audio('https://assets.mixkit.co/active_storage/sfx/2354/2354-preview.mp3'); 

// Estado Global
const state = {
    token: localStorage.getItem('zapzap_token'),
    userLogin: localStorage.getItem('zapzap_user_login'),
    currentChatId: null,
    conversationsCache: [], 
    pollingInterval: null,
    lastMsgCount: 0
};

// --- CONFIGURAÇÃO DA UI ---
const ui = {
    screens: { login: document.getElementById('login-screen'), app: document.getElementById('app-screen') },
    // Novas Views (Abas)
    views: {
        chat: document.getElementById('view-chat'),
        clients: document.getElementById('view-clients')
    },
    login: { 
        form: document.getElementById('login-form'), 
        user: document.getElementById('username'), 
        pass: document.getElementById('password'), 
        btn: document.getElementById('btn-login'), 
        error: document.getElementById('login-error') 
    },
    sidebar: {
        self: document.querySelector('.sidebar'),
        userDisplay: document.getElementById('display-user'),
        list: document.getElementById('conversation-list'),
        searchInput: document.getElementById('search-input'),
        btnLogout: document.getElementById('btn-logout'),
        refresh: document.getElementById('btn-refresh')
    },
    chat: {
        container: document.getElementById('chat-container'),
        btnBack: document.getElementById('btn-back'), // AGORA VAI EXISTIR
        area: document.querySelector('.chat-area'),
        empty: document.getElementById('empty-state'),
        headerName: document.getElementById('header-name'),
        headerStatus: document.getElementById('header-status'),
        box: document.getElementById('messages-box'),
        input: document.getElementById('msg-input'),
        btnSend: document.getElementById('btn-send'),
        btnBack: document.getElementById('btn-back'), 
        btnTransfer: document.getElementById('btn-transfer'),
        btnEnd: document.getElementById('btn-end')
    },
    modal: {
        el: document.getElementById('transfer-modal'),
        list: document.getElementById('attendants-list'),
        confirm: document.getElementById('btn-confirm-transfer'),
        cancel: document.getElementById('btn-cancel-transfer'),
        close: document.getElementById('btn-close-modal')
    },
    clientModal: {
        el: document.getElementById('client-modal'),
        close: document.getElementById('btn-close-client'),
        phone: document.getElementById('client-phone-edit'),
        name: document.getElementById('client-name-edit'),
        btnSave: document.getElementById('btn-save-client')
    },
    chatHeaderName: document.getElementById('header-name'),
    toasts: document.getElementById('toast-container')
};

let selectedTransferId = null;
let editingClientId = null;

// --- INICIALIZAÇÃO ---
document.addEventListener('DOMContentLoaded', () => {
    if (state.token) initApp();
    else showScreen('login');
    
    // Auto-resize do Textarea
    if(ui.chat.input){
        ui.chat.input.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
            if(this.value === '') this.style.height = '24px';
        });
    }
});

// --- SISTEMA DE NAVEGAÇÃO (ABAS) ---
window.navigate = function(viewName) {
    // 1. Esconde todas as views
    Object.values(ui.views).forEach(v => v.classList.remove('active'));
    // 2. Remove ativo dos botões laterais
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    
    // 3. Mostra a view desejada
    if(ui.views[viewName]) ui.views[viewName].classList.add('active');
    
    // 4. Ativa o botão correspondente
    const btn = document.querySelector(`.nav-btn[onclick="navigate('${viewName}')"]`);
    if(btn) btn.classList.add('active');

    // 5. Se for aba de clientes, carrega a tabela
    if(viewName === 'clients') loadClientsTable();
};

// --- AUTH ---
ui.login.form.addEventListener('submit', async (e) => {
    e.preventDefault();
    setLoading(ui.login.btn, true);
    try {
        const res = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ login: ui.login.user.value, senha: ui.login.pass.value })
        });
        if (!res.ok) throw new Error('Login falhou');
        
        const token = await res.text();
        localStorage.setItem('zapzap_token', token);
        localStorage.setItem('zapzap_user_login', ui.login.user.value);
        state.token = token;
        state.userLogin = ui.login.user.value;
        initApp();
        showToast('Bem-vindo de volta!', 'success');
    } catch (err) {
        ui.login.error.textContent = "Usuário ou senha incorretos";
        ui.login.error.style.display = 'block';
    } finally {
        setLoading(ui.login.btn, false);
    }
});

ui.sidebar.btnLogout.addEventListener('click', () => {
    localStorage.clear();
    location.reload();
});

// --- LÓGICA PRINCIPAL ---
function initApp() {
    showScreen('app');
    // Começa no chat
    navigate('chat');
    
    document.getElementById('display-username').textContent = state.userLogin || 'Você';
    
    loadConversations();
    setInterval(loadConversations, 4000); 
    setInterval(() => {
        if(state.currentChatId) loadMessages(state.currentChatId);
    }, 3000); 

    // Configura clique no header para editar cliente
    if(ui.chatHeaderName) {
         ui.chatHeaderName.parentElement.style.cursor = 'pointer'; 
         ui.chatHeaderName.parentElement.onclick = () => {
            if (state.currentChatId) {
                openClientModalFromChat();
            }
        };
    }
}

ui.sidebar.list.addEventListener('click', (e) => {
    // Procura se o clique foi dentro de um item de conversa
    const item = e.target.closest('.conversation-item');
    
    // Se clicou fora, não faz nada
    if (!item) return;

    // Pega o ID que guardamos no Passo 1
    const id = item.dataset.id;
    
    // Procura os dados da conversa e abre
    const conversa = state.conversationsCache.find(c => c.conversaId == id);
    if (conversa) {
        openChat(conversa);
    }
});

// --- CONVERSAS ---
async function loadConversations() {
    const res = await apiCall('/painel/atendimento/conversas');
    if(!res) return;
    const list = await res.json();
    
    if(JSON.stringify(list) !== JSON.stringify(state.conversationsCache)) {
        state.conversationsCache = list;
        renderConversations(list);
    }
}

ui.sidebar.searchInput.addEventListener('input', (e) => {
    const term = e.target.value.toLowerCase();
    const filtered = state.conversationsCache.filter(c => 
        c.telefoneCliente.includes(term) || c.status.toLowerCase().includes(term)
    );
    renderConversations(filtered);
});

function renderConversations(list) {
    ui.sidebar.list.innerHTML = '';
    list.forEach(c => {
        const div = document.createElement('div');
        div.dataset.id = c.conversaId; 
        
        // Destaque visual CORRETO - compara com state.currentChatId
        div.className = `conversation-item ${state.currentChatId === c.conversaId ? 'active' : ''}`;
        
        const colorIndex = parseInt(c.telefoneCliente.replace(/\D/g, '').slice(-1)) || 0;
        const colors = ['#00a884', '#00bcd4', '#ff9800', '#e91e63', '#9c27b0', '#3f51b5'];
        const displayName = c.nomeCliente || formatPhone(c.telefoneCliente);

        div.innerHTML = `
            <div class="avatar-gen" style="background:${colors[colorIndex % colors.length]}">
                <span class="material-icons-round">person</span>
            </div>
            <div class="conv-info">
                <div class="conv-top">
                    <span class="conv-name" title="${displayName}">${displayName}</span>
                    <span class="conv-time">${c.ultimaMensagem ? formatSmartDate(new Date(c.ultimaMensagem.data)) : ''}</span>
                </div>
                <div class="conv-status">${c.status === 'BOT' ? '🤖 Robô' : '👤 Atendimento'}</div>
            </div>
        `;
        ui.sidebar.list.appendChild(div);
    });
}
// --- CHAT ---
function openChat(c) {
    state.currentChatId = c.conversaId;
    const displayName = c.nomeCliente || formatPhone(c.telefoneCliente);
    ui.chat.headerName.textContent = displayName;
    ui.chat.headerStatus.textContent = c.status;
    
    // IMPORTANTE: Garantir que o empty state esteja escondido
    ui.chat.empty.classList.remove('active');
    ui.chat.empty.style.display = 'none';
    
    // Mostra o container do chat SEMPRE
    ui.chat.container.classList.add('active');
    ui.chat.container.style.display = 'flex'; 
    
    // Adiciona classe para animar no mobile
    ui.views.chat.classList.add('chat-open');
    
    // Scroll para o topo (importante para mobile)
    ui.chat.box.scrollTop = 0;
    
    // Carrega mensagens
    loadMessages(c.conversaId, true);
    renderConversations(state.conversationsCache);
}

// Adicione também um listener para o btnBack (seta) para garantir limpeza
if(ui.chat.btnBack) {
    ui.chat.btnBack.addEventListener('click', () => {
        // Remove a classe que mostra o chat, voltando para a lista
        ui.views.chat.classList.remove('chat-open');
        
        // Limpa o estado mas NÃO limpa mensagens (para caso volte)
        state.currentChatId = null;
        
        // Pequeno delay para animação
        setTimeout(() => {
            ui.chat.container.classList.remove('active');
            renderConversations(state.conversationsCache);
        }, 300);
    });
}

// 4. Ajuste no botão Encerrar (btnEnd) para também voltar a tela
// 4. Ajuste no botão Encerrar (btnEnd) para também voltar a tela
ui.chat.btnEnd.addEventListener('click', async () => {
    if(confirm('Encerrar atendimento?')) {
        await apiCall(`/painel/atendimento/conversas/${state.currentChatId}/encerrar`, 'POST');
        
        // UI Updates - Limpar estado mais completamente
        state.currentChatId = null;
        
        // IMPORTANTE: Reset completo da UI do chat
        ui.views.chat.classList.remove('chat-open');
        ui.chat.container.classList.remove('active');
        ui.chat.container.style.display = 'none';
        
        // Limpar mensagens
        ui.chat.box.innerHTML = '';
        
        // Mostra empty state apenas se for desktop
        if(window.innerWidth > 768) {
            ui.chat.empty.classList.add('active');
            ui.chat.empty.style.display = 'flex';
        } else {
            // No mobile, garante que o empty state não apareça
            ui.chat.empty.classList.remove('active');
            ui.chat.empty.style.display = 'none';
        }

        // Limpar input
        ui.chat.input.value = '';
        ui.chat.input.style.height = '24px';
        
        loadConversations();
        showToast('Atendimento encerrado', 'success');
    }
});

async function loadMessages(id, forceScroll = false) {
    const res = await apiCall(`/painel/atendimento/conversas/${id}/mensagens`);
    if(!res) return;
    const msgs = await res.json();
    
    if(msgs.length > state.lastMsgCount) {
        const last = msgs[msgs.length - 1];
        if(last.origem !== 'HUMANO' && !forceScroll) NOTIFICATION_SOUND.play().catch(e=>{});
    }
    state.lastMsgCount = msgs.length;
    renderMessages(msgs, forceScroll);
}

function renderMessages(msgs, forceScroll) {
    const box = ui.chat.box;
    const isNearBottom = box.scrollHeight - box.scrollTop - box.clientHeight < 150;
    
    const currentConv = state.conversationsCache.find(x => x.conversaId === state.currentChatId);
    const clientName = currentConv ? (currentConv.nomeCliente || 'Cliente') : 'Cliente';

    box.innerHTML = '';
    let lastDate = null;

    msgs.forEach(m => {
        const date = new Date(m.data);
        const dateStr = date.toLocaleDateString();
        
        if(dateStr !== lastDate) {
            const divider = document.createElement('div');
            divider.className = 'date-divider';
            divider.textContent = formatSmartDate(date);
            box.appendChild(divider);
            lastDate = dateStr;
        }

        const isMe = m.origem === 'HUMANO' || m.origem === 'BOT';
        const div = document.createElement('div');
        div.className = `message ${isMe ? 'sent' : 'received'}`;
        
        div.innerHTML = `
            ${!isMe ? `<div style="font-size:0.7rem; font-weight:bold; color:#e542a3; margin-bottom:2px">${escapeHtml(clientName)}</div>` : ''}
            <div class="msg-text">${escapeHtml(m.texto)}</div>
            <div class="msg-meta">
                ${date.toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}
                ${isMe ? '<span class="material-icons-round" style="font-size:14px; margin-left:2px">done_all</span>' : ''}
            </div>
        `;
        box.appendChild(div);
    });

    if(forceScroll || isNearBottom) box.scrollTop = box.scrollHeight;
}

ui.chat.input.addEventListener('keydown', (e) => {
    if(e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); sendMessage(); }
});
ui.chat.btnSend.addEventListener('click', sendMessage);

async function sendMessage() {
    const text = ui.chat.input.value.trim();
    if(!text || !state.currentChatId) return;
    ui.chat.input.value = '';
    ui.chat.input.style.height = '24px'; 
    await apiCall(`/painel/atendimento/conversas/${state.currentChatId}/responder`, 'POST', {texto: text});
    loadMessages(state.currentChatId, true);
}

// --- GESTÃO DE CLIENTES (Tabela) ---
async function loadClientsTable() {
    const tbody = document.getElementById('clients-table-body');
    if(!tbody) return;
    tbody.innerHTML = '<tr><td colspan="3">Carregando...</td></tr>';

    const res = await apiCall('/clientes'); // Requer GET /clientes no Java
    if(res && res.ok) {
        const clientes = await res.json();
        tbody.innerHTML = '';
        clientes.forEach(c => {
            tbody.innerHTML += `
                <tr>
                    <td>${c.nome || 'Sem nome'}</td>
                    <td>${formatPhone(c.telefone)}</td>
                    <td>
                        <button class="btn-icon" onclick="editClient(${c.id})" title="Editar">
                            <span class="material-icons-round">edit</span>
                        </button>
                    </td>
                </tr>
            `;
        });
    } else {
        tbody.innerHTML = '<tr><td colspan="3">Erro ao carregar (Verifique se o backend tem GET /clientes).</td></tr>';
    }
}

// Novo Cliente
const formNew = document.getElementById('new-client-form');
if(formNew) {
    formNew.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nome = document.getElementById('new-client-name').value;
        const telefone = document.getElementById('new-client-phone').value;

        const res = await apiCall('/clientes', 'POST', { nome, telefone });
        if(res && res.ok) {
            showToast('Cliente cadastrado!', 'success');
            document.getElementById('new-client-modal').classList.remove('active');
            loadClientsTable();
        } else {
            showToast('Erro ao cadastrar.', 'error');
        }
    });
}

function openNewClientModal() {
    document.getElementById('new-client-modal').classList.add('active');
}

// --- EDIÇÃO DE CLIENTE (Modal Unificado) ---

// Chama quando clica no cabeçalho do chat
async function openClientModalFromChat() {
    const currentChat = state.conversationsCache.find(c => c.conversaId === state.currentChatId);
    if (!currentChat || !currentChat.clienteId) {
        showToast('Erro: Cliente não identificado.', 'error');
        return;
    }
    await openEditClientModal(currentChat.clienteId);
}

// Chama quando clica na tabela de clientes (window para ser acessível do HTML)
window.editClient = async function(id) {
    await openEditClientModal(id);
}

async function openEditClientModal(id) {
    editingClientId = id;
    ui.clientModal.el.classList.add('active');
    
    ui.clientModal.name.value = 'Carregando...';
    ui.clientModal.name.disabled = true;

    try {
        const res = await apiCall(`/clientes/${id}`);
        if (res && res.ok) {
            const cliente = await res.json();
            ui.clientModal.phone.value = formatPhone(cliente.telefone);
            ui.clientModal.name.value = cliente.nome || ''; 
        } else {
            showToast('Erro ao buscar detalhes.', 'error');
        }
    } catch (e) {
        console.error(e);
        showToast('Erro de conexão.', 'error');
    } finally {
        ui.clientModal.name.disabled = false;
        ui.clientModal.name.focus();
    }
}

// Salvar Edição
if(ui.clientModal.btnSave){
    ui.clientModal.btnSave.addEventListener('click', async () => {
        const novoNome = ui.clientModal.name.value.trim();
        if (!editingClientId) return;

        const btn = ui.clientModal.btnSave;
        const originalText = btn.textContent;
        btn.textContent = 'Salvando...';
        btn.disabled = true;

        try {
            const res = await apiCall(`/clientes/${editingClientId}`, 'PUT', { nome: novoNome });

            if (res && res.ok) {
                showToast('Cliente atualizado!', 'success');
                ui.clientModal.el.classList.remove('active');
                
                // Atualiza em tempo real
                if(state.currentChatId) {
                    const currentChat = state.conversationsCache.find(c => c.conversaId === state.currentChatId);
                    if(currentChat && currentChat.clienteId === editingClientId) {
                        ui.chatHeaderName.textContent = novoNome;
                    }
                }
                
                // Recarrega as listas
                loadConversations();
                loadClientsTable();
            } else {
                showToast('Erro ao atualizar.', 'error');
            }
        } catch (e) {
            showToast('Erro de conexão.', 'error');
        } finally {
            btn.textContent = originalText;
            btn.disabled = false;
        }
    });
}

if(ui.clientModal.close){
    ui.clientModal.close.addEventListener('click', () => ui.clientModal.el.classList.remove('active'));
}

// --- UTILS ---
async function apiCall(endpoint, method='GET', body=null) {
    const headers = { 'Authorization': `Bearer ${state.token}`, 'Content-Type': 'application/json' };
    try {
        const res = await fetch(API_URL + endpoint, { method, headers, body: body ? JSON.stringify(body) : null });
        if(res.status === 403) { localStorage.clear(); location.reload(); }
        return res;
    } catch(e) { console.error(e); return null; }
}

function showScreen(name) {
    Object.values(ui.screens).forEach(s => s.classList.remove('active'));
    ui.screens[name].classList.add('active');
}

function formatPhone(p) {
    if(!p) return 'Desconhecido';
    return p.replace('whatsapp:+', '').replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
}

function formatSmartDate(date) {
    const today = new Date();
    const yesterday = new Date(today); yesterday.setDate(yesterday.getDate() - 1);
    
    if(date.toDateString() === today.toDateString()) return 'Hoje';
    if(date.toDateString() === yesterday.toDateString()) return 'Ontem';
    return date.toLocaleDateString();
}

function escapeHtml(text) { return text ? text.replace(/</g, "&lt;") : ''; }

function showToast(msg, type='success') {
    const div = document.createElement('div');
    div.className = `toast ${type}`;
    div.innerHTML = `<span class="material-icons-round">${type==='success'?'check_circle':'error'}</span> ${msg}`;
    ui.toasts.appendChild(div);
    setTimeout(() => div.remove(), 4000);
}

function setLoading(btn, active) {
    btn.disabled = active;
    btn.innerHTML = active ? '<div class="spinner"></div>' : 'Entrar';
}


// --- LÓGICA DE TRANSFERÊNCIA ---

// 1. Abrir Modal e Carregar Atendentes
if (ui.chat.btnTransfer) {
    ui.chat.btnTransfer.addEventListener('click', async () => {
        if (!state.currentChatId) return;

        ui.modal.el.classList.add('active');
        ui.modal.list.innerHTML = '<div style="padding:10px">Carregando atendentes...</div>';
        ui.modal.confirm.disabled = true;
        selectedTransferId = null;

        try {
            // Chama o endpoint ajustado que lista usuários da empresa
            const res = await apiCall('/usuarios'); 
            if (res && res.ok) {
                const usuarios = await res.json();
                renderAttendantsList(usuarios);
            } else {
                ui.modal.list.innerHTML = '<div style="color:red; padding:10px">Erro ao carregar lista.</div>';
            }
        } catch (e) {
            console.error(e);
            ui.modal.list.innerHTML = '<div style="color:red; padding:10px">Erro de conexão.</div>';
        }
    });
}

function renderAttendantsList(usuarios) {
    ui.modal.list.innerHTML = '';
    
    if (usuarios.length === 0) {
        ui.modal.list.innerHTML = '<div style="padding:10px; color:#666">Nenhum outro atendente disponível.</div>';
        return;
    }

    usuarios.forEach(u => {
        const item = document.createElement('div');
        item.className = 'attendant-item'; // Adicione css para isso se quiser (padding: 10px; cursor: pointer...)
        item.style.padding = '10px';
        item.style.borderBottom = '1px solid #eee';
        item.style.cursor = 'pointer';
        item.innerHTML = `
            <div style="font-weight:bold">${u.login}</div>
            <div style="font-size:0.8rem; color:#888">${u.role || 'Atendente'}</div>
        `;
        
        item.onclick = () => {
            // Remove seleção visual dos outros
            Array.from(ui.modal.list.children).forEach(c => c.style.background = 'transparent');
            // Seleciona este
            item.style.background = '#eef1f4';
            selectedTransferId = u.id; // Precisa vir do DTO atualizado
            ui.modal.confirm.disabled = false;
        };
        
        ui.modal.list.appendChild(item);
    });
}

// 2. Confirmar Transferência
if (ui.modal.confirm) {
    ui.modal.confirm.addEventListener('click', async () => {
        if (!state.currentChatId || !selectedTransferId) return;

        const btn = ui.modal.confirm;
        btn.textContent = 'Transferindo...';
        btn.disabled = true;

        try {
            const res = await apiCall(`/painel/atendimento/conversas/${state.currentChatId}/transferir/${selectedTransferId}`, 'PATCH');
            
            if (res && res.ok) {
                showToast('Conversa transferida com sucesso!', 'success');
                ui.modal.el.classList.remove('active');
                // Limpa o chat atual pois não pertence mais ao usuário
                state.currentChatId = null;
                ui.chat.container.classList.remove('active');
                ui.chat.empty.classList.add('active');
                loadConversations();
            } else {
                const err = await res.json(); // Tenta ler erro do backend
                showToast(err.mensagem || 'Erro ao transferir.', 'error');
            }
        } catch (error) {
            showToast('Erro de comunicação.', 'error');
        } finally {
            btn.textContent = 'Confirmar';
            btn.disabled = false;
        }
    });
}

// 3. Fechar Modal
if (ui.modal.close) ui.modal.close.onclick = () => ui.modal.el.classList.remove('active');
if (ui.modal.cancel) ui.modal.cancel.onclick = () => ui.modal.el.classList.remove('active');