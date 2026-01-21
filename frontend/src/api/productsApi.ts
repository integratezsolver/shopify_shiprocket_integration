import axiosClient from './axiosClient';

export interface Product {
  id: number;
  productId: string;
  title: string;
  productType?: string;
  status?: string;
  tags?: string;
  variants?: ProductVariant[];
}

export interface ProductVariant {
  id: number;
  variantId: string;
  title: string;
  sku?: string;
  price?: number;
  inventoryQuantity?: number;
}

export const productsApi = {
  syncAllProducts: async (): Promise<string> => {
    const response = await axiosClient.get<string>('/api/products/1');
    return response.data;
  },

  syncProduct: async (productId: string): Promise<string> => {
    const response = await axiosClient.get<string>(`/api/products/${productId}`);
    return response.data;
  },
};

