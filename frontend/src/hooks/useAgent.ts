import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as api from "@/lib/api";
import type { Agent, AgentCreateUpdate } from "@/lib/types";

export function useAgent(agentId: number) {
  return useQuery<Agent>({
    queryKey: ["agent", agentId],
    queryFn: () => api.getAgent(agentId),
    enabled: agentId > 0,
    staleTime: 1000 * 30,
  });
}

export function useUpdateAgent(agentId: number) {
  const queryClient = useQueryClient();

  return useMutation<Agent, Error, AgentCreateUpdate>({
    mutationFn: (body: AgentCreateUpdate) => api.updateAgent(agentId, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agent", agentId] });
      queryClient.invalidateQueries({ queryKey: ["agents"] });
    },
  });
}