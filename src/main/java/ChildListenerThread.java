import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.zookeeper.data.Stat;
import org.json.JSONObject;
import serviceregistry.Member;
import serviceregistry.MemberListener;
import serviceregistry.MembershipEvent;
import serviceregistry.MembershipEventType;

import java.util.List;

//This Child listener is for one Path=Service
public class ChildListenerThread extends Thread {

    private MemberListener memberListener = null;
    private CuratorFramework client = null;
    private String path = "";
    private Member myMember = null;
    private ServiceDiscovery serviceDiscovery = null;
    private boolean isSelfAdded = false;

    ChildListenerThread(CuratorFramework client, String path, MemberListener memberListener, Member member, ServiceDiscovery serviceDiscovery) {
        this.client = client;
        this.path = path;
        this.memberListener = memberListener;
        this.myMember = member;
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public void run() {
        TreeCache treeCache = new TreeCache(client, path);
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TreeCacheListener treeCacheListener = (curatorFramework, event) -> {
//            StringBuilder builder = new StringBuilder();
//            expand(builder);
//            System.out.println(builder.toString());
            if (event.getData().getPath().length() > path.length()) {
                Member member = expandSpecificNode(event.getData().getPath());

                switch (event.getType()) {
                    case NODE_ADDED: {
                        System.out.println("Node added: " + "PATH =" + event.getData().getPath()
                                + "  ADDED_ID = " + ZKPaths.getNodeFromPath(event.getData().getPath())
                                + "  MY_ID = " + myMember.getId());

                        if (ZKPaths.getNodeFromPath(event.getData().getPath()).equals(myMember.getId())) {
                            isSelfAdded = true;
                            System.out.println("IS Self Added? "+isSelfAdded);
                        }

                        this.memberListener.onMemberAddedOrRemovedEvent(new MembershipEvent(MembershipEventType.MEMBER_ADDED, member),isSelfAdded);

//                            System.out.println("Node added: " + "PATH =" + event.getData().getPath() + "  ID = " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                            }else{
//                            System.out.println("Self Node added: " + "PATH =" + event.getData().getPath() + "  ID = " + ZKPaths.getNodeFromPath(event.getData().getPath()));
//                            this.memberListener.onSelfAddedEvent(new MembershipEvent(MembershipEventType.MEMBER_ADDED, member));
//                            isSelfAdded = true;
//                        }
                        break;
                    }

                    case NODE_UPDATED: {
                        System.out.println("Node changed: " + "PATH =" + event.getData().getPath() + "  ID = " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        this.memberListener.onMemberUpdatedEvent(new MembershipEvent(MembershipEventType.MEMBER_UPDATED, member));
                        break;
                    }

                    case NODE_REMOVED: {
                        System.out.println("Node removed: " + "PATH =" + event.getData().getPath() + "  ID = " + ZKPaths.getNodeFromPath(event.getData().getPath()));
                        this.memberListener.onMemberAddedOrRemovedEvent(new MembershipEvent(MembershipEventType.MEMBER_REMOVED, member),isSelfAdded);
                        //ToDo Re Distribute Partitions among available services
                        break;
                    }
                    default:
                        break;
                }
            }
        };

        treeCache.getListenable().addListener(treeCacheListener);

        while (true) {
            //Keep the Thread Running Forever
        }
    }

    private Member expandSpecificNode(String childPath) {
        Member member = new Member();
        try {

            byte[] data = client.getData().forPath(childPath);
            // NO DATA
            if ((data == null) || (data.length == 0)) {
                return null;
            }
            String payloadString = new String(data);
            JSONObject jsonObj = new JSONObject(payloadString);

            member.setId((String) jsonObj.get("id"));
            member.setName((String) jsonObj.get("name"));

        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
        return member;
    }

    private void expand(StringBuilder builder) {
        try {
            GetChildrenBuilder childrenBuilder = client.getChildren();
            List<String> children = childrenBuilder.forPath(path);
            for (String child : children) {
                String childPath = path + "/" + child;
                System.out.println("childPath ------------------" + childPath);
                String body;
                Stat stat = client.checkExists().forPath(childPath);

                byte[] data = client.getData().forPath(childPath);
                if ((data == null) || (data.length == 0)) {
                    System.out.println("NO DATA");
                    return;
                } else {
                    String s = new String(data);
                    System.out.println("Text Decryted : " + s);
                }
            }
        } catch (Exception e) {
            builder.append(e.toString()).append("\n");
        }
    }

}
