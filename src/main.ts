import './style.css';
import {
  forgetInvitation,
  isInvited,
  matchesPassword,
  rememberInvitation,
} from './access';

const app = document.querySelector<HTMLElement>('#app')!;
if (!app) throw new Error('The app mount point is missing.');

function showGate(): void {
  app.innerHTML = `
    <main class="entry-panel" aria-label="Password entry">
      <form class="entry-form">
        <label class="visually-hidden" for="password">Password</label>
        <input id="password" name="password" type="password" autocomplete="current-password" required />
        <button type="submit">Continue</button>
        <p class="entry-error" role="alert" aria-live="polite"></p>
      </form>
    </main>
  `;
  const form = app.querySelector<HTMLFormElement>('.entry-form')!;
  const input = app.querySelector<HTMLInputElement>('#password')!;
  const button = form.querySelector<HTMLButtonElement>('button')!;
  const error = form.querySelector<HTMLParagraphElement>('.entry-error')!;
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    button.disabled = true;
    error.textContent = '';
    try {
      if (await matchesPassword(input.value)) {
        rememberInvitation();
        window.history.replaceState(null, '', '/');
        showLauncher();
        return;
      }
      error.textContent = 'That password did not match.';
      input.select();
    } catch {
      error.textContent = 'The password could not be checked. Try again.';
    } finally {
      button.disabled = false;
    }
  });
}

function showLauncher(): void {
  app.innerHTML = `
    <main class="launcher" aria-labelledby="launcher-title">
      <div class="launcher-heading">
        <p class="entry-eyebrow">Choose a mode</p>
        <h1 id="launcher-title">Widener's Baby Punting Adventure</h1>
        <p>Two takes on the same game. Start with the original browser port.</p>
      </div>
      <div class="mode-list">
        <a class="mode-card mode-card-active" href="/classic">
          <span class="mode-number">01 / Available</span>
          <strong>Classic</strong>
          <span>The faithful port of the original game.</span>
          <span class="mode-cta">Play Classic <span aria-hidden="true">→</span></span>
        </a>
        <div class="mode-card mode-card-disabled" aria-label="Revamp, coming later">
          <span class="mode-number">02 / Coming later</span>
          <strong>Revamp</strong>
          <span>A new version is in the works.</span>
          <button type="button" disabled>Coming later</button>
        </div>
      </div>
      <button class="text-button" type="button" data-action="lock">Lock this browser</button>
    </main>
  `;
  app
    .querySelector<HTMLButtonElement>('[data-action="lock"]')!
    .addEventListener('click', () => {
      forgetInvitation();
      showGate();
    });
}

async function showClassic(): Promise<void> {
  app.innerHTML = `
    <a class="classic-back" href="/">← Modes</a>
    <div class="classic-mount"></div>
  `;
  try {
    const { startClassicGame } = await import('./classic');
    await startClassicGame(app.querySelector<HTMLElement>('.classic-mount')!);
  } catch (error) {
    app.innerHTML = `
      <main class="entry-panel">
        <h1>The game could not start</h1>
        <p class="entry-copy">Please reload the page or return to the mode launcher.</p>
        <a href="/">← Modes</a>
      </main>
    `;
    console.error(error);
  }
}

if (!isInvited()) {
  showGate();
} else if (window.location.pathname.replace(/\/$/, '') === '/classic') {
  showClassic();
} else {
  showLauncher();
}
