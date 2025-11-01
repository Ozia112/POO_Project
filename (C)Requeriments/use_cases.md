# Use Cases

## Use Case 1 - Manage Prices and Rental Rates and Products
**Primary Actor**: Administrator
- **Description**: The administrator manages the rental rates and product prices within the system.
- **Flow of Events**:
  1. The **administrator** logs into the system and accesses the "Manage Prices" section.
  2. The **administrator** adjusts the rental rates and product prices.
  3. The system updates the rates and prices in real-time.
- **Preconditions**: The administrator is authenticated in the system.
- **Postconditions**: The rental rates and product prices are updated in the system.

## Use Case 2 - Generate and View Sales and Revenue Reports
**Primary Actor**: Administrator
- **Description**: The administrator can generate and view reports related to sales and revenue.
- **Flow of Events**:
  1. The **administrator** selects the "Generate Reports" option.
  2. The **administrator** chooses the type of report (sales, revenue).
  3. The system generates the report in Excel or PDF format.
- **Preconditions**: The administrator has access to the reporting system.
- **Postconditions**: The report is generated and available for download.

## Use Case 3 - Manage Product Catalog
**Primary Actor**: Administrator
- **Description**: The administrator manages the product catalog, adding or removing products for sale.
- **Flow of Events**:
  1. The **administrator** accesses the "Product Catalog" section.
  2. The **administrator** adds new products or removes existing products.
  3. The system updates the product catalog accordingly.
- **Preconditions**: The administrator is authenticated and has access to the catalog.
- **Postconditions**: The product catalog is updated.

## Use Case 4 - Register Locker Rentals with Details (Customer, Duration, Cost)
**Primary Actor**: Administrator
- **Description**: The administrator registers a locker rental, associating it with customer details, rental duration, and cost.
- **Flow of Events**:
  1. The **administrator** selects the "Register Locker Rental" option.
  2. The **administrator** enters the customer's details, rental duration, and cost.
  3. The system calculates the total cost and confirms the rental.
- **Preconditions**: The administrator is logged in.
- **Postconditions**: A rental record is created with customer details, rental duration, and cost.

## Use Case 5 - End Rental and Mark Locker as Available
**Primary Actor**: Administrator
- **Description**: The administrator ends a rental and marks the locker as available for the next customer.
- **Flow of Events**:
  1. The **administrator** selects the option to "End Rental".
  2. The system confirms the rental end and updates the locker status as available.
- **Preconditions**: The rental must be active.
- **Postconditions**: The rental is ended, and the locker is marked as available.

## Use Case 6 - Cancel Rental
**Primary Actor**: Administrator
- **Description**: The administrator can cancel an active rental and free the locker for another customer.
- **Flow of Events**:
  1. The **administrator** selects the "Cancel Rental" option.
  2. The system verifies that the rental is active and processes the cancellation.
- **Preconditions**: The rental must be active.
- **Postconditions**: The rental is canceled, and the locker is freed.

## Use Case 7 - Apply Promotional Discount to Rental
**Primary Actor**: Administrator
- **Description**: The administrator applies a promotional discount to a rental.
- **Flow of Events**:
  1. The **administrator** selects the rental and applies a discount.
  2. The system updates the rental cost with the discount applied.
- **Preconditions**: The administrator has access to apply discounts.
- **Postconditions**: The rental cost is updated with the discount.

## Use Case 8 - Record Product Sales
**Primary Actor**: Administrator
- **Description**: The administrator records the sale of products, linking them to customers with locker rentals.
- **Flow of Events**:
  1. The **administrator** registers the sale of products.
  2. The system links the sale to the customer's locker rental.
- **Preconditions**: The product is available in the catalog.
- **Postconditions**: The sale is recorded, and the inventory is updated.

## Use Case 9 - Link Product Sales to Customers with Locker Rentals
**Primary Actor**: Administrator
- **Description**: The administrator links product sales to customers who have rented a locker.
- **Flow of Events**:
  1. The **administrator** selects a product sale and links it to a customer.
  2. The system ensures the customer has an active locker rental before linking the sale.
- **Preconditions**: The product sale is recorded.
- **Postconditions**: The sale is linked to the customer with a locker rental.

## Use Case 10 - Apply Discount to Product Sales
**Primary Actor**: Administrator
- **Description**: The administrator applies a discount to a product sale.
- **Flow of Events**:
  1. The **administrator** applies a discount to the selected products.
  2. The system updates the total price with the discount.
- **Preconditions**: The administrator has access to apply discounts.
- **Postconditions**: The total price of the product sale is updated with the discount applied.

