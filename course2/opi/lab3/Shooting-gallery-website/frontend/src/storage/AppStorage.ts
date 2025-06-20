import {createStore, Reducer} from "redux";
import AppState from "./state/AppState.ts";
import Action from "../models/Action.ts";
import Point from "../models/Point.ts";
import PointState from "./state/PoinState.ts";

export const defaultPointState: PointState ={
    lastPoint: null,
    pointCount: null,
    points: null,
    currPage: null
}

export const defaultState: AppState = {
    username: null,
    accessToken: null,
    popupText: [],
    pointsState: defaultPointState
};

const reducer: Reducer<AppState, Action<string | Point | Point[] | number | PointState | AppState>> = (state: AppState = defaultState, action: Action<AppState | string | number | Point | Point[] | PointState>): AppState => {
    switch (action.type){
        case "SET_POINTS":
            return {...state, pointsState: {...state.pointsState, points: action.payload as Point[]} as PointState}
        case "SET_LAST_POINT":
            return {...state, pointsState: {...state.pointsState, lastPoint: action.payload as Point} as PointState}
        case "SET_USERNAME":
            return {...state, username: action.payload as string};
        case "SET_ACCESS_TOKEN":
            return {...state, accessToken: action.payload as string};
        case "SET_POINT_COUNT":
            return {...state, pointsState: {...state.pointsState, pointCount: action.payload as number} as PointState}
        case "SET_CURR_PAGE":
            return {...state, pointsState: {...state.pointsState, currPage: action.payload as number} as PointState}
        case "SET_POINT_STATE":
            return {...state, pointsState: action.payload as PointState};
        case "SET_APP_STATE":
            return action.payload as AppState;
        case "ADD_POPUP":
            return {...state, popupText: [...state.popupText, action.payload as string]};
        case "REMOVE_POPUP":
            return {...state, popupText: state.popupText.filter(text => text != action.payload as string)};
        case "CLEAR_POPUPS":
            return {...state, popupText: []};
        default:
            return state;
    }
}

const store = createStore(reducer);

export default store;