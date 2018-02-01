import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SimpExec {

	public static void main(String[] args) throws ExecutionException, InterruptedException{
		ExecutorService threadPool = Executors.newFixedThreadPool(8);
		Counter counter = new Counter();

		long start = System.nanoTime();

		List<Future<Double>> futures = new ArrayList<>();
		for (int i = 0; i < 400; i++) {
			final int j = i;
			futures.add(
					CompletableFuture.supplyAsync(
							() -> counter.count(j),
							threadPool
					));
		}

		double value = 0;
		for (Future<Double> future : futures) {
			value += future.get();
		}

		System.out.println((System.nanoTime() - start) / (1000_000_000) + " " + value);
	}
}

class Counter {

	public Double count(double a) {
		for (int i = 0; i < 1000000; i++) {
			a = a + Math.tan(a);
		}

		return a;
	}
}
