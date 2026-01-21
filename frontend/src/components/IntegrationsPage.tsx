import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { ShoppingBag, Truck, CheckCircle2, Plus } from 'lucide-react';
import { useState } from 'react';
import IntegrationSettings from './IntegrationSettings';

export default function IntegrationsPage() {
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [selectedIntegration, setSelectedIntegration] = useState<any>(null);

  const connectedIntegrations = [
    {
      id: 1,
      name: 'Shopify',
      type: 'Sales Channel',
      icon: ShoppingBag,
      status: 'Connected',
      store: 'my-store.myshopify.com',
      color: 'text-green-600',
    },
    {
      id: 2,
      name: 'Shiprocket',
      type: '3PL / Fulfillment',
      icon: Truck,
      status: 'Connected',
      store: 'Account ID: SR12345',
      color: 'text-orange-600',
    },
  ];

  const availableIntegrations = [
    {
      id: 3,
      name: 'WooCommerce',
      type: 'Sales Channel',
      icon: ShoppingBag,
      description: 'Connect your WooCommerce store',
      comingSoon: true,
    },
    {
      id: 4,
      name: 'Amazon',
      type: 'Sales Channel',
      icon: ShoppingBag,
      description: 'Connect your Amazon seller account',
      comingSoon: true,
    },
    {
      id: 5,
      name: 'DHL',
      type: '3PL / Fulfillment',
      icon: Truck,
      description: 'Connect DHL for shipping',
      comingSoon: true,
    },
  ];

  const handleOpenSettings = (integration: any) => {
    setSelectedIntegration(integration);
    setSettingsOpen(true);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl mb-2">Integrations</h1>
        <p className="text-gray-600">Manage your connected platforms</p>
      </div>

      {/* Connected Integrations */}
      <div>
        <h2 className="text-xl mb-4">Connected Integrations</h2>
        <div className="grid gap-4 md:grid-cols-2">
          {connectedIntegrations.map((integration) => {
            const Icon = integration.icon;
            return (
              <Card key={integration.id}>
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div className="flex items-center gap-3">
                      <div className={`${integration.color}`}>
                        <Icon className="size-10" />
                      </div>
                      <div>
                        <CardTitle>{integration.name}</CardTitle>
                        <CardDescription>{integration.type}</CardDescription>
                      </div>
                    </div>
                    <Badge variant="secondary" className="flex items-center gap-1">
                      <CheckCircle2 className="size-3" />
                      {integration.status}
                    </Badge>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="text-sm text-gray-600">
                      {integration.store}
                    </div>
                    <div className="flex gap-2">
                      <Button 
                        variant="outline" 
                        size="sm" 
                        className="flex-1"
                        onClick={() => handleOpenSettings(integration)}
                      >
                        Settings
                      </Button>
                      <Button variant="outline" size="sm" className="flex-1">
                        Disconnect
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>

      {/* Available Integrations */}
      <div>
        <h2 className="text-xl mb-4">Available Integrations</h2>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {availableIntegrations.map((integration) => {
            const Icon = integration.icon;
            return (
              <Card key={integration.id} className="relative">
                {integration.comingSoon && (
                  <div className="absolute top-4 right-4">
                    <Badge variant="secondary">Coming Soon</Badge>
                  </div>
                )}
                <CardHeader>
                  <div className="flex items-center gap-3 mb-2">
                    <Icon className="size-8 text-gray-600" />
                    <div>
                      <CardTitle className="text-lg">{integration.name}</CardTitle>
                      <CardDescription className="text-xs">{integration.type}</CardDescription>
                    </div>
                  </div>
                  <CardDescription>{integration.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <Button 
                    variant="outline" 
                    size="sm" 
                    className="w-full"
                    disabled={integration.comingSoon}
                  >
                    <Plus className="size-4 mr-2" />
                    {integration.comingSoon ? 'Coming Soon' : 'Connect'}
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>

      <IntegrationSettings
        open={settingsOpen}
        onOpenChange={setSettingsOpen}
        integration={selectedIntegration}
      />
    </div>
  );
}