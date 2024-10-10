import React from "react";
import "./Header.css";
import macedonia from "../../assets/macedonia.jpg";
import Navbar from "../navigation/NavBar";

const Header = ({ user, refreshUser }) => {
  return (
    <div>
      {/* Header Section */}
      <div className="header-container">
        <div className="navbar-wrapper">
          <Navbar user={user} refreshUser={refreshUser} />
        </div>

        <div className="header-page">
          <h1 className="title">Вашиот мајстор е на дофат!</h1>
          <p className="description">
          Нашиот систем ви нуди лесен и брз начин за пронаоѓање на професионални мајстори за сите ваши домашни потреби. Без разлика дали ви е потребен водоинсталатер, електричар или домар, нашата платформа ви овозможува директен контакт со потребните лица.
          Нашата мисија е да го направиме секојдневното одржување на домот едноставно, ефикасно и достапно за секого.
          </p>
          <button className="explore-button">Истражи повеќе</button>
        </div>
      </div>

      {/* Below Header Section */}
      <div className="logo-text-section">
        <div className="left-logo">
          <img src={macedonia} alt="Macedonia Logo" className="macedonia-logo" />
        </div>
        <div className="right-text">
          <p>
            Системот е наменет за огласи во областа на Македонија.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Header;
