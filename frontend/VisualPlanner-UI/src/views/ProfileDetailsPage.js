import React from "react";
import Header from "../components/navigation/NavBar"
import ProfileDetailsForm from "../components/profileDetailsForm/ProfileDetailsForm";
import "../components/registrationForm/RegisterForm.css";

const ProfileDetailsPage = ({ user, userPosts, refreshUserPosts, refreshUser, userId }) => {
  return (
    <div>
      <Header user={user} refreshUser={refreshUser} />
      <div className="profile-details-container">
        <ProfileDetailsForm
          user={user}
          userPosts={userPosts}
          refreshUserPosts={refreshUserPosts}
          refreshUser={refreshUser}
          userId={userId}
        />
      </div>
      {/*<Footer />*/}
    </div>
  );
};

export default ProfileDetailsPage;
