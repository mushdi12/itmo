import {FC} from "react";
import {useSelector} from "react-redux";
import {selectAccessToken, selectLastPoint, selectPoints} from "../../../storage/AppStateSelectors.ts";
import {Arc, Circle, Group, Label, Layer, Line, Rect, Stage, Text} from "react-konva";
import {
    CANVAS_FIGURE_COLOR, CANVAS_UNIT_SEGMENT, CANVAS_X_CENTER,
    CANVAS_X_SIZE, CANVAS_Y_CENTER, CANVAS_Y_SIZE, HIT_COLOR, R_TAGS_COEF, SLIP_COLOR
} from "../../../utils/const/PointsConst.ts";
import {KonvaEventObject} from "konva/lib/Node";
import PointsService from "../../../services/PointsService.ts";
import Point from "../../../models/Point.ts";
import ActionEnum from "../../../models/enums/ActionEnum.ts";
import AuthService from "../../../services/AuthService.ts";
import {defaultState} from "../../../storage/AppStorage.ts";
import {useDispatch} from "react-redux";
import {R_DOESNT_SELECT_MESSAGE, SERVER_ERROR_MESSAGE} from "../../../utils/const/PopupConst.ts";

const Graphic: FC<{r: number | undefined}> = ({r})=> {
    const dispatcher = useDispatch();
    const points = useSelector(selectPoints);
    const lastPoint = useSelector(selectLastPoint);
    const accessToken = useSelector(selectAccessToken);

    const clickOnGraphic = async (event: KonvaEventObject<MouseEvent>) => {
        event.evt.preventDefault();
        if (r != undefined && accessToken != null) {
            const currX = (event.evt.offsetX - CANVAS_X_CENTER) / CANVAS_UNIT_SEGMENT * r;
            const currY = (CANVAS_Y_CENTER - event.evt.offsetY) / CANVAS_UNIT_SEGMENT * r;
            const resp = await PointsService.createPoint(currX, currY, r, accessToken);
            if (resp.ok){
                const lastPoint: Point = await resp.json();
                if (lastPoint) {
                    dispatcher({type: ActionEnum.SET_LAST_POINT, payload: lastPoint});
                }
            } else if (resp.status == 401) {
                const resp = await AuthService.refresh();
                if (resp.username != null && resp.token != null) {
                    const newResp = await PointsService.createPoint(currX, currY, r, resp.token);
                    if (newResp.ok){
                        const lastPoint: Point = await newResp.json();
                        console.log("lastPoint", lastPoint);
                        if (lastPoint){
                            dispatcher({type: ActionEnum.SET_LAST_POINT, payload: lastPoint});
                            dispatcher({type: ActionEnum.SET_USERNAME, payload: resp.username});
                            dispatcher({type: ActionEnum.SET_ACCESS_TOKEN, payload: resp.token});
                        }
                    } else {
                        dispatcher({type: ActionEnum.ADD_POPUP, payload: SERVER_ERROR_MESSAGE});
                    }
                } else {
                    dispatcher({type: ActionEnum.SET_APP_STATE, payload: defaultState});
                }
            }
        } else {
            dispatcher({type: ActionEnum.ADD_POPUP, payload: R_DOESNT_SELECT_MESSAGE});
        }
    };

    return (<section style={{display: "flex", justifyContent: "center", alignItems: "center"}}>
        <Stage width={CANVAS_X_SIZE} height={CANVAS_Y_SIZE} onClick={clickOnGraphic}>
        <Layer>
            <Rect opacity={0.5} fill={CANVAS_FIGURE_COLOR} x={CANVAS_X_CENTER - CANVAS_UNIT_SEGMENT/2} y={CANVAS_Y_CENTER - CANVAS_UNIT_SEGMENT} width={CANVAS_UNIT_SEGMENT / 2} height={CANVAS_UNIT_SEGMENT}/>
            <Arc opacity={0.5} fill={CANVAS_FIGURE_COLOR} x={CANVAS_X_CENTER} y={CANVAS_Y_CENTER} angle={90} innerRadius={0.0000000001} outerRadius={CANVAS_UNIT_SEGMENT / 2} rotation={90} clockwise={false}/>
            <Line opacity={0.5} fill={CANVAS_FIGURE_COLOR} closed={true} points={[CANVAS_X_CENTER, CANVAS_Y_CENTER, CANVAS_X_CENTER + CANVAS_UNIT_SEGMENT, CANVAS_Y_CENTER, CANVAS_X_CENTER, CANVAS_Y_CENTER + CANVAS_UNIT_SEGMENT]}/>
            <Line opacity={0.5} strokeWidth={0.5} stroke="white" points={[0, CANVAS_Y_CENTER, CANVAS_X_SIZE, CANVAS_Y_CENTER]}/>
            <Line opacity={0.5} strokeWidth={0.5} stroke="white" points={[CANVAS_X_CENTER, 0, CANVAS_X_CENTER, CANVAS_Y_SIZE]}/>
            {
                R_TAGS_COEF.map(coef => <Label x={CANVAS_X_CENTER + 7} y={CANVAS_Y_CENTER - coef*CANVAS_UNIT_SEGMENT}>
                        <Text text={r != undefined ? String(coef*r) : ""} fill="white" fontSize={14}/>
                    </Label>
                )
            }
            {
                R_TAGS_COEF.map(coef => <Label x={CANVAS_X_CENTER + coef*CANVAS_UNIT_SEGMENT} y={CANVAS_Y_CENTER + 7}>
                        <Text text={r != undefined ? String(coef*r) : ""} fill="white" fontSize={14}/>
                    </Label>
                )
            }
            <Group>
                {
                    lastPoint != null && r != undefined &&
                    <Circle fill={lastPoint.hitting ? HIT_COLOR : SLIP_COLOR} radius={8} x={lastPoint.x*CANVAS_UNIT_SEGMENT/r + CANVAS_X_CENTER} y={CANVAS_Y_CENTER - lastPoint.y*CANVAS_UNIT_SEGMENT/r}/>
                }
                {
                    points != null && r != undefined && points.map(point => <Circle fill={point.hitting ? HIT_COLOR : SLIP_COLOR} radius={8} x={point.x*CANVAS_UNIT_SEGMENT/r + CANVAS_X_CENTER} y={CANVAS_Y_CENTER - point.y*CANVAS_UNIT_SEGMENT/r}/>)
                }
            </Group>
        </Layer>
    </Stage>
    </section>);
}

export default Graphic;