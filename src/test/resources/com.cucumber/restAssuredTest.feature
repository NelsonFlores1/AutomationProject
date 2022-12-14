Feature: Create, Get, Update, Delete and Get token of an Item through API

Scenario: Creating a new Item
  When Creating an item with the name "Test-item"
  When Getting the item by ID
  When Updating the item with the name "Test-item Updated"
  When Deleting the item "Test-item Updated"
  When Getting the authentication token for the item