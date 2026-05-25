import type {
  Agent,
  AgentCreateUpdate,
  CheckIn,
  CheckInCreate,
  RouteHistory,
  SyncLog,
} from "@/lib/types";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "";

async function apiFetch<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
    },
    ...options,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API request failed: ${response.status} ${errorText}`);
  }

  if (response.status === 204) {
    return undefined as unknown as T;
  }

  return response.json() as Promise<T>;
}

export async function listAgents(): Promise<Agent[]> {
  return apiFetch<Agent[]>("/api/agents");
}

export async function createAgent(body: AgentCreateUpdate): Promise<Agent> {
  return apiFetch<Agent>("/api/agents", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export async function getAgent(id: number): Promise<Agent> {
  return apiFetch<Agent>(`/api/agents/${id}`);
}

export async function updateAgent(id: number, body: AgentCreateUpdate): Promise<Agent> {
  return apiFetch<Agent>(`/api/agents/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });
}

export async function deleteAgent(id: number): Promise<void> {
  return apiFetch<void>(`/api/agents/${id}`, {
    method: "DELETE",
  });
}

export async function createCheckIn(agentId: number, body: CheckInCreate): Promise<CheckIn> {
  return apiFetch<CheckIn>(`/api/agents/${agentId}/check-ins`, {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export async function listCheckIns(agentId: number): Promise<CheckIn[]> {
  return apiFetch<CheckIn[]>(`/api/agents/${agentId}/check-ins`);
}

export async function getRouteHistory(agentId: number): Promise<RouteHistory> {
  return apiFetch<RouteHistory>(`/api/agents/${agentId}/route`);
}

export async function listCheckInsAll(filters?: { agentId?: number; type?: string }): Promise<CheckIn[]> {
  const query = new URLSearchParams();
  if (filters?.agentId !== undefined) query.set("agentId", String(filters.agentId));
  if (filters?.type) query.set("type", filters.type);
  const queryString = query.toString();
  const result = await apiFetch<{ data: CheckIn[] }>(`/api/v1/check-ins${queryString ? `?${queryString}` : ""}`);
  return result.data;
}

export async function syncAgents(): Promise<{ synced: number }> {
  return apiFetch<{ synced: number }>("/api/v1/sync/agents", { method: "POST" });
}

export async function syncLocations(): Promise<{ synced: number }> {
  return apiFetch<{ synced: number }>("/api/v1/sync/locations", { method: "POST" });
}

export async function syncCheckIns(): Promise<{ synced: number; syncToken: string }> {
  return apiFetch<{ synced: number; syncToken: string }>("/api/v1/sync/check-ins", { method: "POST" });
}

export async function syncAll(): Promise<{ agents: number; locations: number; checkIns: number }> {
  return apiFetch<{ agents: number; locations: number; checkIns: number }>("/api/v1/sync/all", { method: "POST" });
}

export async function listSyncLogs(): Promise<SyncLog[]> {
  return apiFetch<SyncLog[]>("/api/v1/sync/logs");
}

export async function getSyncLogs(syncType: string): Promise<SyncLog[]> {
  return apiFetch<SyncLog[]>(`/api/v1/sync/logs/${syncType}`);
}