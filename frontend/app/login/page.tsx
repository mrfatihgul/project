"use client";

import type { FormEvent } from "react";
import { useState } from "react";
import { useRouter } from "next/navigation";

const API_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

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
      const response = await fetch(`${API_URL}/api/auth/login`, {
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
    <main style={{ padding: 24, fontFamily: "sans-serif" }}>
      <h1>Giriş yap</h1>

      <form onSubmit={login}>
        <div>
          <input
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            placeholder="Kullanıcı adı"
            required
          />
        </div>

        <div>
          <input
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            placeholder="Şifre"
            required
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? "Giriş yapılıyor..." : "Giriş yap"}
        </button>
      </form>

      {error && <p style={{ color: "red" }}>{error}</p>}
    </main>
  );
}