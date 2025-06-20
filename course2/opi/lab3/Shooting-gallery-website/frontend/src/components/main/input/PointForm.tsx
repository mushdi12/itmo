import {Dispatch, FC, SetStateAction, useState} from "react";
import {
    MAX_Y_VALUE,
    MIN_Y_VALUE,
    R_BUTTON_VALUES,
    X_BUTTON_VALUES,
} from "../../../utils/const/PointsConst.ts";
import {useDispatch, useSelector} from "react-redux";
import {selectAccessToken} from "../../../storage/AppStateSelectors.ts";
import PointsService from "../../../services/PointsService.ts";
import Point from "../../../models/Point.ts";
import ActionEnum from "../../../models/enums/ActionEnum.ts";
import {defaultState} from "../../../storage/AppStorage.ts";
import AuthService from "../../../services/AuthService.ts";
import {R_DOESNT_SELECT_MESSAGE, X_DOESNT_SELECT_MESSAGE, Y_INCORRECT_VALUE} from "../../../utils/const/PopupConst.ts";
import pointFormStyles from "../../../styles/PointForm.module.css";

const PointForm: FC<{r: number | undefined, setR: Dispatch<SetStateAction<number | undefined>>}> = ({r, setR}) => {
    const dispatcher = useDispatch();
    const [x, setX] = useState<number>();
    const [y, setY] = useState<number>();
    const accessToken = useSelector(selectAccessToken);

    const chooseOne = (value: number, toolbarId: string) => {
        const xBar = document.getElementById(toolbarId);
        if (xBar != null){
            const buttonList = xBar.getElementsByTagName("input");
            for (const button of buttonList) {
                if (button.value == String(value)){
                    button.style.backgroundColor = "#b3e600";
                    button.style.color = "black";
                    button.style.borderColor = "#b3e600";
                } else {
                    button.style.backgroundColor = "black";
                    button.style.color = "white";
                    button.style.borderColor = "white";
                }
            }
        }
    };

    const handleXClick = (event: React.MouseEvent<HTMLInputElement>) => {
        event.preventDefault();
        const currX: number = Number(event.currentTarget.value);
        chooseOne(currX, "x-list");
        setX(currX);
    }

    const handleRClick = (event: React.MouseEvent<HTMLInputElement>) => {
        event.preventDefault();
        const currR: number = Number(event.currentTarget.value);
        chooseOne(currR, "r-list");
        setR(currR);
    };

    const handleChangeY = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault();
        const currY: number = Number(event.target.value);
        if (currY && MIN_Y_VALUE <= currY && MAX_Y_VALUE >= currY) {
            setY(currY);
        }
    };

    const createPoint = async (event: React.MouseEvent<HTMLInputElement>) => {
        event.preventDefault();
        if (x && y && r && accessToken){
            const resp: Response = await PointsService.createPoint(x, y, r, accessToken);
            if (resp.ok){
                console.log("lastPoint", resp);
                console.log("lelle")
                const lastPoint: Point = await resp.json();
                console.log("lastPoint", lastPoint);
                if (lastPoint) {
                    dispatcher({type: ActionEnum.SET_LAST_POINT, payload: lastPoint});
                }
            } else if (resp.status == 401) {
                const resp = await AuthService.refresh();
                if (resp.username != null && resp.token != null) {
                    const newResp = await PointsService.createPoint(x, y, r, resp.token);
                    if (newResp.ok){
                        const lastPoint: Point = await newResp.json();
                        if (lastPoint){
                            dispatcher({type: ActionEnum.SET_LAST_POINT, payload: lastPoint});
                            dispatcher({type: ActionEnum.SET_USERNAME, payload: resp.username});
                            dispatcher({type: ActionEnum.SET_ACCESS_TOKEN, payload: resp.token});
                        }
                    }
                } else {
                    dispatcher({type: ActionEnum.SET_APP_STATE, payload: defaultState});
                }
            }
        } else{
            if (!x){
                dispatcher({type: ActionEnum.ADD_POPUP, payload: X_DOESNT_SELECT_MESSAGE});
            }
            if (!r){
                dispatcher({type: ActionEnum.ADD_POPUP, payload: R_DOESNT_SELECT_MESSAGE});
            }
            if (!y){
                dispatcher({type: ActionEnum.ADD_POPUP, payload: Y_INCORRECT_VALUE});
            }
        }
    }

    return (
        <form className={pointFormStyles.form}>
            <section className={pointFormStyles.label}>
                <label>X:</label>
                <div id="x-list" className={pointFormStyles.buttons}>
                    {X_BUTTON_VALUES.map((number) => (
                        <input
                            className={pointFormStyles.button}
                            type="button"
                            value={number}
                            onClick={handleXClick}
                        />
                    ))}
                </div>
            </section>
            <section className={pointFormStyles.label}>
                <label>Y:</label>
                <input
                    type="text"
                    placeholder="-3...3"
                    onChange={handleChangeY}
                    required
                    className={pointFormStyles.input}
                />
            </section>
            <section className={pointFormStyles.label}>
                <label>R:</label>
                <div id="r-list" className={pointFormStyles.buttons}>
                    {R_BUTTON_VALUES.map((number) => (
                        <input
                            className={pointFormStyles.button}
                            type="button"
                            value={number}
                            onClick={handleRClick}
                        />
                    ))}
                </div>
            </section>
            <section className={pointFormStyles.label}>
                <input
                    value="Paint!"
                    type="button"
                    onClick={createPoint}
                    className={pointFormStyles.submitButton}
                />
            </section>
        </form>
    );
}

export default PointForm;