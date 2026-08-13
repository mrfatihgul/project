import { test, expect } from '@playwright/test';

test('kullanıcı kayıt olabiliyor', async ({ page }) => {
  const username = `e2e_user_${Date.now()}`;
  const password = 'test123456';

  await page.goto('/register');

  await expect(page.getByRole('heading', { name: 'Kayıt ol' })).toBeVisible();

  await page.getByLabel('Kullanıcı adı').fill(username);
  await page.getByLabel('Şifre').fill(password);

  const registerResponsePromise = page.waitForResponse(
    (response) =>
      response.url().includes('/api/auth/register') &&
      response.request().method() === 'POST'
  );

  await page.getByRole('button', { name: 'Kayıt ol' }).click();

  const registerResponse = await registerResponsePromise;
  expect(registerResponse.status()).toBe(201);

  await expect(page).toHaveURL('/');

  await expect
    .poll(async () => page.evaluate(() => localStorage.getItem('accessToken')))
    .not.toBeNull();
});
