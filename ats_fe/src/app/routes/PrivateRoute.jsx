import { useContext } from "react";
import { AuthContext} from "@/app/providers/AuthProvider.jsx";
import { Navigate, useLocation, Outlet } from "react-router-dom";
import { Spinner } from "react-bootstrap";

const PrivateRoute = ({ allowedRoles }) => {
  const { user, loading } = useContext(AuthContext);
  const location = useLocation();

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <Spinner animation="border" variant="primary" />
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />; // Redirect to login if not authenticated
  }

  const roles = user.roles || []; // Assuming user object has a roles property
  const hasAccess = allowedRoles.some((role) => roles.includes(role));

  console.log("PrivateRoute: user roles:", roles);
  console.log("PrivateRoute: allowedRoles:", allowedRoles);

  if (!hasAccess) {
    return <Navigate to="/login" replace />; // Redirect to unauthorized page if no access
  }

  return <Outlet />; // Render the child routes if authenticated and authorized
};

export default PrivateRoute;
