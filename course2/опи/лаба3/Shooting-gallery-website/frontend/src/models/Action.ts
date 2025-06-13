import {Action as ReduxAction} from "redux";

export default interface Action<T> extends ReduxAction{
    payload: T
}