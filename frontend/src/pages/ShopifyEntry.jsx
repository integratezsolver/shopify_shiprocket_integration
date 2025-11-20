import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import axiosClient from "../api/axiosClient";

export default function ShopifyEntry() {

  const navigate = useNavigate();
  const { login } = useAuth();

  // Extract query params
  const params = new URLSearchParams(window.location.search);
  const shop = params.get("shop");

  useEffect(() => {
    async function authenticate() {
      try {
        const res = await axiosClient.get(`/app-auth?shop=${shop}`);

        if (res.data.token) {
          login(res.data.token);
          navigate("/dashboard");
        }
      } catch (err) {
        console.log("Auto-login failed", err);
        navigate("/login");
      }
    }

    authenticate();
  }, []);

  return <div>Loading...</div>;
}
