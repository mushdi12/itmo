import Point from "../../models/Point.ts";

export default interface PointState {
    lastPoint: Point | null,
    currPage: number | null;
    pointCount: number | null,
    points: Point[] | null
};