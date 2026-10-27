import { Link } from "react-router-dom";
import { ArrowUpRight, Check } from "lucide-react";
import { Button } from "../components/ui/Button.jsx";
import { Header } from "../components/Header.jsx";
import { Footer } from "../components/Footer.jsx";
import { useDocumentTitle } from "../hooks/useDocumentTitle.js";

const features = [
  ["Agendamento simplificado", "Escolha o dia e o horário do serviço pelo formulário ou pelo contato da oficina."],
  ["Diagnóstico antes do reparo", "Nada é trocado sem antes identificar a causa real do problema."],
  ["Orçamento transparente", "Detalhamento de peças e serviços antes de autorizar qualquer reparo."],
  ["Histórico do veículo", "Cada visita registra o que foi feito, criando o histórico completo do carro."],
  ["Acompanhamento do serviço", "Você é avisado em cada etapa, da entrada do veículo à entrega."],
  ["Garantia do serviço", "Todo reparo sai com garantia registrada e orientações de cuidados."],
];

const benefits = [
  ["Previsibilidade", "Manutenção em dia, sem imprevistos no orçamento nem na agenda."],
  ["Menos quebras", "Revisão periódica reduz a chance de parada no meio do caminho."],
  ["Valor preservado", "Histórico completo e revisões em dia ajudam na revenda do veículo."],
  ["Segurança", "Freios, suspensão e pneus verificados a cada visita do plano."],
  ["Prioridade", "Planos têm agenda reservada: menos espera para atendimento."],
  ["Clareza", "Você sabe exatamente o que está incluso em cada visita à oficina."],
];

const plans = [
  {
    name: "Essencial",
    audience: "Para o uso diário",
    price: "R$ 149",
    period: "/mês",
    featured: false,
    items: [
      "Troca de óleo e filtros programada",
      "Inspeção de 20 pontos a cada visita",
      "10% de desconto em serviços avulsos",
      "1 revisão de freios por ano",
    ],
  },
  {
    name: "Completo",
    audience: "Para quem usa o carro todos os dias",
    price: "R$ 289",
    period: "/mês",
    featured: true,
    items: [
      "Tudo do plano Essencial",
      "Freios e suspensão verificados a cada visita",
      "Revisão semestral completa inclusa",
      "20% de desconto em serviços avulsos",
      "Atendimento prioritário na agenda",
    ],
  },
  {
    name: "Frota",
    audience: "Para empresas e frotas",
    price: "Sob consulta",
    period: "",
    featured: false,
    items: [
      "Até 10 veículos por contrato",
      "Relatório mensal de cada veículo",
      "Gerente de conta dedicado",
      "Agendamento programado em lote",
    ],
  },
];

export default function Planos() {
  useDocumentTitle("Torque Auto | Funcionalidades e planos");

  return (
    <div className="min-h-screen font-mono">
      <Header current="/planos" />
      <main>
        <section className="grid-bg section-border">
          <div className="page-container section-space">
            <p className="eyebrow">
              <span className="signal-dot" />
              Funcionalidades · Benefícios · Planos
            </p>
            <h1 className="plan-title">Manutenção em plano, sem surpresa no bolso.</h1>
            <p className="body-copy hero-description">
              Veja como a oficina cuida do seu carro em cada visita e escolha o plano que combina com
              a sua rotina.
            </p>
            <div className="hero-actions">
              <Button asChild variant="technical" size="lg">
                <Link to="/" hash="orcamento">
                  Solicitar orçamento <ArrowUpRight />
                </Link>
              </Button>
              <Button asChild variant="technicalOutline" size="lg">
                <a href="#planos">Ver planos</a>
              </Button>
            </div>
          </div>
        </section>

        <section id="funcionalidades" className="section-border">
          <div className="page-container section-space">
            <div className="section-heading">
              <h2>Funcionalidades</h2>
              <span className="micro-label">06 inclusas</span>
            </div>
            <div className="services-grid">
              {features.map(([name, description]) => (
                <article key={name} className="service-item">
                  <h3>{name}</h3>
                  <p>{description}</p>
                </article>
              ))}
            </div>
          </div>
        </section>

        <section id="beneficios" className="section-border">
          <div className="page-container section-space">
            <div className="section-heading">
              <h2>Principais benefícios</h2>
              <span className="micro-label">Para você e seu carro</span>
            </div>
            <div className="services-grid">
              {benefits.map(([name, description]) => (
                <article key={name} className="service-item">
                  <h3>{name}</h3>
                  <p>{description}</p>
                </article>
              ))}
            </div>
          </div>
        </section>

        <section id="planos" className="grid-bg section-border">
          <div className="page-container section-space">
            <div className="section-heading">
              <h2>Planos disponíveis</h2>
              <span className="micro-label">03 planos</span>
            </div>
            <div className="plans-grid">
              {plans.map((plan) => (
                <article key={plan.name} className={`plan-card${plan.featured ? " featured" : ""}`}>
                  <div className="plan-top">
                    <h3 className="plan-name">{plan.name}</h3>
                    {plan.featured && <span className="plan-badge">Mais escolhido</span>}
                  </div>
                  <p className="plan-for">{plan.audience}</p>
                  <p className="plan-price">
                    {plan.price}
                    {plan.period && <span className="plan-period">{plan.period}</span>}
                  </p>
                  <ul className="plan-features">
                    {plan.items.map((item) => (
                      <li key={item}>
                        <Check size={14} />
                        {item}
                      </li>
                    ))}
                  </ul>
                  <Button asChild variant={plan.featured ? "technical" : "technicalOutline"} className="w-full">
                    <Link to="/" hash="orcamento">
                      Solicitar orçamento
                    </Link>
                  </Button>
                </article>
              ))}
            </div>
            <p className="plan-note">* Valores ilustrativos — condições finais confirmadas no orçamento.</p>
          </div>
        </section>

        <section className="grid-bg section-border">
          <div className="quote-container section-space">
            <p className="eyebrow justify-center">Pronto para escolher seu plano</p>
            <h2>Fale com a oficina</h2>
            <p className="body-copy mt-4">
              Conte como você usa o seu carro e receba a recomendação do plano ideal.
            </p>
            <div className="hero-actions justify-center">
              <Button asChild variant="technical" size="lg">
                <Link to="/" hash="orcamento">
                  Solicitar orçamento <ArrowUpRight />
                </Link>
              </Button>
            </div>
          </div>
        </section>
      </main>
      <Footer />
    </div>
  );
}
