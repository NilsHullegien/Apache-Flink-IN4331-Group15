package types.Stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class StockFindSerializer implements Serializer<StockFind> {
	@Override
	public void configure(Map map, boolean b) {

	}

	@Override
	public byte[] serialize(String s, StockFind object) {
		byte[] retVal = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retVal = objectMapper.writeValueAsBytes(object);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return retVal;
	}

	@Override
	public void close() {

	}
}
