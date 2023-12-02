package client.part2;

import client.ThreadWork;

public class Part2Client {

    public static void main(String[] args) throws Exception {
        if(args.length != 4){
            return;
        }
        int threadGroupSize = Integer.parseInt(args[0]);
        int numThreadGroups = Integer.parseInt(args[1]);
        int delay = Integer.parseInt(args[2]);
        String ipAddr = args[3];
        ThreadWork threadWork = new ThreadWork(ipAddr, threadGroupSize, numThreadGroups, delay, true);
        threadWork.run();
    }
}
