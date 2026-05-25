"use client";

import type { RouteHistory } from "@/lib/types";

interface RouteHistoryCardProps {
  routeHistory: RouteHistory;
}

export function RouteHistoryCard({ routeHistory }: RouteHistoryCardProps) {
  return (
    <div className="space-y-6 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold text-slate-900">Rota do dia</h2>
          <p className="text-sm text-slate-600">Distância total: {routeHistory.totalDistanceKm.toFixed(2)} km</p>
        </div>
      </div>

      <div className="overflow-hidden rounded-3xl border border-slate-200">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50 text-left text-xs uppercase tracking-[0.15em] text-slate-500">
            <tr>
              <th className="px-4 py-3">Hora</th>
              <th className="px-4 py-3">Latitude</th>
              <th className="px-4 py-3">Longitude</th>
              <th className="px-4 py-3">Precisão</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200">
            {routeHistory.locations.map((location) => (
              <tr key={location.id} className="hover:bg-slate-50">
                <td className="px-4 py-3 text-slate-600">{new Date(location.recordedAt).toLocaleTimeString()}</td>
                <td className="px-4 py-3">{location.lat.toFixed(6)}</td>
                <td className="px-4 py-3">{location.lng.toFixed(6)}</td>
                <td className="px-4 py-3">{location.accuracy} m</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
