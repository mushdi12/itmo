import {FC, useEffect} from "react";
import {useDispatch, useSelector} from "react-redux";
import {
    selectAccessToken,
    selectCurrPage,
    selectLastPoint,
    selectPointCount,
} from "../../../storage/AppStateSelectors.ts";
import {PAGE_SIZE} from "../../../utils/const/PointsConst.ts";
import PointsService from "../../../services/PointsService.ts";
import ActionEnum from "../../../models/enums/ActionEnum.ts";
import AuthService from "../../../services/AuthService.ts";
import {defaultState} from "../../../storage/AppStorage.ts";
import Point from "../../../models/Point.ts";
import paginationStyles from "../../../styles/Pagination.module.css";

const PaginationFooter: FC = () => {
    const dispatcher = useDispatch();
    const currPage = useSelector(selectCurrPage);
    const pointCount = useSelector(selectPointCount);
    const lastPoint = useSelector(selectLastPoint);
    const token = useSelector(selectAccessToken);

    const loadCountPoints = async () => {
        if (token) {
            const countResp = await PointsService.getCountPoints(token);
            if (countResp.ok) {
                const countNumber: number = Number((await countResp.json()).result);
                console.log("countPoints", countNumber);
                if (countNumber) {
                    if (currPage == null && countNumber > PAGE_SIZE){
                        dispatcher({type: ActionEnum.SET_CURR_PAGE, payload: 1});
                    }
                    dispatcher({type: ActionEnum.SET_POINT_COUNT, payload: countNumber})
                }
            } else if (countResp.status == 401){
                const refreshResp = await AuthService.refresh();
                if (refreshResp.username != null && refreshResp.token != null){
                    dispatcher({type: ActionEnum.SET_USERNAME, payload: refreshResp.username});
                    dispatcher({type: ActionEnum.SET_ACCESS_TOKEN, payload: refreshResp.token})
                } else {
                    dispatcher({type: ActionEnum.SET_APP_STATE, payload: defaultState});
                }
            }
        }
    }

    useEffect(() => {
        loadCountPoints();
    }, [lastPoint, token]);

    const loadPoints = async (currPage: number) => {
        if (token && pointCount != null){
            const start: number = (currPage - 1) * PAGE_SIZE;
            const limit: number = Math.min(PAGE_SIZE, pointCount);
            const resp = await PointsService.getSlice(start, limit, token);
            if (resp.ok){
                const points: Point[] = await resp.json();
                if (points){
                    dispatcher({type: ActionEnum.SET_POINTS, payload: points});
                }
            } else if (resp.status == 401){
                const refreshResp = await AuthService.refresh();
                if (refreshResp.username != null && refreshResp.token != null){
                    dispatcher({type: ActionEnum.SET_USERNAME, payload: refreshResp.username});
                    dispatcher({type: ActionEnum.SET_ACCESS_TOKEN, payload: refreshResp.token})
                } else {
                    dispatcher({type: ActionEnum.SET_APP_STATE, payload: defaultState});
                }
            }
        }
    }

    useEffect(() => {
        if (currPage){
            loadPoints(currPage);
        } else {
            loadPoints(1);
        }
    }, [currPage]);

    if (pointCount == null || currPage == null || pointCount != null && pointCount <= PAGE_SIZE){
        return <tfoot></tfoot>;
    };

    const prevPage = async (event: React.MouseEvent<HTMLButtonElement>) =>{
        event.preventDefault();
        if (currPage && currPage > 1) {
            dispatcher({type: ActionEnum.SET_CURR_PAGE, payload: currPage - 1});
        }
    };

    const nextPage = (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();
        if (currPage && currPage*PAGE_SIZE < pointCount){
            dispatcher({type: ActionEnum.SET_CURR_PAGE, payload: currPage + 1});
        }
    };

    return <section className={paginationStyles.footer}>
        <section className={paginationStyles.buttonContainer}>
    {
        currPage > 1 && <button className={paginationStyles.pageButton} onClick={prevPage}>Prev</button>
    }
        </section>
        <section className={paginationStyles.pageLabelContainer}>
    <label>
        {currPage} of {Math.trunc((pointCount - 1) / PAGE_SIZE) + 1}
    </label>
        </section>
        <section className={paginationStyles.buttonContainer}>
    {
        currPage * PAGE_SIZE < pointCount && <button className={paginationStyles.pageButton} onClick={nextPage}>Next</button>
    }
        </section>
    </section>;
}

export default PaginationFooter;