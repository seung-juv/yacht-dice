export const MAX_DICE_ROLL_COUNT = 3;
export const DICE_SIZE = 2;
export const DICE_LENGTH = 5;
export const WALL_WEIGHT = 0.5;
export const WALL_HEIGHT = 1;
export const TABLE_WIDTH = 30;
export const TABLE_HEIGHT = 30;
export const TABLE_WEIGHT = 0.1;
export const KEEP_TABLE_DICE_OFFSET = 0.3;
export const KEEP_TABLE_WIDTH =
  WALL_WEIGHT +
  (DICE_SIZE + WALL_WEIGHT + KEEP_TABLE_DICE_OFFSET * 2) * DICE_LENGTH;
export const KEEP_TABLE_HEIGHT =
  WALL_WEIGHT * 2 + DICE_SIZE + KEEP_TABLE_DICE_OFFSET * 2;
export const WALL_COLOR = '#FF0000';
