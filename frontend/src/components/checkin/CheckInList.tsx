"use client";

import type { CheckIn } from "@/lib/types";

interface CheckInListProps {
  checkIns: CheckIn[];
}

export function CheckInList({ checkIns }: CheckInListProps) {
  if (checkIns.length === 0) {
    return (
      <div className="rounded-3xl border border-slate-200 bg-white p-6 text-slate-600 shadow-sm">
        Nenhum check-in registrado ainda.
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50 text-left text-xs uppercase tracking-[0.15em] text-slate-500">
          <tr>
            <th className="px-4 py-3">Momento</th>
            <th className="px-4 py-3">Tipo</th>
            <th className="px-4 py-3">Endereço</th>
            <th className="px-4 py-3">Notas</th>
            <th className="px-4 py-3">Coordenadas</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200">
          {checkIns.map((checkIn) => (
            <tr key={checkIn.id} className="hover:bg-slate-50">
              <td className="px-4 py-3 text-slate-600">
                {checkIn.occurredAt
                  ? new Date(checkIn.occurredAt).toLocaleString()
                  : "—"}
              </td>
              <td className="px-4 py-3">
                <span className="inline-flex rounded-full bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-700">
                  {checkIn.type || "—"}
                </span>
              </td>
              <td className="px-4 py-3">{checkIn.address || "—"}</td>
              <td className="px-4 py-3">{checkIn.notes || "—"}</td>
              <td className="px-4 py-3 text-slate-600">
                {checkIn.latitude.toFixed(6)}, {checkIn.longitude.toFixed(6)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}