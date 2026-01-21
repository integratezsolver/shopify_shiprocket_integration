package com.integratez.platform.modules.shopify.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ShopifyResponse {
    private Data data;

    @lombok.Data
    public static class Data {
        private Order order;
    }

    @lombok.Data
    public static class Order {
        private String id;
        private String name;
        private String createdAt;
        private String currencyCode;
        private String displayFulfillmentStatus;
        private String displayFinancialStatus;
        private Boolean fullyPaid;
        private String cancelReason;
        private String cancelledAt;

        private Customer customer;
        private PriceSet totalPriceSet;
        private PriceSet subtotalPriceSet;
        private PriceSet totalShippingPriceSet;
        private PriceSet totalTaxSet;
        private PriceSet totalDutiesSet;

        private LineItemConnection lineItems;
        private FulfillmentConnection fulfillments;

        private Address shippingAddress;
        private Address billingAddress;

        private TransactionConnection transactions;
        private RefundConnection refunds;

        private SuggestedRefund suggestedRefund;
    }

    @lombok.Data
    public static class Customer {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @lombok.Data
    public static class PriceSet {
        private MoneySet shopMoney;
    }

    @lombok.Data
    public static class MoneySet {
        private String amount;
        private String currencyCode;
    }

    @lombok.Data
    public static class LineItemConnection {
        private List<Edge<LineItem>> edges;
    }

    @lombok.Data
    public static class LineItem {
        private String id;
        private String name;
        private int quantity;
        private String sku;
        private String variantTitle;

        private PriceSet originalUnitPriceSet;
        private PriceSet discountedTotalSet;
        private List<Duty> duties;
    }

    @lombok.Data
    public static class Duty {
        private String id;
        private String harmonizedSystemCode;
        private String countryCodeOfOrigin;
        private PriceSet priceSet;
    }

    @lombok.Data
    public static class FulfillmentConnection {
        private List<Edge<Fulfillment>> edges;
    }

    @lombok.Data
    public static class Fulfillment {
        private String id;
        private String status;
        private List<TrackingInfo> trackingInfo;
    }

    @lombok.Data
    public static class TrackingInfo {
        private String number;
        private String url;
        private String company;
    }

    @lombok.Data
    public static class Address {
        private String name;
        private String address1;
        private String address2;
        private String city;
        private String province;
        private String country;
        private String zip;
        private String phone;
    }

    @lombok.Data
    public static class TransactionConnection {
        private List<Edge<Transaction>> edges;
    }

    @lombok.Data
    public static class Transaction {
        private String id;
        private String kind;
        private String status;
        private PriceSet amountSet;
    }

    @lombok.Data
    public static class RefundConnection {
        private List<Edge<Refund>> edges;
    }

    @lombok.Data
    public static class Refund {
        private String id;
        private String createdAt;
        private PriceSet totalRefundedSet;
    }

    @lombok.Data
    public static class SuggestedRefund {
        private List<RefundDuty> refundDuties;
        private PriceSet totalDutiesSet;
    }

    @lombok.Data
    public static class RefundDuty {
        private PriceSet amountSet;
        private Duty originalDuty;
    }

    @lombok.Data
    public static class Edge<T> {
        private T node;
    }
}
