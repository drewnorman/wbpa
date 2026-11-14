import {
  Application,
  Assets,
  Container,
  Graphics,
  Rectangle,
  Sprite,
  Texture,
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
const CLOUD_SPEED = 5.25;
const CLOUD_SPAN = 3600;
const FACE_ANGLE = (60 * Math.PI) / 180;
const FACE_COS = Math.cos(FACE_ANGLE);
const FACE_SIN = Math.sin(FACE_ANGLE);
const FACE_OFFSET_X = 0.7;
const FACE_OFFSET_Y = -0.6;

function rotateFacePoint(
  x: number,
  y: number,
  centerX: number,
  centerY: number,
): [number, number] {
  const dx = x - centerX;
  const dy = y - centerY;
  return [
    centerX + dx * FACE_COS + dy * FACE_SIN + FACE_OFFSET_X,
    centerY + dy * FACE_COS - dx * FACE_SIN + FACE_OFFSET_Y,
  ];
}

export function createGameRenderer() {
  const app = new Application();
  const textures = new Map<string, Texture>();
  const sky = new Graphics();
  const clouds = new Container();
  const cloudSprites: Sprite[] = [];
  const menuGround = new Container();
  const world = new Container();
  const cans = new Container();
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
  let lastCloudTime = 0;

  function resetCamera(): void {
    cameraX = 0;
    cameraY = 0;
  }

  async function init(): Promise<HTMLCanvasElement> {
    await app.init({
      width: WIDTH,
      height: HEIGHT,
      preference: 'webgl',
      antialias: true,
      autoStart: false,
      backgroundColor: 0x000000,
      resolution: Math.min(Math.max(window.devicePixelRatio || 1, 3), 4),
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
    app.stage.addChild(sky, clouds, world, hud, menu);
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

  function addDigit(
    container: Container,
    digit: number,
    x: number,
    y: number,
    width: number,
    height: number,
  ): void {
    const url = imageUrls.digits[digit];
    const shadow = sprite(url, x + 1, y + 1, width, height);
    shadow.tint = 0x4a3700;
    container.addChild(shadow, sprite(url, x, y, width, height));
  }

  function createClouds(): void {
    const urls = [
      imageUrls.cloud1,
      imageUrls.cloud2,
      imageUrls.cloud3,
      imageUrls.cloud4,
    ];
    let seed = 739;
    const random = (): number => {
      seed = (seed * 1664525 + 1013904223) >>> 0;
      return seed / 0x100000000;
    };
    let x = -170;
    while (x < CLOUD_SPAN) {
      x += 140 + random() * 170;
      const y = 12 + random() * 140;
      const cloud = sprite(
        urls[Math.floor(random() * urls.length)],
        x,
        y,
        105,
        105,
      );
      clouds.addChild(cloud);
      cloudSprites.push(cloud);
    }
  }

  function moveClouds(): void {
    const now = performance.now();
    const seconds = lastCloudTime
      ? Math.min((now - lastCloudTime) / 1000, 0.1)
      : 0;
    lastCloudTime = now;
    for (const cloud of cloudSprites) {
      cloud.x += CLOUD_SPEED * seconds;
      if (cloud.x > CLOUD_SPAN) cloud.x = -105;
    }
  }

  function createWorld(): void {
    for (let index = 0; index < CAN_COUNT; index += 1) {
      const centerX = (FIRST_CAN_X + index * CAN_SPACING) * PIXELS_PER_METER;
      const bottomY = HEIGHT - CAN_Y * PIXELS_PER_METER;
      const canHeight = CAN_HEIGHT * PIXELS_PER_METER;
      cans.addChild(
        sprite(
          imageUrls.can,
          centerX - (CAN_WIDTH * PIXELS_PER_METER) / 2,
          bottomY - canHeight,
          CAN_WIDTH * PIXELS_PER_METER,
          canHeight,
        ),
      );
      const number = String(index + 1);
      for (let digit = 0; digit < number.length; digit += 1) {
        addDigit(
          cans,
          Number(number[digit]),
          centerX - (number.length * 10) / 2 + digit * 10,
          bottomY - canHeight / 2 - 5.5,
          10,
          11,
        );
      }
    }
    const portrait = sprite(imageUrls.widener, -5, 188, 102, 121);
    const nearLegFrame = new Rectangle(80, 420, 135, 345);
    const nearLeg = new Sprite(
      new Texture({ source: portrait.texture.source, frame: nearLegFrame }),
    );
    nearLeg.position.set(
      portrait.x + (nearLegFrame.x / portrait.texture.width) * portrait.width,
      portrait.y + (nearLegFrame.y / portrait.texture.height) * portrait.height,
    );
    nearLeg.width =
      (nearLegFrame.width / portrait.texture.width) * portrait.width;
    nearLeg.height =
      (nearLegFrame.height / portrait.texture.height) * portrait.height;
    world.addChild(babyGraphics, leg, portrait, nearLeg);
    world.addChild(cans);
    for (let x = 0; x < 3700; x += 105) {
      world.addChild(sprite(imageUrls.ground, x, 272, 140, 70));
    }
    menuGround.addChild(sprite(imageUrls.ground, 0, 272, 140, 70));
    menuGround.addChild(sprite(imageUrls.ground, 105, 272, 140, 70));
    menuGround.addChild(sprite(imageUrls.ground, 210, 272, 140, 70));
    menuGround.addChild(sprite(imageUrls.ground, 315, 272, 140, 70));
  }

  function createHud(): void {
    hud.addChild(
      sprite(imageUrls.scoreLabel, 168, -4, 51.2, 25.6),
      sprite(imageUrls.kicksLeftLabel, 330, 0, 76.8, 19.2),
      scoreDigits,
      kicksDigits,
      powerBar,
    );
  }

  function createMenu(): void {
    menu.addChild(
      menuBaby,
      menuGround,
      menuArt,
      cursor,
      endScoreDigits,
      endHighDigits,
    );
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
      addDigit(
        container,
        digit,
        x + index * 9 * scale,
        y,
        8 * scale,
        9 * scale,
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
    leg
      .poly(
        bodyPoints(physics.shin, [
          [-0.6, -0.85],
          [0.6, -0.85],
          [0.6, 1.25],
          [-0.6, 1.25],
        ]),
      )
      .fill(0xe8bb8e);
    const [kneeX, kneeY] = bodyPoints(physics.thigh, [[0.1, -1.35]]);
    leg.circle(kneeX, kneeY, 0.6 * PIXELS_PER_METER).fill(0xe8bb8e);
    leg
      .poly(
        bodyPoints(physics.thigh, [
          [-0.75, -1.35],
          [0.75, -1.35],
          [0.75, 1.5],
          [-0.75, 1.5],
        ]),
      )
      .fill(0x4e4e4e);
    leg
      .poly(
        bodyPoints(physics.foot, [
          [-0.35, 0.25],
          [-0.35, -0.75],
          [2, -0.75],
          [0.6, 0.25],
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
    const headCenterX = -4.2;
    const headCenterY = -1.4;
    const [headX, headY] = rotated(headCenterX, headCenterY);
    babyGraphics.circle(headX, headY, 2.8).fill(baby.skinColor);
    const facePoint = (x: number, y: number): [number, number] =>
      rotated(...rotateFacePoint(x, y, headCenterX, headCenterY));
    for (const eyeX of [-5.8, -4.4]) {
      const [x, y] = facePoint(eyeX, -2.3);
      babyGraphics.circle(x, y, 0.45).fill(0x000000);
    }
    const [mouthX, mouthY] = facePoint(-5.1, -0.6);
    if (baby.screaming) babyGraphics.circle(mouthX, mouthY, 0.9).fill(0x000000);
    else {
      const [startX, startY] = facePoint(-5.7, -0.6);
      const [endX, endY] = facePoint(-4.5, -0.6);
      babyGraphics.moveTo(startX, startY).lineTo(endX, endY).stroke({
        color: 0x000000,
        width: 0.45,
      });
    }
  }

  function drawHud(state: RenderState): void {
    if (state.round.score !== lastScore) {
      const { length } = String(state.round.score);
      drawNumber(
        scoreDigits,
        state.round.score,
        193 - (length * 18 - 2) / 2,
        24,
        2,
      );
      lastScore = state.round.score;
    }
    if (state.round.kicksLeft !== lastKicks) {
      const { length } = String(state.round.kicksLeft);
      drawNumber(
        kicksDigits,
        state.round.kicksLeft,
        366 - (length * 18 - 2) / 2,
        24,
        2,
      );
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
      cursor.rect(x - 1.5, y + 1.5, 13, 5).fill(0x494700);
      cursor.rect(x, y, 13, 5).fill(0xfff600);
    }
    if (screen === 'end') {
      if (state.round.score !== lastEndScore) {
        drawNumber(endScoreDigits, state.round.score, 190, 98, 2);
        lastEndScore = state.round.score;
      }
      if (state.highScore !== lastHigh) {
        drawNumber(endHighDigits, state.highScore, 190, 120, 2);
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
      const headCenterX = -7;
      const headCenterY = -2;
      const [headX, headY] = rotated(headCenterX, headCenterY);
      menuBaby.circle(headX, headY, 4).fill(0xe8bb8e);
      const facePoint = (x: number, y: number): [number, number] =>
        rotated(...rotateFacePoint(x, y, headCenterX, headCenterY));
      for (const eyeX of [-9, -7.2]) {
        const [x, y] = facePoint(eyeX, -3);
        menuBaby.circle(x, y, 0.6).fill(0x000000);
      }
      const [mouthStartX, mouthStartY] = facePoint(-8.7, -0.7);
      const [mouthEndX, mouthEndY] = facePoint(-7.2, -0.7);
      menuBaby
        .moveTo(mouthStartX, mouthStartY)
        .lineTo(mouthEndX, mouthEndY)
        .stroke({ color: 0x000000, width: 0.5 });
    }
  }

  function render(state: RenderState): void {
    if (state.screen !== 'intro' && state.screen !== 'end') moveClouds();
    else lastCloudTime = 0;
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
