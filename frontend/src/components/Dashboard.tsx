import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { 
  ShoppingCart, 
  TrendingUp, 
  Package,
  DollarSign,
  RefreshCw
} from 'lucide-react';
import { useState } from 'react';
import { productsApi } from '../api/productsApi';
import { toast } from 'sonner';

export default function Dashboard() {
  const [isSyncing, setIsSyncing] = useState(false);

  const handleSyncProducts = async () => {
    setIsSyncing(true);
    try {
      await productsApi.syncAllProducts();
      toast.success('Products synced successfully', {
        description: 'All products have been synced from Shopify',
      });
    } catch (error: any) {
      toast.error('Failed to sync products', {
        description: error.message || 'Please try again later',
      });
    } finally {
      setIsSyncing(false);
    }
  };
  // Mock data for orders
  const orderStats = [
    { label: 'Last 24 Hours', value: 12, change: '+8%', color: 'text-blue-600' },
    { label: 'Last 7 Days', value: 87, change: '+12%', color: 'text-green-600' },
    { label: 'Last 30 Days', value: 342, change: '+23%', color: 'text-purple-600' },
    { label: 'Total Orders', value: 1847, change: '', color: 'text-orange-600' },
  ];

  // Mock data for best selling products
  const bestSellingProducts = [
    { id: 1, name: 'Premium Wireless Headphones', sales: 145, revenue: '$14,500', image: '🎧' },
    { id: 2, name: 'Smart Watch Pro', sales: 132, revenue: '$19,800', image: '⌚' },
    { id: 3, name: 'Laptop Stand Adjustable', sales: 98, revenue: '$4,900', image: '💻' },
    { id: 4, name: 'USB-C Hub 7-in-1', sales: 87, revenue: '$4,350', image: '🔌' },
    { id: 5, name: 'Wireless Mouse Ergonomic', sales: 76, revenue: '$3,040', image: '🖱️' },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl mb-2">Dashboard</h1>
          <p className="text-gray-600">Welcome to your Integratez dashboard</p>
        </div>
        <Button onClick={handleSyncProducts} disabled={isSyncing}>
          <RefreshCw className={`size-4 mr-2 ${isSyncing ? 'animate-spin' : ''}`} />
          {isSyncing ? 'Syncing...' : 'Sync Products from Shopify'}
        </Button>
      </div>

      {/* Order Statistics */}
      <div>
        <h2 className="text-xl mb-4">Order Statistics</h2>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          {orderStats.map((stat) => (
            <Card key={stat.label}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm">{stat.label}</CardTitle>
                <ShoppingCart className="size-4 text-gray-600" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl">{stat.value}</div>
                {stat.change && (
                  <p className={`text-xs ${stat.color} flex items-center gap-1 mt-1`}>
                    <TrendingUp className="size-3" />
                    {stat.change} from last period
                  </p>
                )}
              </CardContent>
            </Card>
          ))}
        </div>
      </div>

      {/* Best Selling Products */}
      <div>
        <h2 className="text-xl mb-4">Best Selling Products</h2>
        <Card>
          <CardHeader>
            <CardTitle>Top 5 Products</CardTitle>
            <CardDescription>Your best performing products this month</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {bestSellingProducts.map((product, index) => (
                <div
                  key={product.id}
                  className="flex items-center justify-between p-4 rounded-lg border hover:bg-gray-50 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <div className="flex items-center justify-center size-12 bg-blue-50 rounded-lg text-2xl">
                      {product.image}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span>{product.name}</span>
                        {index === 0 && <Badge variant="secondary">Top Seller</Badge>}
                      </div>
                      <div className="text-sm text-gray-600">{product.sales} units sold</div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="flex items-center gap-1 text-green-600">
                      <DollarSign className="size-4" />
                      <span>{product.revenue}</span>
                    </div>
                    <div className="text-sm text-gray-600">Revenue</div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Quick Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0">
            <CardTitle className="text-sm">Active Products</CardTitle>
            <Package className="size-4 text-gray-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">248</div>
            <p className="text-xs text-gray-600 mt-1">Across all channels</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0">
            <CardTitle className="text-sm">Pending Shipments</CardTitle>
            <ShoppingCart className="size-4 text-gray-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">23</div>
            <p className="text-xs text-gray-600 mt-1">Ready to ship</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0">
            <CardTitle className="text-sm">Revenue (30 days)</CardTitle>
            <DollarSign className="size-4 text-gray-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl">$54,230</div>
            <p className="text-xs text-green-600 mt-1">+18% from last month</p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
