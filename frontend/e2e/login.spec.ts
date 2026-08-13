import { test, expect } from '@playwright/test';

const API_BASE = process.env.PLAYWRIGHT_API_URL ?? 'http://localhost:8080';

test('kullanıcı giriş yapabiliyor', async ({ page, request }) => {
  const username = `e2e_login_${Date.now()}`;
  const password = 'test123456';

  const registerResponse = await request.post(`${API_BASE}/api/auth/register`, {
    data: {
      username,
      password,
    },
  });

  expect(registerResponse.status()).toBe(201);

  await page.goto('/login');

  await expect(page.getByRole('heading', { name: 'Giriş yap' })).toBeVisible();

  await page.getByLabel('Kullanıcı adı').fill(username);
  await page.getByLabel('Şifre').fill(password);

  const loginResponsePromise = page.waitForResponse(
    (response) =>
      response.url().includes('/api/auth/login') &&
      response.request().method() === 'POST'
  );

  await page.getByRole('button', { name: 'Giriş yap' }).click();

  const loginResponse = await loginResponsePromise;
  expect(loginResponse.status()).toBe(200);

  await expect(page).toHaveURL('/');

  await expect
    .poll(async () => page.evaluate(() => localStorage.getItem('accessToken')))
    .not.toBeNull();
});
