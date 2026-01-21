import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Badge } from './ui/badge';
import { Zap, ShoppingBag, Truck, CheckCircle2, ArrowRight } from 'lucide-react';

type OnboardingStep = 'sales-channel' | 'fulfillment' | 'complete';

export default function OnboardingPage() {
  const [step, setStep] = useState<OnboardingStep>('sales-channel');
  const [shopifyStore, setShopifyStore] = useState('');
  const [salesChannelConnected, setSalesChannelConnected] = useState(false);
  const [fulfillmentConnected, setFulfillmentConnected] = useState(false);
  const { completeOnboarding } = useAuth();
  const navigate = useNavigate();

  const handleConnectShopify = () => {
    if (!shopifyStore) return;
    
    // Simulate OAuth redirect to Shopify
    // In production, this would redirect to Shopify OAuth page
    setTimeout(() => {
      setSalesChannelConnected(true);
      setStep('fulfillment');
    }, 1000);
  };

  const handleConnectShiprocket = () => {
    // Simulate connecting to Shiprocket
    setTimeout(() => {
      setFulfillmentConnected(true);
      setStep('complete');
    }, 1000);
  };

  const handleComplete = () => {
    completeOnboarding();
    navigate('/dashboard');
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white p-4">
      <div className="container mx-auto max-w-4xl py-8">
        {/* Header */}
        <div className="flex items-center justify-center gap-2 mb-8">
          <Zap className="size-10 text-blue-600" />
          <span className="text-3xl">Integratez</span>
        </div>

        {/* Progress Steps */}
        <div className="flex items-center justify-center gap-4 mb-12">
          <div className="flex items-center gap-2">
            <div className={`flex items-center justify-center size-10 rounded-full ${
              salesChannelConnected ? 'bg-green-600 text-white' : 'bg-blue-600 text-white'
            }`}>
              {salesChannelConnected ? <CheckCircle2 className="size-5" /> : '1'}
            </div>
            <span>Sales Channel</span>
          </div>
          
          <div className="w-12 h-0.5 bg-gray-300"></div>
          
          <div className="flex items-center gap-2">
            <div className={`flex items-center justify-center size-10 rounded-full ${
              fulfillmentConnected ? 'bg-green-600 text-white' : 
              step === 'fulfillment' ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              {fulfillmentConnected ? <CheckCircle2 className="size-5" /> : '2'}
            </div>
            <span>Fulfillment</span>
          </div>
          
          <div className="w-12 h-0.5 bg-gray-300"></div>
          
          <div className="flex items-center gap-2">
            <div className={`flex items-center justify-center size-10 rounded-full ${
              step === 'complete' ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}>
              {step === 'complete' ? <CheckCircle2 className="size-5" /> : '3'}
            </div>
            <span>Complete</span>
          </div>
        </div>

        {/* Sales Channel Step */}
        {step === 'sales-channel' && (
          <Card className="max-w-2xl mx-auto">
            <CardHeader className="text-center">
              <CardTitle>Connect Your Sales Channel</CardTitle>
              <CardDescription>
                Choose your primary sales channel to get started
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <Card className="border-2 hover:border-blue-600 transition-colors">
                  <CardContent className="p-6">
                    <div className="flex items-start justify-between mb-4">
                      <div className="flex items-center gap-3">
                        <ShoppingBag className="size-10 text-green-600" />
                        <div>
                          <h3 className="text-xl">Shopify</h3>
                          <p className="text-sm text-gray-600">Connect your Shopify store</p>
                        </div>
                      </div>
                      <Badge>Popular</Badge>
                    </div>
                    
                    <div className="space-y-4">
                      <div className="space-y-2">
                        <Label htmlFor="shopify-store">Store Name</Label>
                        <div className="flex gap-2">
                          <Input
                            id="shopify-store"
                            placeholder="your-store"
                            value={shopifyStore}
                            onChange={(e) => setShopifyStore(e.target.value)}
                          />
                          <span className="flex items-center text-gray-600">.myshopify.com</span>
                        </div>
                      </div>
                      
                      <Button 
                        className="w-full" 
                        onClick={handleConnectShopify}
                        disabled={!shopifyStore}
                      >
                        Connect Shopify
                        <ArrowRight className="size-4 ml-2" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>

                <div className="text-center text-sm text-gray-600">
                  More sales channels coming soon
                </div>
              </div>
            </CardContent>
          </Card>
        )}

        {/* Fulfillment Step */}
        {step === 'fulfillment' && (
          <Card className="max-w-2xl mx-auto">
            <CardHeader className="text-center">
              <CardTitle>Connect Your 3PL / Fulfillment</CardTitle>
              <CardDescription>
                Connect your fulfillment provider to start syncing orders
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <Card className="border-2 hover:border-blue-600 transition-colors">
                  <CardContent className="p-6">
                    <div className="flex items-start justify-between mb-4">
                      <div className="flex items-center gap-3">
                        <Truck className="size-10 text-orange-600" />
                        <div>
                          <h3 className="text-xl">Shiprocket</h3>
                          <p className="text-sm text-gray-600">Connect your Shiprocket account</p>
                        </div>
                      </div>
                      <Badge>Recommended</Badge>
                    </div>
                    
                    <Button 
                      className="w-full" 
                      onClick={handleConnectShiprocket}
                    >
                      Connect Shiprocket
                      <ArrowRight className="size-4 ml-2" />
                    </Button>
                  </CardContent>
                </Card>

                <div className="text-center">
                  <Button 
                    variant="outline" 
                    onClick={() => setStep('complete')}
                  >
                    Skip for now
                  </Button>
                </div>

                <div className="text-center text-sm text-gray-600">
                  More fulfillment providers coming soon
                </div>
              </div>
            </CardContent>
          </Card>
        )}

        {/* Complete Step */}
        {step === 'complete' && (
          <Card className="max-w-2xl mx-auto">
            <CardHeader className="text-center">
              <div className="flex justify-center mb-4">
                <CheckCircle2 className="size-16 text-green-600" />
              </div>
              <CardTitle>You're All Set!</CardTitle>
              <CardDescription>
                Your integrations are connected and ready to use
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                  <div className="flex items-center gap-3 mb-2">
                    <CheckCircle2 className="size-5 text-green-600" />
                    <span>Shopify connected</span>
                  </div>
                  {fulfillmentConnected && (
                    <div className="flex items-center gap-3">
                      <CheckCircle2 className="size-5 text-green-600" />
                      <span>Shiprocket connected</span>
                    </div>
                  )}
                </div>

                <div className="space-y-2">
                  <p className="text-sm text-gray-600">
                    Your dashboard is ready with all your order and inventory data. 
                    Start managing your integrations now!
                  </p>
                </div>

                <Button className="w-full" size="lg" onClick={handleComplete}>
                  Go to Dashboard
                  <ArrowRight className="size-4 ml-2" />
                </Button>
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
