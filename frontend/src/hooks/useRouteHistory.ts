import { useQuery } from "@tanstack/react-query";
import * as api from "@/lib/api";
import type { RouteHistory } from "@/lib/types";

export function useRouteHistory(agentId: number) {
  return useQuery<RouteHistory>({
    queryKey: ["agent", agentId, "route"],
    queryFn: () => api.getRouteHistory(agentId),
    enabled: agentId > 0,
    staleTime: 1000 * 30,
  });
}