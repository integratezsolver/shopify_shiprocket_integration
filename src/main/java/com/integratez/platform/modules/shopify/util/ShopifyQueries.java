package com.integratez.platform.modules.shopify.util;public class ShopifyQueries {

    public static final String GET_ORDER_BY_ID = """
    query GetOrder($id: ID!) {
      order(id: $id) {
        id
        name
        createdAt
        currencyCode
        displayFulfillmentStatus
        displayFinancialStatus
        fullyPaid
        cancelReason
        cancelledAt

        customer {
          id
          firstName
          lastName
          email
          phone
        }

        totalPriceSet { shopMoney { amount currencyCode } }
        subtotalPriceSet { shopMoney { amount currencyCode } }
        totalShippingPriceSet { shopMoney { amount currencyCode } }
        totalTaxSet { shopMoney { amount currencyCode } }

        lineItems(first: 50) {
          nodes {
            id
            name
            quantity
            sku
            variantTitle
            discountedTotalSet { shopMoney { amount currencyCode } }
            originalTotalSet { shopMoney { amount currencyCode } }
          }
        }

        fulfillments(first: 20) {
          id
          status
          trackingInfo {
            number
            url
            company
          }
        }

        shippingAddress {
          name
          address1
          address2
          city
          province
          country
          zip
          phone
        }

        billingAddress {
          name
          address1
          address2
          city
          province
          country
          zip
          phone
        }

        transactions(first: 20) {
          id
          kind
          status
          amountSet {
            shopMoney { amount currencyCode }
          }
        }

        refunds(first: 20) {
          id
          createdAt
          totalRefundedSet { shopMoney { amount currencyCode } }
        }
      }
    }
    """;







    public static final String GET_ALL_PRODUCTS = """
    query GetAllProducts($first: Int!, $after: String) {
      products(first: $first, after: $after) {
        pageInfo {
          hasNextPage
          endCursor
        }
        edges {
          node {
            id
            title
            description
            productType
            status
            tags
            variants(first: 50) {
              edges {
                node {
                  id
                  title
                  sku
                  price
                  inventoryQuantity
                }
              }
            }
          }
        }
      }
    }
""";



        public static final String GET_ORDERS_BY_DATE = """
        query GetOrdersByDate(
          $query: String!,
          $first: Int!,
          $after: String
        ) {
          orders(first: $first, query: $query, after: $after) {
            pageInfo {
              hasNextPage
              endCursor
            }
            nodes {
              id
              name
              createdAt
              displayFulfillmentStatus
              displayFinancialStatus
              fullyPaid
              currencyCode

              customer {
                id
                firstName
                lastName
                email
                phone
              }

              shippingAddress {
                name
                address1
                address2
                city
                province
                country
                zip
                phone
              }

              billingAddress {
                name
                address1
                address2
                city
                province
                country
                zip
                phone
              }

              lineItems(first: 50) {
                nodes {
                  id
                  name
                  quantity
                  sku
                  variantTitle
                  discountedTotalSet { shopMoney { amount currencyCode } }
                }
              }

              fulfillments(first: 20) {
                id
                status
              }

              transactions(first: 20) {
                id
                kind
                status
              }

              refunds(first: 20) {
                id
                createdAt
              }
            }
          }
        }
    """;






}