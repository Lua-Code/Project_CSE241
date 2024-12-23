package serverApplication;

import Classes.Product;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class ProductKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext context) throws IOException {
        return Product.fromStringKey(key);
    }
}
