"use client";

import type { SyncLog } from "@/lib/types";

interface SyncLogTableProps {
  logs: SyncLog[];
}

const statusStyles: Record<SyncLog["status"], string> = {
  SUCCESS: "bg-emerald-100 text-emerald-700",
  FAILED: "bg-rose-100 text-rose-700",
  RUNNING: "bg-amber-100 text-amber-700",
};

export function SyncLogTable({ logs }: SyncLogTableProps) {
  if (logs.length === 0) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-6 text-slate-600 shadow-sm">
        Nenhum log de sincronização disponível.
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50 text-left text-xs uppercase tracking-[0.15em] text-slate-500">
          <tr>
            <th className="px-4 py-3">Tipo</th>
            <th className="px-4 py-3">Status</th>
            <th className="px-4 py-3">Registros</th>
            <th className="px-4 py-3">Iniciado</th>
            <th className="px-4 py-3">Finalizado</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200">
          {logs.map((log) => (
            <tr key={log.id} className="hover:bg-slate-50">
              <td className="px-4 py-3">{log.syncType}</td>
              <td className="px-4 py-3">
                <span className={`inline-flex rounded-full px-3 py-1 text-[0.75rem] font-semibold ${statusStyles[log.status]}`}>
                  {log.status}
                </span>
              </td>
              <td className="px-4 py-3">{log.recordsProcessed}</td>
              <td className="px-4 py-3">{new Date(log.startedAt).toLocaleString()}</td>
              <td className="px-4 py-3">{log.finishedAt ? new Date(log.finishedAt).toLocaleString() : "—"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
