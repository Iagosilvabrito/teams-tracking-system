"use client";

import { useState, type FormEvent } from "react";
import { useQuery } from "@tanstack/react-query";
import { listCheckInsAll } from "@/lib/api";
import { CheckInList } from "@/components/checkin/CheckInList";

export default function CheckInsPage() {
  const [agentId, setAgentId] = useState("");
  const [type, setType] = useState("");
  const [filters, setFilters] = useState({ agentId: "", type: "" });

  const { data: checkIns = [], isLoading, error } = useQuery({
    queryKey: ["checkIns", filters.agentId, filters.type],
    queryFn: () =>
      listCheckInsAll({
        agentId: filters.agentId.trim() ? Number(filters.agentId) : undefined,
        type: filters.type.trim() ? filters.type : undefined,
      }),
    staleTime: 1000 * 30,
  });

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFilters({ agentId, type });
  }

  return (
    <div className="space-y-8">
      <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-slate-900">Check-ins</h1>
            <p className="text-sm leading-6 text-slate-600">Busque check-ins gerais por agente ou tipo.</p>
          </div>
        </div>
      </div>

      <div className="grid gap-8 xl:grid-cols-[320px_1fr]">
        <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
          <h2 className="mb-4 text-lg font-semibold text-slate-900">Filtro de check-ins</h2>
          <form onSubmit={handleSearch} className="space-y-4">
            <div className="space-y-2">
              <label className="block text-sm font-medium text-slate-700">ID do agente</label>
              <input
                value={agentId}
                onChange={(event) => setAgentId(event.target.value)}
                type="number"
                min={1}
                className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
                placeholder="Digite o ID do agente"
              />
            </div>

            <div className="space-y-2">
              <label className="block text-sm font-medium text-slate-700">Tipo de check-in</label>
              <input
                value={type}
                onChange={(event) => setType(event.target.value)}
                className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
                placeholder="EXAMPLE, DELIVERY, etc."
              />
            </div>

            <button
              type="submit"
              className="inline-flex items-center justify-center rounded-full bg-slate-950 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-slate-800"
            >
              Buscar check-ins
            </button>
          </form>
        </div>

        <div className="space-y-4">
          <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-lg font-semibold text-slate-900">Resultados</h2>
            <p className="mt-1 text-sm text-slate-600">Veja os check-ins retornados pela API conforme o filtro aplicado.</p>
          </div>

          {isLoading ? (
            <div className="rounded-3xl border border-slate-200 bg-white p-6 text-slate-700 shadow-sm">Carregando check-ins...</div>
          ) : error ? (
            <div className="rounded-3xl border border-rose-200 bg-rose-50 p-6 text-rose-700 shadow-sm">Erro ao carregar check-ins.</div>
          ) : (
            <CheckInList checkIns={checkIns} />
          )}
        </div>
      </div>
    </div>
  );
}
