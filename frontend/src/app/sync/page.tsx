"use client";

import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  getSyncLogs,
  listSyncLogs,
  syncAgents,
  syncAll,
  syncCheckIns,
  syncLocations,
} from "@/lib/api";
import { SyncLogTable } from "@/components/SyncLogTable";

export default function SyncPage() {
  const queryClient = useQueryClient();
  const [syncTypeFilter, setSyncTypeFilter] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  const logsQuery = useQuery({
    queryKey: ["syncLogs", syncTypeFilter],
    queryFn: () => (syncTypeFilter ? getSyncLogs(syncTypeFilter) : listSyncLogs()),
    staleTime: 1000 * 30,
  });

  const syncAgentsMutation = useMutation({
    mutationFn: syncAgents,
    onSuccess(result) {
      setMessage(`Agentes sincronizados: ${result.synced}`);
      queryClient.invalidateQueries({ queryKey: ["syncLogs"] });
    },
    onError(error: unknown) {
      setMessage(`Erro ao sincronizar agentes: ${error instanceof Error ? error.message : "Falha"}`);
    },
  });

  const syncLocationsMutation = useMutation({
    mutationFn: syncLocations,
    onSuccess(result) {
      setMessage(`Localizações sincronizadas: ${result.synced}`);
      queryClient.invalidateQueries({ queryKey: ["syncLogs"] });
    },
    onError(error: unknown) {
      setMessage(`Erro ao sincronizar localizações: ${error instanceof Error ? error.message : "Falha"}`);
    },
  });

  const syncCheckInsMutation = useMutation({
    mutationFn: syncCheckIns,
    onSuccess(result) {
      setMessage(`Check-ins sincronizados: ${result.synced}`);
      queryClient.invalidateQueries({ queryKey: ["syncLogs"] });
    },
    onError(error: unknown) {
      setMessage(`Erro ao sincronizar check-ins: ${error instanceof Error ? error.message : "Falha"}`);
    },
  });

  const syncAllMutation = useMutation({
    mutationFn: syncAll,
    onSuccess(result) {
      setMessage(`Tudo sincronizado: agentes ${result.agents}, localizações ${result.locations}, check-ins ${result.checkIns}`);
      queryClient.invalidateQueries({ queryKey: ["syncLogs"] });
    },
    onError(error: unknown) {
      setMessage(`Erro ao sincronizar tudo: ${error instanceof Error ? error.message : "Falha"}`);
    },
  });

  return (
    <div className="space-y-8">
      <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-slate-900">Sincronização</h1>
            <p className="text-sm leading-6 text-slate-600">Execute sincronizações e acompanhe os logs do backend.</p>
          </div>
        </div>
      </div>

      <div className="grid gap-8 xl:grid-cols-[360px_1fr]">
        <div className="space-y-6">
          <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-lg font-semibold text-slate-900">Ações de sincronização</h2>
            <div className="space-y-3">
              <button
                type="button"
                onClick={() => syncAgentsMutation.mutate()}
                disabled={syncAgentsMutation.isPending}
                className="w-full rounded-full bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {syncAgentsMutation.isPending ? "Sincronizando agentes..." : "Sincronizar agentes"}
              </button>

              <button
                type="button"
                onClick={() => syncLocationsMutation.mutate()}
                disabled={syncLocationsMutation.isPending}
                className="w-full rounded-full bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {syncLocationsMutation.isPending ? "Sincronizando localizações..." : "Sincronizar localizações"}
              </button>

              <button
                type="button"
                onClick={() => syncCheckInsMutation.mutate()}
                disabled={syncCheckInsMutation.isPending}
                className="w-full rounded-full bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {syncCheckInsMutation.isPending ? "Sincronizando check-ins..." : "Sincronizar check-ins"}
              </button>

              <button
                type="button"
                onClick={() => syncAllMutation.mutate()}
                disabled={syncAllMutation.isPending}
                className="w-full rounded-full bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {syncAllMutation.isPending ? "Sincronizando tudo..." : "Sincronizar tudo"}
              </button>
            </div>
          </div>

          <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-lg font-semibold text-slate-900">Último resultado</h2>
            <p className="text-sm text-slate-600">O resultado da última ação de sincronização aparece aqui.</p>
            {message ? (
              <div className="mt-4 rounded-3xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-700">{message}</div>
            ) : (
              <div className="mt-4 rounded-3xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-600">Nenhuma ação executada ainda.</div>
            )}
          </div>
        </div>

        <div className="space-y-4">
          <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
              <div>
                <h2 className="text-lg font-semibold text-slate-900">Logs de sincronização</h2>
                <p className="mt-1 text-sm text-slate-600">Filtre por tipo ou veja todos os logs recentes do backend.</p>
              </div>
            </div>

            <div className="mt-6 grid gap-4 sm:grid-cols-[1fr_auto]">
              <label className="space-y-2">
                <span className="text-sm font-medium text-slate-700">Tipo de log</span>
                <input
                  value={syncTypeFilter}
                  onChange={(event) => setSyncTypeFilter(event.target.value)}
                  placeholder="AGENT_SYNC, POSITION_SYNC, CHECKIN_SYNC"
                  className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
                />
              </label>
              <button
                type="button"
                onClick={() => logsQuery.refetch()}
                className="inline-flex items-center justify-center rounded-full bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800"
              >
                Atualizar logs
              </button>
            </div>
          </div>

          {logsQuery.isLoading ? (
            <div className="rounded-3xl border border-slate-200 bg-white p-6 text-slate-700 shadow-sm">Carregando logs...</div>
          ) : logsQuery.error ? (
            <div className="rounded-3xl border border-rose-200 bg-rose-50 p-6 text-rose-700 shadow-sm">Erro ao carregar logs de sincronização.</div>
          ) : (
            <SyncLogTable logs={logsQuery.data ?? []} />
          )}
        </div>
      </div>
    </div>
  );
}