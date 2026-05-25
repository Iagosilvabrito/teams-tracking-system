import { useQuery } from "@tanstack/react-query";
import * as api from "@/lib/api";
import type { SyncLog } from "@/lib/types";

export function useSyncLogs() {
  return useQuery<SyncLog[]>({
    queryKey: ["syncLogs"],
    queryFn: api.listSyncLogs,
    staleTime: 1000 * 20,
  });
}