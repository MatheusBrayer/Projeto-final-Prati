# Torque Auto — React + Vite (JavaScript)

Versão em **React** com **Vite** e **JavaScript** (sem TypeScript) da página da oficina
Torque Auto. É a mesma aparência do projeto original em TanStack Start, trocando o
roteador por React Router e os arquivos `.tsx` por `.jsx`.

## Como rodar

```sh
npm install
npm run dev
```

Abra o endereço que aparece no terminal (por padrão `http://localhost:5173`).

Outros comandos:

```sh
npm run lint     # verifica o código com ESLint
npm run build    # gera a pasta dist/ pronta para publicar
npm run preview  # testa o build gerado
npm run format   # formata o código com Prettier
```

## Páginas

| Rota       | Arquivo               | O que mostra                                      |
| ---------- | --------------------- | ------------------------------------------------- |
| `/`        | `src/pages/Home.jsx`  | Início, serviços, a oficina e o pedido de orçamento |
| `/planos`  | `src/pages/Planos.jsx`| Funcionalidades, benefícios e os três planos      |
| qualquer   | `src/pages/NotFound.jsx` | Página 404                                     |

## Estrutura

```text
index.html                 título, descrição e fontes da página
vite.config.js             React + Tailwind CSS v4
eslint.config.js           regras do ESLint (JavaScript/JSX)
src/
  main.jsx                 liga o React ao index.html
  App.jsx                  rotas e rolagem até as âncoras
  styles.css               cores, fontes e layout responsivo
  components/
    Header.jsx             cabeçalho fixo com menu do celular
    Footer.jsx             rodapé
    Brand.jsx              logo Torque Auto
    ui/Button.jsx          botões (variantes technical e technicalOutline)
  hooks/
    useScrollToHash.js     rola até a seção pedida pelo link
    useDocumentTitle.js    muda o título conforme a página
  pages/                   Home, Planos e NotFound
  assets/                  mechanic.jpg e workshop.jpg
```

## O que mudou em relação ao original

- **React Router** no lugar de TanStack Router (`<Link to="/" hash="orcamento">`);
  a rolagem até a âncora é feita por `useScrollToHash`.
- **Tailwind CSS v4** pelo plugin `@tailwindcss/vite`, sem arquivo de configuração.
- **JavaScript** em todos os arquivos (`.jsx` e `.js`), sem tipos.
- Cabeçalho e rodapé viraram componentes compartilhados entre as duas páginas.

## Avisos

- O formulário de orçamento **não envia nada**: ele apenas baixa um arquivo
  `pedido-de-orcamento.txt` com o que foi digitado.
- Os preços dos planos (R$ 149, R$ 289 e "sob consulta") são **exemplos**.
- "Torque Auto" é uma **marca ilustrativa**; o contato no rodapé está "a confirmar".
