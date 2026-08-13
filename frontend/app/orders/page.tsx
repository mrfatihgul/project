"use client";

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

type Order = {
  id: number;
  username: string;
  itemId: number;
  createdAt: string;
};

export default function OrdersPage() {
  const router = useRouter();

  const [catalog, setCatalog] = useState<Item[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [orderingId, setOrderingId] = useState<number | null>(null);

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

  const loadData = useCallback(async () => {
    const token = getToken();
    if (!token) {
      return;
    }

    setError(null);

    try {
      const [catalogResponse, ordersResponse] = await Promise.all([
        fetch("/api/items/catalog", {
          headers: { Authorization: `Bearer ${token}` },
        }),
        fetch("/api/orders", {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ]);

      if (catalogResponse.status === 401 || ordersResponse.status === 401) {
        logout();
        return;
      }

      if (!catalogResponse.ok) {
        throw new Error("Katalog alınamadı");
      }

      if (!ordersResponse.ok) {
        throw new Error("Siparişler alınamadı");
      }

      setCatalog(await catalogResponse.json());
      setOrders(await ordersResponse.json());
    } catch {
      setError("Sipariş sayfası yüklenemedi");
    } finally {
      setLoading(false);
    }
  }, [getToken, logout]);

  useEffect(() => {
    void loadData();
  }, [loadData]);

  async function placeOrder(itemId: number) {
    const token = getToken();
    if (!token) {
      return;
    }

    setError(null);
    setMessage(null);
    setOrderingId(itemId);

    try {
      const response = await fetch("/api/orders", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ itemId }),
      });

      if (response.status === 401) {
        logout();
        return;
      }

      if (!response.ok) {
        setError("Sipariş verilemedi");
        return;
      }

      setMessage(`#${itemId} için sipariş oluşturuldu`);
      await loadData();
    } catch {
      setError("Order servisine bağlanılamadı");
    } finally {
      setOrderingId(null);
    }
  }

  if (loading) {
    return <p className="loading">Siparişler yükleniyor...</p>;
  }

  return (
    <main className="app-shell">
      <div className="page-header">
        <div>
          <h1>Siparişler</h1>
          <p>Katalogdaki ürünleri görüntüle ve sipariş ver</p>
        </div>
        <div className="header-actions">
          <Link href="/" className="btn btn-secondary">
            Ürünlerim
          </Link>
          <button type="button" className="btn btn-ghost" onClick={logout}>
            Çıkış yap
          </button>
        </div>
      </div>

      {error && <p className="error">{error}</p>}
      {message && <p className="success">{message}</p>}

      <section className="panel">
        <h2 className="section-title">Ürün kataloğu</h2>
        {catalog.length === 0 ? (
          <p className="muted">Henüz sipariş verilecek ürün yok.</p>
        ) : (
          <ul className="item-list">
            {catalog.map((item) => (
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
                    className="btn btn-primary"
                    disabled={orderingId === item.id}
                    onClick={() => void placeOrder(item.id)}
                  >
                    {orderingId === item.id ? "Sipariş veriliyor..." : "Sipariş ver"}
                  </button>
                </div>
                <div className="item-meta">
                  <span className="chip">Tip: {item.details?.type || "-"}</span>
                  <span className="chip">
                    Materyal: {item.details?.material || "-"}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="panel">
        <h2 className="section-title">Siparişlerim</h2>
        {orders.length === 0 ? (
          <p className="muted">Henüz siparişin yok.</p>
        ) : (
          <ul className="item-list">
            {orders.map((order) => {
              const item = catalog.find((entry) => entry.id === order.itemId);
              return (
                <li key={order.id} className="item-card">
                  <div className="item-card-top">
                    <div>
                      <h2>
                        Sipariş #{order.id}
                        {item ? ` · ${item.name}` : ` · Item #${order.itemId}`}
                      </h2>
                      <p className="muted">
                        {new Date(order.createdAt).toLocaleString("tr-TR")}
                      </p>
                    </div>
                    <span className="chip">Item ID: {order.itemId}</span>
                  </div>
                </li>
              );
            })}
          </ul>
        )}
      </section>
    </main>
  );
}
