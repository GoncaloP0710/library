package bftsmart.tests.normal;

import controller.IBenchmarkStrategy;
import controller.IWorkerStatusListener;
import controller.WorkerHandler;
import worker.IProcessingResult;
import worker.ProcessInformation;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nuria
 */
public class CounterTestStrategy implements IBenchmarkStrategy, IWorkerStatusListener {
	private final Logger logger = LoggerFactory.getLogger("benchmarking");
	private String clientCommand;
	private String serverCommand;
	private final Set<Integer> serverWorkersIds;
	private final Set<Integer> clientWorkersIds;
	private final Lock lock;
	private final Condition sleepCondition;
	private CountDownLatch serversReadyCounter;
	private CountDownLatch clientsReadyCounter;
	private final AtomicBoolean error;

	private int nLoadRequests;
	private int increment;

	public CounterTestStrategy() {
		increment = 5;
		nLoadRequests = 1_000;
		this.clientCommand =  "java -Djava.security.properties=./config/java" +
				".security -Dlogback.configurationFile=./config/logback.xml -cp lib/* " +
				"bftsmart.tests.normal.CounterTestClient " + "1000 " + nLoadRequests + " " + increment;
		this.serverCommand = "java -Djava.security.properties=./config/java" +
				".security -Dlogback.configurationFile=./config/logback.xml -cp lib/* " +
				"bftsmart.demo.counter.CounterServer ";
		this.lock = new ReentrantLock(true);
		this.sleepCondition = lock.newCondition();
		this.serverWorkersIds = new HashSet<>();
		this.clientWorkersIds = new HashSet<>();
		this.error = new AtomicBoolean(false);
	}

