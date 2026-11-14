import { describe, expect, it } from 'vitest';
import { createHighScoreStore } from './storage';

describe('browser high score', () => {
  it('persists a score for a later game session', () => {
    const values = new Map<string, string>();
    const storage = {
      getItem: (key: string) => values.get(key) ?? null,
      setItem: (key: string, value: string) => {
        values.set(key, value);
      },
    };
    createHighScoreStore(storage).save(120);
    expect(createHighScoreStore(storage).read()).toBe(120);
  });
});
