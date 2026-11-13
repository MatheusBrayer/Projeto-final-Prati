import { useState } from "react";
import { Link } from "react-router-dom";
import "./Home.css";

const features = [
  {
    icon: "◈",
    number: "01",
    title: "Clientes em ordem",
    text: "Centralize os cadastros e encontre as informações de cada cliente sem perder tempo.",
  },
  {
    icon: "▣",
    number: "02",
    title: "Veículos organizados",
    text: "Associe veículos aos clientes e mantenha as informações disponíveis para a equipe.",
  },
  {
    icon: "☷",
    number: "03",
    title: "Ordens de serviço",
    text: "Registre, organize e acompanhe as ordens de serviço em um só lugar.",
  },
  {
    icon: "↗",
    number: "04",
    title: "Portal do cliente",
    text: "Ofereça acesso às informações de serviço pelo link exclusivo da sua oficina.",
  },
];

const faqs = [
  {
    question: "Como faço para contratar?",
    answer:
      "Clique em Contratar agora e fale com nossa equipe pelo WhatsApp. O cadastro da oficina e a liberação do acesso são realizados após o atendimento.",
  },
  {
    question: "Preciso instalar algum programa?",
    answer:
      "Não. O AutoGestão Pro é uma plataforma web, acessada pelo navegador em dispositivos compatíveis.",
  },
  {
    question: "Meus clientes precisam acessar o painel administrativo?",
    answer:
      "Não. Cada oficina possui um link próprio para o acesso dos clientes, separado da área de gestão.",
  },
  {
    question: "Quais recursos estão disponíveis?",
    answer:
      "A versão inicial contempla gestão de clientes, veículos, ordens de serviço e acesso do cliente. Outros módulos poderão ser adicionados futuramente.",
  },
];

function Icon({ name, size = 20 }) {
  const paths = {
    arrow: (
      <>
        <path d="M5 12h14" />
        <path d="m12 5 7 7-7 7" />
      </>
    ),
    check: <path d="m5 12 4 4L19 6" />,
    menu: <path d="M4 7h16M4 12h16M4 17h16" />,
    close: <path d="M5 5l14 14M19 5 5 19" />,
    wrench: (
      <path d="M14.7 6.3a5 5 0 0 0-6.4 6.4L3 18a2.1 2.1 0 0 0 3 3l5.3-5.3a5 5 0 0 0 6.4-6.4l-3.4 3.4-3-3 3.4-3.4Z" />
    ),
    shield: (
      <>
        <path d="m12 2 8 4v6c0 5-3.5 8.5-8 10-4.5-1.5-8-5-8-10V6l8-4Z" />
        <path d="m9 12 2 2 4-4" />
      </>
    ),
    message: (
      <>
        <path d="M20 11.5a8 8 0 0 1-8 8 8.7 8.7 0 0 1-3.6-.8L4 20l1.3-4.4A8.5 8.5 0 1 1 20 11.5Z" />
        <path d="M9 10h6M9 13h4" />
      </>
    ),
  };

  return (
    <svg
      aria-hidden="true"
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      {paths[name]}
    </svg>
  );
}

