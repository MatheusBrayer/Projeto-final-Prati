import { useState } from "react";
import { ArrowUpRight, Check, Download, MessageCircle } from "lucide-react";
import { Button } from "../components/ui/Button.jsx";
import { Header } from "../components/Header.jsx";
import { Footer } from "../components/Footer.jsx";
import { useDocumentTitle } from "../hooks/useDocumentTitle.js";
import { HIRE_MESSAGE, WHATSAPP_DISPLAY, quoteMessage, whatsappUrl } from "../lib/contact.js";
import mechanic from "../assets/mechanic.jpg";
import workshop from "../assets/workshop.jpg";

const services = [
  ["Motor & injeção", "Manutenção preventiva, revisão de injeção e testes de compressão."],
  ["Freios & suspensão", "Troca de discos, pastilhas, amortecedores e alinhamento."],
  ["Elétrica automotiva", "Bateria, alternador e diagnóstico de falhas elétricas."],
  ["Ar-condicionado", "Higienização, recarga de gás e reparo de compressor."],
  ["Transmissão", "Troca de óleo, embreagem e revisão de câmbio manual ou automático."],
  ["Check-up geral", "Inspeção dos principais sistemas para cuidar de cada detalhe do veículo."],
];

const aboutPoints = [
  "Diagnóstico antes de qualquer reparo.",
  "Manutenção preventiva e corretiva.",
  "Atenção aos principais sistemas do veículo.",
];

