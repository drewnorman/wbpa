const ACCESS_COOKIE = 'wbpa_invited';

// The source and deployed assets are public. This hash provides an invitation
// screen, not authorization for files or future APIs.
const PASSWORD_HASH =
  '481006be3469db73757d3782627be249b7e832e9e6142bcf8ca153201fc95a49';

function cookieAttributes(): string {
  return `Path=/; SameSite=Lax${window.location.protocol === 'https:' ? '; Secure' : ''}`;
}

export function isInvited(cookies = document.cookie): boolean {
  return cookies
    .split(';')
    .some((cookie) => cookie.trim() === `${ACCESS_COOKIE}=1`);
}

export function rememberInvitation(): void {
  document.cookie = `${ACCESS_COOKIE}=1; ${cookieAttributes()}`;
}

export function forgetInvitation(): void {
  document.cookie = `${ACCESS_COOKIE}=; Max-Age=0; ${cookieAttributes()}`;
}

export async function matchesPassword(password: string): Promise<boolean> {
  const bytes = new TextEncoder().encode(password.trim());
  const digest = await crypto.subtle.digest('SHA-256', bytes);
  const hex = Array.from(new Uint8Array(digest), (byte) =>
    byte.toString(16).padStart(2, '0'),
  ).join('');
  return hex === PASSWORD_HASH;
}
