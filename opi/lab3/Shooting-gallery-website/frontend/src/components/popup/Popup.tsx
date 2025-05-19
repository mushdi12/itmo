import {FC} from "react";
import ActionEnum from "../../models/enums/ActionEnum.ts";
import {useDispatch} from "react-redux";
import popupStyles from "../../styles/Popup.module.css";

const Popup: FC<{text: string}> = ({text}) => {
    const dispatcher = useDispatch();

    const deleteMessage = (event: React.MouseEvent<HTMLButtonElement>) => {
        event.preventDefault();
        const message: string = event.currentTarget.value;
        if (message){
            dispatcher({type: ActionEnum.REMOVE_POPUP, payload: message});
        }
    };

    return (<section className={popupStyles.popupWrapper}>
            <label>{text}</label>
            <button className={popupStyles.popupWrapper} value={text} onClick={deleteMessage}>Close</button>
        </section>);
}

export default Popup;