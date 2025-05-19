import {ReactNode} from "react";
import {useDispatch, useSelector} from "react-redux";
import {selectAccessToken, selectUsername} from "../storage/AppStateSelectors.ts";
import AuthService from "../services/AuthService.ts";
import ActionEnum from "../models/enums/ActionEnum.ts";
import {defaultPointState} from "../storage/AppStorage.ts";

const RefreshTokenWrapper = (redirect: ReactNode, component: ReactNode): ReactNode => {
    const dispatcher = useDispatch();
    const usernameSelector = useSelector(selectUsername);
    const tokenSelector = useSelector(selectAccessToken);
    if (tokenSelector != null && usernameSelector != null) {
        return redirect;
    } else {
        AuthService.refresh().then((response) => {
            if (response.token != null && response.username != null) {
                dispatcher({type: ActionEnum.SET_ACCESS_TOKEN, payload: response.token});
                dispatcher({type: ActionEnum.SET_USERNAME, payload: response.username});
                dispatcher({type: ActionEnum.SET_POINT_STATE, payload: defaultPointState})
            }
        })
    }

    return component;
};

export default RefreshTokenWrapper;