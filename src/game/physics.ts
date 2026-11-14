import { Box, Polygon, RevoluteJoint, Vec2, World, type Body } from 'planck';
import {
  CAN_COUNT,
  CAN_SPACING,
  CAN_Y,
  CAN_WIDTH,
  CAN_HEIGHT,
  FIRST_CAN_X,
  type Position,
} from './rules';

export type ContactSound = 'can-hit' | 'thud' | 'grass-rustle';

const BABY_VERTICES = [
  [-1, 1],
  [-1.5, 0.5],
  [-1.5, -0.25],
  [-0.5, -1],
  [1, -1.25],
  [1.5, -0.5],
  [1.5, -0.25],
  [1, 0.25],
] as const;

const BLANKET_COLORS = [0x7f0000, 0xc8c800, 0x7f007f, 0x00007f, 0x007f00];
const SKIN_COLORS = [0xe8bb8e, 0x4b3b2b, 0xb77a3d, 0xd2b496, 0x7a5f44];
const LEG_LOWERING = 1;
const TRAPPED_BABY_SECONDS = 5;
const MAX_FLIGHT_SECONDS = 30;

export interface BabyState {
  body: Body;
  blanketColor: number;
  skinColor: number;
  screaming: boolean;
}

export function createGamePhysics(onContact: (sound: ContactSound) => void) {
  const world = new World(Vec2(0, -9.8));
  const cans: Body[] = [];
  const ground = world.createBody({
    position: Vec2(0, 2),
    userData: 'ground',
  });
  ground.createFixture(Box(1200, 2), { density: 1, restitution: 0.3 });

  for (let index = 0; index < CAN_COUNT; index += 1) {
    const can = world.createBody({
      position: Vec2(FIRST_CAN_X + index * CAN_SPACING, CAN_Y),
      userData: 'can',
    });
    can.createFixture(Box(CAN_WIDTH / 2, 0.1));
    can.createFixture(
      Box(
        0.1,
        CAN_HEIGHT / 2,
        Vec2(-CAN_WIDTH / 2 + 0.1, CAN_HEIGHT / 2 - 0.1),
      ),
    );
    can.createFixture(
      Box(0.1, CAN_HEIGHT / 2, Vec2(CAN_WIDTH / 2 - 0.1, CAN_HEIGHT / 2 - 0.1)),
    );
    cans.push(can);
  }

  const thigh = world.createDynamicBody({
    position: Vec2(3, 10 - LEG_LOWERING),
    userData: 'thigh',
  });
  thigh.createFixture(Box(0.5, 1.5), {
    density: 0.1,
    friction: 1,
    restitution: 0.08,
  });
  const shin = world.createDynamicBody({
    position: Vec2(3.1, 7.4 - LEG_LOWERING),
    userData: 'shin',
  });
  shin.createFixture(Box(0.4, 1.25), {
    density: 0.1,
    friction: 1,
    restitution: 0.5,
  });
  const foot = world.createDynamicBody({
    position: Vec2(3.05, 6.3 - LEG_LOWERING),
    userData: 'foot',
  });
  foot.createFixture(
    Polygon([Vec2(0, 0), Vec2(0, -0.75), Vec2(2, -0.75), Vec2(0.6, 0)]),
    { density: 0.1, friction: 1, restitution: 0.5 },
  );

  const hanger = world.createBody();
  world.createJoint(
    RevoluteJoint(
      { lowerAngle: -1.4, upperAngle: 1.5, enableLimit: true },
      hanger,
      thigh,
      Vec2(3, 11.5 - LEG_LOWERING),
    ),
  );
  world.createJoint(
    RevoluteJoint(
      { lowerAngle: -2.2, upperAngle: 0, enableLimit: true },
      thigh,
      shin,
      Vec2(3.1, 8.65 - LEG_LOWERING),
    ),
  );
  world.createJoint(
    RevoluteJoint(
      { lowerAngle: -0.4, upperAngle: 0.1, enableLimit: true },
      shin,
      foot,
      Vec2(3.05, 6.55 - LEG_LOWERING),
    ),
  );

  world.on('begin-contact', (contact) => {
    const first = contact.getFixtureA().getBody().getUserData();
    const second = contact.getFixtureB().getBody().getUserData();
    if (first !== 'baby' && second !== 'baby') return;
    const other = first === 'baby' ? second : first;
    if (other === 'can') onContact('can-hit');
    if (other === 'ground') onContact('grass-rustle');
    if (other === 'thigh' || other === 'shin' || other === 'foot') {
      onContact('thud');
    }
  });

  function spawnBaby(): BabyState {
    const body = world.createDynamicBody({
      position: Vec2(7.6, 30),
      userData: 'baby',
    });
    body.createFixture(
      Polygon(BABY_VERTICES.map(([x, y]) => Vec2(x * 1.1, y * 1.1))),
      { density: 0.03, friction: 1.2, restitution: 0.003 },
    );
    return {
      body,
      blanketColor:
        BLANKET_COLORS[Math.floor(Math.random() * BLANKET_COLORS.length)],
      skinColor: SKIN_COLORS[Math.floor(Math.random() * SKIN_COLORS.length)],
      screaming: false,
    };
  }

  function nextBaby(): void {
    world.destroyBody(physics.baby.body);
    physics.baby = spawnBaby();
    physics.prepping = false;
  }

  function step(): void {
    world.step(1 / 60, 8, 3);
    if (physics.prepping) {
      thigh.applyTorque(-40);
      if (thigh.getAngle() < -1.5) thigh.setAngularVelocity(0);
    }
    if (physics.firing) {
      thigh.applyAngularImpulse(5);
      if (thigh.getAngle() > 0.8) {
        physics.firing = false;
        thigh.setAngularVelocity(0);
        physics.resetting = true;
      }
    }
    if (physics.resetting) {
      thigh.setAngularVelocity(-1);
      if (thigh.getAngle() <= 0.2) {
        thigh.setAngularVelocity(0);
        physics.resetting = false;
      }
    }
  }

  function getBabyPosition(): Position {
    const { x, y } = physics.baby.body.getPosition();
    return { x, y };
  }

  function isBabySettled(secondsSinceKick: number): boolean {
    if (!physics.baby.body.isAwake()) return true;
    const { x, y } = physics.baby.body.getPosition();
    return (
      (secondsSinceKick >= TRAPPED_BABY_SECONDS && x < 10 && y < 10) ||
      secondsSinceKick >= MAX_FLIGHT_SECONDS
    );
  }

  const physics = {
    world,
    cans,
    thigh,
    shin,
    foot,
    baby: spawnBaby(),
    prepping: false,
    firing: false,
    resetting: false,
    nextBaby,
    step,
    getBabyPosition,
    isBabySettled,
  };
  return physics;
}

export type GamePhysics = ReturnType<typeof createGamePhysics>;

export const BABY_SHAPE = BABY_VERTICES.map(([x, y]) => ({
  x: x * 1.1,
  y: y * 1.1,
}));
