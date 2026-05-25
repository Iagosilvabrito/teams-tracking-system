"use client";

import Link from "next/link";
import type { Agent } from "@/lib/types";

interface AgentTableProps {
  agents: Agent[];
  onDelete: (id: number) => void;
  deletingId?: number;
}

function formatDate(date: string | null) {
  if (!date) return "—";
  return new Date(date).toLocaleString("pt-BR");
}

export function AgentTable({ agents, onDelete, deletingId }: AgentTableProps) {
  function handleDelete(agent: Agent) {
    if (confirm(`Deseja realmente excluir o agente "${agent.name}"?`)) {
      onDelete(agent.id);
    }
  }

  return (
    <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50 text-left text-xs uppercase tracking-[0.15em] text-slate-500">
          <tr>
            <th className="px-4 py-3">Nome</th>
            <th className="px-4 py-3">Status</th>
            <th className="px-4 py-3">Último visto</th>
            <th className="px-4 py-3">Ações</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200">
          {agents.map((agent) => (
            <tr key={agent.id} className="hover:bg-slate-50">
              <td className="px-4 py-3 font-medium text-slate-800">{agent.name}</td>
              <td className="px-4 py-3">
                <span className={`inline-flex rounded-full px-3 py-1 text-[0.75rem] font-semibold ${
                  agent.status === "ACTIVE"
                    ? "bg-emerald-100 text-emerald-700"
                    : "bg-slate-100 text-slate-700"
                }`}>
                  {agent.status === "ACTIVE" ? "Ativo" : "Inativo"}
                </span>
              </td>
              <td className="px-4 py-3 text-slate-600">{formatDate(agent.lastSeenAt)}</td>
              <td className="px-4 py-3 space-x-2">
                <Link
                  className="rounded-full border border-slate-300 bg-slate-50 px-4 py-2 text-slate-700 transition hover:bg-slate-100"
                  href={`/agents/${agent.id}`}
                >
                  Ver
                </Link>
                <button
                  type="button"
                  onClick={() => handleDelete(agent)}
                  disabled={deletingId === agent.id}
                  className="rounded-full bg-rose-500 px-4 py-2 text-white transition hover:bg-rose-600 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {deletingId === agent.id ? "Removendo..." : "Excluir"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}