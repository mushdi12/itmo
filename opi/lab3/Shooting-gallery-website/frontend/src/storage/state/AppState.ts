import PointState from "./PoinState.ts";

export default interface AppState{
    username: string | null,
    accessToken: string | null,
    popupText: string[]
    pointsState: PointState;
}