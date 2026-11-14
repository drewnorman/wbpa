import { describe, expect, it } from 'vitest';
import { finishKick, initialRound, scoreLanding } from './rules';

describe('original scoring rules', () => {
  it('awards ten points per can center passed', () => {
    expect(scoreLanding({ x: 89, y: 8 })).toBe(0);
    expect(scoreLanding({ x: 91, y: 8 })).toBe(10);
    expect(scoreLanding({ x: 171, y: 8 })).toBe(30);
  });

  it('adds twenty points for resting inside a can', () => {
    expect(scoreLanding({ x: 90, y: 3.5 })).toBe(30);
    expect(scoreLanding({ x: 130, y: 3.5 })).toBe(40);
  });

  it('ends a round after ten kicks', () => {
    let round = initialRound();
    for (let index = 0; index < 10; index += 1) {
      round = finishKick(round, { x: 90, y: 3.5 });
    }
    expect(round).toEqual({ score: 300, kicksLeft: 0 });
    expect(finishKick(round, { x: 650, y: 3.5 })).toEqual(round);
  });
});
