import React, { useEffect, useState } from 'react';
import { getOrdensServico } from '../../services/api';

export default function Dashboard() {
  const [stats, setStats] = useState({
    ordensEmAndamento: 0,
    ordensFinalizadasHoje: 0,
    faturamentoTotal: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchDashboardData() {
      try {
        const ordens = await getOrdensServico();

        // Pega a data de hoje no formato YYYY-MM-DD para comparar se precisar, 
        // ou conta todas as finalizadas. Vamos contar as finalizadas em geral (ou ajustar por data se preferir).
        const emAndamento = ordens.filter(os => os.status !== 'FINALIZADA').length;
        const finalizadas = ordens.filter(os => os.status === 'FINALIZADA').length;
        
        const faturamento = ordens
          .filter(os => os.status === 'FINALIZADA')
          .reduce((acc, os) => acc + (Number(os.valorTotal) || 0), 0);

        setStats({
          ordensEmAndamento: emAndamento,
          ordensFinalizadasHoje: finalizadas,
          faturamentoTotal: faturamento
        });
      } catch (error) {
        console.error('Erro ao carregar dados do dashboard:', error);
      } finally {
        setLoading(false);
      }
    }
    fetchDashboardData();
  }, []);

  return (
    <div style={{ padding: '32px', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '26px', fontWeight: '700', color: '#1e293b', margin: 0 }}>Dashboard da Oficina</h1>
        <p style={{ fontSize: '14px', color: '#64748b', marginTop: '4px' }}>Acompanhe o status das ordens de serviço e o faturamento.</p>
      </div>

      {loading ? (
        <p style={{ color: '#64748b', fontSize: '14px' }}>Carregando métricas...</p>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '20px' }}>
          
          {/* Card OS em Andamento */}
          <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
            <span style={{ fontSize: '12px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase' }}>Ordens em Andamento</span>
            <h2 style={{ fontSize: '36px', fontWeight: '800', color: '#d97706', margin: '8px 0 0 0' }}>{stats.ordensEmAndamento}</h2>
          </div>

          {/* Card OS Finalizadas */}
          <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
            <span style={{ fontSize: '12px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase' }}>Ordens Finalizadas</span>
            <h2 style={{ fontSize: '36px', fontWeight: '800', color: '#16a34a', margin: '8px 0 0 0' }}>{stats.ordensFinalizadasHoje}</h2>
          </div>

          {/* Card Faturamento Total */}
          <div style={{ backgroundColor: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
            <span style={{ fontSize: '12px', fontWeight: '700', color: '#64748b', textTransform: 'uppercase' }}>Faturamento Total</span>
            <h2 style={{ fontSize: '36px', fontWeight: '800', color: '#2563eb', margin: '8px 0 0 0' }}>
              R$ {stats.faturamentoTotal.toFixed(2)}
            </h2>
          </div>

        </div>
      )}
    </div>
  );
}