package client.part2;

import client.ThreadWork;

public class Part2Client {

    public static void main(String[] args) throws Exception {
        int threadGroupSize = Integer.parseInt(args[0]);
        int numThreadGroups = Integer.parseInt(args[1]);
        int delay = Integer.parseInt(args[2]);
        String ipAddr = args[4];
        ThreadWork threadWork = new ThreadWork(ipAddr, threadGroupSize, numThreadGroups, delay, true);
        threadWork.run();
    }
}
