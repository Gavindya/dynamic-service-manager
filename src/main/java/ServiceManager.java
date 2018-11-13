import serviceregistry.Member;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;

//ToDo bucket range using Hash(serviceID) mod Buckets
public class ServiceManager {

    private Member member;
    private int totalNumberOfPartitions;
    private static ServiceManager serviceManager = null;

    private ServiceManager() {
    }

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ServiceManager();
        }
        return serviceManager;
    }

    /**
     * After setMember(), it reads configuration to fin out the total number of partitions
     */
    public ServiceManager setMember(Member member, String configurationFilePath) {
        this.member = member;
        this.readConfiguration(configurationFilePath);
        return this;
    }

    public ServiceManager setMember(Member member) {
        this.member = member;
        this.readConfiguration("/home/gavindya/Desktop/FYP/servicemanager/src/main/resources/conf.txt");
        return this;
    }

    private void readConfiguration(String fileName) {
        try {
            Optional<String> configuration = new BufferedReader(new FileReader(fileName))
                    .lines().findFirst();
            configuration.ifPresent(conf-> totalNumberOfPartitions = (new Integer(conf.split("=")[1])));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //ToDo dynamically Assign workload




//    public void registerService(String data) {
//
//        System.out.println("registering node , data = " + data);
//
//        int dividend = totalNumberOfPartitions, divisor = services.size() + 1;
//        int partionsPerService = (dividend / divisor);
//        int remainder = (dividend % divisor);
//
//        services.forEach(s -> s.updateDataPartitions(partionsPerService));
//
//        SampleService sampleService = new SampleService(this.serviceName);
//        sampleService.updateDataPartitions(partionsPerService + remainder);
//        sampleService.setData(data);
//
//        services.add(sampleService);
//
//        sampleService.startServing();
//    }

}
