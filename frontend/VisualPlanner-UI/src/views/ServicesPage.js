import React from "react";
import "../components/addServiceForm/addServiceForm.css";
import CardServices from "../components/cardServices/CardServices";
import Header from "../components/navigation/NavBar";

function App({
  activityTypes,
  posts,
  currentPage,
  setCurrentPage,
  totalPages,
  activityTypeId,
  refreshUser,
  refreshPosts,
  handleActivityTypeClick,
  user,
}) {
  return (
    <div className="app-container">
      <Header user={user} refreshUser={refreshUser} />
      <div className="content-container">
        <CardServices
          activityTypes={activityTypes}
          posts={posts}
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          totalPages={totalPages}
          activityTypeId={activityTypeId}
          refreshPosts={refreshPosts}
          refreshUser={refreshUser}
          handleActivityTypeClick={handleActivityTypeClick}
        />
      </div>
    </div>
  );
}

export default App;
