import axiosInstance from "../axiosInstance";

const getUserRequesterRequests = async (id) => {
  try {
    const response = await axiosInstance.get(`/users/user-requester/${id}`);
    console.log("user-requester", response);
    return response.data;
  } catch (error) {
    console.error(error);
  }
};

export default getUserRequesterRequests;
