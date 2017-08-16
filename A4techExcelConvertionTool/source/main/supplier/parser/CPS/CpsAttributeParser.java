package parser.CPS;

import com.a4tech.product.model.Product;
import com.a4tech.product.model.ProductConfigurations;

public class CpsAttributeParser {
  public Product keepExistingProduct(Product existingProduct){
	  Product newProduct = new Product();
	  ProductConfigurations oldConfig = existingProduct.getProductConfigurations();
	  ProductConfigurations newConfig = new ProductConfigurations();
	  
	  
	  return newProduct;
  }
}
