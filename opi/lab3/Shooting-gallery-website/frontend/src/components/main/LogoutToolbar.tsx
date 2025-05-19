import {FC} from "react";
import {useDispatch, useSelector} from "react-redux";
import {selectUsername} from "../../storage/AppStateSelectors.ts";
import AuthService from "../../services/AuthService.ts";
import ActionEnum from "../../models/enums/ActionEnum.ts";
import {defaultState} from "../../storage/AppStorage.ts";
import logoutStyles from "../../styles/LogoutToolbar.module.css";

const LogoutToolbar: FC = () => {
    const dispatcher = useDispatch();
    const username = useSelector(selectUsername);

    const logout = async (event: React.MouseEvent<HTMLButtonElement>)=> {
        event.preventDefault();
        await AuthService.logout();
        dispatcher({type: ActionEnum.SET_APP_STATE, payload: defaultState});
    };

    return <div style={{width: "80%", marginTop: "10px"}}>
    <section className={logoutStyles.horizontalContent}>
        {
            username != null &&
            <label>{username}</label>
        }
        <button className={logoutStyles.logoutButton} onClick={logout}>Logout</button>
    </section>
    </div>;
}

export default LogoutToolbar;