package client;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadWork {

    private final String ipAddr;

    private final int threadGroupSize;
    private final int numThreadGroups;
    private final long delay;
    private final boolean p99;

    private final AtomicInteger SUCCESS = new AtomicInteger(0);

    private final Vector<Long> postLatencies = new Vector<>();
    private final Vector<Long> getLatencies = new Vector<>();

    public static final ConcurrentHashMap<Integer, AtomicInteger> throughputData = new ConcurrentHashMap<>();


    private final int total;
    private final CountDownLatch latch;

    private static final Gson instance = new Gson();

    public static final long startTime = System.currentTimeMillis();



    public ThreadWork(String ipAddr, int threadGroupSize, int numThreadGroups, long delay, boolean p99) {
        this.ipAddr = ipAddr;
        this.threadGroupSize = threadGroupSize;
        this.numThreadGroups = numThreadGroups;
        this.delay = delay;
        this.p99 = p99;
        latch = new CountDownLatch(10 + threadGroupSize * numThreadGroups);
        total = threadGroupSize * numThreadGroups * 1000 * 2 + 10 * 10 * 2;
    }

    public void run() throws Exception {

        InputStream ins = ThreadWork.class.getResourceAsStream("/nmtb.png");
        OutputStream os = new FileOutputStream("./nmtb.png");
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = ins.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();

        ExecutorService executorService = Executors.newFixedThreadPool(threadGroupSize * numThreadGroups);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                request(10);
            });
        }
        CountDownLatch latch = new CountDownLatch(threadGroupSize * numThreadGroups);
        long startTime = System.currentTimeMillis();
        // Execute
        for (int i = 0; i < numThreadGroups; i++) {
            for (int j = 0; j < threadGroupSize; j++) {
                executorService.execute(() -> request(1000));
            }
            Thread.sleep(delay * 1000);
        }
        long endTime = System.currentTimeMillis();
        latch.await();
        executorService.shutdown();

        double wallTime = (endTime - startTime) * 0.001;
        int success = SUCCESS.get();
        int fail = total - success;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String wallTimeStr = decimalFormat.format(wallTime);
        String throughput = decimalFormat.format(success / (wallTime));
        System.out.println("----------------Server Request Result----------------");
        System.out.println("Total Requests: " + total + " requests");
        System.out.println("Successful: " + success + " ," + "Failed: " + fail);
        String server = ipAddr.contains("8080") ? "Java" : "Go";
        System.out.println(server + " Server:\n" + "Thread Group Size: " + threadGroupSize + ",Num Thread Group: " + numThreadGroups + ",Delay: " + delay);
        System.out.println("Wall Time: " + wallTimeStr + " s,Throughput: " +  throughput+ " /sec");

        if(p99){
            System.out.println("Post Response Times:");
            PlotUtil.calStats(postLatencies);

            System.out.println("Get Response Times:");
            PlotUtil.calStats(getLatencies);

            PlotUtil.plot(throughputData);
        }
    }

    public void request(int num){
        for (int i = 0; i < num; i++) {
            try {
                String result = getRequest();
                System.out.println("GET:" + result);
                SUCCESS.incrementAndGet();
                if(p99){
                    int second = (int) ((System.currentTimeMillis() - startTime) / 1000);
                    throughputData.computeIfAbsent(second, k -> new AtomicInteger()).incrementAndGet();
                }
            } catch (Exception ignored) {

            }
            try {
                String result = postRequest();
                System.out.println("POST:" + result);
                SUCCESS.incrementAndGet();
                if(p99){
                    int second = (int) ((System.currentTimeMillis() - startTime) / 1000);
                    throughputData.computeIfAbsent(second, k -> new AtomicInteger()).incrementAndGet();
                }
            } catch (Exception ignored) {

            }
        }
        latch.countDown();
    }

    public String postRequest() throws Exception {
//        System.out.println("Post Response");
        OkHttpClient client = new OkHttpClient();
        File file = new File("./nmtb.png");
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("artist", "John Doe");
        jsonMap.put("title", "Artwork Title");
        jsonMap.put("year", "2023");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "nmtb.png",
                        RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("json", instance.toJson(jsonMap))
                .build();

        Request request = new Request.Builder()
                .url(ipAddr + "/")
                .post(requestBody)
                .build();
        long start = System.currentTimeMillis();
        try (Response response = client.newCall(request).execute()){
            long end = System.currentTimeMillis();
            if(response.isSuccessful()){
                throw new IOException("Unexpected code " + response);
            }
            if(p99){
                postLatencies.add(end - start);
                writeToCSV("POST", end - start, response.code());
            }
            return response.body().toString();
        }finally {

        }
    }

    public String getRequest() throws Exception {
//        System.out.println("GET Response");
        String albumID = "1";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ipAddr + "/" + albumID)
                .get()
                .build();
        long start = System.currentTimeMillis();
        try (Response response = client.newCall(request).execute()){
            long end = System.currentTimeMillis();
            if(!response.isSuccessful()){
                throw new IOException("Unexpected code " + response);
            }
            if(p99){
                getLatencies.add(end - start);
                writeToCSV("GET", end - start, response.code());
            }
            return response.body().toString();
        }finally {

        }
    }

    private void writeToCSV(String requestType, long latency, int responseCode) {
        String filePath = "requests.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            if (!Files.exists(Paths.get(filePath)) || Files.size(Paths.get(filePath)) == 0) {
                writer.println("Request Type, Latency, Response Code");
            }
            writer.println(requestType + "," + latency + "," + responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