export default function Home() {
  useDocumentTitle("Torque Auto | Diagnóstico e manutenção automotiva");
  const [prepared, setPrepared] = useState(false);
  const [whatsappLink, setWhatsappLink] = useState("");

  function prepareRequest(event) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const name = String(data.get("name") ?? "");
    const vehicle = String(data.get("vehicle") ?? "");
    const problem = String(data.get("problem") ?? "");
    const text = `SOLICITAÇÃO DE ORÇAMENTO\n\nNome: ${data.get("name")}\nVeículo: ${data.get(
      "vehicle",
    )}\nServiço ou problema: ${data.get(
      "problem",
    )}\n\nPedido preparado. Envie à oficina pelo canal oficial de contato.`;
    const url = URL.createObjectURL(new Blob([text], { type: "text/plain;charset=utf-8" }));
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = "pedido-de-orcamento.txt";
    anchor.click();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
    setWhatsappLink(whatsappUrl(quoteMessage({ name, vehicle, problem })));
    setPrepared(true);
  }

  return (
    <div id="inicio" className="min-h-screen font-mono">
      <Header current="/" />
      <main>
        <section className="grid-bg section-border">
          <div className="page-container hero-grid">
            <div className="hero-copy">
              <p className="eyebrow">
                <span className="signal-dot" />
                Oficina mecânica · diagnóstico de precisão
              </p>
              <h1>Seu carro volta pra rua com a precisão que merece.</h1>
              <p className="body-copy hero-description">
                Do motor ao sistema elétrico, cuidado em cada detalhe. Diagnóstico, manutenção e
                reparo para você seguir em frente com tranquilidade.
              </p>
              <div className="hero-actions">
                <Button asChild variant="technical" size="lg">
                  <a href="#orcamento">
                    Solicitar orçamento <ArrowUpRight />
                  </a>
                </Button>
                <Button asChild variant="technicalOutline" size="lg">
                  <a href="#servicos">Ver serviços</a>
                </Button>
              </div>
              <dl className="hero-facts">
                <div>
                  <dt>Diagnóstico</dt>
                  <dd>Precisão</dd>
                </div>
                <div>
                  <dt>Manutenção</dt>
                  <dd>Cuidado</dd>
                </div>
                <div>
                  <dt>Atendimento</dt>
                  <dd>Clareza</dd>
                </div>
              </dl>
            </div>
            <div className="hero-media">
              <img
                src={mechanic}
                alt="Mecânico usando torquímetro na manutenção de um motor"
                width={1088}
                height={1360}
                fetchPriority="high"
              />
              <div className="image-caption">
                <p className="micro-label">Cada detalhe importa</p>
                <p>
                  <Check size={16} /> Precisão no cuidado
                </p>
              </div>
            </div>
          </div>
        </section>

        <section id="servicos" className="section-border">
          <div className="page-container section-space">
            <div className="section-heading">
              <h2>Serviços de oficina</h2>
              <span className="micro-label">06 categorias</span>
            </div>
            <div className="services-grid">
              {services.map(([name, description], index) => (
                <article key={name} className="service-item">
                  <span className="service-number">0{index + 1}</span>
                  <h3>{name}</h3>
                  <p>{description}</p>
                </article>
              ))}
            </div>
          </div>
        </section>

        <section id="oficina" className="section-border">
          <div className="page-container about-grid section-space">
            <img
              className="about-image"
              src={workshop}
              alt="Oficina organizada com veículo em elevador e bancada de ferramentas"
              width={1200}
              height={912}
              loading="lazy"
            />
            <div>
              <p className="eyebrow">A oficina</p>
              <h2 className="about-title">Tecnologia e transparência em cada reparo.</h2>
              <p className="body-copy mt-5">
                Um cuidado completo começa por entender o seu carro. Da identificação do problema à
                escolha da manutenção, clareza em cada etapa faz a diferença.
              </p>
              <ul className="about-list">
                {aboutPoints.map((text) => (
                  <li key={text}>
                    <span className="signal-dot" />
                    {text}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </section>

        <section id="orcamento" className="grid-bg section-border">
          <div className="quote-container section-space">
            <p className="eyebrow justify-center">Vamos cuidar do seu carro</p>
            <h2>Peça seu orçamento agora</h2>
            <p className="body-copy mt-4">
              Descreva o problema ou a manutenção desejada e prepare seu pedido de orçamento.
            </p>
            <form className="quote-form" onSubmit={prepareRequest} onChange={() => {
                setPrepared(false);
                setWhatsappLink("");
              }}>
              <label htmlFor="name">Seu nome</label>
              <input
                id="name"
                name="name"
                autoComplete="name"
                placeholder="Como podemos chamar você?"
                required
                maxLength={100}
              />
              <label htmlFor="vehicle">Seu veículo</label>
              <input
                id="vehicle"
                name="vehicle"
                placeholder="Modelo · ano · motorização"
                required
                maxLength={150}
              />
              <label htmlFor="problem">O que precisa ser feito?</label>
              <textarea
                id="problem"
                name="problem"
                placeholder="Conte o que está acontecendo com seu carro…"
                rows={4}
                required
                maxLength={3000}
              />
              <Button type="submit" variant="technical" className="w-full h-11 mt-2">
                <Download />
                Preparar solicitação
              </Button>
              <p className="form-note">
                O pedido é baixado como texto e você pode enviá-lo direto pelo WhatsApp da oficina.
              </p>
              {prepared && (
                <div className="form-done">
                  <p role="status" className="form-status">
                    <Check size={18} />
                    Pedido preparado e baixado. Envie pelo WhatsApp para a oficina receber.
                  </p>
                  <Button asChild variant="technical" className="w-full h-11">
                    <a href={whatsappLink} target="_blank" rel="noopener noreferrer">
                      <MessageCircle />
                      Enviar pelo WhatsApp
                    </a>
                  </Button>
                </div>
              )}
            </form>
          </div>
        </section>
        <section id="contato" className="section-border">
          <div className="page-container contact-grid section-space">
            <div>
              <p className="eyebrow">
                <span className="signal-dot" />
                Contato
              </p>
              <h2>Fale com a oficina e contrate.</h2>
              <p className="body-copy mt-5">
                Escolha um plano ou conte o que o seu carro precisa. A resposta chega pelo WhatsApp, no
                horário em que você pode falar.
              </p>
              <ul className="about-list">
                {[
                  "Atendimento direto pelo WhatsApp.",
                  "Orçamento antes de qualquer reparo.",
                  "Endereço e horário combinados no primeiro contato.",
                ].map((text) => (
                  <li key={text}>
                    <span className="signal-dot" />
                    {text}
                  </li>
                ))}
              </ul>
            </div>
            <div className="contact-card">
              <p className="micro-label">WhatsApp</p>
              <p className="contact-number">{WHATSAPP_DISPLAY}</p>
              <p className="contact-hint">
                O botão abre a conversa já com uma mensagem pronta sobre contratação.
              </p>
              <Button asChild variant="technical" size="lg" className="w-full">
                <a href={whatsappUrl(HIRE_MESSAGE)} target="_blank" rel="noopener noreferrer">
                  Quero contratar <ArrowUpRight />
                </a>
              </Button>
            </div>
          </div>
        </section>
      </main>
      <Footer />
    </div>
  );
}
