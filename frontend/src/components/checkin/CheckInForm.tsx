"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import type { CheckInCreate } from "@/lib/types";

export const checkInFormSchema = z.object({
  lat: z.coerce.number().min(-90, "Latitude inválida").max(90, "Latitude inválida"),
  lng: z.coerce.number().min(-180, "Longitude inválida").max(180, "Longitude inválida"),
  address: z.string().min(2, "Endereço é obrigatório"),
  type: z.string().optional(),
  notes: z.string().optional(),
});

export type CheckInFormInput = z.infer<typeof checkInFormSchema>;

interface CheckInFormProps {
  onSubmit: (values: CheckInFormInput) => Promise<void> | void;
}

export function CheckInForm({ onSubmit }: CheckInFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(checkInFormSchema),
    defaultValues: { lat: 0, lng: 0, address: "", type: "", notes: "" },
  });

  return (
    <form onSubmit={handleSubmit((values) => onSubmit(values as CheckInFormInput))} className="space-y-4 rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
      <div className="grid gap-4 sm:grid-cols-2">
        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700">Latitude</label>
          <input
            type="number"
            step="any"
            {...register("lat", { valueAsNumber: true })}
            className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
          />
          {errors.lat && <p className="text-sm text-red-600">{errors.lat.message}</p>}
        </div>

        <div className="space-y-2">
          <label className="block text-sm font-medium text-slate-700">Longitude</label>
          <input
            type="number"
            step="any"
            {...register("lng", { valueAsNumber: true })}
            className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
          />
          {errors.lng && <p className="text-sm text-red-600">{errors.lng.message}</p>}
        </div>
      </div>

      <div className="space-y-2">
        <label className="block text-sm font-medium text-slate-700">Endereço</label>
        <input
          {...register("address")}
          className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
        />
        {errors.address && <p className="text-sm text-red-600">{errors.address.message}</p>}
      </div>

      <div className="space-y-2">
        <label className="block text-sm font-medium text-slate-700">Tipo de check-in</label>
        <input
          {...register("type")}
          className="w-full rounded-xl border border-slate-300 bg-slate-50 px-4 py-2 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
          placeholder="DELIVERY, PICKUP, VISIT, etc."
        />
        {errors.type && <p className="text-sm text-red-600">{errors.type.message}</p>}
      </div>

      <div className="space-y-2">
        <label className="block text-sm font-medium text-slate-700">Notas</label>
        <textarea
          rows={3}
          {...register("notes")}
          className="w-full rounded-3xl border border-slate-300 bg-slate-50 px-4 py-3 text-sm outline-none transition focus:border-slate-500 focus:bg-white"
        />
      </div>

      <button
        type="submit"
        disabled={isSubmitting}
        className="inline-flex items-center justify-center rounded-full bg-slate-950 px-5 py-2.5 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
      >
        Registrar check-in
      </button>
    </form>
  );
}
