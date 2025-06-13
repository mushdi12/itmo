import {FC, useState} from "react";
import {getRegEnumValue, RegEnum} from "../../models/enums/RegisterEnum.ts";
import FormState from "../../models/FormState.ts";
import AuthService from "../../services/AuthService.ts";
import {useDispatch} from "react-redux";
import ActionEnum from "../../models/enums/ActionEnum.ts";
import {defaultPointState} from "../../storage/AppStorage.ts";
import {
    INCORRECT_LOGIN_MESSAGE,
    NOT_REPEATABLE_PASSWORD_MESSAGE,
    USER_ALREADY_EXISTS_MESSAGE,
    USER_DOESNT_EXIST_MESSAGE
} from "../../utils/const/PopupConst.ts";
import formStyles from "../../styles/Form.module.css";

const AuthForm: FC = () => {
    const dispatcher = useDispatch();
    const [formState, setFormState] = useState<FormState>({username: '', password: ''});
    const [actionType, setActionType] = useState<RegEnum>(RegEnum.NONE);
    const [repeatPassword, setRepPass] = useState<string>();

    const chooseActionType = (action: RegEnum) => {
        const sign = document.getElementById("sign");
        const reg = document.getElementById("reg");
        if (sign != null && reg != null){
            if (action == RegEnum.REG){
                reg.style.color = "black";
                reg.style.backgroundColor = "#b3e600";
                reg.style.borderColor = "#b3e600";
                sign.style.color = "black";
                sign.style.backgroundColor = "white";
                sign.style.borderColor = "white";
            } else if (action == RegEnum.SIGN){
                sign.style.color = "black";
                sign.style.backgroundColor = "#b3e600";
                sign.style.borderColor = "#b3e600";
                reg.style.color = "black";
                reg.style.backgroundColor = "white";
                reg.style.borderColor = "white";
            }
        }
    }

    const submit = async (event: React.MouseEvent<HTMLInputElement>) => {
        event.preventDefault();
        switch (actionType){
            case RegEnum.SIGN: {
                const token = await AuthService.signIn(formState);
                if (token) {
                    if (token != "") {
                        dispatcher({type: ActionEnum.SET_USERNAME, payload: formState.username});
                        dispatcher({type: ActionEnum.SET_ACCESS_TOKEN, payload: token});
                        dispatcher({type: ActionEnum.SET_POINT_STATE, payload: defaultPointState});
                        dispatcher({type: ActionEnum.CLEAR_POPUPS});
                    } else {
                        dispatcher({type: ActionEnum.ADD_POPUP, payload: USER_DOESNT_EXIST_MESSAGE});
                    }
                } else {
                    dispatcher({type: ActionEnum.ADD_POPUP, payload: INCORRECT_LOGIN_MESSAGE});
                }
                break;
            }
            case RegEnum.REG: {
                if (checkRepeat()) {
                    const token = await AuthService.register(formState);
                    console.log(token);
                    if (token == null){
                        dispatcher({type: ActionEnum.ADD_POPUP, payload: INCORRECT_LOGIN_MESSAGE});
                    } else if (token == ""){
                        dispatcher({type: ActionEnum.ADD_POPUP, payload: USER_ALREADY_EXISTS_MESSAGE});
                    } else {
                        dispatcher({type: ActionEnum.SET_USERNAME, payload: formState.username});
                        dispatcher({type: ActionEnum.SET_ACCESS_TOKEN, payload: token});
                        dispatcher({type: ActionEnum.SET_POINT_STATE, payload: defaultPointState});
                        dispatcher({type: ActionEnum.CLEAR_POPUPS});
                    }
                } else {
                    dispatcher({type: ActionEnum.ADD_POPUP, payload: NOT_REPEATABLE_PASSWORD_MESSAGE});
                }
                break;
            }
        }
    }

    const checkRepeat = (): boolean => {
       return repeatPassword == formState.password;
    };

    const handleChangeUsername = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault();
        const value: string = event.target.value.trim();
        setFormState({...formState, username: value})
    }

    const handleChangePassword = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault();
        const value: string = event.target.value.trim();
        setFormState({...formState, password: value})
    }

    const handleChangeRepPassword = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault();
        const value: string = event.target.value.trim();
        setRepPass(value);
    };

    const handleChangeActions = (event: React.MouseEvent<HTMLInputElement>) => {
        event.preventDefault();
        const value: RegEnum = getRegEnumValue(event.currentTarget.value);
        chooseActionType(value);
        setActionType(value);
    }

    return (
        <form className={formStyles.form}>
            <section className={formStyles.verticalSpaceContainer}>
                <section className={formStyles.horizontalSpaceContainer}>
                    <input
                        id="sign"
                        className={formStyles.regButton}
                        type="button"
                        value="Sign in"
                        onClick={handleChangeActions}
                    />
                    <input
                        id="reg"
                        className={formStyles.regButton}
                        type="button"
                        value="Register"
                        onClick={handleChangeActions}
                    />
                </section>

                <section className={formStyles.verticalLeftContainer}>
                    <div>Username:</div>
                    <input
                        className={formStyles.inputText}
                        id="username"
                        placeholder="Username"
                        type="text"
                        onChange={handleChangeUsername}
                        required
                    />
                </section>

                <section className={formStyles.verticalLeftContainer}>
                    <div>Password:</div>
                    <input
                        className={formStyles.inputText}
                        id="password"
                        placeholder="Password"
                        type="password"
                        onChange={handleChangePassword}
                        required
                    />
                </section>

                {actionType === RegEnum.REG && (
                    <section className={formStyles.verticalLeftContainer}>
                        <div>Repeat password:</div>
                        <input
                            className={formStyles.inputText}
                            id="repeat-password"
                            placeholder="Repeat password"
                            type="password"
                            value={repeatPassword}
                            onChange={handleChangeRepPassword}
                            required
                        />
                    </section>
                )}

                {actionType !== RegEnum.NONE && (
                    <section className={formStyles.verticalSpaceContainer}>
                        <input
                            className={formStyles.regButton}
                            type="submit"
                            value="Submit"
                            onClick={submit}
                        />
                    </section>
                )}
            </section>
        </form>
    );
};

export default AuthForm