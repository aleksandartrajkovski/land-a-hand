import HeaderSmall from "../components/navigation/NavBar";
import React from "react";
import ServiceDetails from "../components/cardServices/ServiceDetails";

function App({ user , refreshUser}) {
  return (
    <div className="app-container">
      <HeaderSmall user={user} refreshUser={refreshUser} />
      <div className="content-container">
        <ServiceDetails user={user} refreshUser={refreshUser}/>
      </div>
    </div>
  );
}

export default App;
