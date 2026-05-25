"use client";

import { useSyncLogs } from "@/hooks/useSyncLogs";
import { SyncLogTable } from "@/components/SyncLogTable";

export default function MonitoringPage() {
  const { data: logs = [], isLoading, error } = useSyncLogs();

  return (
    <div className="space-y-8">
      <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-semibold text-slate-900">Monitoramento</h1>
        <p className="mt-2 text-sm text-slate-600">Veja os últimos logs de sincronização do backend.</p>
      </div>

      <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        {isLoading ? (
          <div className="text-slate-700">Carregando logs...</div>
        ) : error ? (
          <div className="rounded-3xl border border-rose-200 bg-rose-50 p-6 text-rose-700">Erro ao carregar logs.</div>
        ) : (
          <SyncLogTable logs={logs} />
        )}
      </div>
    </div>
  );
}
