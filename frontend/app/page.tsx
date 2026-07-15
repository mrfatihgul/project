"use client";

import { FormEvent, useEffect, useState } from "react";

type Item = {
  id: number;
  name: string;
};

export default function Home() {
  const [items, setItems] = useState<Item[]>([]);
  const [name, setName] = useState("");
  const [error, setError] = useState<string | null>(null);

  async function loadItems() {
    setError(null);
    const res = await fetch("http://localhost:8080/api/items");
    if (!res.ok) {
      setError("Liste alınamadı");
      return;
    }
    setItems(await res.json());
  }

  async function addItem(e: FormEvent) {
    e.preventDefault();
    setError(null);
    const res = await fetch("http://localhost:8080/api/items", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name }),
    });
    if (!res.ok) {
      setError("Ekleme başarısız");
      return;
    }
    setName("");
    await loadItems();
  }

  useEffect(() => {
    loadItems();
  }, []);

  return (
    <main style={{ padding: 24, fontFamily: "sans-serif" }}>
      <h1>Items</h1>

      <form onSubmit={addItem} style={{ marginBottom: 16 }}>
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="isim"
          required
        />
        <button type="submit">Ekle</button>
      </form>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <ul>
        {items.map((item) => (
          <li key={item.id}>
            {item.id}: {item.name}
          </li>
        ))}
      </ul>
    </main>
  );
}