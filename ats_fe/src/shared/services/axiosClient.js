import axios from "axios";
import { v4 as uuidv4 } from "uuid";
import authService from "@/features/auth/services/auth.service";

const BASE_URL = "http://localhost:8080";

const axiosClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Send/receive the httpOnly accessToken cookie on every request
  timeout: 5000, // 5 seconds timeout
});

axiosClient.interceptors.request.use(
  function (config) {
    console.log("Calling", config.method, config.url);
    config.headers.set("X-Correlation-ID", uuidv4());
    return config; // you must return config so the call can proceed
  },
  function (error) {
    return Promise.reject(error);
  },
);

// Response interceptor: xử lý khi nhận response lỗi
axiosClient.interceptors.response.use(
  function (response) {
    return response; // Response thành công → trả về bình thường
  },
  async function (error) {
    const originalConfig = error.config;

    // Log lỗi ra console để debug
    if (error.response) {
      console.log("Error response:", error.response.status, originalConfig.url);
    }

    // ===== LOGIC REFRESH TOKEN =====
    // Điều kiện refresh:
    // 1. Server trả 401 (Unauthorized) → accessToken hết hạn
    // 2. Chưa retry lần nào (tránh lặp vô hạn)
    // 3. QUAN TRỌNG: Không phải các URL sau:
    //    - /refresh 401 → refreshToken hết hạn → dừng, không gọi lại (tránh loop)
    //    - /login 401 → sai mật khẩu → không phải token hết hạn
    const skipRefreshUrls = [
      "/api/v1/auths/refresh",
      "/api/v1/auths/login",
    ];

    if (
      error.response &&
      error.response.status === 401 &&
      !originalConfig._retry &&
      !skipRefreshUrls.includes(originalConfig.url)
    ) {
      originalConfig._retry = true; // Đánh dấu đã retry

      try {
        console.log("AccessToken hết hạn. Đang refresh...");

        // Gọi API refresh → backend đọc refreshToken từ cookie → trả về accessToken mới (cookie)
        const response = await authService.refreshToken();
        const userProfile = response.data;

        // Cập nhật lại user trong localStorage
        if (userProfile) {
          localStorage.setItem("user", JSON.stringify(userProfile));
        }

        console.log("Refresh thành công! Gọi lại request ban đầu...");

        // Gọi lại request ban đầu với accessToken mới (đã set trong cookie bởi backend)
        return axiosClient(originalConfig);
      } catch (_error) {
        console.error("Refresh token thất bại → logout", _error);

        // RefreshToken cũng hết hạn → xóa session, đẩy về trang login
        localStorage.removeItem("user");
        if (window.location.pathname !== "/login") {
          window.location.href = "/login";
        }

        return Promise.reject(_error);
      }
    }

    return Promise.reject(error);
  },
);

export default axiosClient;
