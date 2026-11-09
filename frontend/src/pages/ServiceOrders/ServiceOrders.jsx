import React, { useEffect, useState } from 'react';
import { getOrdensServico, createOrdemServico, finalizarOrdemServico } from '../../services/api';

export default function ServiceOrders() {
  const [ordens, setOrdens] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modelo, setModelo] = useState('');
  const [placa, setPlaca] = useState('');
  const [cliente, setCliente] = useState('');
  const [problema, setProblema] = useState('');
  const [status, setStatus] = useState('EM ANDAMENTO');
  const [valorTotal, setValorTotal] = useState('');

  async function fetchOrdens() {
    try {
      const data = await getOrdensServico();
      setOrdens(data);
    } catch (error) {
      console.error('Erro ao carregar ordens de serviço:', error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchOrdens();
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      const novaOrdem = {
        modelo,
        placa,
        cliente,
        problema,
        status,
        valorTotal: parseFloat(valorTotal) || 0
      };

      await createOrdemServico(novaOrdem);
      
      setIsModalOpen(false);
      setModelo('');
      setPlaca('');
      setCliente('');
      setProblema('');
      setStatus('EM ANDAMENTO');
      setValorTotal('');
      
      fetchOrdens();
    } catch (error) {
      alert('Erro ao criar ordem de serviço.');
    }
  }

  async function handleFinalizar(id) {
    try {
      await finalizarOrdemServico(id);
      fetchOrdens();
    } catch (error) {
      alert('Erro ao finalizar a ordem de serviço.');
    }
  }

  return (
    <div style={{ padding: '32px', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '26px', fontWeight: '700', color: '#1e293b', margin: 0 }}>Ordens de Serviço</h1>
          <p style={{ fontSize: '14px', color: '#64748b', marginTop: '4px' }}>Acompanhe o andamento dos serviços e manutenções na oficina.</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          style={{ 
            backgroundColor: '#2563eb', color: '#ffffff', fontWeight: '600', 
            padding: '10px 16px', borderRadius: '8px', border: 'none', cursor: 'pointer', fontSize: '14px'
          }}
        >
          + Nova Ordem de Serviço
        </button>
      </div>

      <div style={{ backgroundColor: '#ffffff', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)', borderRadius: '12px', overflow: 'hidden', border: '1px solid #e2e8f0' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
          <thead>
            <tr style={{ backgroundColor: '#f8fafc', borderBottom: '1px solid #e2e8f0' }}>
              <th style={{ padding: '16px', fontSize: '12px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase' }}>OS #</th>
              <th style={{ padding: '16px', fontSize: '12px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase' }}>Veículo / Placa</th>
              <th style={{ padding: '16px', fontSize: '12px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase' }}>Cliente</th>
              <th style={{ padding: '16px', fontSize: '12px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase' }}>Problema Relatado</th>
              <th style={{ padding: '16px', fontSize: '12px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase' }}>Status</th>
              <th style={{ padding: '16px', fontSize: '12px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase' }}>Valor Total</th>
              <th style={{ padding: '16px', fontSize: '12px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase', textAlign: 'center' }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="7" style={{ padding: '24px', textAlign: 'center', color: '#64748b', fontSize: '14px' }}>Carregando...</td>
              </tr>
            ) : ordens.length === 0 ? (
              <tr>
                <td colSpan="7" style={{ padding: '24px', textAlign: 'center', color: '#64748b', fontSize: '14px' }}>Nenhuma ordem cadastrada.</td>
              </tr>
            ) : (
              ordens.map((os) => (
                <tr key={os.id} style={{ borderBottom: '1px solid #e2e8f0' }}>
                  <td style={{ padding: '16px', fontSize: '14px', fontWeight: '700', color: '#1e293b' }}>{String(os.id).padStart(3, '0')}</td>
                  <td style={{ padding: '16px', fontSize: '14px', color: '#334155' }}>
                    <div style={{ fontWeight: '600' }}>{os.modelo}</div>
                    <div style={{ fontSize: '12px', color: '#64748b' }}>{os.placa}</div>
                  </td>
                  <td style={{ padding: '16px', fontSize: '14px', color: '#334155' }}>{os.cliente}</td>
                  <td style={{ padding: '16px', fontSize: '14px', color: '#64748b', maxWidth: '200px' }}>{os.problema}</td>
                  <td style={{ padding: '16px', fontSize: '14px' }}>
                    <span style={{ 
                      padding: '4px 10px', fontSize: '11px', fontWeight: '700', borderRadius: '9999px',
                      backgroundColor: os.status === 'FINALIZADA' ? '#dcfce7' : '#fef9c3',
                      color: os.status === 'FINALIZADA' ? '#166534' : '#854d0e'
                    }}>
                      {os.status}
                    </span>
                  </td>
                  <td style={{ padding: '16px', fontSize: '14px', fontWeight: '700', color: '#1e293b' }}>R$ {Number(os.valorTotal || 0).toFixed(2)}</td>
                  <td style={{ padding: '16px', textAlign: 'center' }}>
                    {os.status !== 'FINALIZADA' && (
                      <button 
                        onClick={() => handleFinalizar(os.id)}
                        style={{
                          backgroundColor: '#16a34a', color: '#ffffff', border: 'none',
                          padding: '6px 12px', borderRadius: '6px', fontSize: '12px', fontWeight: '600', cursor: 'pointer'
                        }}
                      >
                        Finalizar
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <div style={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 50, padding: '16px' }}>
          <div style={{ backgroundColor: '#ffffff', borderRadius: '12px', width: '100%', maxWidth: '500px', padding: '24px', boxShadow: '0 20px 25px -5px rgba(0,0,0,0.1)' }}>
            <h2 style={{ fontSize: '20px', fontWeight: '700', color: '#1e293b', marginBottom: '16px' }}>Nova Ordem de Serviço</h2>
            
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '12px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '4px', textTransform: 'uppercase' }}>Modelo do Veículo</label>
                  <input 
                    type="text" required placeholder="Ex: Fuscão Preto" 
                    value={modelo} onChange={(e) => setModelo(e.target.value)}
                    style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', boxSizing: 'border-box' }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '4px', textTransform: 'uppercase' }}>Placa</label>
                  <input 
                    type="text" required placeholder="BYY5D73" 
                    value={placa} onChange={(e) => setPlaca(e.target.value)}
                    style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', boxSizing: 'border-box' }}
                  />
                </div>
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '4px', textTransform: 'uppercase' }}>Cliente Proprietário</label>
                <input 
                  type="text" required placeholder="Ex: Eduardo Vilches" 
                  value={cliente} onChange={(e) => setCliente(e.target.value)}
                  style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', boxSizing: 'border-box' }}
                />
              </div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '4px', textTransform: 'uppercase' }}>Problema Relatado</label>
                <textarea 
                  required rows="3" placeholder="Descreva o problema..." 
                  value={problema} onChange={(e) => setProblema(e.target.value)}
                  style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', boxSizing: 'border-box', fontFamily: 'inherit' }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '4px', textTransform: 'uppercase' }}>Status</label>
                  <select 
                    value={status} onChange={(e) => setStatus(e.target.value)}
                    style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', boxSizing: 'border-box', backgroundColor: '#fff' }}
                  >
                    <option value="EM ANDAMENTO">EM ANDAMENTO</option>
                    <option value="FINALIZADA">FINALIZADA</option>
                    <option value="PENDENTE">PENDENTE</option>
                  </select>
                </div>

                <div>
                  <label style={{ display: 'block', fontSize: '12px', fontWeight: '700', color: '#475569', marginBottom: '4px', textTransform: 'uppercase' }}>Valor Total (R$)</label>
                  <input 
                    type="number" step="0.01" required placeholder="0.00" 
                    value={valorTotal} onChange={(e) => setValorTotal(e.target.value)}
                    style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '14px', boxSizing: 'border-box' }}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '16px' }}>
                <button 
                  type="button" onClick={() => setIsModalOpen(false)}
                  style={{ padding: '10px 16px', borderRadius: '6px', border: '1px solid #cbd5e1', backgroundColor: '#fff', color: '#334155', fontWeight: '600', cursor: 'pointer', fontSize: '14px' }}
                >
                  Cancelar
                </button>
                <button 
                  type="submit"
                  style={{ padding: '10px 16px', borderRadius: '6px', border: 'none', backgroundColor: '#2563eb', color: '#fff', fontWeight: '600', cursor: 'pointer', fontSize: '14px' }}
                >
                  Salvar OS
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}