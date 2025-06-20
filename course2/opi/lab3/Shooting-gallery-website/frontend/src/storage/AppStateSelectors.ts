import AppState from "./state/AppState.ts";
import Point from "../models/Point.ts";

export const selectUsername = (state: AppState): string | null => {
    return state.username;
};

export const selectAccessToken = (state: AppState): string | null => {
    return state.accessToken;
};

export const selectLastPoint = (state: AppState): Point | null => {
    return state.pointsState.lastPoint;
};

export const selectPoints = (state: AppState): Point[] | null => {
    return state.pointsState.points;
};

export const selectState = (state: AppState): AppState => {
    return state;
};

export const selectPointCount = (state: AppState): number | null => {
    return state.pointsState.pointCount;
};

export const selectCurrPage = (state: AppState): number | null => {
    return state.pointsState.currPage;
};

export const selectPopupMessage = (state: AppState): string[] => {
    return state.popupText;
}