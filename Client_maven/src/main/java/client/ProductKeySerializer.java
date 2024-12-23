package client;

import Classes.Product;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ProductKeySerializer extends StdSerializer<Product> {
    public ProductKeySerializer() {
        super(Product.class);
    }

    @Override
    public void serialize(Product product, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeFieldName(product.toStringKey());
    }
}
