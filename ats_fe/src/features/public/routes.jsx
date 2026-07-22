import React from "react";
import { Route } from "react-router-dom";
import PublicLayout from "@/app/layout/PublicLayout";
import PublicHomePage from "@/features/public/pages/PublicHomePage";
import AboutUsPage from "@/features/public/pages/AboutUsPage";
import ViewJobListPage from "@/features/jobs/pages/ViewJobListPage";
import PublicJobDetailPage from "@/features/jobs/pages/PublicJobDetailPage";
import ApplyJobPage from "@/features/jobs/pages/ApplyJobPage";


const publicRoutes = (
  <Route path="/" element={<PublicLayout />}>
    <Route index element={<PublicHomePage />} />
    <Route path="abouts" element={<AboutUsPage />} />
    <Route path="careers" element={<ViewJobListPage />} />
    <Route path="careers/job/:id" element={<PublicJobDetailPage />} />
    <Route path="careers/job/:id/apply" element={<ApplyJobPage />} />
    <Route
      path="*"
      element={<h1 style={{ height: "100vh" }}>404 Not Found</h1>}
    />
  </Route>
);

export default publicRoutes;
