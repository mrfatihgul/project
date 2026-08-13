import { test, expect } from '@playwright/test';

test('ana sayfa açılıyor', async ({ page }) => {
  await page.goto('/');

  await expect(page).toHaveTitle(/.+/);
});