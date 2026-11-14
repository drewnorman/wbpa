import { describe, expect, it } from 'vitest';
import { Vec2 } from 'planck';
import { createGamePhysics } from './physics';
import { FIRST_CAN_X } from './rules';

describe('original kick physics', () => {
  it('can launch a timed kick past the first can', () => {
    const physics = createGamePhysics(() => undefined);
    physics.prepping = true;
    for (let tick = 0; tick < 130; tick += 1) physics.step();
    physics.prepping = false;
    physics.firing = true;

    let farthestX = physics.getBabyPosition().x;
    for (let tick = 0; tick < 600; tick += 1) {
      physics.step();
      farthestX = Math.max(farthestX, physics.getBabyPosition().x);
      if (physics.isBabySettled(tick / 60)) break;
    }

    expect(farthestX).toBeGreaterThan(FIRST_CAN_X);
  });

  it('ends a turn when the baby stays trapped near the kicking foot', () => {
    const physics = createGamePhysics(() => undefined);
    physics.baby.body.setTransform(Vec2(4, 5), 0);

    expect(physics.isBabySettled(4.9)).toBe(false);
    expect(physics.isBabySettled(5)).toBe(true);
  });
});
