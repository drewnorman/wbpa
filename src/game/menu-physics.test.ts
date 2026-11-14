import { describe, expect, it } from 'vitest';
import { MENU_PIXELS_PER_METER, createMenuPhysics } from './menu-physics';

describe('title screen baby', () => {
  it('rebounds across the screen without escaping its edges', () => {
    const menu = createMenuPhysics();
    const width = 400 / MENU_PIXELS_PER_METER;
    const height = 300 / MENU_PIXELS_PER_METER;
    let minX = width;
    let maxX = 0;
    let minY = height;
    let maxY = 0;

    for (let tick = 0; tick < 1200; tick += 1) {
      menu.step();
      const position = menu.baby.getPosition();
      minX = Math.min(minX, position.x);
      maxX = Math.max(maxX, position.x);
      minY = Math.min(minY, position.y);
      maxY = Math.max(maxY, position.y);
      expect(position.x).toBeGreaterThan(-2);
      expect(position.x).toBeLessThan(width + 2);
      expect(position.y).toBeGreaterThan(2);
      expect(position.y).toBeLessThan(height + 2);
    }

    expect(minX).toBeLessThan(6);
    expect(maxX).toBeGreaterThan(width - 6);
    expect(minY).toBeGreaterThan(3);
    expect(minY).toBeLessThan(5);
    expect(maxY).toBeGreaterThan(height - 6);
  });
});
