"use client";

import type { FormEvent } from "react";
import Link from "next/link";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function login(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        setError("Kullanıcı adı veya şifre yanlış");
        return;
      }

      const data: { token: string } = await response.json();

      localStorage.setItem("accessToken", data.token);
      router.replace("/");
    } catch {
      setError("Backend'e bağlanılamadı");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="panel auth-card">
        <h1>Giriş yap</h1>
        <p>Ürünlerini görmek için hesabına gir</p>

        <form className="stack" onSubmit={login} style={{ marginTop: "1.25rem" }}>
          <div className="field">
            <label htmlFor="username">Kullanıcı adı</label>
            <input
              id="username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="Kullanıcı adı"
              required
            />
          </div>

          <div className="field">
            <label htmlFor="password">Şifre</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Şifre"
              required
            />
          </div>

          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? "Giriş yapılıyor..." : "Giriş yap"}
          </button>
        </form>

        {error && <p className="error">{error}</p>}

        <p className="auth-switch">
          Hesabın yok mu? <Link href="/register">Kayıt ol</Link>
        </p>
      </section>
    </main>
  );
}
