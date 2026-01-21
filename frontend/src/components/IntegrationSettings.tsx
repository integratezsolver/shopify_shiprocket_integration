import { useState } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Switch } from './ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { ShoppingBag, Truck, Save, Trash2 } from 'lucide-react';

interface IntegrationSettingsProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  integration: {
    id: number;
    name: string;
    type: string;
    icon: any;
    status: string;
    store: string;
    color: string;
  } | null;
}

export default function IntegrationSettings({ open, onOpenChange, integration }: IntegrationSettingsProps) {
  if (!integration) return null;

  const isShopify = integration.name === 'Shopify';
  const isShiprocket = integration.name === 'Shiprocket';

  // Shopify settings state
  const [shopifySettings, setShopifySettings] = useState({
    storeName: 'my-store',
    autoSyncOrders: true,
    autoSyncInventory: true,
    syncInterval: '15',
    importProducts: true,
    webhooksEnabled: true,
  });

  // Shiprocket settings state
  const [shiprocketSettings, setShiprocketSettings] = useState({
    accountId: 'SR12345',
    autoCreateShipments: true,
    autoUpdateTracking: true,
    defaultCourier: 'automatic',
    pickupLocation: 'Warehouse-1',
    notifyCustomers: true,
  });

  const handleSaveSettings = () => {
    console.log('Saving settings...');
    // In production, this would save to your backend
    onOpenChange(false);
  };

  const handleDisconnect = () => {
    if (confirm(`Are you sure you want to disconnect ${integration.name}?`)) {
      console.log('Disconnecting integration...');
      // In production, this would disconnect the integration
      onOpenChange(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <div className="flex items-center gap-3">
            {integration.name === 'Shopify' ? (
              <ShoppingBag className={`size-8 ${integration.color}`} />
            ) : (
              <Truck className={`size-8 ${integration.color}`} />
            )}
            <div>
              <DialogTitle>{integration.name} Settings</DialogTitle>
              <DialogDescription>
                Configure your {integration.name} integration
              </DialogDescription>
            </div>
          </div>
        </DialogHeader>

        <Tabs defaultValue="general" className="mt-4">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="general">General</TabsTrigger>
            <TabsTrigger value="sync">Sync Settings</TabsTrigger>
            <TabsTrigger value="advanced">Advanced</TabsTrigger>
          </TabsList>

          <TabsContent value="general" className="space-y-4">
            {isShopify && (
              <>
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Store Information</CardTitle>
                    <CardDescription>Basic store details</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="store-name">Store Name</Label>
                      <div className="flex gap-2">
                        <Input
                          id="store-name"
                          value={shopifySettings.storeName}
                          onChange={(e) => setShopifySettings({ ...shopifySettings, storeName: e.target.value })}
                        />
                        <span className="flex items-center text-gray-600">.myshopify.com</span>
                      </div>
                    </div>

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Connection Status</Label>
                        <p className="text-sm text-gray-600">
                          Your store is currently connected
                        </p>
                      </div>
                      <Badge variant="secondary" className="flex items-center gap-1">
                        <div className="size-2 bg-green-600 rounded-full"></div>
                        Connected
                      </Badge>
                    </div>
                  </CardContent>
                </Card>
              </>
            )}

            {isShiprocket && (
              <>
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Account Information</CardTitle>
                    <CardDescription>Basic account details</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="account-id">Account ID</Label>
                      <Input
                        id="account-id"
                        value={shiprocketSettings.accountId}
                        onChange={(e) => setShiprocketSettings({ ...shiprocketSettings, accountId: e.target.value })}
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="pickup-location">Default Pickup Location</Label>
                      <Input
                        id="pickup-location"
                        value={shiprocketSettings.pickupLocation}
                        onChange={(e) => setShiprocketSettings({ ...shiprocketSettings, pickupLocation: e.target.value })}
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Connection Status</Label>
                        <p className="text-sm text-gray-600">
                          Your account is currently connected
                        </p>
                      </div>
                      <Badge variant="secondary" className="flex items-center gap-1">
                        <div className="size-2 bg-green-600 rounded-full"></div>
                        Connected
                      </Badge>
                    </div>
                  </CardContent>
                </Card>
              </>
            )}
          </TabsContent>

          <TabsContent value="sync" className="space-y-4">
            {isShopify && (
              <>
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Sync Preferences</CardTitle>
                    <CardDescription>Configure what and how often to sync</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Auto-sync Orders</Label>
                        <p className="text-sm text-gray-600">
                          Automatically sync new orders from Shopify
                        </p>
                      </div>
                      <Switch
                        checked={shopifySettings.autoSyncOrders}
                        onCheckedChange={(checked) => 
                          setShopifySettings({ ...shopifySettings, autoSyncOrders: checked })
                        }
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Auto-sync Inventory</Label>
                        <p className="text-sm text-gray-600">
                          Keep inventory levels synchronized
                        </p>
                      </div>
                      <Switch
                        checked={shopifySettings.autoSyncInventory}
                        onCheckedChange={(checked) => 
                          setShopifySettings({ ...shopifySettings, autoSyncInventory: checked })
                        }
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Import Products</Label>
                        <p className="text-sm text-gray-600">
                          Automatically import new products
                        </p>
                      </div>
                      <Switch
                        checked={shopifySettings.importProducts}
                        onCheckedChange={(checked) => 
                          setShopifySettings({ ...shopifySettings, importProducts: checked })
                        }
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="sync-interval">Sync Interval (minutes)</Label>
                      <Input
                        id="sync-interval"
                        type="number"
                        value={shopifySettings.syncInterval}
                        onChange={(e) => setShopifySettings({ ...shopifySettings, syncInterval: e.target.value })}
                      />
                      <p className="text-xs text-gray-600">
                        How often to check for updates (minimum 5 minutes)
                      </p>
                    </div>
                  </CardContent>
                </Card>
              </>
            )}

            {isShiprocket && (
              <>
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Fulfillment Settings</CardTitle>
                    <CardDescription>Configure shipment automation</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Auto-create Shipments</Label>
                        <p className="text-sm text-gray-600">
                          Automatically create shipments for new orders
                        </p>
                      </div>
                      <Switch
                        checked={shiprocketSettings.autoCreateShipments}
                        onCheckedChange={(checked) => 
                          setShiprocketSettings({ ...shiprocketSettings, autoCreateShipments: checked })
                        }
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Auto-update Tracking</Label>
                        <p className="text-sm text-gray-600">
                          Sync tracking information automatically
                        </p>
                      </div>
                      <Switch
                        checked={shiprocketSettings.autoUpdateTracking}
                        onCheckedChange={(checked) => 
                          setShiprocketSettings({ ...shiprocketSettings, autoUpdateTracking: checked })
                        }
                      />
                    </div>

                    <div className="flex items-center justify-between">
                      <div className="space-y-0.5">
                        <Label>Notify Customers</Label>
                        <p className="text-sm text-gray-600">
                          Send tracking updates to customers
                        </p>
                      </div>
                      <Switch
                        checked={shiprocketSettings.notifyCustomers}
                        onCheckedChange={(checked) => 
                          setShiprocketSettings({ ...shiprocketSettings, notifyCustomers: checked })
                        }
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="default-courier">Default Courier Selection</Label>
                      <select
                        id="default-courier"
                        value={shiprocketSettings.defaultCourier}
                        onChange={(e) => setShiprocketSettings({ ...shiprocketSettings, defaultCourier: e.target.value })}
                        className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                      >
                        <option value="automatic">Automatic (Best Rate)</option>
                        <option value="fastest">Fastest Delivery</option>
                        <option value="dhl">DHL</option>
                        <option value="bluedart">Blue Dart</option>
                        <option value="delhivery">Delhivery</option>
                      </select>
                    </div>
                  </CardContent>
                </Card>
              </>
            )}
          </TabsContent>

          <TabsContent value="advanced" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Advanced Settings</CardTitle>
                <CardDescription>Advanced configuration options</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {isShopify && (
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Webhooks</Label>
                      <p className="text-sm text-gray-600">
                        Enable real-time webhooks for instant updates
                      </p>
                    </div>
                    <Switch
                      checked={shopifySettings.webhooksEnabled}
                      onCheckedChange={(checked) => 
                        setShopifySettings({ ...shopifySettings, webhooksEnabled: checked })
                      }
                    />
                  </div>
                )}

                <div className="pt-4 border-t">
                  <Button 
                    variant="destructive" 
                    className="w-full"
                    onClick={handleDisconnect}
                  >
                    <Trash2 className="size-4 mr-2" />
                    Disconnect {integration.name}
                  </Button>
                  <p className="text-xs text-gray-600 mt-2 text-center">
                    This will remove the integration and stop all syncing
                  </p>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>

        <div className="flex justify-end gap-2 pt-4 border-t">
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={handleSaveSettings}>
            <Save className="size-4 mr-2" />
            Save Changes
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
