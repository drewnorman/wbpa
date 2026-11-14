export const imageUrls = {
  intro: new URL('../assets/screens/intro-screen.png', import.meta.url).href,
  title: new URL('../assets/screens/title-screen.png', import.meta.url).href,
  instructions: new URL(
    '../assets/screens/instructions-screen.png',
    import.meta.url,
  ).href,
  credits: new URL('../assets/screens/credits-screen.png', import.meta.url)
    .href,
  end: new URL('../assets/screens/end-screen.png', import.meta.url).href,
  endNewHigh: new URL(
    '../assets/screens/end-screen-new-high.png',
    import.meta.url,
  ).href,
  can: new URL('../assets/sprites/can.png', import.meta.url).href,
  ground: new URL('../assets/sprites/ground.png', import.meta.url).href,
  widener: new URL('../assets/sprites/widener.png', import.meta.url).href,
  cloud1: new URL('../assets/sprites/cloud1.png', import.meta.url).href,
  cloud2: new URL('../assets/sprites/cloud2.png', import.meta.url).href,
  cloud3: new URL('../assets/sprites/cloud3.png', import.meta.url).href,
  cloud4: new URL('../assets/sprites/cloud4.png', import.meta.url).href,
  score: new URL('../assets/fonts/score.png', import.meta.url).href,
  kicksLeft: new URL('../assets/fonts/kicks-left.png', import.meta.url).href,
  digits: [
    new URL('../assets/fonts/zero.png', import.meta.url).href,
    new URL('../assets/fonts/one.png', import.meta.url).href,
    new URL('../assets/fonts/two.png', import.meta.url).href,
    new URL('../assets/fonts/three.png', import.meta.url).href,
    new URL('../assets/fonts/four.png', import.meta.url).href,
    new URL('../assets/fonts/five.png', import.meta.url).href,
    new URL('../assets/fonts/six.png', import.meta.url).href,
    new URL('../assets/fonts/seven.png', import.meta.url).href,
    new URL('../assets/fonts/eight.png', import.meta.url).href,
    new URL('../assets/fonts/nine.png', import.meta.url).href,
  ],
} as const;

export const musicUrls = {
  menu: new URL('../assets/music/menuTheme.mp3', import.meta.url).href,
  outdoor: new URL('../assets/music/outdoorAmbient.mp3', import.meta.url).href,
} as const;

export const soundUrls = {
  'can-hit': new URL('../assets/sound/can-hit.mp3', import.meta.url).href,
  thud: new URL('../assets/sound/thud.mp3', import.meta.url).href,
  'grass-rustle': new URL('../assets/sound/grass-rustle.mp3', import.meta.url)
    .href,
  cries: [
    new URL('../assets/sound/baby-crying.mp3', import.meta.url).href,
    new URL('../assets/sound/baby-crying-2.mp3', import.meta.url).href,
    new URL('../assets/sound/baby-crying-3.mp3', import.meta.url).href,
  ],
} as const;
