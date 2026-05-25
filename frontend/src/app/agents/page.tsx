"use client";

import { useState } from "react";
import { AgentForm } from "@/components/agent/AgentForm";
import { AgentTable } from "@/components/agent/AgentTable";
import { useAgents, useCreateAgent, useDeleteAgent } from "@/hooks/useAgents";

export default function AgentsPage() {
  const { data: agents = [], isLoading, error } = useAgents();
  const createAgent = useCreateAgent();
  const deleteAgent = useDeleteAgent();
  const [deletingId, setDeletingId] = useState<number | null>(null);

  async function handleCreate(data: Parameters<typeof createAgent.mutateAsync>[0]) {
    await createAgent.mutateAsync(data);
  }

  async function handleDelete(id: number) {
    setDeletingId(id);
    try {
      await deleteAgent.mutateAsync(id);
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <div className="space-y-8">
      <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-slate-900">Agentes</h1>
            <p className="text-sm leading-6 text-slate-600">Gerencie os agentes, crie novos registros e acesse o detalhe de cada um.</p>
          </div>
        </div>
      </div>

      <div className="grid gap-8 xl:grid-cols-[360px_1fr]">
        <div>
          <h2 className="mb-4 text-lg font-semibold text-slate-900">Criar agente</h2>
          <AgentForm onSubmit={handleCreate} submitLabel="Criar agente" />
          {createAgent.isError && (
            <p className="mt-3 rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">Erro: {createAgent.error?.message}</p>
          )}
        </div>

        <div className="space-y-4">
          <div className="flex items-center justify-between gap-4 rounded-3xl border border-slate-200 bg-white px-6 py-5 shadow-sm">
            <div>
              <h2 className="text-lg font-semibold text-slate-900">Lista de agentes</h2>
              <p className="text-sm text-slate-600">Use a tabela abaixo para visualizar e remover agentes.</p>
            </div>
          </div>

          {isLoading ? (
            <div className="rounded-3xl border border-slate-200 bg-white p-6 text-slate-700 shadow-sm">Carregando agentes...</div>
          ) : error ? (
            <div className="rounded-3xl border border-rose-200 bg-rose-50 p-6 text-rose-700 shadow-sm">Erro ao carregar agentes.</div>
          ) : (
            <AgentTable agents={agents} onDelete={handleDelete} deletingId={deletingId ?? undefined} />
          )}
        </div>
      </div>
    </div>
  );
}
