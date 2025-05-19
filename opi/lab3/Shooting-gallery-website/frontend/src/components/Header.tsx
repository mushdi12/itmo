import {FC} from "react";
import headerStyles from "../styles/Header.module.css";

const Header: FC = () => {
    return (
        <header className={headerStyles.header}>
            <section className={headerStyles.verticalSpaceContainer}>
            <div className="header-text">Bragin Roman</div>
            <div className="header-text">group: P3216</div>
            <div className="header-text">isu: 408319</div>
            </section>
        </header>
    );
};

export default Header;