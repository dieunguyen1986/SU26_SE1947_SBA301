import React, { useEffect, useState } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Form,
  Alert,
} from "react-bootstrap";
import { ArrowLeft, Briefcase, GeoAlt, CashStack } from "react-bootstrap-icons";
import { useParams, useNavigate } from "react-router-dom";
import jobService from "../services/job.service";
import axios from "axios";

// ===== CẤU HÌNH =====
const API_BASE_URL = "http://localhost:8081"; // Port của ats_springboot_mongodb

const ApplyJobPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  // State cho job data
  const [job, setJob] = useState(null);

  // State cho form fields
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [file, setFile] = useState(null);

  // State cho trạng thái submit
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: "", text: "" });

  // ===== BƯỚC 0: Lấy thông tin Job =====
  useEffect(() => {
    async function fetchJob() {
      try {
        const jobData = await jobService.findById(id);
        setJob(jobData);
      } catch (err) {
        console.error("Failed to fetch job:", err);
      }
    }
    fetchJob();
  }, [id]);

  // ===== XỬ LÝ CHỌN FILE =====
  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    setFile(selectedFile);
  };

  // ===== XỬ LÝ SUBMIT FORM =====
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: "", text: "" });

    try {
      // ----- BƯỚC 1: Upload file CV lên Cloudinary → lấy về URL -----
      const formData = new FormData();
      formData.append("file", file);

      const uploadResponse = await axios.post(
        `${API_BASE_URL}/api/v1/candidates`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );

      // Lấy URL file từ response
      const cvFileUrl = uploadResponse.data.url;
      console.log("Upload thành công, URL:", cvFileUrl);

      // ----- BƯỚC 2: Tạo Candidate với URL file vừa upload -----
      const candidate = {
        fullName: fullName,
        email: email,
        phoneNumber: phoneNumber,
        jobId: id,
        cvFileId: cvFileUrl,
      };

      const createResponse = await axios.post(
        `${API_BASE_URL}/api/v1/candidates`,
        candidate,
        {
          headers: { "Content-Type": "application/json" },
        }
      );

      console.log("Tạo candidate thành công:", createResponse.data);
      setMessage({ type: "success", text: "Ứng tuyển thành công! CV của bạn đã được gửi." });

    } catch (err) {
      console.error("Lỗi:", err);
      setMessage({
        type: "danger",
        text: "Có lỗi xảy ra: " + (err.response?.data?.message || err.message),
      });
    } finally {
      setLoading(false);
    }
  };

  // ===== LOADING STATE =====
  if (!job) {
    return (
      <Container className="py-5 text-center text-muted">
        Loading...
      </Container>
    );
  }

  // ===== GIAO DIỆN =====
  return (
    <Container className="py-4" style={{ maxWidth: 960 }}>
      {/* Nút quay lại */}
      <Button
        variant="link"
        className="text-decoration-none mb-3 ps-0"
        style={{ color: "#4f46e5" }}
        onClick={() => navigate(`/careers/job/${id}`)}
      >
        <ArrowLeft className="me-1" /> Quay lại chi tiết công việc
      </Button>

      <Row className="g-4">
        {/* ========== CỘT TRÁI: Form ứng tuyển ========== */}
        <Col lg={8}>
          <Card className="shadow-sm" style={{ border: "1px solid #e2e8f0", borderRadius: 16 }}>
            <Card.Body className="p-4">
              <h4 className="fw-bold mb-1" style={{ color: "#1e293b" }}>
                Ứng tuyển vị trí
              </h4>
              <h5 className="mb-4" style={{ color: "#4f46e5" }}>
                {job.title}
              </h5>

              {/* Hiển thị thông báo thành công / lỗi */}
              {message.text && (
                <Alert variant={message.type}>{message.text}</Alert>
              )}

              <Form onSubmit={handleSubmit}>
                {/* Họ và tên */}
                <Form.Group className="mb-3">
                  <Form.Label>Họ và tên <span className="text-danger">*</span></Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Nguyễn Văn A"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                  />
                </Form.Group>

                {/* Email */}
                <Form.Group className="mb-3">
                  <Form.Label>Email <span className="text-danger">*</span></Form.Label>
                  <Form.Control
                    type="email"
                    placeholder="example@email.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </Form.Group>

                {/* Số điện thoại */}
                <Form.Group className="mb-3">
                  <Form.Label>Số điện thoại <span className="text-danger">*</span></Form.Label>
                  <Form.Control
                    type="tel"
                    placeholder="0901 234 567"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    required
                  />
                </Form.Group>

                {/* Upload CV */}
                <Form.Group className="mb-4">
                  <Form.Label>Upload CV <span className="text-danger">*</span></Form.Label>
                  <Form.Control
                    type="file"
                    accept=".pdf,.doc,.docx"
                    onChange={handleFileChange}
                    required
                  />
                  <Form.Text className="text-muted">
                    Hỗ trợ PDF, DOC, DOCX (tối đa 10MB)
                  </Form.Text>
                </Form.Group>

                {/* Nút submit */}
                <Button
                  type="submit"
                  className="w-100 fw-semibold py-2"
                  style={{
                    backgroundColor: "#4f46e5",
                    border: "none",
                    borderRadius: 10,
                    fontSize: 16,
                  }}
                  disabled={loading}
                >
                  {loading ? "Đang gửi..." : "Nộp đơn ứng tuyển"}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        {/* ========== CỘT PHẢI: Thông tin Job ========== */}
        <Col lg={4}>
          <Card className="border-0 shadow-sm" style={{ borderRadius: 16, position: "sticky", top: 24 }}>
            <Card.Body className="p-4">
              <h6 className="fw-bold mb-3" style={{ color: "#1e293b" }}>
                Thông tin công việc
              </h6>

              <div className="mb-3">
                <div className="small text-muted d-flex align-items-center gap-1">
                  <Briefcase size={14} /> Vị trí
                </div>
                <div className="fw-semibold">{job.title}</div>
              </div>

              <div className="mb-3">
                <div className="small text-muted d-flex align-items-center gap-1">
                  <GeoAlt size={14} /> Địa điểm
                </div>
                <div className="fw-semibold">{job.location}</div>
              </div>

              <div className="mb-3">
                <div className="small text-muted d-flex align-items-center gap-1">
                  <CashStack size={14} /> Mức lương
                </div>
                <div className="fw-semibold">{job.salary}</div>
              </div>

              <hr />

              <div>
                <div className="small text-muted mb-2">Kỹ năng yêu cầu</div>
                <div className="d-flex flex-wrap gap-2">
                  {job.skills?.map((s) => (
                    <span
                      key={s}
                      className="badge"
                      style={{
                        backgroundColor: "#f1f5f9",
                        color: "#475569",
                        padding: "5px 10px",
                        fontSize: 12,
                      }}
                    >
                      {s}
                    </span>
                  ))}
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default ApplyJobPage;
