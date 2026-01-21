import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Search, Filter, Download } from 'lucide-react';

export default function OrdersPage() {
  const orders = [
    { id: '#ORD-1234', customer: 'John Doe', date: '2025-11-20', items: 3, total: '$299.97', status: 'Pending', channel: 'Shopify' },
    { id: '#ORD-1233', customer: 'Jane Smith', date: '2025-11-20', items: 1, total: '$149.99', status: 'Processing', channel: 'Shopify' },
    { id: '#ORD-1232', customer: 'Mike Johnson', date: '2025-11-19', items: 2, total: '$89.98', status: 'Shipped', channel: 'Shopify' },
    { id: '#ORD-1231', customer: 'Sarah Williams', date: '2025-11-19', items: 1, total: '$99.99', status: 'Delivered', channel: 'Shopify' },
    { id: '#ORD-1230', customer: 'Tom Brown', date: '2025-11-18', items: 4, total: '$419.96', status: 'Shipped', channel: 'Shopify' },
    { id: '#ORD-1229', customer: 'Emily Davis', date: '2025-11-18', items: 2, total: '$179.98', status: 'Processing', channel: 'Shopify' },
    { id: '#ORD-1228', customer: 'David Miller', date: '2025-11-17', items: 1, total: '$49.99', status: 'Delivered', channel: 'Shopify' },
    { id: '#ORD-1227', customer: 'Lisa Anderson', date: '2025-11-17', items: 3, total: '$249.97', status: 'Cancelled', channel: 'Shopify' },
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'Processing':
        return 'bg-blue-100 text-blue-800';
      case 'Shipped':
        return 'bg-purple-100 text-purple-800';
      case 'Delivered':
        return 'bg-green-100 text-green-800';
      case 'Cancelled':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl mb-2">Orders</h1>
          <p className="text-gray-600">Manage and track all your orders</p>
        </div>
        <Button variant="outline">
          Sync Orders
        </Button>
      </div>

      {/* Summary Cards */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">Pending</div>
            <div className="text-2xl">8</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">Processing</div>
            <div className="text-2xl">15</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">Shipped</div>
            <div className="text-2xl">32</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">Delivered</div>
            <div className="text-2xl">145</div>
          </CardContent>
        </Card>
      </div>

      {/* Filters and Search */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex gap-4">
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder="Search orders by ID or customer..."
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

      {/* Orders Table */}
      <Card>
        <CardHeader>
          <CardTitle>Recent Orders</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-4">Order ID</th>
                  <th className="text-left py-3 px-4">Customer</th>
                  <th className="text-left py-3 px-4">Date</th>
                  <th className="text-left py-3 px-4">Items</th>
                  <th className="text-left py-3 px-4">Total</th>
                  <th className="text-left py-3 px-4">Status</th>
                  <th className="text-left py-3 px-4">Channel</th>
                  <th className="text-left py-3 px-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.id} className="border-b hover:bg-gray-50">
                    <td className="py-3 px-4">{order.id}</td>
                    <td className="py-3 px-4">{order.customer}</td>
                    <td className="py-3 px-4 text-gray-600">{order.date}</td>
                    <td className="py-3 px-4">{order.items}</td>
                    <td className="py-3 px-4">{order.total}</td>
                    <td className="py-3 px-4">
                      <Badge className={getStatusColor(order.status)} variant="secondary">
                        {order.status}
                      </Badge>
                    </td>
                    <td className="py-3 px-4">{order.channel}</td>
                    <td className="py-3 px-4">
                      <Button variant="ghost" size="sm">
                        View
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
