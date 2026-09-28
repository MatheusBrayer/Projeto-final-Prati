# Projeto Final +PraTi

Sistema desenvolvido como projeto final do curso **+PraTi**, utilizando arquitetura full stack.

## Tecnologias

### Frontend

* React
* JavaScript / TypeScript
* Vite

### Backend

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* Maven

### Banco de dados

* PostgreSQL
* DBeaver

### Versionamento

* Git
* GitHub
* Conventional Commits

---

## Estrutura do projeto

```text
Projeto-final-Prati/
├── frontend/
├── backend/
└── database/
    ├── schema.sql
    └── seed.sql
```

* `frontend/` → aplicação React
* `backend/` → API REST em Spring Boot
* `database/` → scripts de criação e população do banco

---

## Configuração do ambiente

### 1. Clonar o repositório

```bash
git clone URL_DO_REPOSITORIO
cd Projeto-final-Prati
```

### 2. Configurar o banco de dados

É necessário ter o **PostgreSQL** instalado e em execução.

Depois, criar o banco:

```text
Oficina
```

Os scripts necessários estão na pasta:

```text
database/
```

Execute primeiro:

```text
schema.sql
```

e depois:

```text
seed.sql
```

### 3. Configurar o backend

Entre na pasta:

```bash
cd backend
```

Crie um arquivo chamado:

```text
.env
```

O arquivo deve conter:

```env
DB_URL=jdbc:postgresql://localhost:5432/Oficina
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
```

O arquivo `.env` **não deve ser enviado para o GitHub**, pois contém credenciais.

Utilize o `.env.example` como referência.

### 4. Executar o backend

O backend utiliza:

* Java 21+
* Maven

Execute o projeto pela IDE ou utilizando Maven.

Por padrão, a API será executada em:

```text
http://localhost:8080
```

---

# Fluxo de trabalho com Git

Para evitar conflitos e manter a `main` estável, não trabalhamos diretamente nela.

## Branches

A branch principal é:

```text
main
```

A branch de desenvolvimento é:

```text
develop
```

Novas funcionalidades devem ser desenvolvidas em branches próprias:

```text
feature/nome-da-funcionalidade
```

Exemplos:

```text
feature/clientes
feature/veiculos
feature/ordens-servico
feature/estoque
```

### Criando uma branch

Antes de começar uma tarefa:

```bash
git checkout develop
git pull origin develop
git checkout -b feature/nome-da-funcionalidade
```

Depois do desenvolvimento:

```bash
git add .
git commit -m "feat: adiciona cadastro de clientes"
git push -u origin feature/nome-da-funcionalidade
```

Depois, abra um **Pull Request** no GitHub para `develop`.

---

# Conventional Commits

As mensagens de commit seguem o padrão **Conventional Commits**.

Formato:

```text
tipo: descrição
```

### Tipos principais

| Tipo       | Utilização                                 |
| ---------- | ------------------------------------------ |
| `feat`     | Nova funcionalidade                        |
| `fix`      | Correção de bug                            |
| `refactor` | Refatoração sem alteração de comportamento |
| `docs`     | Documentação                               |
| `test`     | Testes                                     |
| `chore`    | Configuração ou manutenção                 |
| `build`    | Dependências ou processo de build          |
| `ci`       | Integração/entrega contínua                |
| `style`    | Formatação/estilo                          |
| `perf`     | Melhorias de performance                   |

### Exemplos

```bash
git commit -m "feat: adiciona cadastro de clientes"
```

```bash
git commit -m "fix: corrige validação do CPF"
```

```bash
git commit -m "refactor: reorganiza camada de serviços"
```

```bash
git commit -m "docs: atualiza instruções de instalação"
```

```bash
git commit -m "test: adiciona testes para ClienteService"
```

```bash
git commit -m "chore: configura conexão com PostgreSQL"
```

---

# Regras importantes

* Não fazer `push` diretamente na `main`.
* Criar uma branch para cada funcionalidade ou tarefa.
* Manter a branch atualizada antes de iniciar o desenvolvimento.
* Utilizar mensagens de commit seguindo Conventional Commits.
* Criar Pull Request para integrar alterações.
* Não versionar arquivos `.env`.
* Não colocar senhas, tokens ou outras credenciais no código.
* Sempre revisar as alterações antes de abrir um Pull Request.

---

## Fluxo resumido

```text
main
  │
  └── develop
        │
        ├── feature/clientes
        │
        ├── feature/veiculos
        │
        └── feature/ordens-servico
                │
                └── Pull Request
                        ↓
                     develop
                        │
                        └── Pull Request
                                ↓
                               main
```
