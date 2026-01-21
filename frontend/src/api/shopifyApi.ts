import axiosClient from './axiosClient';

export interface ShopifyAppAuthResponse {
  token: string;
  username: string;
  email: string;
}

export const shopifyApi = {
  appAuth: async (shop: string): Promise<ShopifyAppAuthResponse> => {
    const response = await axiosClient.get<ShopifyAppAuthResponse>('/shopify/app-auth', {
      params: { shop },
    });
    return response.data;
  },

  install: async (shop: string): Promise<string> => {
    const response = await axiosClient.get<string>('/shopify/install', {
      params: { shop },
    });
    return response.data;
  },
};

