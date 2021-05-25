import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Deployment of FLINK: https://ci.apache.org/projects/flink/flink-docs-master/docs/deployment/resource-providers/standalone/docker/
 * Tutorial: https://www.baeldung.com/apache-flink
 */
public class Main {

	public static void main(String[] args) {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		//TODO this dataset will be filled from the stream
		DataStream<Integer> amounts = env.fromElements(1, 29, 40, 50);
	}
}
