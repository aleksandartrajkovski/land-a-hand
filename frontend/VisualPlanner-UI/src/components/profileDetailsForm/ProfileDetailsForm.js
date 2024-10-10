import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./ProfileDetailsForm.css";
import axiosInstance from "../../services/axiosInstance";
import { useNavigate } from "react-router-dom";
import getUserPosterRequests from "../../services/userService/getUserTaskRequests";
import getUserRequesterRequests from "../../services/userService/getUserRequesterRequests";
import acceptRequest from "../../services/requestService/acceptRequest";
import declineRequest from "../../services/requestService/declineRequest";
import { toast } from "react-toastify";
import EditProfileForm from "../editProfileForm/EditProfileForm";

const ProfileDetailsForm = ({ user, userPosts, refreshUserPosts }) => {
  const navigate = useNavigate();
  const [postRequestsPoster, setPostRequestsPoster] = useState([]);
  const [answeredRequestsPoster, setAnsweredRequestsPoster] = useState([]); // Store answered requests
  const [editMode, setEditMode] = useState(false);
  const [currentImageIndexes, setCurrentImageIndexes] = useState({});

  useEffect(() => {
    const initialIndexes = {};
    userPosts.forEach((post) => {
      initialIndexes[post.id] = 0;
    });
    setCurrentImageIndexes(initialIndexes);
  }, [userPosts]);

  const getStatusLabel = (status) => {
    switch (status) {
      case "PENDING":
        return "На чекање"; 
      case "ACCEPTED":
        return "Одобрено"; 
      case "DECLINED":
        return "Одбиено"; 
      default:
        return "Непознат статус"; 
    }
  };

  const handleNextImage = (postId, picturesLength, event) => {
    event.stopPropagation();
    setCurrentImageIndexes((prevIndexes) => ({
      ...prevIndexes,
      [postId]: (prevIndexes[postId] + 1) % picturesLength, 
    }));
  };

  const handlePreviousImage = (postId, picturesLength, event) => {
    event.stopPropagation(); 
    setCurrentImageIndexes((prevIndexes) => ({
      ...prevIndexes,
      [postId]: (prevIndexes[postId] - 1 + picturesLength) % picturesLength, 
    }));
  };

  const toggleEditMode = () => {
    setEditMode(!editMode);
  };

  const fetchPostRequestsPoster = async (userId) => {
    try {
      const response = await getUserPosterRequests(userId);
      
      // Separate PENDING requests and ANSWERED (ACCEPTED/DECLINED) requests
      const pendingRequests = response.filter(request => request.status === 'PENDING');
      const answeredRequests = response.filter(request => request.status === 'ACCEPTED' || request.status === 'DECLINED');
      
      setPostRequestsPoster(pendingRequests); // Only PENDING requests go here
      setAnsweredRequestsPoster(answeredRequests); // Answered requests go here
      
    } catch (error) {
      console.error("Error fetching post requests:", error);
    }
  };


  useEffect(() => {
    if (user) {
      fetchPostRequestsPoster(user.id);
      console.log("User:",user);
    }
  }, [user]);

  const handleAccept = async (requestId) => {
    try {
      await acceptRequest(requestId);
  
      // Move the request from pending to answered
      const acceptedRequest = postRequestsPoster.find((request) => request.requestId === requestId);
  
      setPostRequestsPoster((prevRequests) =>
        prevRequests.filter((request) => request.requestId !== requestId) // Remove from unanswered requests
      );
  
      setAnsweredRequestsPoster((prevRequests) => [
        ...prevRequests,
        { ...acceptedRequest, status: "ACCEPTED" } // Move to answered requests with ACCEPTED status
      ]);
  
      toast.success("Успешно одобривте барање.");
    } catch (error) {
      console.error("Error accepting request:", error.message);
    }
  };
  
  const handleDecline = async (requestId) => {
    try {
      await declineRequest(requestId);
  
      // Move the request from pending to answered
      const declinedRequest = postRequestsPoster.find((request) => request.requestId === requestId);
  
      setPostRequestsPoster((prevRequests) =>
        prevRequests.filter((request) => request.requestId !== requestId) // Remove from unanswered requests
      );
  
      setAnsweredRequestsPoster((prevRequests) => [
        ...prevRequests,
        { ...declinedRequest, status: "DECLINED" } // Move to answered requests with DECLINED status
      ]);
  
      toast.success("Успешно одбивте барање.");
    } catch (error) {
      console.error("Error declining request:", error.message);
    }
  };

  const handleLogout = async () => {
    localStorage.removeItem("userData");
    try {
      await axiosInstance.post("/users/logout");
      console.log("Logout successful");
      navigate("/");
      window.location.reload();
    } catch (error) {
      console.error("Logout error:", error.message);
    }
  };

  const handlePostDelete = async (postId) => {
    try {
      await axiosInstance.delete(`/posts/${postId}`);
      refreshUserPosts(user.id);
      toast.success("Успешно избришана задача.");
    } catch (error) {
      console.error("Delete error:", error.message);
    }
  };

  const handleEdit = (post) => {
    navigate("/addService", { state: { post } });
  };

  if (!user) {
    return (
      <div className="profile-details-container">
        <div className="profile-header">
          Сакаш да си го видиш профилот?
          <Link to="/login" className="button">
            Логирај се.
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-details-container">
      <div className="details-flex">
        {editMode ? (
          <div>
            <EditProfileForm user={user} onCancel={toggleEditMode} />
            <button className="button edit-information" onClick={toggleEditMode}>
              &larr; Назад кон профилот
            </button>
          </div>
        ) : (
          <div className="profile-info">
            <h2>Информации за профилот</h2>
            <div className="profile-header">
              {user && user.picture ? (
                <img
                  src={`data:image/png;base64,${user.picture}`}
                  alt={user.serviceName}
                />
              ) : (
                <p>Немате прикачено слика</p>
              )}
            </div>

            {user && (
              <>
                <p>
                  <strong>Име и презиме:</strong> {`${user.name} ${user.surname}`}
                </p>
                <p>
                  <strong>Телефонски број:</strong> {user.phone}
                </p>
                <p>
                  <strong>Е-пошта:</strong> {user.email}
                </p>
                <p>
                  <strong>Кратка биографија:</strong> {user.bio}
                </p>
                <p>
                  <strong>Адреса на живеење:</strong> {user.location}
                </p>
              </>
            )}
            <br />
            <br />
            <button className="button edit-information" onClick={toggleEditMode}>
              Изменете ги податоците
            </button>
            <button className="button logout" onClick={handleLogout}>
              Одлогирај се
            </button>
            <br />
          </div>
        )}

        <div className="services-details">
          <h2>Моментално креирани задачи</h2>
          <div className="services-list">
            {userPosts.length === 0 ? (
              <p>Сеуште немате креирано задача. Креирајте ја тука!</p>
            ) : (
              userPosts.map((service) => {
                const pictures = service.picture;
                const currentIndex = currentImageIndexes[service.id];

                return (
                  <div key={service.id} className="service-item">
                    <div className="image-container">
                      {pictures && pictures.length > 0 ? (
                        <>
                          <img
                            src={`data:image/jpeg;base64,${pictures[currentIndex]}`}
                            alt="Service Visual"
                            className="service-image"
                          />
                          <div className="image-navigation">
                            <span
                              className="arrow-left"
                              onClick={(e) => handlePreviousImage(service.id, pictures.length, e)}
                            >
                              &#8592;
                            </span>
                            <span
                              className="arrow-right"
                              onClick={(e) => handleNextImage(service.id, pictures.length, e)}
                            >
                              &#8594;
                            </span>
                          </div>
                        </>
                      ) : (
                        <p>Се вчитува...</p>
                      )}
                    </div>
                    <h7>
                      <Link to={`/details/${service.id}`}>
                        {service.activityTypeName}
                      </Link>
                    </h7>
                    <p>{service.description}</p>
                    <button className="button edit" onClick={() => handleEdit(service)}>
                      Измени
                    </button>
                    <button className="button delete" onClick={() => handlePostDelete(service.id)}>
                      Избриши
                    </button>
                  </div>
                );
              })
            )}
          </div>
          <Link to="/addService" className="button add-service">
            Додади нова задача +
          </Link>
        </div>

        <div className="requests-section">
          <h2>Непрегледани задачи</h2>
          <table className="requests-table">
            <thead>
              <tr>
                <th>Креирано барање од</th>
                <th>Е-пошта на барачот</th>
                <th>Кратка биографија на корисникот</th>
                <th>Задача</th>
                <th>Време</th>
                <th>Акции</th>
              </tr>
            </thead>
            <tbody>
              {postRequestsPoster.length === 0 ? (
                <tr>
                  <td colSpan="5">Моментално нема непрегледани задачи</td>
                </tr>
              ) : (
                postRequestsPoster.map((request) => (
                  <tr key={request.id}>
                    <td>{request.userRequesterName}</td>
                    <td>{request.userRequesterEmail}</td>
                    <td>{request.userBio}</td>
                    <td>
                      <Link to={`/details/${request.postId}`}>
                        {request.postName}
                      </Link>
                    </td>
                    <td>{request.availabilityTime}</td>
                    <td>
                      <button
                        className="button accept"
                        onClick={() => handleAccept(request.requestId)}
                      >
                        Одобри
                      </button>
                      <button
                        className="button decline"
                        onClick={() => handleDecline(request.requestId)}
                      >
                        Одбиј
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="requests-section">
  <h2>Прегледани задачи</h2>
  <table className="requests-table">
    <thead>
      <tr>
        <th>Пратено од</th>
        <th>Задача</th>
        <th>Кратка биографија на корисникот</th>
        <th>Статус</th>
      </tr>
    </thead>
    <tbody>
      {answeredRequestsPoster.length === 0 ? (
        <tr>
          <td colSpan="4">Немате одговорени задачи.</td>
        </tr>
      ) : (
        answeredRequestsPoster.map((request) => (
          <tr key={request.id}>
            <td>{request.userRequesterEmail}</td>
            <td>
              <Link to={`/details/${request.postId}`}>
                {request.postName}
              </Link>
            </td>
            <td>{request.userBio}</td>
            <td className={`status-${request.status.toLowerCase()}`}>
              {getStatusLabel(request.status)} {/* Display the correct status */}
            </td>
          </tr>
        ))
      )}
    </tbody>
  </table>
</div>

      </div>
    </div>
  );
};

export default ProfileDetailsForm;
