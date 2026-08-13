import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import LoginPage from "./page";


const replaceMock = vi.fn();

vi.mock("next/navigation", () => ({
  useRouter: () => ({
    replace: replaceMock,
  }),
}));


describe("LoginPage", () => {

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });


  it("should render login form", () => {

    render(<LoginPage />);

    expect(
      screen.getByRole("heading", { name: "Giriş yap" })
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Kullanıcı adı")
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Şifre")
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: "Giriş yap" })
    ).toBeInTheDocument();
  });


  it("should login and save token when credentials are valid", async () => {

    const fetchMock = vi.spyOn(globalThis, "fetch");

    fetchMock.mockResolvedValue({
      ok: true,
      json: async () => ({
        token: "test-token",
      }),
    } as Response);


    render(<LoginPage />);


    fireEvent.change(
      screen.getByLabelText("Kullanıcı adı"),
      {
        target: {
          value: "fatih",
        },
      }
    );

    fireEvent.change(
      screen.getByLabelText("Şifre"),
      {
        target: {
          value: "123456",
        },
      }
    );

    fireEvent.click(
      screen.getByRole("button", { name: "Giriş yap" })
    );


    await waitFor(() => {

      expect(fetchMock).toHaveBeenCalledWith(
        "/api/auth/login",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            username: "fatih",
            password: "123456",
          }),
        }
      );

    });


    expect(
      localStorage.getItem("accessToken")
    ).toBe("test-token");

    expect(replaceMock).toHaveBeenCalledWith("/");
  });


  it("should show error when credentials are wrong", async () => {

    const fetchMock = vi.spyOn(globalThis, "fetch");

    fetchMock.mockResolvedValue({
      ok: false,
    } as Response);


    render(<LoginPage />);


    fireEvent.change(
      screen.getByLabelText("Kullanıcı adı"),
      {
        target: {
          value: "fatih",
        },
      }
    );

    fireEvent.change(
      screen.getByLabelText("Şifre"),
      {
        target: {
          value: "wrong-password",
        },
      }
    );

    fireEvent.click(
      screen.getByRole("button", { name: "Giriş yap" })
    );


    expect(
      await screen.findByText("Kullanıcı adı veya şifre yanlış")
    ).toBeInTheDocument();
  });


  it("should show error when backend cannot be reached", async () => {

    const fetchMock = vi.spyOn(globalThis, "fetch");

    fetchMock.mockRejectedValue(
      new Error("Connection failed")
    );


    render(<LoginPage />);


    fireEvent.change(
      screen.getByLabelText("Kullanıcı adı"),
      {
        target: {
          value: "fatih",
        },
      }
    );

    fireEvent.change(
      screen.getByLabelText("Şifre"),
      {
        target: {
          value: "123456",
        },
      }
    );

    fireEvent.click(
      screen.getByRole("button", { name: "Giriş yap" })
    );


    expect(
      await screen.findByText("Backend'e bağlanılamadı")
    ).toBeInTheDocument();
  });

});