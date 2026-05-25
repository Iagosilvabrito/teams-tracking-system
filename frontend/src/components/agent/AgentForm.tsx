"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import type { AgentCreateUpdate } from "@/lib/types";

export const agentFormSchema = z.object({
  name: z.string().min(2, "Nome é obrigatório"),
  status: z.enum(["ACTIVE", "INACTIVE"]),
});

export type AgentFormInput = z.infer<typeof agentFormSchema>;

interface AgentFormProps {
  onSubmit: (values: AgentFormInput) => Promise<void> | void;
  initialValues?: AgentCreateUpdate;
  submitLabel: string;
}

export function AgentForm({ onSubmit, initialValues, submitLabel }: AgentFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<AgentFormInput>({
    resolver: zodResolver(agentFormSchema),
    defaultValues: initialValues ?? { name: "", status: "ACTIVE" },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="space-y-2">
        <label className="block text-sm font-medium text-slate-700">Nome</label>
        <input
          {...register("name")}
          className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
          placeholder="Nome do agente"
        />
        {errors.name && <p className="text-sm text-red-600">{errors.name.message}</p>}
      </div>

      <div className="space-y-2">
        <label className="block text-sm font-medium text-slate-700">Status</label>
        <select
          {...register("status")}
          className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
        >
          <option value="ACTIVE">Ativo</option>
          <option value="INACTIVE">Inativo</option>
        </select>
        {errors.status && <p className="text-sm text-red-600">{errors.status.message}</p>}
      </div>

      <button
        type="submit"
        disabled={isSubmitting}
        className="inline-flex items-center justify-center rounded-full bg-slate-950 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {submitLabel}
      </button>
    </form>
  );
}
