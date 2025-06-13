import {FC} from "react";
import {useSelector} from "react-redux";
import {selectPopupMessage} from "../../storage/AppStateSelectors.ts";
import Popup from "./Popup.tsx";
import popupStyles from "../../styles/Popup.module.css";

const ModalControl: FC = () => {
    const popupMessages = useSelector(selectPopupMessage);

    return (
        <div className={popupStyles.modalControlContainer}>
            {
                popupMessages.length > 0 && popupMessages.map(text => <Popup text={text}/>)
            }
        </div>
    );
};

export default ModalControl;