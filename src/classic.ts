import { createGameAudio } from './game/audio';
import { createGamePhysics } from './game/physics';
import { createMenuPhysics } from './game/menu-physics';
import { createGameRenderer, type Screen } from './game/render';
import { finishKick, initialRound, type RoundState } from './game/rules';
import { createHighScoreStore } from './game/storage';

const STEP_SECONDS = 0.02;
const INTRO_SECONDS = 8.5;

export async function startClassicGame(mount: HTMLElement): Promise<void> {
  const audio = createGameAudio();
  const renderer = createGameRenderer();
  const highScoreStore = createHighScoreStore();
  let physics = createGamePhysics((sound) => audio.playContact(sound));
  let menuPhysics = createMenuPhysics();
  let round: RoundState = initialRound();
  let screen: Screen = 'intro';
  let highScore = highScoreStore.read();
  let newRecord = false;
  let selected = 0;
  let power = 0;
  let kickReady = false;
  let kickUsed = false;
  let secondsSinceKick = 0;
  let cryTriggerActive = false;
  let elapsed = 0;
  let accumulator = 0;
  let lastFrame = 0;

  mount.innerHTML = `
      <main class="game-shell">
        <div class="game-frame" tabindex="0" role="application" aria-label="Widener's Baby Punting Adventure"></div>
        <div class="game-controls">
          <button type="button" data-action="primary">Skip intro</button>
          <button type="button" data-action="secondary">Skip intro</button>
        </div>
        <p class="game-help">Use Enter and Space, or the buttons below the game.</p>
        <p class="game-status" aria-live="polite"></p>
      </main>
    `;
  const frame = mount.querySelector<HTMLDivElement>('.game-frame')!;
  const primary = mount.querySelector<HTMLButtonElement>(
    '[data-action="primary"]',
  )!;
  const secondary = mount.querySelector<HTMLButtonElement>(
    '[data-action="secondary"]',
  )!;
  const help = mount.querySelector<HTMLParagraphElement>('.game-help')!;
  const status = mount.querySelector<HTMLParagraphElement>('.game-status')!;

  function setScreen(nextScreen: Screen): void {
    if (
      nextScreen === 'title' &&
      screen !== 'title' &&
      screen !== 'instructions' &&
      screen !== 'credits'
    ) {
      menuPhysics = createMenuPhysics();
    }
    screen = nextScreen;
    elapsed = 0;
    audio.setScene(
      nextScreen === 'intro'
        ? 'silent'
        : nextScreen === 'playing' || nextScreen === 'end'
          ? 'outdoor'
          : 'menu',
    );
    updateControls();
  }

  function startRound(): void {
    audio.stopCry();
    physics = createGamePhysics((sound) => audio.playContact(sound));
    round = initialRound();
    power = 0;
    kickReady = false;
    kickUsed = false;
    secondsSinceKick = 0;
    cryTriggerActive = false;
    newRecord = false;
    renderer.resetCamera();
    setScreen('playing');
  }

  function primaryAction(): void {
    switch (screen) {
      case 'intro':
        setScreen('title');
        break;
      case 'title':
        if (selected === 0) startRound();
        else if (selected === 1) setScreen('instructions');
        else if (selected === 2) setScreen('credits');
        else {
          status.textContent =
            'You can close this browser tab when you are finished.';
        }
        break;
      case 'instructions':
      case 'credits':
        setScreen('title');
        break;
      case 'playing':
        if (!kickUsed) {
          physics.prepping = true;
          kickReady = true;
          updateControls();
        }
        break;
      case 'end':
        startRound();
        break;
    }
  }

  function secondaryAction(): void {
    if (screen === 'intro') setScreen('title');
    else if (screen === 'playing') {
      if (kickReady && !kickUsed) {
        physics.prepping = false;
        physics.firing = true;
        kickReady = false;
        kickUsed = true;
        secondsSinceKick = 0;
        updateControls();
      }
    } else if (screen === 'end') {
      selected = 0;
      setScreen('title');
    } else if (screen === 'title') {
      selected = (selected + 1) % 4;
      status.textContent = '';
    } else setScreen('title');
  }

  function handleCanvasTap(event: PointerEvent): void {
    if (screen === 'intro') {
      setScreen('title');
      return;
    }
    const rect = frame.getBoundingClientRect();
    const x = ((event.clientX - rect.left) / rect.width) * 400;
    const y = ((event.clientY - rect.top) / rect.height) * 300;
    if (screen === 'title') {
      if (x > 245 && y > 88 && y < 232) {
        selected = Math.min(3, Math.max(0, Math.round((y - 106) / 38)));
        primaryAction();
      }
    } else if (screen === 'instructions' || screen === 'credits') {
      setScreen('title');
    } else if (screen === 'end') {
      if (y > 190) secondaryAction();
      else primaryAction();
    }
  }

  function updateControls(): void {
    const labels: Record<Screen, [string, string, string]> = {
      intro: [
        'Skip intro',
        'Skip intro',
        'The original intro will continue automatically.',
      ],
      title: [
        'Select',
        'Next option',
        'Use ↑/↓ to choose, then Enter to select. Tap a menu item to open it.',
      ],
      instructions: [
        'Return',
        'Return',
        'Press Enter or tap the screen to return.',
      ],
      credits: ['Return', 'Return', 'Press Enter or tap the screen to return.'],
      playing: [
        'Pull back (Enter)',
        'Kick (Space)',
        'Pull back, then kick. The round lasts ten kicks.',
      ],
      end: [
        'Play again',
        'Main menu',
        'Enter replays; Space returns to the title.',
      ],
    };
    const [primaryLabel, secondaryLabel, helpText] = labels[screen];
    primary.textContent = primaryLabel;
    secondary.textContent = secondaryLabel;
    secondary.hidden = screen === 'intro';
    primary.disabled = screen === 'playing' && kickUsed;
    secondary.disabled = screen === 'playing' && !kickReady;
    help.textContent =
      screen === 'playing'
        ? kickUsed
          ? 'Wait for the baby to land.'
          : kickReady
            ? 'Press Space when the falling baby reaches the leg.'
            : 'Press Enter to pull back, then time Space to kick.'
        : helpText;
    status.textContent =
      screen === 'playing'
        ? `Score ${round.score}. Kicks left ${round.kicksLeft}.`
        : screen === 'end'
          ? `Final score ${round.score}. High score ${highScore}.`
          : '';
  }

  function tick(): void {
    elapsed += STEP_SECONDS;
    if (screen === 'intro' && elapsed >= INTRO_SECONDS) {
      setScreen('title');
      return;
    }
    if (
      screen === 'title' ||
      screen === 'instructions' ||
      screen === 'credits'
    ) {
      menuPhysics.step();
      return;
    }
    if (screen !== 'playing') return;
    physics.step();
    if (kickUsed) secondsSinceKick += STEP_SECONDS;
    if (physics.prepping) power = Math.min(100, power + 2);
    const velocity = physics.baby.body.getLinearVelocity();
    const cryTrigger = Math.abs(velocity.x) > 13 || Math.abs(velocity.y) > 13;
    if (cryTrigger && !cryTriggerActive) audio.playCry();
    if (!cryTrigger && cryTriggerActive) audio.stopCry();
    cryTriggerActive = cryTrigger;
    physics.baby.screaming = audio.isCryPlaying();
    if (physics.isBabySettled(secondsSinceKick)) {
      audio.stopCry();
      cryTriggerActive = false;
      round = finishKick(round, physics.getBabyPosition());
      if (round.kicksLeft === 0) {
        newRecord = round.score > highScore;
        if (newRecord) {
          highScore = round.score;
          highScoreStore.save(highScore);
        }
        setScreen('end');
      } else {
        physics.nextBaby();
        power = 0;
        kickReady = false;
        kickUsed = false;
        secondsSinceKick = 0;
        updateControls();
      }
    }
  }

  function frameLoop(time: number): void {
    const delta = lastFrame ? Math.min((time - lastFrame) / 1000, 0.1) : 0;
    lastFrame = time;
    accumulator += delta;
    while (accumulator >= STEP_SECONDS) {
      tick();
      accumulator -= STEP_SECONDS;
    }
    renderer.render({
      screen,
      physics,
      menuPhysics,
      round,
      highScore,
      newRecord,
      selected,
      power,
      elapsed,
    });
    requestAnimationFrame((nextTime) => frameLoop(nextTime));
  }

  frame.appendChild(await renderer.init());
  primary.addEventListener('click', () => {
    audio.unlock();
    primaryAction();
    frame.focus();
  });
  secondary.addEventListener('click', () => {
    audio.unlock();
    secondaryAction();
    frame.focus();
  });
  frame.addEventListener('pointerdown', (event) => {
    audio.unlock();
    frame.focus();
    handleCanvasTap(event);
  });
  window.addEventListener('keydown', (event) => {
    if (!['Enter', 'Space', 'ArrowUp', 'ArrowDown'].includes(event.code)) {
      return;
    }
    if (
      event.target instanceof HTMLButtonElement ||
      event.target instanceof HTMLAnchorElement
    ) {
      return;
    }
    event.preventDefault();
    audio.unlock();
    if (event.repeat && screen !== 'playing') return;
    if (event.code === 'ArrowUp' && screen === 'title') {
      selected = Math.max(0, selected - 1);
      status.textContent = '';
    }
    if (event.code === 'ArrowDown' && screen === 'title') {
      selected = Math.min(3, selected + 1);
      status.textContent = '';
    }
    if (event.code === 'Enter' && !event.repeat) primaryAction();
    if (event.code === 'Space' && !event.repeat) secondaryAction();
  });
  updateControls();
  requestAnimationFrame((time) => frameLoop(time));
}