	@Override
	public void executeBenchmark(WorkerHandler[] workerHandlers, Properties benchmarkParameters) {
		logger.info("Running counter strategy");
		String workingDirectory = benchmarkParameters.getProperty("experiment.working_directory");
		boolean isbft = Boolean.parseBoolean(benchmarkParameters.getProperty("experiment.bft"));
		int f = Integer.parseInt(benchmarkParameters.getProperty("experiment.f"));
		String hosts = "0 127.0.0.1 11000 11001\n" + 
					   "1 127.0.0.1 11010 11011\n" + 
					   "2 127.0.0.1 11020 11021\n" + 
					   "3 127.0.0.1 11030 11031\n" + 
					   "\n7001 127.0.0.1 11100";
		
		int nServers = (isbft ? 3*f+1 : 2*f+1);

		//Separate workers
		WorkerHandler[] serverWorkers = new WorkerHandler[nServers];
		WorkerHandler clientWorker = workerHandlers[workerHandlers.length - 1];
		System.arraycopy(workerHandlers, 0, serverWorkers, 0, nServers);
		Arrays.stream(serverWorkers).forEach(w -> serverWorkersIds.add(w.getWorkerId()));
		clientWorkersIds.add(clientWorker.getWorkerId());

		//Setup workers
		String setupInformation = String.format("%b\t%d\t"+hosts, isbft, f);
		Arrays.stream(workerHandlers).forEach(w -> w.setupWorker(setupInformation));

		try {
			lock.lock();
			//Start servers
			startServers(workingDirectory, serverWorkers);
			if (error.get())
				return;

			//Start client that continuously send requests
			startClients(workingDirectory, clientWorker);
			if (error.get())
				return;

			logger.info("Client sending requests...");

			logger.info("Waiting 10 seconds");
			sleepSeconds(10);
			
			//Saving monitoring
			logger.info("Writing monitoring on file");
			clientWorker.requestProcessingResult(); 
			serverWorkers[0].requestProcessingResult();
			serverWorkers[1].requestProcessingResult();
			sleepSeconds(5);


			logger.info("Counter test was a success");
		} catch (InterruptedException ignore) {
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
		
		finally {
			lock.unlock();
		}
	}

	private void startClients(String workingDirectory, WorkerHandler... clientWorkers) throws InterruptedException, IOException {
		System.out.println("Starting client...");
		clientsReadyCounter = new CountDownLatch(1);
		String currentWorkerDirectory = workingDirectory + "worker" + clientWorkers[0].getWorkerId()
				+ File.separator;
		ProcessInformation[] commands = new ProcessInformation[] {
				new ProcessInformation("sar -u -r -n DEV 1", currentWorkerDirectory),
				new ProcessInformation(clientCommand, currentWorkerDirectory),
		};

		clientWorkers[0].startWorker(50, commands, this);
		clientsReadyCounter.await();
	}

	private void startServers(String workingDirectory, WorkerHandler... serverWorkers) throws InterruptedException, IOException {
		System.out.println("Starting servers...");
		serversReadyCounter = new CountDownLatch(serverWorkers.length);
		for (int i = 0; i < serverWorkers.length; i++) {
			String command = serverCommand +i;
			WorkerHandler serverWorker = serverWorkers[i];
			String currentWorkerDirectory = workingDirectory + "worker" + serverWorker.getWorkerId()
					+ File.separator;
			ProcessInformation[] commands = commandList(i, command, currentWorkerDirectory);
			serverWorker.startWorker(0, commands, this);
			sleepSeconds(1);
		}
		serversReadyCounter.await();
	}

	private ProcessInformation[] commandList(int serverId, String command, String currentWorkerDirectory){

		ProcessInformation[] commands={
				new ProcessInformation(command, currentWorkerDirectory)
		};

		if(serverId<=1){
			commands = new ProcessInformation[] {
                    new ProcessInformation("sar -u -r -n DEV 1", currentWorkerDirectory),
                    new ProcessInformation(command, currentWorkerDirectory)
            };
			return commands;
		}

		return commands;
	}

	private void sleepSeconds(long duration) throws InterruptedException {
		lock.lock();
		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.schedule(() -> {
			lock.lock();
			sleepCondition.signal();
			lock.unlock();
		}, duration, TimeUnit.SECONDS);
		sleepCondition.await();
		scheduledExecutorService.shutdown();
		lock.unlock();
	}

	@Override
	public void onReady(int workerId) {
		if (serverWorkersIds.contains(workerId)) {
			serversReadyCounter.countDown();
		} else if (clientWorkersIds.contains(workerId)) {
			clientsReadyCounter.countDown();
		}
	}

	@Override
	public void onEnded(int workerId) {

	}

	@Override
	public void onError(int workerId, String errorMessage) {
		if (serverWorkersIds.contains(workerId)) {
			System.err.printf("Error in server worker %d\n", workerId);
			if (serversReadyCounter != null) {
				serversReadyCounter.countDown();
			}
		} else if (clientWorkersIds.contains(workerId)) {
			System.err.printf("Error in client worker %d\n", workerId);
			if (clientsReadyCounter != null)
				clientsReadyCounter.countDown();
		} else {
			System.out.printf("Error in unused worker %d\n", workerId);
		}
		error.set(true);
	}

	@Override
	public void onResult(int workerId, IProcessingResult processingResult) {
		Measurement measurement = (Measurement) processingResult;
		String[][] measurements = measurement.getMeasurements();

		if(!(measurements == null || measurements.length == 0 || measurements[0].length == 0)){
			storeResumedMeasurements(workerId, measurements);
		}
	}

	private void storeResumedMeasurements(int workerId, String[][] measurements) {
		String fileName = "monitoring_data" + String.valueOf(workerId) + ".csv";
		try (BufferedWriter resultFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
		resultFile.write("time(HH:mm:ss),user(%),system(%), mem_used(%)\n");
		for (int i = 0; i < measurements[0].length; i++) {

			String time = measurements[0][i];
			String user = measurements[1][i].replace(",",".");
			String sys = measurements[2][i].replace(",",".");

			String mem_used = measurements[3][i].replace(",",".");


			resultFile.write(String.format("%s,%s,%s,%s\n", time, user, sys, mem_used));
		}

		resultFile.write("time(HH:mm:ss), iface, rxkB/s, txkB/s\n");
		for (int i = 0; i < measurements[4].length; i++) {

			String time = measurements[4][i];
			String iface = measurements[5][i];
			String r = measurements[6][i].replace(",",".");
			String t = measurements[7][i].replace(",",".");


			resultFile.write(String.format("%s,%s,%s,%s\n", time, iface, r, t));
		}

		resultFile.flush();
		} catch (IOException e) {
		logger.error("Error while storing summarized results", e);
		}
	}
}