function DashboardPreview() {
  return (
    <div
      className="ap-preview"
      aria-label="Exemplo ilustrativo da interface da plataforma"
    >
      <div className="ap-preview-sidebar">
        <div className="ap-preview-brand">
          <span className="ap-preview-brand-icon">A</span>
          <span>
            auto<span className="ap-preview-brand-strong">gestão</span>
          </span>
        </div>

        <div className="ap-preview-side-label">MENU PRINCIPAL</div>
        <div className="ap-preview-side-link is-active">
          ▦ <span>Visão geral</span>
        </div>
        <div className="ap-preview-side-link">
          ♙ <span>Clientes</span>
        </div>
        <div className="ap-preview-side-link">
          ▤ <span>Veículos</span>
        </div>
        <div className="ap-preview-side-link">
          ☷ <span>Ordens de serviço</span>
        </div>

        <div className="ap-preview-side-bottom">
          ● <span>Oficina conectada</span>
        </div>
      </div>

      <div className="ap-preview-main">
        <div className="ap-preview-top">
          <span>
            Visão geral <span className="ap-preview-muted">/ Dashboard</span>
          </span>
          <span className="ap-preview-avatar">OC</span>
        </div>

        <div className="ap-preview-heading">
          <div>
            <small>PAINEL DA OFICINA</small>
            <h3>
              Bom dia, equipe <span>✳</span>
            </h3>
            <p>Confira o resumo das suas operações.</p>
          </div>
          <span className="ap-preview-period">Este mês ▾</span>
        </div>

        <div className="ap-preview-stats">
          <div>
            <span>Ordens abertas</span>
            <strong>24</strong>
            <small>↗ Em acompanhamento</small>
          </div>
          <div>
            <span>Em andamento</span>
            <strong>18</strong>
            <small>● Serviços ativos</small>
          </div>
          <div>
            <span>Clientes</span>
            <strong>156</strong>
            <small>↗ Cadastros registrados</small>
          </div>
        </div>

        <div className="ap-preview-table">
          <div className="ap-preview-table-heading">
            <strong>Ordens recentes</strong>
            <span>Visualização ilustrativa</span>
          </div>

          <div className="ap-preview-tr ap-preview-th">
            <span>SERVIÇO</span>
            <span>VEÍCULO</span>
            <span>STATUS</span>
          </div>

          <div className="ap-preview-tr">
            <span>Revisão preventiva</span>
            <span>Honda Civic</span>
            <span>
              <i className="ap-status ap-status-blue">Em andamento</i>
            </span>
          </div>

          <div className="ap-preview-tr">
            <span>Troca de óleo</span>
            <span>VW Polo</span>
            <span>
              <i className="ap-status ap-status-green">Concluída</i>
            </span>
          </div>

          <div className="ap-preview-tr">
            <span>Sistema de freios</span>
            <span>Fiat Argo</span>
            <span>
              <i className="ap-status ap-status-amber">Aguardando</i>
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function Home() {
  const [menuOpen, setMenuOpen] = useState(false);
  const [openFaq, setOpenFaq] = useState(0);

  // Busca o número no .env
  const phone = (import.meta.env.VITE_WHATSAPP_COMERCIAL || "").replace(
    /\D/g,
    "",
  );

  const message = encodeURIComponent(
    "Olá! Conheci o AutoGestão Pro e gostaria de saber mais sobre a contratação da plataforma.",
  );

  const whatsappUrl = phone ? `https://wa.me/${phone}?text=${message}` : null;

  const contact = (label, className = "ap-btn ap-btn-primary") =>
    whatsappUrl ? (
      <a
        className={className}
        href={whatsappUrl}
        target="_blank"
        rel="noopener noreferrer"
      >
        <Icon name="message" size={18} />
        {label}
        <Icon name="arrow" size={17} />
      </a>
    ) : (
      <span
        className={`${className} ap-btn-disabled`}
        title="Configure VITE_WHATSAPP_COMERCIAL no .env do frontend"
        aria-disabled="true"
      >
        <Icon name="message" size={18} />
        Contato em breve
      </span>
    );

  return (
    <div className="ap-home">
      <header className="ap-header">
        <div className="ap-container ap-header-inner">
          <Link className="ap-logo" to="/" aria-label="AutoGestão Pro, início">
            <span className="ap-logo-symbol">
              <Icon name="wrench" size={20} />
            </span>
            <span>
              AutoGestão <b>Pro</b>
            </span>
          </Link>

          <nav
            className={menuOpen ? "ap-nav is-open" : "ap-nav"}
            aria-label="Navegação principal"
          >
            <a href="#funcionalidades" onClick={() => setMenuOpen(false)}>
              Funcionalidades
            </a>
            <a href="#como-funciona" onClick={() => setMenuOpen(false)}>
              Como funciona
            </a>
            <a href="#planos" onClick={() => setMenuOpen(false)}>
              Contratação
            </a>
            <a href="#faq" onClick={() => setMenuOpen(false)}>
              Dúvidas
            </a>
            <Link
              className="ap-nav-mobile-login"
              to="/login"
              onClick={() => setMenuOpen(false)}
            >
              Entrar na plataforma
            </Link>
          </nav>

          <div className="ap-header-actions">
            <Link className="ap-login" to="/login">
              Entrar <Icon name="arrow" size={16} />
            </Link>
            <button
              type="button"
              className="ap-menu-toggle"
              aria-label={menuOpen ? "Fechar menu" : "Abrir menu"}
              aria-expanded={menuOpen}
              onClick={() => setMenuOpen(!menuOpen)}
            >
              <Icon name={menuOpen ? "close" : "menu"} size={24} />
            </button>
          </div>
        </div>
      </header>

      <main>
        <section className="ap-hero">
          <div className="ap-container">
            <div className="ap-hero-copy">
              <div className="ap-eyebrow">
                <span className="ap-eyebrow-dot" />A NOVA FORMA DE GERENCIAR SUA
                OFICINA
              </div>

              <h1>
                Menos correria.
                <br />
                <span>Mais controle.</span>
                <br />
                Sua oficina evolui.
              </h1>

              <p>
                Clientes, veículos e ordens de serviço em um único lugar.
                Organize sua operação, ganhe clareza e dedique mais tempo ao que
                realmente importa.
              </p>

              <div className="ap-hero-actions">
                {contact("Quero contratar")}
                <a className="ap-btn ap-btn-outline" href="#funcionalidades">
                  Explorar funcionalidades
                  <Icon name="arrow" size={18} />
                </a>
              </div>

              <div className="ap-hero-note">
                <Icon name="shield" size={17} />
                Acesso exclusivo para cada oficina e seus clientes
              </div>
            </div>

            <div className="ap-hero-visual">
              <div className="ap-preview-glow" />
              <DashboardPreview />
              <div className="ap-floating-note">
                <span>✓</span>
                <div>
                  <strong>Mais organização</strong>
                  <small>Em cada etapa do serviço</small>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="ap-benefit-strip">
          <div className="ap-container ap-benefit-inner">
            <span>UMA PLATAFORMA. TUDO CONECTADO.</span>
            <div>
              <span>✓ Gestão centralizada</span>
              <span>✓ Acesso pelo navegador</span>
              <span>✓ Portal do cliente</span>
            </div>
          </div>
        </section>

        <section id="funcionalidades" className="ap-section ap-features">
          <div className="ap-container">
            <div className="ap-section-intro">
              <span className="ap-kicker">FUNCIONALIDADES</span>
              <h2>
                O dia a dia da sua oficina,
                <br />
                <em>sem complicação.</em>
              </h2>
              <p>
                Ferramentas que trabalham juntas para transformar informações
                espalhadas em uma rotina mais organizada.
              </p>
            </div>

            <div className="ap-feature-grid">
              {features.map((feature) => (
                <article className="ap-feature-card" key={feature.number}>
                  <div className="ap-feature-top">
                    <span className="ap-feature-icon">{feature.icon}</span>
                    <span className="ap-feature-number">{feature.number}</span>
                  </div>
                  <h3>{feature.title}</h3>
                  <p>{feature.text}</p>
                  <span className="ap-feature-line" />
                </article>
              ))}
            </div>
          </div>
        </section>

        <section className="ap-showcase">
          <div className="ap-container ap-showcase-grid">
            <div className="ap-showcase-copy">
              <span className="ap-kicker">FEITO PARA A ROTINA REAL</span>
              <h2>
                Menos informação perdida. <em>Mais decisões claras.</em>
              </h2>
              <p>
                Deixe as planilhas dispersas e os registros difíceis de
                encontrar para trás. Acompanhe o que acontece na oficina com uma
                visão mais simples e conectada.
              </p>

              <div className="ap-check-list">
                <span>
                  <Icon name="check" size={17} />
                  Cadastros de clientes e veículos
                </span>
                <span>
                  <Icon name="check" size={17} />
                  Acompanhamento de ordens de serviço
                </span>
                <span>
                  <Icon name="check" size={17} />
                  Área exclusiva para o cliente
                </span>
              </div>

              <a href="#como-funciona" className="ap-text-link">
                Veja como começar
                <Icon name="arrow" size={18} />
              </a>
            </div>

            <div className="ap-showcase-art">
              <div className="ap-art-circle" />
              <div className="ap-art-card ap-art-card-one">
                <small>CLIENTES E VEÍCULOS</small>
                <strong>Informações conectadas</strong>
                <div className="ap-art-lines">
                  <span />
                  <span />
                  <span />
                </div>
              </div>

              <div className="ap-art-card ap-art-card-two">
                <span className="ap-art-check">✓</span>
                <div>
                  <strong>Ordem atualizada</strong>
                  <small>Seu fluxo mais organizado</small>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section id="como-funciona" className="ap-section ap-steps">
          <div className="ap-container">
            <div className="ap-section-intro ap-center">
              <span className="ap-kicker">SIMPLES PARA COMEÇAR</span>
              <h2>
                Da conversa à gestão
                <br />
                <em>em três passos.</em>
              </h2>
              <p>
                Sem cadastros complicados: nossa equipe acompanha a ativação da
                sua oficina.
              </p>
            </div>

            <div className="ap-step-grid">
              <article>
                <span>01</span>
                <h3>Fale com a equipe</h3>
                <p>
                  Entre em contato pelo WhatsApp e conheça as condições de
                  contratação.
                </p>
              </article>
              <article>
                <span>02</span>
                <h3>Ativamos sua oficina</h3>
                <p>
                  Após a contratação, configuramos o acesso inicial do
                  administrador da oficina.
                </p>
              </article>
              <article>
                <span>03</span>
                <h3>Comece a organizar</h3>
                <p>
                  Acesse a plataforma e gerencie clientes, veículos e ordens de
                  serviço.
                </p>
              </article>
            </div>
          </div>
        </section>

        <section id="planos" className="ap-section ap-pricing">
          <div className="ap-container ap-pricing-grid">
            <div>
              <span className="ap-kicker ap-kicker-light">
                PRONTO PARA EVOLUIR?
              </span>
              <h2>
                Sua oficina merece
                <br />
                <em>uma gestão melhor.</em>
              </h2>
              <p>
                Converse com nossa equipe, tire dúvidas e descubra as condições
                comerciais disponíveis para sua oficina.
              </p>
              <div className="ap-pricing-tags">
                <span>✓ Atendimento direto</span>
                <span>✓ Ativação acompanhada</span>
                <span>✓ Acesso web</span>
              </div>
            </div>

            <div className="ap-pricing-card">
              <span className="ap-pricing-card-label">
                CONTRATAÇÃO PERSONALIZADA
              </span>
              <h3>Vamos conversar?</h3>
              <p>
                Apresentamos a plataforma e explicamos como funciona a
                contratação e a liberação do acesso.
              </p>
              {contact(
                "Contratar pelo WhatsApp",
                "ap-btn ap-btn-primary ap-btn-full",
              )}
              <small>
                Atendimento comercial via WhatsApp. Nenhuma contratação é
                concluída automaticamente.
              </small>
            </div>
          </div>
        </section>

        <section id="faq" className="ap-section ap-faq">
          <div className="ap-container ap-faq-grid">
            <div>
              <span className="ap-kicker">PERGUNTAS FREQUENTES</span>
              <h2>
                Ainda ficou
                <br />
                <em>alguma dúvida?</em>
              </h2>
              <p>
                Reunimos respostas para ajudar você a conhecer melhor a
                plataforma.
              </p>
            </div>

            <div className="ap-faq-list">
              {faqs.map((item, index) => (
                <div className="ap-faq-item" key={item.question}>
                  <button
                    type="button"
                    aria-expanded={openFaq === index}
                    aria-controls={`ap-faq-${index}`}
                    onClick={() => setOpenFaq(openFaq === index ? -1 : index)}
                  >
                    <span>{item.question}</span>
                    <span className="ap-faq-toggle">
                      {openFaq === index ? "−" : "+"}
                    </span>
                  </button>

                  {openFaq === index && (
                    <p id={`ap-faq-${index}`}>{item.answer}</p>
                  )}
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="ap-final-cta">
          <div className="ap-container">
            <div>
              <span>COMECE UMA NOVA FASE</span>
              <h2>
                Organização hoje.
                <br />
                Mais possibilidades amanhã.
              </h2>
              <p>Leve sua oficina para uma rotina mais simples e conectada.</p>
            </div>
            {contact("Falar sobre contratação", "ap-btn ap-btn-light")}
          </div>
        </section>
      </main>

      <footer className="ap-footer">
        <div className="ap-container">
          <div className="ap-footer-main">
            <div>
              <Link className="ap-logo ap-logo-footer" to="/">
                <span className="ap-logo-symbol">
                  <Icon name="wrench" size={20} />
                </span>
                <span>
                  AutoGestão <b>Pro</b>
                </span>
              </Link>
              <p>Gestão mais simples para oficinas que querem ir além.</p>
            </div>

            <div className="ap-footer-links">
              <a href="#funcionalidades">Funcionalidades</a>
              <a href="#como-funciona">Como funciona</a>
              <a href="#planos">Contratação</a>
              <Link to="/login">Área administrativa</Link>
            </div>
          </div>

          <div className="ap-footer-bottom">
            <span>
              © {new Date().getFullYear()} AutoGestão Pro. Todos os direitos
              reservados.
            </span>
            <span>Feito para simplificar sua gestão.</span>
          </div>
        </div>
      </footer>
    </div>
  );
}
