import { musicUrls, soundUrls } from './assets';
import type { ContactSound } from './physics';

type MusicScene = 'menu' | 'outdoor' | 'silent';

export function createGameAudio() {
  const menu = new Audio(musicUrls.menu);
  const outdoor = new Audio(musicUrls.outdoor);
  let scene: MusicScene = 'silent';
  let unlocked = false;
  let activeCry: HTMLAudioElement | null = null;

  menu.loop = true;
  outdoor.loop = true;
  menu.volume = 0.45;
  outdoor.volume = 0.45;

  function unlock(): void {
    unlocked = true;
    playCurrent();
  }

  function setScene(nextScene: MusicScene): void {
    if (scene === nextScene) return;
    menu.pause();
    outdoor.pause();
    scene = nextScene;
    playCurrent();
  }

  function playCurrent(): void {
    if (!unlocked) return;
    const track =
      scene === 'menu' ? menu : scene === 'outdoor' ? outdoor : null;
    if (track) void track.play().catch(() => undefined);
  }

  function playContact(sound: ContactSound): void {
    if (!unlocked) return;
    const audio = new Audio(soundUrls[sound]);
    audio.volume = 0.65;
    void audio.play().catch(() => undefined);
  }

  function playCry(): void {
    if (!unlocked) return;
    const source =
      soundUrls.cries[Math.floor(Math.random() * soundUrls.cries.length)];
    const audio = new Audio(source);
    audio.volume = 0.45;
    stopCry();
    activeCry = audio;
    audio.addEventListener(
      'ended',
      () => {
        if (activeCry === audio) activeCry = null;
      },
      { once: true },
    );
    void audio.play().catch(() => undefined);
  }

  function isCryPlaying(): boolean {
    return activeCry !== null && !activeCry.paused && !activeCry.ended;
  }

  function stopCry(): void {
    activeCry?.pause();
    activeCry = null;
  }

  return { unlock, setScene, playContact, playCry, isCryPlaying, stopCry };
}
