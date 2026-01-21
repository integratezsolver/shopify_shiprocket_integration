import { Link } from 'react-router-dom';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Package, RefreshCw, Truck, ShoppingBag, Zap, Shield } from 'lucide-react';

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white">
      {/* Header */}
      <header className="border-b bg-white/80 backdrop-blur-sm sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Zap className="size-8 text-blue-600" />
            <span className="text-2xl">Integratez</span>
          </div>
          <div className="flex gap-3">
            <Link to="/login">
              <Button variant="outline">Login</Button>
            </Link>
            <Link to="/signup">
              <Button>Sign Up</Button>
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="container mx-auto px-4 py-20 text-center">
        <h1 className="text-5xl mb-6">
          Connect Your Sales Channels & Fulfillment in Minutes
        </h1>
        <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
          Integratez seamlessly connects your Shopify store with Shiprocket and other platforms. 
          Sync orders, inventory, products, and shipments automatically.
        </p>
        <div className="flex gap-4 justify-center">
          <Link to="/signup">
            <Button size="lg" className="text-lg px-8">
              Get Started Free
            </Button>
          </Link>
          <Link to="/login">
            <Button size="lg" variant="outline" className="text-lg px-8">
              Sign In
            </Button>
          </Link>
        </div>
      </section>

      {/* Features Section */}
      <section className="container mx-auto px-4 py-16">
        <h2 className="text-3xl text-center mb-12">
          Powerful Integration Features
        </h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card>
            <CardHeader>
              <RefreshCw className="size-10 text-blue-600 mb-2" />
              <CardTitle>Order Sync</CardTitle>
              <CardDescription>
                Automatically sync orders from multiple sales channels to your fulfillment providers
              </CardDescription>
            </CardHeader>
          </Card>
          
          <Card>
            <CardHeader>
              <Package className="size-10 text-green-600 mb-2" />
              <CardTitle>Inventory Sync</CardTitle>
              <CardDescription>
                Keep inventory levels synchronized across all your platforms in real-time
              </CardDescription>
            </CardHeader>
          </Card>
          
          <Card>
            <CardHeader>
              <ShoppingBag className="size-10 text-purple-600 mb-2" />
              <CardTitle>Product Listing</CardTitle>
              <CardDescription>
                Manage and sync product listings across multiple sales channels effortlessly
              </CardDescription>
            </CardHeader>
          </Card>
          
          <Card>
            <CardHeader>
              <Truck className="size-10 text-orange-600 mb-2" />
              <CardTitle>Shipment Sync</CardTitle>
              <CardDescription>
                Track and sync shipment information automatically across all platforms
              </CardDescription>
            </CardHeader>
          </Card>
        </div>
      </section>

      {/* Integrations Section */}
      <section className="container mx-auto px-4 py-16">
        <h2 className="text-3xl text-center mb-12">
          Connect Your Favorite Platforms
        </h2>
        <div className="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
          <Card>
            <CardHeader>
              <CardTitle>Sales Channels</CardTitle>
              <CardDescription>
                Connect your online stores and marketplaces
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex flex-col gap-2">
                <div className="flex items-center gap-2 p-2 border rounded">
                  <ShoppingBag className="size-5 text-green-600" />
                  <span>Shopify</span>
                </div>
                <div className="text-sm text-gray-500">+ More channels coming soon</div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Fulfillment & 3PL</CardTitle>
              <CardDescription>
                Integrate with logistics and fulfillment providers
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex flex-col gap-2">
                <div className="flex items-center gap-2 p-2 border rounded">
                  <Truck className="size-5 text-orange-600" />
                  <span>Shiprocket</span>
                </div>
                <div className="text-sm text-gray-500">+ More providers coming soon</div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-blue-600 text-white py-16">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl mb-4">
            Ready to Streamline Your Operations?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Start integrating your platforms today with Integratez
          </p>
          <Link to="/signup">
            <Button size="lg" variant="secondary" className="text-lg px-8">
              Create Free Account
            </Button>
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t py-8">
        <div className="container mx-auto px-4 text-center text-gray-600">
          <p>&copy; 2025 Integratez. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
