import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.GetChildrenBuilder;
import serviceregistry.Member;
import serviceregistry.MemberListener;
import serviceregistry.MembershipEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Logger;

public class ChildListenerImpl implements MemberListener {

    private static final Logger LOGGER = Logger.getLogger(CuratorMembershipProtocol.class.toGenericString());
    private static String PATH;
    private int totalPartitions;
    private Member myself;
    private CuratorFramework client;

    public ChildListenerImpl(Member myself, CuratorFramework curatorFramework) {
        this.myself = myself;
        this.client = curatorFramework;
        PATH = "/services/" + myself.getName();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/home/gavindya/Desktop/FYP/servicemanager" +
                    "/src/main/resources/conf.txt"));
            this.totalPartitions = Integer.valueOf(br.readLine().split("=")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMemberAddedOrRemovedEvent(MembershipEvent event, boolean isSelfAdded) {
        if (isSelfAdded) {
            HashMap<String, Integer> offset = distribute();
            if (!offset.isEmpty()) {
                myself.setStartDataOffset(offset.get("start"));
                myself.setEndDataOffset(offset.get("end"));
                System.out.println("\n NODE ID = " + myself.getId() +
                        " start data at-" + myself.getStartDataOffset() +
                        " end at-" + myself.getEndDataOffset() + "\n");
            }
        }
    }

    @Override
    public void onMemberUpdatedEvent(MembershipEvent event) {

    }

    private HashMap<String, Integer> distribute() {

        int myPosition = getMyPosition()+1;
        System.out.println("my pos = "+myPosition);

        System.out.println("DISTRIBUTING..........");
        int totalNodes = getCurrentNodesCount();
        System.out.println("curent nodes = " + totalNodes);
        HashMap<String, Integer> results = new HashMap<>();

        if (totalNodes != 0) {

            int size = Math.round((float) totalPartitions / totalNodes);
            int start = 1 + size * (myPosition - 1);
            int end = (myPosition == totalNodes) ? totalPartitions : start + size - 1;
            results.put("start", start);
            results.put("end", end);
        }
        return results;
    }

    private int getMyPosition() {
        return getCurrentNodeIDs().indexOf(Integer.valueOf(myself.getId()));
    }

    private int getCurrentNodesCount() {
        List<String> children = getCurrentNodes();
        return children.size();
    }

    private List<Integer> getCurrentNodeIDs(){
        List<Integer> childrenIDs = new ArrayList<>();
        for(String c : getCurrentNodes()){
            childrenIDs.add(Integer.valueOf(c));
        }
        childrenIDs.sort(Comparator.naturalOrder());
        return childrenIDs;
    }



    private List<String> getCurrentNodes() {
        GetChildrenBuilder childrenBuilder = client.getChildren();
        List<String> children = new ArrayList<>();
        try {
            children = childrenBuilder.forPath(PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return children;
    }
}
