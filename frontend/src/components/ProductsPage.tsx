import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Search, Filter, Download, Package, RefreshCw } from 'lucide-react';
import { useState, useEffect } from 'react';
import UpdateInventoryDialog from './UpdateInventoryDialog';
import { toast } from 'sonner';
import { productsApi } from '../api/productsApi';

export default function ProductsPage() {
  const [updateDialogOpen, setUpdateDialogOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<any>(null);
  const [isSyncing, setIsSyncing] = useState(false);
  const [products, setProducts] = useState([
    { id: 1, name: 'Premium Wireless Headphones', sku: 'PWH-001', price: '$99.99', stock: 145, status: 'In Stock', channel: 'Shopify' },
    { id: 2, name: 'Smart Watch Pro', sku: 'SWP-002', price: '$149.99', stock: 87, status: 'In Stock', channel: 'Shopify' },
    { id: 3, name: 'Laptop Stand Adjustable', sku: 'LSA-003', price: '$49.99', stock: 234, status: 'In Stock', channel: 'Shopify' },
    { id: 4, name: 'USB-C Hub 7-in-1', sku: 'UCH-004', price: '$39.99', stock: 12, status: 'Low Stock', channel: 'Shopify' },
    { id: 5, name: 'Wireless Mouse Ergonomic', sku: 'WME-005', price: '$29.99', stock: 0, status: 'Out of Stock', channel: 'Shopify' },
    { id: 6, name: 'Bluetooth Speaker Portable', sku: 'BSP-006', price: '$79.99', stock: 156, status: 'In Stock', channel: 'Shopify' },
    { id: 7, name: 'Phone Case Premium', sku: 'PCP-007', price: '$19.99', stock: 432, status: 'In Stock', channel: 'Shopify' },
    { id: 8, name: 'Charging Cable 3-Pack', sku: 'CC3-008', price: '$24.99', stock: 298, status: 'In Stock', channel: 'Shopify' },
  ]);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'In Stock':
        return 'bg-green-100 text-green-800';
      case 'Low Stock':
        return 'bg-yellow-100 text-yellow-800';
      case 'Out of Stock':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const handleOpenUpdateDialog = (product: any) => {
    setSelectedProduct(product);
    setUpdateDialogOpen(true);
  };

  const handleSyncProducts = async () => {
    setIsSyncing(true);
    try {
      await productsApi.syncAllProducts();
      toast.success('Products synced successfully', {
        description: 'All products have been synced from Shopify',
      });
      // In a real app, you would fetch the updated products list here
    } catch (error: any) {
      toast.error('Failed to sync products', {
        description: error.message || 'Please try again later',
      });
    } finally {
      setIsSyncing(false);
    }
  };

  const handleUpdateInventory = (productId: number, newStock: number) => {
    setProducts(products.map(product => {
      if (product.id === productId) {
        let newStatus = 'In Stock';
        if (newStock === 0) newStatus = 'Out of Stock';
        else if (newStock < 20) newStatus = 'Low Stock';
        
        return { ...product, stock: newStock, status: newStatus };
      }
      return product;
    }));
    
    toast.success('Inventory updated successfully', {
      description: `Stock level updated to ${newStock} units`,
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl mb-2">Products</h1>
          <p className="text-gray-600">Manage your product listings across all channels</p>
        </div>
        <div className="flex gap-2">
          <Button onClick={handleSyncProducts} disabled={isSyncing} variant="outline">
            <RefreshCw className={`size-4 mr-2 ${isSyncing ? 'animate-spin' : ''}`} />
            {isSyncing ? 'Syncing...' : 'Sync from Shopify'}
          </Button>
          <Button>
            Add Product
          </Button>
        </div>
      </div>

      {/* Filters and Search */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder="Search products by name or SKU..."
                className="pl-10"
              />
            </div>
            <Button variant="outline">
              <Filter className="size-4 mr-2" />
              Filter
            </Button>
            <Button variant="outline">
              <Download className="size-4 mr-2" />
              Export
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Products Table */}
      <Card>
        <CardHeader>
          <CardTitle>All Products ({products.length})</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-4">Product</th>
                  <th className="text-left py-3 px-4">SKU</th>
                  <th className="text-left py-3 px-4">Price</th>
                  <th className="text-left py-3 px-4">Stock</th>
                  <th className="text-left py-3 px-4">Status</th>
                  <th className="text-left py-3 px-4">Channel</th>
                  <th className="text-left py-3 px-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id} className="border-b hover:bg-gray-50">
                    <td className="py-3 px-4">{product.name}</td>
                    <td className="py-3 px-4 text-gray-600">{product.sku}</td>
                    <td className="py-3 px-4">{product.price}</td>
                    <td className="py-3 px-4">{product.stock}</td>
                    <td className="py-3 px-4">
                      <Badge className={getStatusColor(product.status)} variant="secondary">
                        {product.status}
                      </Badge>
                    </td>
                    <td className="py-3 px-4">{product.channel}</td>
                    <td className="py-3 px-4">
                      <div className="flex gap-2">
                        <Button variant="ghost" size="sm">
                          Edit
                        </Button>
                        <Button 
                          variant="ghost" 
                          size="sm"
                          onClick={() => handleOpenUpdateDialog(product)}
                        >
                          <Package className="size-4 mr-1" />
                          Update Stock
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>

      <UpdateInventoryDialog
        open={updateDialogOpen}
        onOpenChange={setUpdateDialogOpen}
        product={selectedProduct}
        onUpdate={handleUpdateInventory}
      />
    </div>
  );
}