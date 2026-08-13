import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import RegisterPage from "./page";


const replaceMock = vi.fn();

vi.mock("next/navigation", () => ({
  useRouter: () => ({
    replace: replaceMock,
  }),
}));


describe("RegisterPage", () => {

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });


  it("should render register form", () => {

    render(<RegisterPage />);

    expect(
      screen.getByRole("heading", { name: "Kayıt ol" })
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Kullanıcı adı")
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText("Şifre")
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: "Kayıt ol" })
    ).toBeInTheDocument();
  });


  it("should register and save token when registration is successful", async () => {

    const fetchMock = vi.spyOn(globalThis, "fetch");

    fetchMock.mockResolvedValue({
      ok: true,
      status: 201,
      json: async () => ({
        token: "test-token",
      }),
    } as Response);


    render(<RegisterPage />);


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
      screen.getByRole("button", { name: "Kayıt ol" })
    );


    await waitFor(() => {

      expect(fetchMock).toHaveBeenCalledWith(
        "/api/auth/register",
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


  it("should show error when username already exists", async () => {

    const fetchMock = vi.spyOn(globalThis, "fetch");

    fetchMock.mockResolvedValue({
      ok: false,
      status: 409,
    } as Response);


    render(<RegisterPage />);


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
      screen.getByRole("button", { name: "Kayıt ol" })
    );


    expect(
      await screen.findByText("Bu kullanıcı adı zaten kullanılıyor")
    ).toBeInTheDocument();
  });


  it("should show error when registration fails", async () => {

    const fetchMock = vi.spyOn(globalThis, "fetch");

    fetchMock.mockResolvedValue({
      ok: false,
      status: 500,
    } as Response);


    render(<RegisterPage />);


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
      screen.getByRole("button", { name: "Kayıt ol" })
    );


    expect(
      await screen.findByText("Kayıt başarısız")
    ).toBeInTheDocument();
  });


  it("should show error when backend cannot be reached", async () => {

    const fetchMock = vi.spyOn(globalThis, "fetch");

    fetchMock.mockRejectedValue(
      new Error("Connection failed")
    );


    render(<RegisterPage />);


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
      screen.getByRole("button", { name: "Kayıt ol" })
    );


    expect(
      await screen.findByText("Backend'e bağlanılamadı")
    ).toBeInTheDocument();
  });

});