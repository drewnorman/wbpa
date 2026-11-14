import {
  Application,
  Assets,
  Container,
  Graphics,
  Sprite,
  type Texture,
} from 'pixi.js';
import type { Body } from 'planck';
import { imageUrls } from './assets';
import { MENU_PIXELS_PER_METER, type MenuPhysics } from './menu-physics';
import { BABY_SHAPE, type GamePhysics } from './physics';
import {
  CAN_COUNT,
  CAN_SPACING,
  CAN_Y,
  CAN_HEIGHT,
  CAN_WIDTH,
  FIRST_CAN_X,
  type RoundState,
} from './rules';

export type Screen =
  'intro' | 'title' | 'instructions' | 'credits' | 'playing' | 'end';

export interface RenderState {
  screen: Screen;
  physics: GamePhysics;
  menuPhysics: MenuPhysics;
  round: RoundState;
  highScore: number;
  newRecord: boolean;
  selected: number;
  power: number;
  elapsed: number;
}

const WIDTH = 400;
const HEIGHT = 300;
const PIXELS_PER_METER = 15 * 0.35;
const SKY_COLOR = 0x79a2e9;

export function createGameRenderer() {
  const app = new Application();
  const textures = new Map<string, Texture>();
  const sky = new Graphics();
  const clouds = new Container();
  const menuGround = new Container();
  const world = new Container();
  const leg = new Graphics();
  const babyGraphics = new Graphics();
  const hud = new Container();
  const scoreDigits = new Container();
  const kicksDigits = new Container();
  const powerBar = new Graphics();
  const menu = new Container();
  const menuArt = new Sprite();
  const menuBaby = new Graphics();
  const cursor = new Graphics();
  const endScoreDigits = new Container();
  const endHighDigits = new Container();
  let cameraX = 0;
  let cameraY = 0;
  let lastScore = -1;
  let lastKicks = -1;
  let lastEndScore = -1;
  let lastHigh = -1;

  function resetCamera(): void {
    cameraX = 0;
    cameraY = 0;
  }

  async function init(): Promise<HTMLCanvasElement> {
    await app.init({
      width: WIDTH,
      height: HEIGHT,
      preference: 'webgl',
      autoStart: false,
      backgroundColor: 0x000000,
      resolution: Math.min(window.devicePixelRatio || 1, 2),
      autoDensity: true,
    });

    const urls = [
      ...Object.values(imageUrls).filter(
        (value): value is string => typeof value === 'string',
      ),
      ...imageUrls.digits,
    ];
    await Promise.all(
      urls.map(async (url) => {
        textures.set(url, await Assets.load<Texture>(url));
      }),
    );

    sky.rect(0, 0, WIDTH, HEIGHT).fill(SKY_COLOR);
    app.stage.addChild(sky, clouds, menuGround, world, hud, menu);
    createClouds();
    createWorld();
    createHud();
    createMenu();
    app.canvas.style.width = '100%';
    app.canvas.style.height = '100%';
    return app.canvas;
  }

  function texture(url: string): Texture {
    const result = textures.get(url);
    if (!result) throw new Error(`Missing image: ${url}`);
    return result;
  }

  function sprite(
    url: string,
    x: number,
    y: number,
    width: number,
    height: number,
  ): Sprite {
    const imageSprite = new Sprite(texture(url));
    imageSprite.position.set(x, y);
    imageSprite.width = width;
    imageSprite.height = height;
    return imageSprite;
  }

  function createClouds(): void {
    const urls = [
      imageUrls.cloud1,
      imageUrls.cloud2,
      imageUrls.cloud3,
      imageUrls.cloud4,
    ];
    for (let index = 0; index < 35; index += 1) {
      const x = ((index * 179 + 27) % 3600) - 40;
      const y = 18 + ((index * 83) % 135);
      const cloud = sprite(urls[index % urls.length], x, y, 105, 105);
      cloud.alpha = 0.8;
      clouds.addChild(cloud);
    }
  }

  function createWorld(): void {
    for (let x = 0; x < 3700; x += 105) {
      world.addChild(sprite(imageUrls.ground, x, 272, 140, 70));
    }
    for (let index = 0; index < CAN_COUNT; index += 1) {
      const centerX = (FIRST_CAN_X + index * CAN_SPACING) * PIXELS_PER_METER;
      const bottomY = HEIGHT - CAN_Y * PIXELS_PER_METER;
      world.addChild(
        sprite(
          imageUrls.can,
          centerX - (CAN_WIDTH * PIXELS_PER_METER) / 2,
          bottomY - CAN_HEIGHT * PIXELS_PER_METER,
          CAN_WIDTH * PIXELS_PER_METER,
          CAN_HEIGHT * PIXELS_PER_METER,
        ),
      );
      const number = String(index + 1);
      for (let digit = 0; digit < number.length; digit += 1) {
        world.addChild(
          sprite(
            imageUrls.digits[Number(number[digit])],
            centerX - (number.length * 10) / 2 + digit * 10,
            bottomY - 13,
            10,
            11,
          ),
        );
      }
    }
    world.addChild(sprite(imageUrls.widener, -5, 188, 102, 121));
    world.addChild(leg, babyGraphics);
    menuGround.addChild(sprite(imageUrls.ground, 0, 272, 140, 70));
    menuGround.addChild(sprite(imageUrls.ground, 105, 272, 140, 70));
    menuGround.addChild(sprite(imageUrls.ground, 210, 272, 140, 70));
    menuGround.addChild(sprite(imageUrls.ground, 315, 272, 140, 70));
  }

  function createHud(): void {
    hud.addChild(sprite(imageUrls.score, 168, 0, 51, 26));
    hud.addChild(sprite(imageUrls.kicksLeft, 330, 0, 77, 19));
    hud.addChild(scoreDigits, kicksDigits, powerBar);
  }

  function createMenu(): void {
    menu.addChild(menuBaby, menuArt, cursor, endScoreDigits, endHighDigits);
  }

  function drawNumber(
    container: Container,
    value: number,
    x: number,
    y: number,
    scale: number,
  ): void {
    for (const child of container.removeChildren()) child.destroy();
    const digits = String(Math.max(0, Math.floor(value)));
    for (let index = 0; index < digits.length; index += 1) {
      const digit = Number(digits[index]);
      container.addChild(
        sprite(
          imageUrls.digits[digit],
          x + index * 6 * scale,
          y,
          8 * scale,
          9 * scale,
        ),
      );
    }
  }

  function drawWorld(physics: GamePhysics): void {
    const babyPos = physics.getBabyPosition();
    const targetX = Math.min(0, 200 - babyPos.x * PIXELS_PER_METER);
    const targetY =
      babyPos.y * PIXELS_PER_METER > 240
        ? babyPos.y * PIXELS_PER_METER - 240
        : 0;
    cameraX += (targetX - cameraX) * 0.12;
    cameraY += (targetY - cameraY) * 0.12;
    world.position.set(cameraX, cameraY);
    clouds.position.x = cameraX * 0.3;

    leg.clear();
    const bodyPoints = (
      body: Body,
      vertices: readonly (readonly [number, number])[],
    ): number[] => {
      const position = body.getPosition();
      const cx = position.x * PIXELS_PER_METER;
      const cy = HEIGHT - position.y * PIXELS_PER_METER;
      const angle = -body.getAngle();
      return vertices.flatMap(([x, y]) => {
        const px = x * PIXELS_PER_METER;
        const py = -y * PIXELS_PER_METER;
        return [
          cx + px * Math.cos(angle) - py * Math.sin(angle),
          cy + px * Math.sin(angle) + py * Math.cos(angle),
        ];
      });
    };
    const parts = [
      { body: physics.shin, width: 0.8, height: 2.5, color: 0xe8bb8e },
      { body: physics.thigh, width: 1, height: 3, color: 0x4e4e4e },
    ];
    for (const part of parts) {
      const w = part.width / 2;
      const h = part.height / 2;
      leg
        .poly(
          bodyPoints(part.body, [
            [-w, -h],
            [w, -h],
            [w, h],
            [-w, h],
          ]),
        )
        .fill(part.color);
    }
    leg
      .poly(
        bodyPoints(physics.foot, [
          [0, 0],
          [0, -0.75],
          [2, -0.75],
          [0.6, 0],
        ]),
      )
      .fill(0x000000);

    babyGraphics.clear();
    const { baby } = physics;
    const cx = babyPos.x * PIXELS_PER_METER;
    const cy = HEIGHT - babyPos.y * PIXELS_PER_METER;
    const angle = -baby.body.getAngle();
    const rotated = (x: number, y: number): [number, number] => [
      cx + x * Math.cos(angle) - y * Math.sin(angle),
      cy + x * Math.sin(angle) + y * Math.cos(angle),
    ];
    const points = BABY_SHAPE.flatMap(({ x, y }) =>
      rotated(x * PIXELS_PER_METER, -y * PIXELS_PER_METER),
    );
    babyGraphics.poly(points).fill(baby.blanketColor);
    const [headX, headY] = rotated(-4.2, -1.4);
    babyGraphics.circle(headX, headY, 2.8).fill(baby.skinColor);
    const [eyeX, eyeY] = rotated(-5.8, -2.3);
    babyGraphics.circle(eyeX, eyeY, 0.55).fill(0x000000);
    const [mouthX, mouthY] = rotated(-3.2, -0.8);
    if (baby.screaming) babyGraphics.circle(mouthX, mouthY, 0.9).fill(0x000000);
    else babyGraphics.rect(mouthX, mouthY, 1.4, 0.45).fill(0x000000);
  }

  function drawHud(state: RenderState): void {
    if (state.round.score !== lastScore) {
      drawNumber(scoreDigits, state.round.score, 180, 25, 2);
      lastScore = state.round.score;
    }
    if (state.round.kicksLeft !== lastKicks) {
      drawNumber(kicksDigits, state.round.kicksLeft, 365, 24, 2);
      lastKicks = state.round.kicksLeft;
    }
    powerBar.clear();
    powerBar.rect(9, 9, 52, 7).fill(0x808080);
    powerBar.rect(10, 10, 50, 5).fill(0xff0000);
    powerBar.rect(10, 10, state.power / 2, 5).fill(0x00ff00);
  }

  function drawMenu(state: RenderState): void {
    const { screen } = state;
    menuArt.visible = true;
    cursor.clear();
    menuBaby.clear();
    endScoreDigits.visible = screen === 'end';
    endHighDigits.visible = screen === 'end';
    if (screen === 'intro') {
      menuArt.texture = texture(imageUrls.intro);
      menuArt.position.set(55, 121);
      menuArt.width = 290;
      menuArt.height = 58;
      return;
    }
    const url =
      screen === 'title'
        ? imageUrls.title
        : screen === 'instructions'
          ? imageUrls.instructions
          : screen === 'credits'
            ? imageUrls.credits
            : state.newRecord
              ? imageUrls.endNewHigh
              : imageUrls.end;
    menuArt.texture = texture(url);
    menuArt.position.set(0, 0);
    menuArt.width = WIDTH;
    menuArt.height = HEIGHT;
    if (screen === 'title') {
      const y = [106, 144, 182, 218][state.selected];
      const x = [300, 225, 270, 300][state.selected];
      cursor.rect(x, y - 2, 9, 5).fill(0xfff600);
    }
    if (screen === 'end') {
      if (state.round.score !== lastEndScore) {
        drawNumber(endScoreDigits, state.round.score, 230, 119, 2);
        lastEndScore = state.round.score;
      }
      if (state.highScore !== lastHigh) {
        drawNumber(endHighDigits, state.highScore, 230, 142, 2);
        lastHigh = state.highScore;
      }
    } else {
      const body = state.menuPhysics.baby;
      const position = body.getPosition();
      const cx = position.x * MENU_PIXELS_PER_METER;
      const cy = HEIGHT - position.y * MENU_PIXELS_PER_METER;
      const angle = -body.getAngle();
      const rotated = (x: number, y: number): [number, number] => [
        cx + x * Math.cos(angle) - y * Math.sin(angle),
        cy + x * Math.sin(angle) + y * Math.cos(angle),
      ];
      menuBaby
        .poly(
          BABY_SHAPE.flatMap(({ x, y }) =>
            rotated(x * MENU_PIXELS_PER_METER, -y * MENU_PIXELS_PER_METER),
          ),
        )
        .fill(0x7f007f);
      const [headX, headY] = rotated(-7, -2);
      menuBaby.circle(headX, headY, 4).fill(0xe8bb8e);
      const [eyeX, eyeY] = rotated(-9, -3);
      menuBaby.circle(eyeX, eyeY, 0.7).fill(0x000000);
    }
  }

  function render(state: RenderState): void {
    sky.visible = state.screen !== 'intro';
    clouds.visible = state.screen !== 'intro' && state.screen !== 'end';
    menuGround.visible =
      state.screen === 'title' ||
      state.screen === 'instructions' ||
      state.screen === 'credits';
    world.visible = state.screen === 'playing';
    hud.visible = state.screen === 'playing';
    menu.visible = state.screen !== 'playing';
    if (state.screen === 'playing') {
      drawWorld(state.physics);
      drawHud(state);
    } else {
      clouds.position.x = 0;
      drawMenu(state);
    }
    app.renderer.render(app.stage);
  }

  return { init, render, resetCamera };
}
