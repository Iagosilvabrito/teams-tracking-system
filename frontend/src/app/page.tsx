import Link from "next/link";

export default function Home() {
  return (
    <div className="mx-auto flex min-h-[calc(100vh-96px)] max-w-6xl flex-col items-center justify-center gap-8 px-6 py-12 text-slate-900">
      <div className="space-y-4 text-center">
        <p className="text-sm uppercase tracking-[0.3em] text-slate-500">Sistema de rastreamento</p>
        <h1 className="text-4xl font-semibold tracking-tight">Frontend de equipes externas</h1>
        <p className="max-w-2xl text-base leading-7 text-slate-600">
          Painel com listagem de agentes, detalhe por agente, check-ins, rota do dia e monitoramento de sincronização.
        </p>
      </div>

      <div className="grid w-full max-w-4xl gap-4 sm:grid-cols-2">
        <Link
          href="/agents"
          className="rounded-3xl border border-slate-200 bg-white px-6 py-8 text-center shadow-sm transition hover:border-slate-300 hover:shadow-md"
        >
          <p className="text-sm font-medium text-slate-500">Agentes</p>
          <p className="mt-4 text-2xl font-semibold text-slate-900">CRUD e lista</p>
        </Link>
        <Link
          href="/check-ins"
          className="rounded-3xl border border-slate-200 bg-white px-6 py-8 text-center shadow-sm transition hover:border-slate-300 hover:shadow-md"
        >
          <p className="text-sm font-medium text-slate-500">Check-ins</p>
          <p className="mt-4 text-2xl font-semibold text-slate-900">Filtros e lista geral</p>
        </Link>
        <Link
          href="/sync"
          className="rounded-3xl border border-slate-200 bg-white px-6 py-8 text-center shadow-sm transition hover:border-slate-300 hover:shadow-md"
        >
          <p className="text-sm font-medium text-slate-500">Sincronização</p>
          <p className="mt-4 text-2xl font-semibold text-slate-900">Executar e monitorar</p>
        </Link>
        <Link
          href="/monitoring"
          className="rounded-3xl border border-slate-200 bg-white px-6 py-8 text-center shadow-sm transition hover:border-slate-300 hover:shadow-md"
        >
          <p className="text-sm font-medium text-slate-500">Monitoramento</p>
          <p className="mt-4 text-2xl font-semibold text-slate-900">Logs recentes</p>
        </Link>
      </div>
    </div>
  );
}
