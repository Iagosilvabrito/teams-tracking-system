import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import Link from "next/link";
import "./globals.css";
import { Providers } from "./providers";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Teams Tracking System",
  description: "Painel de rastreamento de agentes externos",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html
      lang="pt-BR"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full bg-slate-50 text-slate-900">
        <Providers>
          <div className="min-h-full">
            <header className="border-b border-slate-200 bg-white/90 px-6 py-4 backdrop-blur-sm">
              <div className="mx-auto flex max-w-7xl items-center justify-between gap-4">
                <div>
                  <Link href="/" className="text-lg font-semibold text-slate-900">
                    Equipes Tracking
                  </Link>
                </div>
                <nav className="flex items-center gap-3 text-sm font-medium text-slate-700">
                  <Link href="/agents" className="transition hover:text-slate-900">
                    Agentes
                  </Link>
                  <Link href="/check-ins" className="transition hover:text-slate-900">
                    Check-ins
                  </Link>
                  <Link href="/sync" className="transition hover:text-slate-900">
                    Sincronização
                  </Link>
                  <Link href="/monitoring" className="transition hover:text-slate-900">
                    Monitoramento
                  </Link>
                </nav>
              </div>
            </header>
            <main className="mx-auto w-full max-w-7xl px-6 py-8">{children}</main>
          </div>
        </Providers>
      </body>
    </html>
  );
}
