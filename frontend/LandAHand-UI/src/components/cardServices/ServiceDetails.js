import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import "./ServiceDetails.css";
import CommentsSection from "./CommentsSection";
import getPostById from "../../services/postsService/getTaskById";
import axiosInstance from "../../services/axiosInstance";
import { toast } from "react-toastify";

// Function to map importance level to Macedonian terms
const getImportanceLevelTranslation = (importanceLevel) => {
  switch (importanceLevel) {
    case "LOW_PRIORITY":
      return "Не е итно";
    case "IMPORTANT":
      return "Итно";
    case "CRITICAL":
      return "Многу итно";
    default:
      return importanceLevel; // Return the original value if no match
  }
};

function ServiceDetailsPage({ user }) {
  const { id: postId } = useParams();
  const [service, setService] = useState(null);
  const [selectedAvailability, setSelectedAvailability] = useState(null);
  const [currentImageIndex, setCurrentImageIndex] = useState(0); // State to track the current image index

  const refreshService = async (id) => {
    try {
      const response = await getPostById(id); // Fetch data but don't need to assign to `response`
      setService(response);
    } catch (error) {
      console.error("Error fetching service:", error);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await getPostById(postId); // `response` is not needed, so no need to assign it
        setService(response);
      } catch (error) {
        console.error("Error fetching service:", error);
      }
    };

    if (postId) {
      fetchData();
    }
  }, [postId]);

  useEffect(() => {
    refreshService(postId);
  }, [postId]);

  if (!service) {
    return <div>Се вчитува...</div>;
  }

  const handleApply = async () => {
    if (selectedAvailability) {
      try {
        const requestData = {
          status: "PENDING",
        };

        const response = await axiosInstance.post(
          "/requests/create",
          requestData,
          {
            params: {
              availabilityId: selectedAvailability.id,
              userRequesterId: user.id,
            },
          }
        );

        toast.success("Успешно креирано барање!");
      } catch (error) {
        console.error("Error creating request:", error.message);
        alert("Грешка при креирање на барање. Обидете се повторно.");
      }
    } else {
      toast.error("Изберете време.");
    }
  };

  const excludedProperties = [
    "id",
    "reviews",
    "userId",
    "activityTypeId",
    "availabilities",
    "picture",
    "title",
    "peopleGroup"
  ];

  const propertyChanges = {
    description: "Опис на задача",
    importanceLevel: "Степен на важност",
    price: "Цена",
    activityType: "Тип на задача",
    city:"Град",
    user: "Оваа задача е креирана од :",
  };

  const nextImage = () => {
    if (service.picture && currentImageIndex < service.picture.length - 1) {
      setCurrentImageIndex(currentImageIndex + 1);
    }
  };

  const previousImage = () => {
    if (service.picture && currentImageIndex > 0) {
      setCurrentImageIndex(currentImageIndex - 1);
    }
  };

  return (
    <div>
      <div className="service-card">
        <div className="service-details-container">
          <div className="service-details-image">
            {service.picture && service.picture.length > 0 ? (
              <div style={{ position: "relative" }}>
                <img
                  src={`data:image/png;base64,${service.picture[currentImageIndex]}`}
                  alt={service.serviceName}
                  style={{ width: "100%", height: "auto" }}
                />
                <div
                  style={{
                    position: "absolute",
                    width: "100%",
                    top: "50%",
                    transform: "translateY(-50%)",
                    display: "flex",
                    justifyContent: "space-between",
                  }}
                >
                  <button
                    className="previous-button"
                    onClick={previousImage}
                    disabled={currentImageIndex === 0}
                    style={{ visibility: currentImageIndex === 0 ? "hidden" : "visible" }}
                  >
                    &#8592;
                  </button>
                  <button
                    className="next-button"
                    onClick={nextImage}
                    disabled={currentImageIndex === service.picture.length - 1}
                    style={{ visibility: currentImageIndex === service.picture.length - 1 ? "hidden" : "visible" }}
                  >
                    &#8594;
                  </button>
                </div>
              </div>
            ) : (
              <p>Се вчитува...</p>
            )}
          </div>

          <div className="service-details-info">
            <div className="service-name">{service.serviceName}</div>
            <h1 className="service-description">
              {service.title} 
            </h1>

            <div className="service-properties">
              {Object.entries(service).map(([key, value]) => {
                if (excludedProperties.includes(key)) {
                  return null;
                }
                const propertyName = propertyChanges[key] || key;

                // Handle importanceLevel translation
                if (key === "importanceLevel") {
                  value = getImportanceLevelTranslation(value);
                }

                return (
                  <div className="service-property" key={key}>
                    <div className="service-key">{propertyName}</div>
                    <div className="service-value">
                      {Array.isArray(value) ? value.join(", ") : value}
                    </div>
                  </div>
                );
              })}
            </div>

            <br />
            <div className="availabilities-section">
              <h4>Потребно време во кое треба да се заврши задачата:</h4>
              <table className="availabilities-table">
                <thead>
                  <tr>
                    <th colSpan={2}>Од</th>
                    <th>До</th>
                  </tr>
                </thead>
                <tbody>
                  {service.availabilities.map((availability, index) => (
                    <tr key={index}>
                      <td>
                        <input
                          type="radio"
                          id={`availability${index}`}
                          name="availability"
                          value={availability.availabilityId}
                          checked={selectedAvailability?.id === availability.id}
                          onChange={() => setSelectedAvailability(availability)}
                        />
                      </td>
                      <td>{availability.dateTimeFrom}</td>
                      <td>{availability.dateTimeTo}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <br />
            <button className="apply-button" onClick={handleApply}>
              Испрати
            </button>
          </div>
        </div>
      </div>
      <CommentsSection
        reviews={service.reviews}
        user={user}
        postId={postId}
        refreshService={refreshService}
      />
    </div>
  );
}

export default ServiceDetailsPage;
