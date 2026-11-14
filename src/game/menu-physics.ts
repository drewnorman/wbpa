import { Box, Polygon, Vec2, World } from 'planck';
import { BABY_SHAPE } from './physics';

export const MENU_PIXELS_PER_METER = 15 * 0.35 * 1.6;

export function createMenuPhysics() {
  const world = new World(Vec2(0, -9.8));
  const baby = world.createDynamicBody({
    position: Vec2(22, 20),
    userData: 'menu-baby',
  });
  let groundContact = false;

  baby.createFixture(Polygon(BABY_SHAPE.map(({ x, y }) => Vec2(x, y))), {
    density: 0.03,
    friction: 0.1,
    restitution: 0.95,
  });
  baby.setLinearVelocity(Vec2(12, 27));

  const width = 400 / MENU_PIXELS_PER_METER;
  const height = 300 / MENU_PIXELS_PER_METER;
  // The grass is artwork; the collision plane sits just under its green strip.
  const ground = world.createBody({
    position: Vec2(width / 2, 0),
    userData: 'menu-ground',
  });
  ground.createFixture(Box(width, 2), { restitution: 0.95 });
  const ceiling = world.createBody({
    position: Vec2(width / 2, height + 2),
  });
  ceiling.createFixture(Box(width, 2), { restitution: 0.95 });
  const left = world.createBody({ position: Vec2(-2, height / 2) });
  left.createFixture(Box(2, height), { restitution: 0.95 });
  const right = world.createBody({
    position: Vec2(width + 2, height / 2),
  });
  right.createFixture(Box(2, height), { restitution: 0.95 });
  world.on('begin-contact', (contact) => {
    const first = contact.getFixtureA().getBody().getUserData();
    const second = contact.getFixtureB().getBody().getUserData();
    if (first === 'menu-ground' || second === 'menu-ground') {
      groundContact = true;
    }
  });

  function step(): void {
    world.step(1 / 60, 8, 3);
    const velocity = baby.getLinearVelocity();
    const x =
      Math.abs(velocity.x) < 6 ? (velocity.x < 0 ? -12 : 12) : velocity.x;
    const y = groundContact ? 27 : velocity.y;
    groundContact = false;
    baby.setLinearVelocity(Vec2(x, y));
  }

  return { world, baby, step };
}

export type MenuPhysics = ReturnType<typeof createMenuPhysics>;
