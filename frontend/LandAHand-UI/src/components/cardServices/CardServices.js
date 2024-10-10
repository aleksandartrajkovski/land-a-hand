import React, { useState, useEffect } from "react";
import "./CardService.css";
import { Link } from "react-router-dom";
import axiosInstance from "../../services/axiosInstance";

function CardService({ service, refreshUser }) {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const pictures = service.picture;

  const nextImage = (e) => {
    e.preventDefault();
    if (pictures && currentImageIndex < pictures.length - 1) {
      setCurrentImageIndex(currentImageIndex + 1);
    }
  };

  const previousImage = (e) => {
    e.preventDefault();
    if (pictures && currentImageIndex > 0) {
      setCurrentImageIndex(currentImageIndex - 1);
    }
  };

  return (
    <div className="card-service">
      <Link to={`/details/${service.id}`}>
        <div className="image-container">
          {pictures && pictures.length > 0 ? (
            <>
              <img
                src={`data:image/png;base64,${pictures[currentImageIndex]}`}
                alt={service.serviceName}
              />
              {pictures.length > 1 && (
                <div className="image-navigation">
                  <button
                    className="previous-button"
                    onClick={previousImage}
                    disabled={currentImageIndex === 0}
                  >
                    &#8592;
                  </button>
                  <button
                    className="next-button"
                    onClick={nextImage}
                    disabled={currentImageIndex === pictures.length - 1}
                  >
                    &#8594;
                  </button>
                </div>
              )}
            </>
          ) : (
            <p>Се вчитува...</p>
          )}
        </div>
      </Link>
      <div className="card-details">
        <h3>{service.title}</h3>
        <p>
          <label><b>Опис: </b> </label>
          {service.description}
        </p>
        <p>Цена: {service.price}ден</p>
        <p className="posted-by">Креирано од: {service.userName + " " + service.userSurname} </p>
      </div>
    </div>
  );
}

function Pagination({ currentPage, totalPages, onPageChange }) {
  const pageNumbers = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <div className="pagination">
      {currentPage > 1 && (
        <button onClick={() => onPageChange(currentPage - 1)}>&#8592;</button>
      )}
      {pageNumbers.map((number) => (
        <button
          key={number}
          onClick={() => onPageChange(number)}
          className={currentPage === number ? "active" : ""}
        >
          {number}
        </button>
      ))}
      {currentPage < totalPages && (
        <button onClick={() => onPageChange(currentPage + 1)}>&#8594;</button>
      )}
    </div>
  );
}

function CardServices({
  activityTypes,
  currentPage,
  setCurrentPage,
  totalPages,
  activityTypeId,
  handleActivityTypeClick,
  refreshUser,
}) {
  const [posts, setPosts] = useState([]); // Stores task posts
  const [selectedCity, setSelectedCity] = useState(""); // Currently selected city
  const [cities, setCities] = useState([]); // Available cities

  

  // Fetch available cities from the backend
  useEffect(() => {
    const fetchCities = async () => {
      try {
        const response = await axiosInstance.get("/posts/cities");
        setCities(response.data);
      } catch (error) {
        console.error("Error fetching cities:", error);
      }
    };
    fetchCities();
  }, []);

  const refreshPosts = async (currentPage, activityTypeId, city) => {
    let url = `/posts/filter?page=${currentPage}`;
  
    // Only include activityTypeId if it's a valid number
    if (activityTypeId && !isNaN(activityTypeId)) {
      url += `&activityTypeId=${activityTypeId}`;
    }
  
    // Only include city if it's not empty
    if (city) {
      url += `&city=${city}`;
    }
  
    try {
      const response = await axiosInstance.get(url);
      setPosts(response.data);  // Assuming response contains the filtered tasks
    } catch (error) {
      console.error("Error fetching tasks:", error);
    }
  };

  // Update posts when city or other filters change
  useEffect(() => {
    refreshPosts(currentPage, activityTypeId, selectedCity);
  }, [currentPage, activityTypeId, selectedCity]);


  const handleCityClick = (city) => {
    setSelectedCity(city); // Set the selected city for filtering
  };

  return (
    <div className="App">
      <div className="filters">
        <h3>Мои задачи:</h3>
        
        {/* Activity Type Selection */}
        <p
          className={!activityTypeId ? "active" : ""}
          onClick={() => handleActivityTypeClick(null)}
        >
          Сите
        </p>
        {activityTypes.map((activityType) => (
          <p
            key={activityType.id}
            className={activityTypeId === activityType.id ? "active" : ""}
            onClick={() => handleActivityTypeClick(activityType.id)}
          >
            {activityType.type}
          </p>
        ))}

        {/* City Selection */}
        <h4>Градови:</h4>
        <p
          className={!selectedCity ? "active" : ""}
          onClick={() => handleCityClick("")}
        >
          Сите градови
        </p>
        {cities.map((city, index) => (
          <p
            key={index}
            className={selectedCity === city ? "active" : ""}
            onClick={() => handleCityClick(city)}
          >
            {city}
          </p>
        ))}
      </div>

      <div className="services-pagination-container">
        <div className="card-services">
          {posts.length > 0 ? (
            posts.map((service) => (
              <CardService key={service.id} service={service} />
            ))
          ) : (
            <h2 className="card-services-none">
              Нема активни задачи за оваа категорија.
            </h2>
          )}
        </div>
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </div>
    </div>
  );
}

export default CardServices;
