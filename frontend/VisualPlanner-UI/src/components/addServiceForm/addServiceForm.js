import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./addServiceForm.css";
import axiosInstance from "../../services/axiosInstance";
import { MobileDateTimePicker } from "@mui/x-date-pickers";
import { format, isPast } from "date-fns";
import { toast } from "react-toastify";

const AddServiceForm = ({
  activityTypes,
  petTypes,
  userId,
  refreshUserPosts,
}) => {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const location = useLocation();
  const postToEdit = location.state?.post;
  const temp_user_data = localStorage.getItem("userData");
  const userData = JSON.parse(temp_user_data);
  userId = userData.id;
  const [formData, setFormData] = useState({
    description: "",
    petSize: "",
    price: "",
    activityTypeId: "",
    availabilities: [],
    city:"",
    userId: userId,
  });

  const [selectedImages, setSelectedImages] = useState([]); // Holds the selected images
  const [imagePreviews, setImagePreviews] = useState([]); // Holds the preview URLs

  useEffect(() => {
    if (postToEdit) {
      setFormData({
        ...postToEdit,
        availabilities: postToEdit.availabilities
          ? postToEdit.availabilities.map((availability) => ({
              ...availability,
              dateTimeFrom: new Date(availability.dateTimeFrom),
              dateTimeTo: new Date(availability.dateTimeTo),
            }))
          : [],
        userId: userId,
      });
    }
  }, [postToEdit, userId]);

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Handle file selection (multiple files)
  const handleFileSelect = (files) => {
    const fileArray = Array.from(files); // Convert FileList to array
    setSelectedImages(fileArray);

    // Generate image preview URLs
    const previewArray = fileArray.map((file) => URL.createObjectURL(file));
    setImagePreviews(previewArray);
  };

  const handleDateTimeChange = (index, field, value) => {
    const dateObject = value.$d;
    const isInPast = isPast(dateObject);
    if (isInPast) {
      setError("Датумот што го избравте е во минатото!");
      return;
    }

    const formattedDate = format(dateObject, "dd-MM-yyyy HH:mm:ss");
    const updatedAvailabilities = [...formData.availabilities];
    updatedAvailabilities[index][field] = formattedDate;
    setFormData({
      ...formData,
      availabilities: updatedAvailabilities,
    });
  };

  const handleAddTime = () => {
    setFormData({
      ...formData,
      availabilities: [
        ...formData.availabilities,
        { dateTimeFrom: new Date(), dateTimeTo: new Date() },
      ],
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    
    const formDataToSubmit = new FormData();
    
    // Append the `task` data (stringify it to send it as part of `FormData`)
    formDataToSubmit.append('task', JSON.stringify({
      description: formData.description,
      petSize: formData.petSize,
      price: formData.price,
      activityTypeId: formData.activityTypeId,
      availabilities: formData.availabilities,
      userId: formData.userId,
      title:formData.title,
      city: formData.city,  // Ensure the city is included here
    }));
  
    // Append the pictures (files)
    selectedImages.forEach((file) => {
      formDataToSubmit.append('pictures', file);
    });
  
    try {
      if (postToEdit) {
        await axiosInstance.put(`/posts/${postToEdit.id}`, formDataToSubmit, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        toast.success('Успешно променета задача.');
      } else {
        await axiosInstance.post('/posts/add', formDataToSubmit, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        toast.success('Успешно додадена задача.');
      }
      navigate('/profile');
      refreshUserPosts(userId);
    } catch (error) {
      setError('Грешка при додавање на задача. Обидете се повторно.');
      console.error('Error occurred:', error);
    }
  };

  const handleBackToProfileButton = () => {
    navigate("/profile");
  };

  return (
    <div className="add-service-container">
      <button
        className="button edit-information"
        onClick={handleBackToProfileButton}
      >
        &larr; Назад кон профилот
      </button>
      <div className="add-service-form">
        <h2>{postToEdit ? "Измени ја задачата" : "Додади нова задача"}</h2>
        <form onSubmit={handleSubmit}>
          <select
            name="activityTypeId"
            value={formData.activityTypeId}
            onChange={handleInputChange}
            required
            className="form-input"
          >
            <option value="" disabled>
              Избери тип на задача
            </option>
            {activityTypes.map((activityType) => (
              <option key={activityType.id} value={activityType.id}>
                {activityType.type}
              </option>
            ))}
          </select>

          <input
            type="number"
            name="price"
            placeholder="Цена"
            value={formData.price}
            onChange={handleInputChange}
            required
            className="form-input number-input"
          />
          <input
            type="text"
            name="title"
            placeholder="Наслов"
            value={formData.title}
            onChange={handleInputChange}
            required
            className="form-input"
          />


        

          <select
            name="petSize"
            value={formData.petSize}
            onChange={handleInputChange}
            required
            className="form-input"
          >
            <option value="" disabled>
              Избери ниво на значајност на задачата
            </option>
            <option value="Low_priority">Низок приоритет</option>
            <option value="Important">Среден приоритет</option>
            <option value="Critical">Висок приоритет</option>
          </select>

          <textarea
            name="description"
            placeholder="Додадете опис на задачата"
            value={formData.description}
            onChange={handleInputChange}
            required
            style={{
              width: "100%",
              padding: "15px",
              border: "2px solid #6F4E90",
              borderRadius: "12px",
            }}
          />

          
          <input
            type="text"
            name="city"
            placeholder="Внеси град"
            value={formData.city}
            onChange={handleInputChange}
            required
            className="form-input"
          />

          <div>
            {formData.availabilities.map((availability, index) => (
              <div key={index} className="date-time-container">
                <div className="date-time-picker">
                  <label className="choose-picture-label">Датум од:</label>
                  <MobileDateTimePicker
                    onChange={(value) =>
                      handleDateTimeChange(index, "dateTimeFrom", value)
                    }
                  />
                </div>
                <div className="date-time-picker">
                  <label className="choose-picture-label">Датум до:</label>
                  <MobileDateTimePicker
                    onChange={(value) =>
                      handleDateTimeChange(index, "dateTimeTo", value)
                    }
                  />
                </div>
              </div>
            ))}
          </div>

          <button type="button" onClick={handleAddTime}>
            Додади време за извршување на задачата
          </button>

          <label htmlFor="picture" className="choose-picture-label">
            Избери максимум 5 слики за дополнителен опис на задачата
          </label>
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={(e) => handleFileSelect(e.target.files)}
            className="form-input"
          />

          {imagePreviews.length > 0 && (
            <div>
              <p>Избрани слики:</p>
              {imagePreviews.map((image, index) => (
                <img
                  key={index}
                  src={image}
                  alt={`Preview ${index}`}
                  style={{ width: "200px", height: "200px", margin: "5px" }}
                />
              ))}
            </div>
          )}

          {error && <p className="error-message">{error}</p>}

          <div style={{ marginTop: "50px" }}>
            <button type="submit">
              {postToEdit ? "Измени задача" : "Додади"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddServiceForm;
