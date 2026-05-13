# Sistema SUS - Farmácia Popular

Sistema de gerenciamento de farmácias, produtos e usuarios do SUS desenvolvido em Java com interface gráfica Swing.

## Descrição

O Sistema SUS - Farmácia Popular é uma aplicação desktop que permite aos usuários consultar e adquirir medicamentos disponíveis nas Unidades Básicas de Saúde (UBS). O sistema oferece funcionalidades completas de gerenciamento para administradores e uma interface intuitiva para os clientes.

## Funcionalidades

### Para Usuários (Clientes)

#### 1. Autenticação e Cadastro
- **Login**: Acesso com CPF e senha (case-insensitive)
- **Cadastro**: Criação de conta com validações de segurança
  - CPF (11 dígitos numéricos)
  - Nome completo
  - Email
  - Senha(não pode ser nula)
- **Recuperação de Senha**: Sistema de recuperação via pergunta de segurança (implementação futura)
- **Bloqueio de Conta**: Após 3 tentativas incorretas de login(implementação futura)

#### 2. Seleção de UBS
- Visualização de todas as Unidades Básicas de Saúde disponíveis(somente 2 no momento)
- Busca por nome ou endereço (Implementação futura)
- Informações detalhadas de cada UBS:
  - Nome da unidade
  - Endereço completo (logradouro, número, bairro, estado, CEP)
  - Imagem da unidade
- Seleção da UBS para consultar medicamentos disponíveis

#### 3. Consulta de Medicamentos
- Listagem de medicamentos disponíveis na UBS selecionada
- Busca por nome do medicamento(implementação futura)
- Informações detalhadas de cada remédio:
  - Nome
  - Descrição
  - Tipo (comprimido, cápsula, etc.)
  - Gramatura/dosagem
  - Preço (com desconto simulado)
  - Necessidade de receita médica
  - Imagem do medicamento(implementação futura)
  - Estoque disponível

#### 4. Carrinho de Compras
- Adição de medicamentos ao carrinho
- Seleção de quantidade
- Visualização do total
- Remoção de itens
- Finalização do pedido

#### 5. Sistema de Pedidos
- Criação automática de pedidos
- Upload de receita médica (para medicamentos controlados) ( implementação futura)
  - Formatos que devem ser aceitos: PDF, JPG, JPEG, PNG (implementação futura)
- Validação de cotas
- Validação de estoque
- Desconto automático das cotas utilizadas
- Atualização automática do estoque
- Status do pedido:
  - **Pendente**: Aguardando aprovação (medicamentos com receita)
  - **Aprovado**: Pedido aprovado e processado

### Sistema Gerenciador de dados

O sistema utiliza um arquivo JSON (`dados.json`) para armazenar todas as informações:

```json
{
  "usuarios": [...],
  "ubs": [...],
  "remedios": [...],
  "pedidos": [...],
  "receitas": [...]
}
```

#### Estrutura de UBS
```json
{
  "id": 1,
  "nome": "UBS Jordanópolis",
  "logradouro": "Viela Jangada Nova",
  "numero": "75",
  "bairro": "Jardim Pres.",
  "estado": "SP",
  "cep": "04830-200",
  "imagem": "ubs_1234567890.png"
}
```

#### Estrutura de Remédio
```json
{
  "id": 1,
  "nome": "Paracetamol",
  "preco": 10.50,
  "estoque": 100,
  "precisaReceita": false,
  "ubsId": 1,
  "imagem": "drug_1234567890.png",
  "descricao": "Analgésico e antitérmico",
  "tipo": "Comprimido",
  "gramatura": "500mg"
}
```

## 📁 Estrutura de Diretórios

```
Saude/
├── src/
│   ├── SistemaFinal/                    # Facade - Interface unificada
│   │   └── Main.java
│   ├── modelo/                  # Entidades
│   │   ├── Usuario.java
|   |   |── Admin.java
│   │   ├── Unidade.java
│   │   ├── Remedio.java
│   │   ├── Pedido.java
│   │   └── Pessoa.java
│   ├── visao/                   # Interface gráfica
│   │   ├── TelaInicial.java
│   │   ├── TelaSelecaoUBS.java
│   │   ├── TelaCliente.java
|   |   |── TelaSistemaSaude.java #Classe que gerencia os dados e as outras classes.
│   │   └── TelaAdmin.java
|   |   
│   ├── infra/                # Persistência
│   │   └── GerenciadorJSON.java
│   └── Main.java                # Ponto de entrada
├── imagens/
│   ├── logo.png
│   ├── tela_inicial.png
│   ├── cart.png
│   ├── search.png
│   ├── home.png
│   ├── local.png
│   ├── drug.png
│   └── user_data/
│       └── receitas/            # Receitas médicas enviadas (implementação futura)
├── dados.json                   # Banco de dados em JSON
├── README.md
```

## 🔐 Segurança

### Validações Implementadas
- **CPF**: Apenas números, 11 dígitos
- **Senha**: Não pode ser nula (mais validações futuramente)
- **Bloqueio de Conta**: Após 3 tentativas incorretas ( futuramente)
- **Recuperação de Senha**: Via pergunta de segurança (futuramente)
- **Validação de Cota**: Antes de finalizar pedido
- **Validação de Estoque**: Antes de processar pedido

### Controle de Acesso
- **Usuários Comuns**: Acesso apenas às funcionalidades de cliente
- **Administradores**: Acesso completo ao sistema

## 🚀 Como Executar

### Compilação

```bash
# Navegar até o diretório do projeto
cd Saude

# Compilar todos os arquivos (Windows)
javac -d bin -sourcepath src src\Main.java

# Compilar todos os arquivos (Linux/Mac)
javac -d bin -sourcepath src src/Main.java
```

### Execução

```bash
# Executar o sistema (Windows)
java -cp bin Main

# Executar o sistema (Linux/Mac)
java -cp bin Main
```

### Primeira Execução

1. O sistema criará automaticamente o arquivo `dados.json` na primeira execução
2. Use as credenciais do administrador para acesso completo:
   - **CPF**: `administrador`
   - **Senha**: `123`
3. Ou crie uma conta de usuário comum para testar as funcionalidades de cliente.

### Estrutura de Pacotes

O sistema utiliza a seguinte estrutura de pacotes:
- `SistemaFinal` - Inicialização do sistema
- `modelo` - Entidades
- `visao` - Interface gráfica
- `infra` - Persistência

## 📄 Licença

Este projeto é de uso educacional.
