import {FC, useState} from "react";
import PointForm from "./PointForm.tsx";
import Graphic from "./Graphic.tsx";
import styles from "../../../styles/InputWrapper.module.css"
import {CANVAS_X_SIZE, CANVAS_Y_SIZE} from "../../../utils/const/PointsConst.ts";

const InputPointsWrapper: FC = () => {
    const [currR, setR] = useState<number>();
    return (<section className={styles.verticalSection}>
        <PointForm r={currR} setR={setR}/>
        <div style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
            width: CANVAS_X_SIZE + 2 + "px",
            height: CANVAS_Y_SIZE + 2 + "px",
            border: "solid white 2px",
            margin: "20px auto",
            backgroundColor: "black"
        }}>
        <Graphic r={currR}/>
        </div>
    </section>);
}

export default InputPointsWrapper;