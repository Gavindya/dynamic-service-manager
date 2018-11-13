//import org.apache.curator.framework.CuratorFramework;
//import org.apache.zookeeper.CreateMode;
//import serviceregistry.Member;
//import serviceregistry.MemberListener;
//import serviceregistry.MembershipEvent;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.logging.Logger;
//
//public class MemberListenerImpl implements MemberListener {
//
//    private static final Logger LOGGER = Logger.getLogger(CuratorMembershipProtocol.class.toGenericString());
//    private static String PATH;
//    private int totalPartitions;
//    private Member myself;
//    private List<Member> membersList;
//    private List<Integer> partitions;
//    private int minNumberOfPartitions;
//    private int maxNumberOfPartitions;
//    private CuratorFramework client;
//
//    public MemberListenerImpl(Member member, CuratorFramework curatorFramework) {
//        this.membersList = new ArrayList<>();
//        this.myself = member;
//        this.partitions = new ArrayList<>();
//        this.client = curatorFramework;
//        PATH = "/data/" + member.getName();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader("/home/gavindya/Desktop/FYP/servicemanager" +
//                    "/src/main/resources/conf.txt"));
//            this.totalPartitions = Integer.valueOf(br.readLine().split("=")[1]);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
////    @Override
////    public void onMemberAddedEvent(MembershipEvent event, boolean isSelfAdded) {
////        Member member = event.getMember();
////        //IF memeber_added event is NOT caused by myself add it to current members list
////        if (!member.getId().equals(String.valueOf(myself.getId()))) {
////            membersList.add(member);
////        }
////        //if self is added and no  other instances are there, add all partitions to itself
////        //otherwise calculate number of partitions min and max
////        redistributePartitions(isSelfAdded);
////
////        //if allocated any, de-allocate upto the min num of partitions level starts
//////        if (partitions.size() > minNumberOfPartitions) {
//////            System.out.println("\npreviously allocated partitons exist\n");
//////            deAllocatePartitions();
//////        } else {
////
////        removePreallocatedPartitions();
////        try {
////            Thread.sleep(5000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        if (isSelfAdded) {
////            allocatePartitions();
////        }
////        System.out.println("Allocated partitions successfully");
////    }
//
//    private void removePreallocatedPartitions() {
//        for (Integer partition : partitions) {
//            try {
//                client.delete().forPath(PATH + "/" + partition);
//            } catch (Exception e) {
////                e.printStackTrace();
//            }
//        }
//    }
//
//    private void redistributePartitions(boolean isSelfAdded) {
////        if ((membersList.size() == 0) && isSelfAdded) {
////            setAllPartitions();
////        } else {
//        int dividend = totalPartitions;
//        int divisor = (membersList.size() + 1);
//
//        minNumberOfPartitions = dividend / divisor;
//        maxNumberOfPartitions = minNumberOfPartitions + (dividend % divisor);
//        System.out.println("min = " + minNumberOfPartitions + " max=" + maxNumberOfPartitions);
////        }
//    }
//
//    private void setAllPartitions() {
//        for (int i = 1; i < (totalPartitions + 1); i++) {
//            partitions.add(i);
//        }
//        minNumberOfPartitions = totalPartitions;
//        maxNumberOfPartitions = totalPartitions;
//        System.out.println("min = " + minNumberOfPartitions + " max=" + maxNumberOfPartitions);
//    }
//
//    private void allocatePartitions() {
//
//        int startFrom = 1;
//        if (membersList.size() != 0) {
//            System.out.println("currently available members = " + membersList.size());
//            startFrom = startFrom + ((Integer.valueOf(myself.getId()) - 1) * minNumberOfPartitions);
//            System.out.println("I'm starting from index = " + startFrom);
//        }
//        //Number of partitions currently acquired
//        int acquired = 0;
//        byte[] payload = myself.getId().getBytes();
//
//        int tempEndIndex = startFrom + maxNumberOfPartitions;
//        System.out.println("\n\nEXPECTED END INDEX =" + tempEndIndex + "\n");
//
//        int upperBound;
//        if (tempEndIndex > totalPartitions) {
//            upperBound = maxNumberOfPartitions;
//        } else upperBound = maxNumberOfPartitions;
//
//        while (acquired < upperBound) {
//            try {
//                if (startFrom <= totalPartitions) {
//                    client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(PATH + "/" + startFrom, payload);
//
//                    partitions.add(startFrom);
//
//                    acquired = acquired + 1;
//                    startFrom = startFrom + 1;
//
//                }
////                System.out.println(acquired);
//            } catch (Exception e) {
//                System.out.println("COULDNT ACQUIRE = " + startFrom);
//                startFrom = startFrom + 1;
////                e.printStackTrace();
//            }
//        }
//        System.out.println("TOTAL ACQUIRED = " + (acquired));
//    }
//
//    private void deAllocatePartitions() {
//
//        System.out.println("DE allocation");
//        partitions.sort(Comparator.naturalOrder());
//        int startFrom = (Integer.valueOf(myself.getId()) - 1) * minNumberOfPartitions;
//        System.out.println("start from = " + startFrom);
//        int tempEndIndex = startFrom + maxNumberOfPartitions;
//        System.out.println("temp end = " + tempEndIndex);
//
//        System.out.println("partitions list");
//        for (Integer r : partitions) System.out.print(r + " , ");
//        System.out.println("\n");
//
//        ArrayList<Integer> newPartitions = new ArrayList<>();
//
//        int upperBound;
//        if (tempEndIndex > totalPartitions) {
//            upperBound = maxNumberOfPartitions;
//        } else upperBound = maxNumberOfPartitions;
//
////        while (acquired < upperBound) {
////
////        }
//
//        if (tempEndIndex <= totalPartitions) {
//            System.out.println("end index < = total partions");
//            if (startFrom >= partitions.get(0)) {
//                newPartitions = new ArrayList<>(partitions.subList(startFrom, tempEndIndex));
//            } else {
//                for (int i = (startFrom + 1); i < (tempEndIndex + 1); i++) {
//                    newPartitions.add(i);
//                }
//            }
//        } else {
//            System.out.println("end index EXCEEDS total partions");
//            newPartitions = new ArrayList<>(partitions.subList(startFrom, totalPartitions));
//        }
//
//        System.out.println("\nNEW partitions SUBLIST");
//        for (Integer x : newPartitions) {
//            System.out.print(x + " , ");
//        }
//        System.out.println("\n");
//
//        ArrayList<Integer> temporaryNewPartitions = new ArrayList<>();
//
//        //only delete previously acquired partitons which are nolonger needed
//        for (Integer p : partitions) {
//            if (!newPartitions.contains(p)) {
////                System.out.println("\nneed to delete - "+p+"");
//                try {
////                    System.out.println("deleteing -"+p);
//                    client.delete().forPath(PATH + "/" + p);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                temporaryNewPartitions.add(p);
//            }
//        }
//
//        if (!temporaryNewPartitions.isEmpty()) {
//            if (temporaryNewPartitions.size() < newPartitions.size()) {
//                for (Integer np : newPartitions) {
//                    if (!temporaryNewPartitions.contains(np)) {
//                        allocatePartition(np);
//                    }
//                }
//            }
//        }
//
//        partitions = newPartitions;
//        int acquired = 0;
//
//        for (Integer y : partitions) {
//            System.out.print(y + " , ");
//        }
//        System.out.println("\n");
//
//        acquired = partitions.size();
//        System.out.println("TOTAL ACQUIRED = " + (acquired));
//    }
//
//    private boolean allocatePartition(Integer partitionID) {
//        byte[] payload = myself.getId().getBytes();
//        try {
//            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(PATH + "/" + partitionID, payload);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    public void onMemberRemovedEvent(MembershipEvent event) {
//        LOGGER.info("MEMBER_REMOVED " + event.getMember().getId());
//
//    }
//
//    @Override
//    public void onMemberUpdatedEvent(MembershipEvent event) {
//        LOGGER.info("MEMBER_UPDATED " + event.getMember().getId());
//    }
//
//    @Override
//    public void onMemberAddedEvent(MembershipEvent event, boolean isSelfAdded) {
//
//    }
//}