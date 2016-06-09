package com.a4tech.v2.core.model;

public class PayloadSize {

    private String size;
    private String customerOrderCode;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCustomerOrderCode() {
        return customerOrderCode;
    }

    public void setCustomerOrderCode(String customerOrderCode) {
        this.customerOrderCode = customerOrderCode;
    }
    
    public String toString() {
        return "{\"Size\": \"" + this.getSize() + "\", \"CustomerOrderCode\":\"" + this.getCustomerOrderCode() + "\"}";
    }

}

/*

{
    "ProductConfigurations": {
        "Sizes": {
            "Volume": {
                "Values": [
                    {
                        "Value": [
                            {
                                "Value": "15",
                                "Unit": "kg"
                            }
                        ],
                        "CustomerOrderCode": "11KG-65465"
                    },
                    {
                        "Value": [
                            {
                                "Value": "12",
                                "Unit": "lbs"
                            }
                        ],
                        "CustomerOrderCode": "12LBS-65465"
                    }
                ]
            }
        }
    }
}
*/