const HIGH_SCORE_KEY = 'wbpa.highScore.v1';

export interface HighScoreStore {
  read(): number;
  save(score: number): void;
}

export function createHighScoreStore(
  storage?: Pick<Storage, 'getItem' | 'setItem'>,
): HighScoreStore {
  return {
    read(): number {
      try {
        const value = Number.parseInt(
          (storage ?? window.localStorage).getItem(HIGH_SCORE_KEY) ?? '0',
          10,
        );
        return Number.isFinite(value) && value >= 0 ? value : 0;
      } catch {
        return 0;
      }
    },
    save(score: number): void {
      try {
        (storage ?? window.localStorage).setItem(
          HIGH_SCORE_KEY,
          String(Math.max(0, Math.floor(score))),
        );
      } catch {
        // The round remains playable if browser storage is unavailable.
      }
    },
  };
}
