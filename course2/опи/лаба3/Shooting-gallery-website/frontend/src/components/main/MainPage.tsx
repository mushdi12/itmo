import {FC} from "react";
import {useSelector} from "react-redux";
import {selectAccessToken, selectLastPoint, selectPoints, selectUsername} from "../../storage/AppStateSelectors.ts";
import AuthForm from "../login/AuthForm.tsx";
import InputPointsWrapper from "./input/InputPointsWrapper.tsx";
import PointsTableBody from "./table/PointsTableBody.tsx";
import PointsTableHeader from "./table/PointsTableHeader.tsx";
import PaginationFooter from "./table/PaginationFooter.tsx";
import LogoutToolbar from "./LogoutToolbar.tsx";
import Point from "../../models/Point.ts";
import mainStyles from "../../styles/MainPage.module.css";
import tableStyles from "../../styles/Table.module.css";

const MainPage: FC = () => {
    const username = useSelector(selectUsername);
    const token = useSelector(selectAccessToken);
    const lastPoint = useSelector(selectLastPoint);
    const points = useSelector(selectPoints);
    const lp: (Point | null)[] = [lastPoint];

    if (username == null || token == null){
        return <AuthForm/>;
    }

    return(
        <main className={mainStyles.main}>
            <div className={mainStyles.inputWrapperContainer}>
                <InputPointsWrapper/>
            </div>
            <div className={mainStyles.rightColumn}>
                <LogoutToolbar/>
                    <div style={{textAlign: "center", fontSize: "large", width: "80%"}}>Last point</div>
                    <section className={tableStyles.tableContainer}>
                    <table>
                        <PointsTableHeader/>
                        <PointsTableBody points={lp}/>
                    </table>
                    </section>
                    <div style={{textAlign: "center", fontSize: "large", width: "80%"}}>Points</div>
            <section className={tableStyles.tableContainer}>
                <table>
                    <PointsTableHeader/>
                    <PointsTableBody points={points}/>
                </table>
                <PaginationFooter/>
            </section>
            </div>
        </main>

    )
};

export default MainPage;