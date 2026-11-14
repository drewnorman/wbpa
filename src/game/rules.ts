export const TOTAL_KICKS = 10;
export const CAN_COUNT = 15;
export const CAN_SPACING = 40;
export const FIRST_CAN_X = 90;
export const CAN_Y = 3.5;
export const CAN_WIDTH = 5;
export const CAN_HEIGHT = 6;

export interface Position {
  x: number;
  y: number;
}

export interface RoundState {
  score: number;
  kicksLeft: number;
}

export const initialRound = (): RoundState => ({
  score: 0,
  kicksLeft: TOTAL_KICKS,
});

export function scoreLanding(position: Position): number {
  const insideCan = Array.from(
    { length: CAN_COUNT },
    (_, index) => FIRST_CAN_X + index * CAN_SPACING,
  ).some(
    (x) =>
      position.x > x - CAN_WIDTH / 2 &&
      position.x < x + CAN_WIDTH / 2 &&
      position.y > CAN_Y - CAN_HEIGHT / 2 &&
      position.y < CAN_Y + CAN_HEIGHT / 2,
  );

  let distance = 0;
  for (let index = 0; index < CAN_COUNT; index += 1) {
    const x = FIRST_CAN_X + index * CAN_SPACING;
    if (position.x + (insideCan ? 0.3 : 0) > x) distance += 1;
  }

  return (distance + (insideCan ? 2 : 0)) * 10;
}

export function finishKick(round: RoundState, position: Position): RoundState {
  if (round.kicksLeft <= 0) return round;
  return {
    score: round.score + scoreLanding(position),
    kicksLeft: round.kicksLeft - 1,
  };
}
