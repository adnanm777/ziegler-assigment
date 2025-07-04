# ziegler-assigment

# E-Commerce Automation Testing Project

This project is a TestNG-based automation suite for testing major functionalities of an e-commerce website:  
[https://automationteststore.com](https://automationteststore.com)

---

## 🔧 Tech Stack

- Java 11  
- Maven  
- Selenium WebDriver (v4.18.1)  
- TestNG (v7.9.0)  
- Apache POI (v5.2.5) for Excel (.xlsx) support  
- WebDriverManager (v5.8.0)  

---

## 📁 Project Structure

ecommerce-automation/
│
├── src/
│ └── test/
│ └── java/
│ └── newproject/
│ └── ECommerceTest.java
│
├── testdata.xlsx # Test data file (Excel)
├── pom.xml # Maven configuration
├── report.txt # Execution logs
├── screenshots/ # Failure screenshots
└── README.md




---

## ✅ Features Covered

1. **Homepage Category Test**  
   - Navigate to homepage  
   - List available product categories  
   - Open a random category and validate products

2. **Add Products to Cart**  
   - Adds 2 random products to the cart  
   - Validates cart item count

3. **Validate Cart & Checkout**  
   - Opens cart  
   - Validates products from session memory  
   - Reads user data from Excel and fills checkout form

4. **Negative Registration Test**  
   - Submits incomplete form  
   - Captures and logs validation errors (with screenshots)

5. **Report Generation**  
   - Writes logs and product info to `report.txt`  
   - Captures screenshots into `screenshots/` folder

---

## 📘 Excel Data Format (`testdata.xlsx`)

| FirstName | LastName | Email           | Telephone  | Password |
|-----------|----------|------------------|------------|----------|
| Mohammad  | Adnan      | adnanmasaischool.com    | 8793182354 | Adnan@123 |

