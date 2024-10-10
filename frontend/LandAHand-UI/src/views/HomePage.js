import React from "react";
import Header from "../components/header/Header";
import CardCategory from "../components/cardCategories/CardCategory";
import "../components/cardCategories/CardCategory.css";
import "../components/cardCategories/UsefulPetKnowledge.css";
import UsefulPetKnowledge from "../components/cardCategories/UsefulPetKnowledge";
import PetData from "../components/cardCategories/PetData";
import { Link } from "react-router-dom";

const HomePage = ({ onClick, user, refreshUser }) => {
  return (
    <div>
      <Header user={user} refreshUser={refreshUser} />
      <br />
      <br />
      <div className="custom-container">
        {/* First Row */}
        <div className="custom-row">
          <div className="custom-col">
            <CardCategory
              category="Лесен пристап"
              imageUrl={require("../assets/Plantasks.png")}
              onClick={onClick}
              value="1"
            />
          </div>
          <div className="custom-col">
            <CardCategory
              category="Брз начин на контакт"
              imageUrl={require("../assets/time.png")}
              onClick={onClick}
              value="2"
            />
          </div>
          <div className="custom-col">
            <CardCategory
              category="Лесно менаџирање на задача"
              imageUrl={require("../assets/plan.png")}
              onClick={onClick}
              value="3"
            />
          </div>
        </div>

        
      </div>
      <div className="view-all-button-container">
        <Link to="/services" className="view-all-button">
           Огласи
        </Link>
      </div>{" "}
      <div className="post-container">
        <UsefulPetKnowledge petData={PetData} />
      </div>
    </div>
  );
};

export default HomePage;
