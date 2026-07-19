"use client";

import type { FormEvent } from "react";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

const API_URL =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

type Item = {
  id: number;
  name: string;
};

export default function Home() {
  const router = useRouter();

  const [items, setItems] = useState<Item[]>([]);
  const [name, setName] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [checkingAuth, setCheckingAuth] = useState(true);

  const logout = useCallback(() => {
    localStorage.removeItem("accessToken");
    router.replace("/login");
  }, [router]);

  const getToken = useCallback(() => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      logout();
      return null;
    }
    return token;
  }, [logout]);

  const loadItems = useCallback(async () => {
    const token = getToken();
    if (!token) {
      return;
    }

    try {
      const response = await fetch(`${API_URL}/api/items`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 401) {
        logout();
        return;
      }

      if (!response.ok) {
        throw new Error("Liste alınamadı");
      }

      setItems(await response.json());
    } catch {
      setError("Ürünler alınamadı");
    } finally {
      setCheckingAuth(false);
    }
  }, [getToken, logout]);

  useEffect(() => {
    void loadItems();
  }, [loadItems]);

  function startEdit(item: Item) {
    setEditingId(item.id);
    setName(item.name);
    setError(null);
  }

  function cancelEdit() {
    setEditingId(null);
    setName("");
  }

  async function saveItem(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);

    const token = getToken();
    if (!token) {
      return;
    }

    const isEditing = editingId !== null;
    const url = isEditing
      ? `${API_URL}/api/items/${editingId}`
      : `${API_URL}/api/items`;

    const response = await fetch(url, {
      method: isEditing ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ name }),
    });

    if (response.status === 401) {
      logout();
      return;
    }

    if (!response.ok) {
      setError(isEditing ? "Güncelleme başarısız" : "Ekleme başarısız");
      return;
    }

    setName("");
    setEditingId(null);
    await loadItems();
  }

  if (checkingAuth) {
    return <p>Oturum kontrol ediliyor...</p>;
  }

  return (
    <main style={{ padding: 24, fontFamily: "sans-serif" }}>
      <h1>Ürünler</h1>

      <button type="button" onClick={logout}>
        Çıkış yap
      </button>

      <form onSubmit={saveItem} style={{ marginTop: 16, marginBottom: 16 }}>
        <input
          value={name}
          onChange={(event) => setName(event.target.value)}
          placeholder={editingId ? "Yeni isim" : "Ürün adı"}
          required
        />

        <button type="submit">
          {editingId ? "Kaydet" : "Ekle"}
        </button>

        {editingId !== null && (
          <button type="button" onClick={cancelEdit}>
            İptal
          </button>
        )}
      </form>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <ul>
        {items.map((item) => (
          <li key={item.id}>
            {item.id}: {item.name}{" "}
            <button type="button" onClick={() => startEdit(item)}>
              Düzenle
            </button>
          </li>
        ))}
      </ul>
    </main>
  );
}