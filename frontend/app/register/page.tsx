"use client";

import type { FormEvent } from "react";
import Link from "next/link";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function RegisterPage() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function register(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await fetch("/api/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      });

      if (response.status === 409) {
        setError("Bu kullanıcı adı zaten kullanılıyor");
        return;
      }

      if (!response.ok) {
        setError("Kayıt başarısız");
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
        <h1>Kayıt ol</h1>
        <p>Yeni hesap oluştur ve ürünlerini yönet</p>

        <form
          className="stack"
          onSubmit={register}
          style={{ marginTop: "1.25rem" }}
        >
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
            {loading ? "Kaydediliyor..." : "Kayıt ol"}
          </button>
        </form>

        {error && <p className="error">{error}</p>}

        <p className="auth-switch">
          Zaten hesabın var mı? <Link href="/login">Giriş yap</Link>
        </p>
      </section>
    </main>
  );
}
