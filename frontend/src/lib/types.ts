export type AgentStatus = "ACTIVE" | "INACTIVE";

export interface Agent {
  id: number;
  externalId: string;
  name: string;
  status: AgentStatus;
  currentLat: number;
  currentLng: number;
  lastSeenAt: string;
  createdAt: string;
}

export interface AgentCreateUpdate {
  name: string;
  status: AgentStatus;
}

export interface CheckIn {
  id: string;
  agentId: string;
  type: string;
  source: string;
  latitude: number;
  longitude: number;
  address: string;
  accuracy: number | null;
  speed: number | null;
  notes: string;
  distanceFromPrevious: number | null;
  externalEventId: string | null;
  occurredAt: string | null;
  syncedAt: string | null;
}

export interface CheckInCreate {
  lat: number;
  lng: number;
  address: string;
  type?: string;
  notes?: string;
}

export interface RouteLocation {
  id: number;
  lat: number;
  lng: number;
  accuracy: number;
  recordedAt: string;
}

export interface RouteHistory {
  agentId: number;
  agentName: string;
  totalDistanceKm: number;
  locations: RouteLocation[];
}

export interface SyncLog {
  id: number;
  syncType: string;
  status: "SUCCESS" | "FAILED" | "RUNNING";
  recordsProcessed: number;
  errorMessage: string | null;
  startedAt: string;
  finishedAt: string | null;
}