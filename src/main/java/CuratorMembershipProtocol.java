import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import serviceregistry.Member;
import serviceregistry.MemberListener;
import serviceregistry.MembershipProtocol;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CuratorMembershipProtocol extends MembershipProtocol {

    private static final Logger LOGGER = Logger.getLogger(CuratorMembershipProtocol.class.toGenericString());
    private static final String BASE_PATH = "/services/";
    private CuratorFramework curatorFrameworkClient;
    private ServiceDiscovery<Member> serviceDiscovery;
    private UriSpec uriSpec;
    private ServiceInstance<Member> serviceInstance;

    private Member member;
    //ToDo memberID is set only for testing purposes
    private int memberID;

//    public CuratorMembershipProtocol(int memberID){
//        this.memberID = memberID;
//    }

    @Override
    public void start() {
        String connectionString = "localhost:2181";
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(new ExponentialBackoffRetry(1000/*retryInitialWaitMs*/, 3/*maxRetryCount*/));
        this.curatorFrameworkClient = builder.build();


//-----------------Original Code to be used in MSSTACK ----------------
        curatorFrameworkClient = CuratorFrameworkFactory
//                .newClient(applicationConfiguration.getServiceRegistry().getBootstrap(),new RetryNTimes(5, 1000)
                .newClient(connectionString, new RetryNTimes(5, 1000)
                );
        curatorFrameworkClient.start();
        LOGGER.info("client for " + connectionString + " started");
        createMember();
        setServiceManager();
        listen("LK", new ChildListenerImpl(member, curatorFrameworkClient));
    }

    @Override
    public void stop() {
        curatorFrameworkClient.close();
    }

    private void createMember() {
        //ToDo: added id attribute host and port is redundant because serviceInstance contain host and port
//        this.member = new Member()
//                .setId(applicationConfiguration.getServer().getId())
//                .setName(applicationConfiguration.getServer().getName())
//                .setHost(applicationConfiguration.getServer().getHost())
//                .setPort(applicationConfiguration.getServer().getPort());
        this.member = new Member()
                .setName("order")
                .setHost("127.0.0.1")
                .setPort(9000);
        memberID = getMemberID();
        this.member.setId(String.valueOf(memberID));
        System.out.println("\n\nCREATED MEMBER ID = " + memberID + "\n\n");
    }

    private int getMemberID() {
        List<Integer> existingNodes = getCurrentNodeIDs();
        if (existingNodes.isEmpty()) {
            return 1;
        } else {
            return (existingNodes.get(existingNodes.size() - 1) + 1);
        }
    }

    private List<Integer> getCurrentNodeIDs() {
        List<Integer> childrenIDs = new ArrayList<>();
        for (String c : getCurrentNodes()) {
            childrenIDs.add(Integer.valueOf(c));
        }
        childrenIDs.sort(Comparator.naturalOrder());
        return childrenIDs;
    }

    private List<String> getCurrentNodes() {
        GetChildrenBuilder childrenBuilder = curatorFrameworkClient.getChildren();
        List<String> children = new ArrayList<>();
        try {
            children = childrenBuilder.forPath(BASE_PATH + member.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return children;
    }

    private void setServiceManager() {
        ServiceManager.getInstance().setMember(member);
    }

    @Override
    public Member register() {
        try {
            JsonInstanceSerializer<Member> serializer = new JsonInstanceSerializer<>(Member.class);

            uriSpec = new UriSpec("{scheme}://{address}:{port}");

            serviceInstance = ServiceInstance
                    .<Member>builder()
                    .id(String.valueOf(memberID))
                    .uriSpec(uriSpec)
                    .name(member.getName())
//                    .address(member.getHost())
//                    .port(member.getPort())
                    .payload(member)
                    .build();

            serviceDiscovery = ServiceDiscoveryBuilder.builder(Member.class)
                    .client(curatorFrameworkClient)
                    .basePath(BASE_PATH)
                    .serializer(serializer)
                    .thisInstance(serviceInstance)
                    .build();

            serviceDiscovery.start();
            LOGGER.info("service discovery started");
            return this.member;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public void findMember(String memberName) {
        System.out.println("finding");
        try {
            Collection<ServiceInstance<Member>> instances = serviceDiscovery.queryForInstances(memberName);
            System.out.println(Arrays.toString(new Collection[]{instances}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Member> getRegisteredServices(String serviceName) {
        try {
            String path = BASE_PATH + serviceName;
            List<String> memberPaths = curatorFrameworkClient.getChildren().forPath(path);
            ArrayList<Member> members = new ArrayList<>();
            for (String memPath : memberPaths) {
                String[] attributes = memPath.split(":");
                Member mem = new Member()
                        .setName(memPath)
                        .setHost(attributes[0])
                        .setPort(Integer.valueOf(attributes[1]));
                members.add(mem);
            }
            return members;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Member updateMember(Member member) {
        try {
            uriSpec = new UriSpec(String.format("%s:{%d}", member.getHost(), member.getPort()));
            serviceInstance = ServiceInstance.<Member>builder().address(member.getHost()).name(member.getName())
                    .port(member.getPort()).uriSpec(uriSpec).payload(member).build();

            serviceDiscovery.updateService(serviceInstance);
            return member;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void removeMember(String memberName) {
        try {
            Collection<ServiceInstance<Member>> collection = serviceDiscovery.queryForInstances(memberName);
            if (!collection.isEmpty()) {
                serviceDiscovery.unregisterService(collection.iterator().next());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /*
     *Listen to all instances which are registered under /services/<service_name>/<group_name>
     *     ex: /services/orders/LK
     */
    @Override
    public void listen(String group, MemberListener listener) {

//        String path = BASE_PATH + member.getName() + "/" + group;
        String path = BASE_PATH + member.getName();

        new ChildListenerThread(this.curatorFrameworkClient, path, listener, this.member, this.serviceDiscovery)
                .start();

        System.out.println("Listener thread started");
    }
}
