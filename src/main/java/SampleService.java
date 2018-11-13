//import java.util.ArrayList;
//import java.util.UUID;
//
//public class SampleService extends Thread implements ServiceInstance {
//
//    private String serviceName;
//    private int numberOfDataPartitions;
//    private ArrayList<Integer> dataPartitions;
//    private boolean isGoodToGo = false;
//    private long serviceID;
//    private String data;
//
//    public SampleService(String serviceName) {
//        System.out.println("created service - sample service");
//        this.serviceName = serviceName;
//        this.dataPartitions = new ArrayList<>();
//        this.serviceID = Math.abs(UUID.randomUUID().getLeastSignificantBits());
//    }
//
//    public String getData() {
//        return this.data;
//    }
//
//    public SampleService setData(String data) {
//        this.data = data;
//        return this;
//    }
//
//    public long getServiceId() {
//        return serviceID;
//    }
//
//    public SampleService setGoodToGo(boolean goodToGo) {
//        this.isGoodToGo = goodToGo;
//        return this;
//    }
//
//    public String getServiceName() {
//        return serviceName;
//    }
//
//    public SampleService setServiceName(String serviceName) {
//        this.serviceName = serviceName;
//        return this;
//    }
//
//    public ArrayList<Integer> getDataPartitions() {
//        return dataPartitions;
//    }
//
//    public SampleService setDataPartitions(ArrayList<Integer> dataPartitions) {
//        this.dataPartitions = dataPartitions;
//        return this;
//    }
//
//    @Override
//    public void run() {
//        System.out.println("service thread running");
//        while (true) {
//            if (isGoodToGo) {
//                System.out.println("Service is good to go");
//                //run
//            }
//            //
//        }
//    }
//
//    @Override
//    public void updateDataPartitions(int count) {
//        //Todo allocate znodes
//        this.numberOfDataPartitions = count;
//        System.out.println("updated # of partitions = "+numberOfDataPartitions);
//    }
//
//    @Override
//    public void startServing() {
//        System.out.println("serving now");
//        System.out.println("partitions = "+numberOfDataPartitions);
//        this.isGoodToGo = true;
//    }
//}
