import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import * as api from "@/lib/api";
import type { CheckIn, CheckInCreate } from "@/lib/types";

export function useAgentCheckIns(agentId: number) {
  return useQuery<CheckIn[]>({
    queryKey: ["agent", agentId, "checkIns"],
    queryFn: () => api.listCheckIns(agentId),
    enabled: agentId > 0,
  });
}

export function useCreateCheckIn(agentId: number) {
  const queryClient = useQueryClient();
  return useMutation<CheckIn, Error, CheckInCreate>({
    mutationFn: (body: CheckInCreate) => api.createCheckIn(agentId, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["agent", agentId, "checkIns"] });
    },
  });
}