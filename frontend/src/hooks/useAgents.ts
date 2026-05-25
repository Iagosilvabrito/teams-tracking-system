import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as api from "@/lib/api";
import type { Agent, AgentCreateUpdate } from "@/lib/types";

export function useAgents() {
  return useQuery<Agent[]>({
    queryKey: ["agents"],
    queryFn: api.listAgents,
    staleTime: 1000 * 60,
  });
}

export function useCreateAgent() {
  const queryClient = useQueryClient();
  return useMutation<Agent, Error, AgentCreateUpdate>({
    mutationFn: (body: AgentCreateUpdate) => api.createAgent(body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });
    },
  });
}

export function useDeleteAgent() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id: number) => api.deleteAgent(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agents"] });
    },
  });
}