import {FC} from "react";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import Header from "./Header.tsx";
import AuthForm from "./login/AuthForm.tsx";
import MainPage from "./main/MainPage.tsx";
import RefreshTokenWrapper from "../utils/RefreshTokenWrapper.ts";
import {BASE_URL, LOGIN_PAGE} from "../utils/const/HttpConst.ts";
import ModalControl from "./popup/ModalControl.tsx";

const App: FC = () =>{
    return(
        <div style={{
            width: "100%",
            height: "100%",
            minWidth: "400px",
            minHeight: "400px"
        }}>
            <Header/>
            <ModalControl/>
            <BrowserRouter basename={BASE_URL}>
                <Routes>
                    <Route path={LOGIN_PAGE} element={RefreshTokenWrapper(<MainPage/>, <AuthForm/>)}/>
                    <Route path={"*"} element={<Navigate to={LOGIN_PAGE} replace={true}/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    )
};

export default App;