import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Search, Filter, Download, Truck } from 'lucide-react';

export default function ShipmentsPage() {
  const shipments = [
    { 
      id: 'SHP-5678', 
      orderId: '#ORD-1232', 
      customer: 'Mike Johnson', 
      tracking: 'TRK123456789', 
      carrier: 'Shiprocket',
      date: '2025-11-19', 
      eta: '2025-11-22',
      status: 'In Transit',
      destination: 'Mumbai, MH'
    },
    { 
      id: 'SHP-5677', 
      orderId: '#ORD-1230', 
      customer: 'Tom Brown', 
      tracking: 'TRK123456788', 
      carrier: 'Shiprocket',
      date: '2025-11-18', 
      eta: '2025-11-21',
      status: 'In Transit',
      destination: 'Delhi, DL'
    },
    { 
      id: 'SHP-5676', 
      orderId: '#ORD-1231', 
      customer: 'Sarah Williams', 
      tracking: 'TRK123456787', 
      carrier: 'Shiprocket',
      date: '2025-11-18', 
      eta: '2025-11-20',
      status: 'Delivered',
      destination: 'Bangalore, KA'
    },
    { 
      id: 'SHP-5675', 
      orderId: '#ORD-1228', 
      customer: 'David Miller', 
      tracking: 'TRK123456786', 
      carrier: 'Shiprocket',
      date: '2025-11-17', 
      eta: '2025-11-19',
      status: 'Delivered',
      destination: 'Pune, MH'
    },
    { 
      id: 'SHP-5674', 
      orderId: '#ORD-1234', 
      customer: 'John Doe', 
      tracking: 'TRK123456785', 
      carrier: 'Shiprocket',
      date: '2025-11-20', 
      eta: '2025-11-23',
      status: 'Pending Pickup',
      destination: 'Chennai, TN'
    },
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Pending Pickup':
        return 'bg-yellow-100 text-yellow-800';
      case 'In Transit':
        return 'bg-blue-100 text-blue-800';
      case 'Out for Delivery':
        return 'bg-purple-100 text-purple-800';
      case 'Delivered':
        return 'bg-green-100 text-green-800';
      case 'Failed':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl mb-2">Shipments</h1>
          <p className="text-gray-600">Track and manage your shipments</p>
        </div>
        <Button variant="outline">
          <Truck className="size-4 mr-2" />
          Sync Shipments
        </Button>
      </div>

      {/* Summary Cards */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">Pending Pickup</div>
            <div className="text-2xl">5</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">In Transit</div>
            <div className="text-2xl">18</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">Out for Delivery</div>
            <div className="text-2xl">7</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="text-sm text-gray-600 mb-1">Delivered</div>
            <div className="text-2xl">132</div>
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
                placeholder="Search by tracking number or order ID..."
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

      {/* Shipments Table */}
      <Card>
        <CardHeader>
          <CardTitle>Recent Shipments</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b">
                  <th className="text-left py-3 px-4">Shipment ID</th>
                  <th className="text-left py-3 px-4">Order ID</th>
                  <th className="text-left py-3 px-4">Customer</th>
                  <th className="text-left py-3 px-4">Tracking Number</th>
                  <th className="text-left py-3 px-4">Carrier</th>
                  <th className="text-left py-3 px-4">Destination</th>
                  <th className="text-left py-3 px-4">Ship Date</th>
                  <th className="text-left py-3 px-4">ETA</th>
                  <th className="text-left py-3 px-4">Status</th>
                  <th className="text-left py-3 px-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {shipments.map((shipment) => (
                  <tr key={shipment.id} className="border-b hover:bg-gray-50">
                    <td className="py-3 px-4">{shipment.id}</td>
                    <td className="py-3 px-4">{shipment.orderId}</td>
                    <td className="py-3 px-4">{shipment.customer}</td>
                    <td className="py-3 px-4">
                      <code className="text-xs bg-gray-100 px-2 py-1 rounded">
                        {shipment.tracking}
                      </code>
                    </td>
                    <td className="py-3 px-4">{shipment.carrier}</td>
                    <td className="py-3 px-4 text-gray-600">{shipment.destination}</td>
                    <td className="py-3 px-4 text-gray-600">{shipment.date}</td>
                    <td className="py-3 px-4 text-gray-600">{shipment.eta}</td>
                    <td className="py-3 px-4">
                      <Badge className={getStatusColor(shipment.status)} variant="secondary">
                        {shipment.status}
                      </Badge>
                    </td>
                    <td className="py-3 px-4">
                      <Button variant="ghost" size="sm">
                        Track
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
