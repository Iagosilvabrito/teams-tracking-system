'use client';

import { use, useState } from "react";
import Link from "next/link";
import { AgentForm } from "@/components/agent/AgentForm";
import { CheckInForm } from "@/components/checkin/CheckInForm";
import { CheckInList } from "@/components/checkin/CheckInList";
import { RouteHistoryCard } from "@/components/route/RouteHistoryCard";
import { useAgent, useUpdateAgent } from "@/hooks/useAgent";
import { useAgentCheckIns, useCreateCheckIn } from "@/hooks/useCheckIns";
import { useRouteHistory } from "@/hooks/useRouteHistory";

interface AgentPageProps {
  params: Promise<{ id: string }>;
}

export default function AgentDetailPage({ params }: AgentPageProps) {
  const { id } = use(params);
  const agentId = Number(id);
  const { data: agent, isLoading, error } = useAgent(agentId);
  const updateAgent = useUpdateAgent(agentId);
  const checkIns = useAgentCheckIns(agentId);
  const createCheckIn = useCreateCheckIn(agentId);
  const routeHistory = useRouteHistory(agentId);
  const [creatingCheckIn, setCreatingCheckIn] = useState(false);

  async function handleUpdate(values: Parameters<typeof updateAgent.mutateAsync>[0]) {
    await updateAgent.mutateAsync(values);
  }

  async function handleCheckIn(values: Parameters<typeof createCheckIn.mutateAsync>[0]) {
    setCreatingCheckIn(true);
    try {
      await createCheckIn.mutateAsync(values);
    } finally {
      setCreatingCheckIn(false);
    }
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col gap-4 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">Detalhes do agente</h1>
          <p className="text-sm text-slate-600">Atualize dados, registre check-ins e veja a rota do dia.</p>
          {agent && <p className="mt-2 text-sm text-slate-500">{agent.name} • {agent.externalId}</p>}
        </div>
        <Link href="/agents" className="inline-flex items-center rounded-full border border-slate-300 bg-slate-50 px-4 py-2 text-sm text-slate-700 transition hover:bg-slate-100">
          Voltar para agentes
        </Link>
      </div>

      {isLoading ? (
        <div className="rounded-3xl border border-slate-200 bg-white p-6 text-slate-700 shadow-sm">Carregando agente...</div>
      ) : error || !agent ? (
        <div className="rounded-3xl border border-rose-200 bg-rose-50 p-6 text-rose-700 shadow-sm">Não foi possível carregar o agente.</div>
      ) : (
        <div className="grid gap-8 xl:grid-cols-[380px_1fr]">
          <div className="space-y-6">
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
              <h2 className="mb-4 text-lg font-semibold text-slate-900">Atualizar agente</h2>
              <AgentForm
                initialValues={{ name: agent.name, status: agent.status }}
                onSubmit={handleUpdate}
                submitLabel={updateAgent.status === "pending" ? "Salvando..." : "Salvar alterações"}
              />
              {updateAgent.isError && (
                <p className="mt-3 rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">Erro: {updateAgent.error?.message}</p>
              )}
            </div>

            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
              <h2 className="mb-4 text-lg font-semibold text-slate-900">Registrar check-in</h2>
              <CheckInForm onSubmit={handleCheckIn} />
              {createCheckIn.isError && (
                <p className="mt-3 rounded-2xl bg-rose-50 px-4 py-3 text-sm text-rose-700">Erro: {createCheckIn.error?.message}</p>
              )}
            </div>
          </div>

          <div className="space-y-6">
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
              <h2 className="mb-4 text-lg font-semibold text-slate-900">Check-ins recentes</h2>
              {checkIns.isLoading ? (
                <div className="text-slate-700">Carregando check-ins...</div>
              ) : checkIns.data ? (
                <CheckInList checkIns={checkIns.data} />
              ) : (
                <div className="text-slate-600">Nenhum check-in disponível.</div>
              )}
            </div>

            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
              {routeHistory.isLoading ? (
                <div className="text-slate-700">Carregando rota...</div>
              ) : routeHistory.data ? (
                <RouteHistoryCard routeHistory={routeHistory.data} />
              ) : (
                <div className="text-slate-600">Rota do dia não disponível.</div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}