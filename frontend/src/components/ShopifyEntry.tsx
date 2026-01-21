import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { shopifyApi } from '../api/shopifyApi';

export default function ShopifyEntry() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { setToken } = useAuth();
  const shop = searchParams.get('shop');

  useEffect(() => {
    async function authenticate() {
      if (!shop) {
        console.error('No shop parameter found');
        navigate('/login');
        return;
      }

      try {
        const response = await shopifyApi.appAuth(shop);
        setToken(response.token);
        navigate('/dashboard');
      } catch (error: any) {
        console.error('Auto-login failed:', error);
        navigate('/login');
      }
    }

    authenticate();
  }, [shop, navigate, setToken]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
        <p className="text-gray-600">Authenticating with Shopify...</p>
      </div>
    </div>
  );
}

