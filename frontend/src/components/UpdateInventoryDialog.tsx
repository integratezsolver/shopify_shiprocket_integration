import { useState } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Badge } from './ui/badge';
import { Package, AlertCircle } from 'lucide-react';

interface UpdateInventoryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  product: {
    id: number;
    name: string;
    sku: string;
    stock: number;
    status: string;
  } | null;
  onUpdate: (productId: number, newStock: number) => void;
}

export default function UpdateInventoryDialog({ 
  open, 
  onOpenChange, 
  product,
  onUpdate 
}: UpdateInventoryDialogProps) {
  const [newStock, setNewStock] = useState('');
  const [adjustment, setAdjustment] = useState('');

  if (!product) return null;

  const handleUpdate = () => {
    const stockValue = newStock ? parseInt(newStock) : product.stock + parseInt(adjustment || '0');
    onUpdate(product.id, stockValue);
    onOpenChange(false);
    setNewStock('');
    setAdjustment('');
  };

  const calculateNewStock = () => {
    if (newStock) return parseInt(newStock);
    if (adjustment) return product.stock + parseInt(adjustment);
    return product.stock;
  };

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

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Update Inventory</DialogTitle>
          <DialogDescription>
            Adjust inventory levels for this product
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* Product Info */}
          <div className="bg-gray-50 p-4 rounded-lg space-y-2">
            <div className="flex items-center gap-2">
              <Package className="size-5 text-gray-600" />
              <div className="flex-1">
                <div>{product.name}</div>
                <div className="text-sm text-gray-600">SKU: {product.sku}</div>
              </div>
              <Badge className={getStatusColor(product.status)} variant="secondary">
                {product.status}
              </Badge>
            </div>
            <div className="text-sm">
              <span className="text-gray-600">Current Stock: </span>
              <span className="font-medium">{product.stock} units</span>
            </div>
          </div>

          {/* Update Options */}
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="new-stock">Set New Stock Level</Label>
              <Input
                id="new-stock"
                type="number"
                placeholder="Enter new stock quantity"
                value={newStock}
                onChange={(e) => {
                  setNewStock(e.target.value);
                  setAdjustment('');
                }}
                min="0"
              />
            </div>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-white px-2 text-gray-500">Or</span>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="adjustment">Adjust Stock (+ or -)</Label>
              <Input
                id="adjustment"
                type="number"
                placeholder="e.g., +50 or -10"
                value={adjustment}
                onChange={(e) => {
                  setAdjustment(e.target.value);
                  setNewStock('');
                }}
              />
              <p className="text-xs text-gray-600">
                Use + to add stock or - to reduce stock
              </p>
            </div>
          </div>

          {/* Preview */}
          {(newStock || adjustment) && (
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <div className="flex items-start gap-2">
                <AlertCircle className="size-5 text-blue-600 flex-shrink-0 mt-0.5" />
                <div className="text-sm">
                  <p className="text-blue-900 mb-1">Preview:</p>
                  <p className="text-blue-800">
                    Stock will be updated from <span className="font-medium">{product.stock}</span> to{' '}
                    <span className="font-medium">{calculateNewStock()}</span> units
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>

        <DialogFooter>
          <Button 
            variant="outline" 
            onClick={() => {
              onOpenChange(false);
              setNewStock('');
              setAdjustment('');
            }}
          >
            Cancel
          </Button>
          <Button 
            onClick={handleUpdate}
            disabled={!newStock && !adjustment}
          >
            Update Inventory
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
