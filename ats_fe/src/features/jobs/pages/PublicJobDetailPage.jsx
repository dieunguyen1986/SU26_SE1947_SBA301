import React, { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button, ListGroup } from "react-bootstrap";
import {
  GeoAlt,
  Clock,
  CashStack,
  CalendarEvent,
  Building,
  ArrowLeft,
  HeartFill,
} from "react-bootstrap-icons";
import jobService from "../services/job.service";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";

const categoryStyles = {
  Engineering: { border: "#4f46e5", bg: "#dbeafe", color: "#2563eb" },
  Design: { border: "#7c3aed", bg: "#ede9fe", color: "#7c3aed" },
  "Product Management": { border: "#db2777", bg: "#fce7f3", color: "#db2777" },
  Marketing: { border: "#ea580c", bg: "#ffedd5", color: "#ea580c" },
  Analytics: { border: "#0d9488", bg: "#ccfbf1", color: "#0d9488" },
  "Data Science": { border: "#0d9488", bg: "#ccfbf1", color: "#0d9488" },
};
const defaultStyle = { border: "#4f46e5", bg: "#dbeafe", color: "#2563eb" };

const PublicJobDetailPage = () => {
  const { id } = useParams();
  const [job, setJob] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchJob() {
      try {
        const jobData = await jobService.findById(id);
        setJob(jobData);
      } catch (err) {
        setError("Failed to load job details. Please try again later.");
        console.error("Failed to fetch job details:", err);
      }
    }

    fetchJob();
  }, [id]);

  const handleBackClick = () => {
    navigate("/careers");
  };

  if (!job) {
    return (
      <Container className="py-5 text-center text-muted">
        Loading job details...
      </Container>
    );
  }

  const style = categoryStyles[job.category] || defaultStyle;

  return (
    <Container className="py-4">
      {error && (
        <Container className="py-3">
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        </Container>
      )}
      <Button
        variant="link"
        className="text-decoration-none mb-3 ps-0"
        style={{ color: "#4f46e5" }}
        onClick={handleBackClick}
      >
        <ArrowLeft className="me-1" /> Back to listings
      </Button>

      <Row className="g-4">
        <Col lg={8}>
          <Card
            className="shadow-sm"
            style={{
              border: "1px solid #e2e8f0",
              borderLeft: `8px solid ${style.border}`,
              borderRadius: 12,
              overflow: "hidden",
            }}
          >
            <Card.Body className="p-4">
              <span
                className="badge mb-3"
                style={{
                  backgroundColor: style.bg,
                  color: style.color,
                  fontWeight: 700,
                  fontSize: 11,
                  padding: "6px 10px",
                  letterSpacing: "0.3px",
                }}
              >
                {job.category?.toUpperCase()}
              </span>

              <h2 className="fw-bold mb-3 text-dark">{job.title}</h2>

              <div className="d-flex flex-wrap align-items-center gap-3 mb-3 small text-secondary">
                <span>
                  <GeoAlt className="me-1" />
                  {job.location}
                </span>
                <span>
                  <Clock className="me-1" />
                  Posted {job.publishedAt}
                </span>
                <span>
                  <CalendarEvent className="me-1" />
                  Deadline {job.deadline}
                </span>
                <span
                  className="badge rounded-pill"
                  style={{
                    backgroundColor: "white",
                    border: "1.5px solid #059669",
                    color: "#059669",
                    fontWeight: 600,
                  }}
                >
                  {job.employmentType}
                </span>
              </div>

              <hr />

              <h5 className="fw-bold mt-3 mb-2">Job Description</h5>
              <p className="text-secondary">{job.description}</p>

              <h5 className="fw-bold mt-4 mb-2">Requirements</h5>
              <ListGroup variant="flush">
                {job.requirements?.map((req, idx) => (
                  <ListGroup.Item
                    key={idx}
                    className="ps-0 border-0 text-secondary"
                  >
                    • {req}
                  </ListGroup.Item>
                ))}
              </ListGroup>

              <h5 className="fw-bold mt-4 mb-2">Required Skills</h5>
              <div className="d-flex flex-wrap gap-2">
                {job.skills?.map((s) => (
                  <span
                    key={s}
                    className="badge fw-normal"
                    style={{
                      backgroundColor: "#f1f5f9",
                      color: "#475569",
                      padding: "6px 12px",
                      fontSize: 12,
                    }}
                  >
                    {s}
                  </span>
                ))}
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          <Card className="border-0 shadow-sm" style={{ borderRadius: 12 }}>
            <Card.Body className="p-4">
              <h6 className="text-secondary mb-3">Job Summary</h6>

              <div className="mb-3">
                <div className="small text-muted">
                  <CashStack className="me-2" />
                  Salary Range
                </div>
                <div className="fw-bold text-dark">{job.salary}</div>
              </div>

              <div className="mb-3">
                <div className="small text-muted">
                  <Building className="me-2" />
                  Department
                </div>
                <div className="fw-bold text-dark">{job.category}</div>
              </div>

              <div className="mb-3">
                <div className="small text-muted">
                  <GeoAlt className="me-2" />
                  Location
                </div>
                <div className="fw-bold text-dark">{job.location}</div>
              </div>

              <div className="mb-4">
                <div className="small text-muted">
                  <Clock className="me-2" />
                  Employment Type
                </div>
                <div className="fw-bold text-dark">{job.employmentType}</div>
              </div>

              <Button
                className="w-100 mb-2 fw-semibold"
                style={{ backgroundColor: "#4f46e5", border: "none" }}
                onClick={() => navigate(`/careers/job/${id}/apply`)}
              >
                Apply Now
              </Button>
              <Button variant="outline-danger" className="w-100 fw-semibold">
                <HeartFill className="me-1" /> Save Job
              </Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default PublicJobDetailPage;
