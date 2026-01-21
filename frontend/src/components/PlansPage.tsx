import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Check, Zap } from 'lucide-react';

export default function PlansPage() {
  const plans = [
    {
      name: 'Free',
      price: '$0',
      period: 'forever',
      description: 'Perfect for testing and small businesses',
      current: true,
      features: [
        'Up to 50 orders per month',
        '1 Sales channel',
        '1 Fulfillment provider',
        'Basic sync (every 6 hours)',
        'Email support',
      ],
    },
    {
      name: 'Starter',
      price: '$29',
      period: 'per month',
      description: 'For growing businesses',
      current: false,
      popular: true,
      features: [
        'Up to 500 orders per month',
        '2 Sales channels',
        '2 Fulfillment providers',
        'Fast sync (every hour)',
        'Priority email support',
        'Basic analytics',
      ],
    },
    {
      name: 'Professional',
      price: '$79',
      period: 'per month',
      description: 'For established businesses',
      current: false,
      features: [
        'Up to 2,000 orders per month',
        'Unlimited sales channels',
        'Unlimited fulfillment providers',
        'Real-time sync',
        '24/7 priority support',
        'Advanced analytics',
        'Custom integrations',
        'API access',
      ],
    },
    {
      name: 'Enterprise',
      price: 'Custom',
      period: '',
      description: 'For large-scale operations',
      current: false,
      features: [
        'Unlimited orders',
        'Unlimited sales channels',
        'Unlimited fulfillment providers',
        'Real-time sync',
        'Dedicated account manager',
        'Custom analytics',
        'Custom integrations',
        'API access',
        'SLA guarantee',
        'White-label options',
      ],
    },
  ];

  return (
    <div className="space-y-6">
      <div className="text-center">
        <h1 className="text-3xl mb-2">Subscription Plans</h1>
        <p className="text-gray-600">Choose the perfect plan for your business</p>
      </div>

      {/* Current Plan Banner */}
      <Card className="bg-blue-50 border-blue-200">
        <CardContent className="pt-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Zap className="size-8 text-blue-600" />
              <div>
                <h3 className="text-lg">Current Plan: Free</h3>
                <p className="text-sm text-gray-600">You're currently on the Free plan</p>
              </div>
            </div>
            <div className="text-right">
              <div className="text-sm text-gray-600">Monthly orders used</div>
              <div className="text-2xl">12 / 50</div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Plans Grid */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {plans.map((plan) => (
          <Card
            key={plan.name}
            className={`relative ${
              plan.popular ? 'border-blue-600 border-2' : ''
            }`}
          >
            {plan.popular && (
              <div className="absolute -top-3 left-1/2 transform -translate-x-1/2">
                <Badge className="bg-blue-600">Most Popular</Badge>
              </div>
            )}
            
            <CardHeader className="text-center pb-4">
              <CardTitle className="text-2xl">{plan.name}</CardTitle>
              <CardDescription>{plan.description}</CardDescription>
              <div className="mt-4">
                <span className="text-4xl">{plan.price}</span>
                {plan.period && <span className="text-gray-600"> / {plan.period}</span>}
              </div>
            </CardHeader>
            
            <CardContent className="space-y-4">
              <ul className="space-y-3">
                {plan.features.map((feature, index) => (
                  <li key={index} className="flex items-start gap-2">
                    <Check className="size-5 text-green-600 flex-shrink-0 mt-0.5" />
                    <span className="text-sm">{feature}</span>
                  </li>
                ))}
              </ul>
              
              {plan.current ? (
                <Button variant="outline" className="w-full" disabled>
                  Current Plan
                </Button>
              ) : plan.name === 'Enterprise' ? (
                <Button variant="outline" className="w-full">
                  Contact Sales
                </Button>
              ) : (
                <Button className="w-full">
                  Upgrade to {plan.name}
                </Button>
              )}
            </CardContent>
          </Card>
        ))}
      </div>

      {/* FAQ Section */}
      <Card>
        <CardHeader>
          <CardTitle>Frequently Asked Questions</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <h4 className="mb-2">Can I change my plan at any time?</h4>
            <p className="text-sm text-gray-600">
              Yes, you can upgrade or downgrade your plan at any time. Changes take effect immediately.
            </p>
          </div>
          <div>
            <h4 className="mb-2">What happens if I exceed my order limit?</h4>
            <p className="text-sm text-gray-600">
              You'll be notified when you're approaching your limit. You can upgrade your plan to continue syncing orders.
            </p>
          </div>
          <div>
            <h4 className="mb-2">Is there a contract or commitment?</h4>
            <p className="text-sm text-gray-600">
              No, all plans are month-to-month with no long-term commitment. Cancel anytime.
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
