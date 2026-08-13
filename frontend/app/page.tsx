"use client";

import type { FormEvent } from "react";
import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

type ItemDetail = {
  type: string;
  description: string;
  material: string;
};

type Item = {
  id: number;
  name: string;
  details?: ItemDetail | null;
};

const emptyDetails: ItemDetail = {
  type: "",
  description: "",
  material: "",
};

export default function Home() {
  const router = useRouter();

  const [items, setItems] = useState<Item[]>([]);
  const [name, setName] = useState("");
  const [details, setDetails] = useState<ItemDetail>(emptyDetails);
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
      const response = await fetch("/api/items", {
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

  function resetForm() {
    setEditingId(null);
    setName("");
    setDetails(emptyDetails);
  }

  function startEdit(item: Item) {
    setEditingId(item.id);
    setName(item.name);
    setDetails({
      type: item.details?.type ?? "",
      description: item.details?.description ?? "",
      material: item.details?.material ?? "",
    });
    setError(null);
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
      ? `/api/items/${editingId}`
      : `/api/items`;

    const body = {
      name,
      details: {
        type: details.type,
        description: details.description,
        material: details.material,
      },
    };

    const response = await fetch(url, {
      method: isEditing ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(body),
    });

    if (response.status === 401) {
      logout();
      return;
    }

    if (!response.ok) {
      setError(isEditing ? "Güncelleme başarısız" : "Ekleme başarısız");
      return;
    }

    resetForm();
    await loadItems();
  }

  if (checkingAuth) {
    return <p className="loading">Oturum kontrol ediliyor...</p>;
  }

  return (
    <main className="app-shell">
      <div className="page-header">
        <div>
          <h1>Ürünlerim</h1>
          <p>Kendi ürünlerini ekle, düzenle ve yönet</p>
        </div>
        <div className="header-actions">
          <Link href="/orders" className="btn btn-secondary">
            Siparişler
          </Link>
          <button type="button" className="btn btn-ghost" onClick={logout}>
            Çıkış yap
          </button>
        </div>
      </div>

      <section className="panel">
        <form className="stack" onSubmit={saveItem}>
          <div className="field">
            <label htmlFor="name">Ürün adı</label>
            <input
              id="name"
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Örn. Kalem"
              required
            />
          </div>

          <div className="field-grid">
            <div className="field">
              <label htmlFor="type">Tip</label>
              <input
                id="type"
                value={details.type}
                onChange={(event) =>
                  setDetails((current) => ({
                    ...current,
                    type: event.target.value,
                  }))
                }
                placeholder="Örn. kırtasiye"
                required
              />
            </div>

            <div className="field">
              <label htmlFor="material">Materyal</label>
              <input
                id="material"
                value={details.material}
                onChange={(event) =>
                  setDetails((current) => ({
                    ...current,
                    material: event.target.value,
                  }))
                }
                placeholder="Örn. plastik"
                required
              />
            </div>

            <div className="field" style={{ gridColumn: "1 / -1" }}>
              <label htmlFor="description">Açıklama</label>
              <textarea
                id="description"
                value={details.description}
                onChange={(event) =>
                  setDetails((current) => ({
                    ...current,
                    description: event.target.value,
                  }))
                }
                placeholder="Kısa ürün açıklaması"
                required
              />
            </div>
          </div>

          <div className="actions">
            <button type="submit" className="btn btn-primary">
              {editingId ? "Kaydet" : "Ekle"}
            </button>
            {editingId !== null && (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={resetForm}
              >
                İptal
              </button>
            )}
          </div>
        </form>

        {error && <p className="error">{error}</p>}
      </section>

      <section className="panel">
        <ul className="item-list">
          {items.map((item) => (
            <li key={item.id} className="item-card">
              <div className="item-card-top">
                <div>
                  <h2>
                    #{item.id} · {item.name}
                  </h2>
                  <p className="muted">
                    {item.details?.description || "Açıklama yok"}
                  </p>
                </div>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => startEdit(item)}
                >
                  Düzenle
                </button>
              </div>
              <div className="item-meta">
                <span className="chip">
                  Tip: {item.details?.type || "-"}
                </span>
                <span className="chip">
                  Materyal: {item.details?.material || "-"}
                </span>
              </div>
            </li>
          ))}
        </ul>
      </section>
    </main>
  );
}
